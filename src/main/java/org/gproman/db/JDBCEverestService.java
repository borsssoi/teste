/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gproman.db;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.gproman.db.everest.dao.EverestForumTopicDAO;
import org.gproman.db.everest.dao.EverestRaceDAO;
import org.gproman.db.everest.dao.EverestStatusDAO;
import org.gproman.db.everest.dao.EverestTrackDAO;
import org.gproman.db.everest.dao.SearchParams;
import org.gproman.db.everest.orm.EverestForumTopicDAOImpl;
import org.gproman.db.everest.orm.EverestNormalizedRaceDAOImpl;
import org.gproman.db.everest.orm.EverestStatusDAOImpl;
import org.gproman.db.everest.orm.EverestTrackDAOImpl;
import org.gproman.db.model.dao.TyreSupplierDAO;
import org.gproman.db.model.orm.TyreSupplierDAOImpl;
import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.EverestStatus;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.track.Track;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JDBC implementation of the EverestService
 */
public class JDBCEverestService
        implements
        EverestService {

    private final Logger         logger           = LoggerFactory.getLogger(JDBCEverestService.class);

    private String               url;
    private String               login;
    private String               pwd;
    private Connection           conn;

    private EverestStatusDAO     statusDAO        = EverestStatusDAOImpl.INSTANCE;
    private EverestTrackDAO      trackDAO         = EverestTrackDAOImpl.INSTANCE;
    private EverestRaceDAO       raceDAO          = EverestNormalizedRaceDAOImpl.INSTANCE;
    private TyreSupplierDAO      supplierDAO      = TyreSupplierDAOImpl.INSTANCE;
    private EverestForumTopicDAO topicDAO         = EverestForumTopicDAOImpl.INSTANCE;

    private static final String  SQL_CHECK_SCHEMA = "SELECT COUNT(*) AS COUNT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'EVEREST_STATUS'";

    private boolean              dirty            = false;

    public JDBCEverestService(String url,
            String login,
            String pwd) {
        this.url = url;
        this.login = login;
        this.pwd = pwd;
    }

    private void setDirty() {
        this.dirty = true;
    }

    public boolean wasModified() {
        return this.dirty;
    }

    @Override
    public int start() {
        int previousSchemaVersion = 0;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url, login, pwd);
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(SQL_CHECK_SCHEMA);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt("COUNT");
            if (count == 0) {
                logger.info("New everest database... creating schema...");
                // create the schema
                RunScript.execute(conn, new InputStreamReader(getClass().getResourceAsStream("/everest_1.sql")));
                commit();
                logger.info("Everest schema successfuly created.");
            } else {
                previousSchemaVersion = getEverestStatus().getSchemaVersion();
            }

            for (int i = 2; i <= CURRENT_SCHEMA_VERSION; i++) {
                if (getEverestStatus().getSchemaVersion() < i) {
                    logger.info("Updating everest schema to version " + i);
                    RunScript.execute(conn, new InputStreamReader(getClass().getResourceAsStream("/everest_" + i + ".sql")));
                    commit();
                    logger.info("Everest schema successfuly updated.");
                }
            }
        } catch (Exception e) {
            logger.error("Error starting up database connection. Impossible to continue.", e);
            JOptionPane.showMessageDialog(null,
                    "O banco de dados do Evereste local está bloqueado por\n" +
                            "outro programa. Por favor feche o outro programa e\n" +
                            "execute o GMT novamente.",
                    "Banco de dados bloqueado",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return previousSchemaVersion;
    }

    @Override
    public void shutdown() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection.", e);
            }

        }
    }

    @Override
    public boolean isInitialized() {
        return getTrackById(1) != null;
    }

    @Override
    public ForumTopic getForumTopic(TopicType type, Integer season, Integer race) {
        try {
            return topicDAO.loadTopic(type, season, race, conn);
        } catch (SQLException e) {
            logger.error("Error loading topic.", e);
        }
        return null;
    }

    @Override
    public void store(ForumTopic topic) {
        try {
            topicDAO.createOrUpdate(topic, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving topic.", e);
            rollback();
        }
    }

    @Override
    public void deleteAllForumTopic() {
        try {
            topicDAO.deleteAll(conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error deleting all topics.", e);
            rollback();
        }
    }

    @Override
    public EverestStatus getEverestStatus() {
        try {
            return statusDAO.load(1, conn);
        } catch (SQLException e) {
            logger.error("Error loading everest status.", e);
        }
        return null;
    }

    @Override
    public EverestMetrics getEverestMetrics() {
        try {
            return statusDAO.loadMetrics(conn);
        } catch (SQLException e) {
            logger.error("Error loading everest metrics.", e);
        }
        return null;
    }

    @Override
    public void store(EverestStatus status) {
        try {
            statusDAO.update(status, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving everest status.", e);
            rollback();
        }
    }

    @Override
    public Track getTrackById(Integer trackId) {
        try {
            return trackDAO.load(trackId, conn);
        } catch (SQLException e) {
            logger.error("Error loading track with id = " + trackId, e);
        }
        return null;
    }

    @Override
    public Track getTrackByName(String name) {
        try {
            return trackDAO.loadByName(name, conn);
        } catch (SQLException e) {
            logger.error("Error loading track with name = " + name, e);
        }
        return null;
    }

    @Override
    public List<Track> getAllTracks() {
        try {
            return trackDAO.loadAllTracks(conn);
        } catch (SQLException e) {
            logger.error("Error loading tracks", e);
        }
        return null;
    }

    @Override
    public void store(Track tr) {
        try {
            trackDAO.createOrUpdate(tr, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving track " + tr, e);
            rollback();
        }
    }

    @Override
    public void store(NormalizedRace race) {
        try {
            raceDAO.createOrUpdate(race, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving race " + race, e);
            rollback();
        }
    }

    @Override
    public List<NormalizedRace> getRaces(Integer season, Integer race) {
        try {
            return raceDAO.loadRacesBySeasonRace(season, race, conn);
        } catch (SQLException e) {
            logger.error("Error loading races for season " + season + " race " + race, e);
        }
        return null;
    }

    @Override
    public List<NormalizedRace> getRaces(SearchParams params) {
        try {
            return raceDAO.loadRaces(params, conn);
        } catch (SQLException e) {
            logger.error("Error loading races based on parameters", e);
        }
        return null;
    }

    private void commit() {
        try {
            conn.commit();
            setDirty();
        } catch (SQLException e) {
            logger.error("Error committing transaction. Rolling it back.", e);
            rollback();
        }
    }

    private void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            logger.error("Error rolling back transaction. Nothing to do, sorry.", e);
        }
    }

    @Override
    public void store(TyreSupplierAttrs supplier) {
        try {
            supplierDAO.createOrUpdate(supplier, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving tyre supplier: " + supplier, e);
            rollback();
        }
    }

    @Override
    public TyreSupplierAttrs getTyreSupplier(Integer seasonNumber, String supplierName) {
        try {
            TyreSupplierAttrs supplier = supplierDAO.loadSupplierBySeasonNumber(seasonNumber, supplierName, conn);
            return supplier;
        } catch (SQLException e) {
            logger.error("Error loading supplier " + supplierName + " for season " + seasonNumber + ".", e);
        }
        return null;
    }

    @Override
    public List<TyreSupplierAttrs> getTyreSuppliersForSeason(Integer seasonNumber) {
        try {
            List<TyreSupplierAttrs> suppliers = supplierDAO.loadSuppliersForSeason(seasonNumber, conn);
            return suppliers;
        } catch (SQLException e) {
            logger.error("Error loading suppliers for season " + seasonNumber + ".", e);
        }
        return Collections.emptyList();
    }

}
