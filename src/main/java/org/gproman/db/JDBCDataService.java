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

import org.gproman.db.model.dao.ApplicationStatusDAO;
import org.gproman.db.model.dao.CarDAO;
import org.gproman.db.model.dao.DriverDAO;
import org.gproman.db.model.dao.DriverWearWeightDAO;
import org.gproman.db.model.dao.ManagerDAO;
import org.gproman.db.model.dao.RaceDAO;
import org.gproman.db.model.dao.SeasonDAO;
import org.gproman.db.model.dao.TrackDAO;
import org.gproman.db.model.dao.TyreSupplierDAO;
import org.gproman.db.model.dao.WearCoefsDAO;
import org.gproman.db.model.dao.WearPlanDAO;
import org.gproman.db.model.orm.ApplicationStatusDAOImpl;
import org.gproman.db.model.orm.CarDAOImpl;
import org.gproman.db.model.orm.DriverDAOImpl;
import org.gproman.db.model.orm.DriverWearWeightDAOImpl;
import org.gproman.db.model.orm.ManagerDAOImpl;
import org.gproman.db.model.orm.RaceDAOImpl;
import org.gproman.db.model.orm.SeasonDAOImpl;
import org.gproman.db.model.orm.TrackDAOImpl;
import org.gproman.db.model.orm.TyreSupplierDAOImpl;
import org.gproman.db.model.orm.WearCoefsDAOImpl;
import org.gproman.db.model.orm.WearPlanDAOImpl;
import org.gproman.model.ApplicationStatus;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.WearPlan;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.track.Track;
import org.gproman.model.track.WearCoefs;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JPA implementation of the DataService
 */
public class JDBCDataService
        implements
        DataService {

    private final Logger         logger                 = LoggerFactory.getLogger(JDBCDataService.class);

    private String               url;
    private String               login;
    private String               pwd;
    private Connection           conn;

    private ApplicationStatusDAO statusDAO              = ApplicationStatusDAOImpl.INSTANCE;
    private TrackDAO             trackDAO               = TrackDAOImpl.INSTANCE;
    private ManagerDAO           managerDAO             = ManagerDAOImpl.INSTANCE;
    private WearCoefsDAO         wearDAO                = WearCoefsDAOImpl.INSTANCE;
    private DriverWearWeightDAO  dwearDAO               = DriverWearWeightDAOImpl.INSTANCE;

    private DriverDAO            driverDAO              = DriverDAOImpl.INSTANCE;
    private CarDAO               carDAO                 = CarDAOImpl.INSTANCE;
    private RaceDAO              raceDAO                = RaceDAOImpl.INSTANCE;
    private SeasonDAO            seasonDAO              = SeasonDAOImpl.INSTANCE;
    private TyreSupplierDAO      supplierDAO            = TyreSupplierDAOImpl.INSTANCE;

    private WearPlanDAO          wearPlanDAO            = WearPlanDAOImpl.INSTANCE;
    
    private static final String  SQL_CHECK_SCHEMA       = "SELECT COUNT(*) AS COUNT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'APPLICATION_STATUS'";

    private boolean              dirty                  = false;

    public JDBCDataService(String url,
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
                logger.info("New database... creating schema...");
                // create the schema
                RunScript.execute(conn, new InputStreamReader(getClass().getResourceAsStream("/schema_1.sql")));
                commit();
                logger.info("Schema successfuly created.");
            } else {
                previousSchemaVersion = getApplicationStatus().getSchemaVersion();
            }

            for (int i = 2; i <= CURRENT_SCHEMA_VERSION; i++) {
                if (getApplicationStatus().getSchemaVersion() < i) {
                    logger.info("Updating schema to version " + i);
                    RunScript.execute(conn, new InputStreamReader(getClass().getResourceAsStream("/schema_" + i + ".sql")));
                    commit();
                    logger.info("Schema successfuly updated.");
                }
            }
        } catch (Exception e) {
            logger.error("Error starting up database connection. Impossible to continue.", e);
            JOptionPane.showMessageDialog( null, 
                    "O banco de dados local do GMT está bloqueado por\n"+
                    "outro programa. Por favor feche o outro programa\n"+
                    "e execute o GMT novamente.",
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
    public ApplicationStatus getApplicationStatus() {
        try {
            return statusDAO.load(1, conn);
        } catch (SQLException e) {
            logger.error("Error loading application status.", e);
        }
        return null;
    }

    @Override
    public void store(ApplicationStatus status) {
        try {
            statusDAO.update(status, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving application status.", e);
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
    public Manager getManager() {
        try {
            return managerDAO.load(1, conn);
        } catch (SQLException e) {
            logger.error("Error loading manager.", e);
        }
        return null;
    }

    @Override
    public void store(Manager manager) {
        try {
            managerDAO.createOrUpdate(manager, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving manager " + manager, e);
            rollback();
        }
    }

    @Override
    public WearCoefs getWearCoefs() {
        try {
            return wearDAO.load(1, conn);
        } catch (SQLException e) {
            logger.error("Error loading wear polinomial coeficients.", e);
        }
        return null;
    }

    @Override
    public void store(WearCoefs wp) {
        try {
            wearDAO.createOrUpdate(wp, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving wear coeficients " + wp, e);
            rollback();
        }
    }

    @Override
    public DriverWearWeight getDriverAttributesWearWeight() {
        try {
            return dwearDAO.load(1, conn);
        } catch (SQLException e) {
            logger.error("Error loading driver wear weight coeficients.", e);
        }
        return null;
    }

    @Override
    public void store(DriverWearWeight daww) {
        try {
            dwearDAO.createOrUpdate(daww, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving driver wear weight coeficients " + daww, e);
            rollback();
        }
    }

    @Override
    public Driver getDriver(Integer id) {
        try {
            return driverDAO.load(id, conn);
        } catch (SQLException e) {
            logger.error("Error loading driver for id = " + id, e);
        }
        return null;
    }

    @Override
    public void store(Driver driver) {
        try {
            driverDAO.create(driver, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving driver " + driver, e);
            rollback();
        }
    }

    @Override
    public Car getCar(Integer id) {
        try {
            return carDAO.load(id, conn);
        } catch (SQLException e) {
            logger.error("Error loading car for id = " + id, e);
        }
        return null;
    }

    @Override
    public void store(Car car) {
        try {
            carDAO.create(car, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving car " + car, e);
            rollback();
        }
    }

    @Override
    public Race getNextRace() {
        try {
            ApplicationStatus status = getApplicationStatus();
            return status.getCurrentSeason() != null && status.getNextRace() != null ?
                    raceDAO.loadRaceByNumber(status.getCurrentSeason(), status.getNextRace(), conn) :
                    null;
        } catch (SQLException e) {
            logger.error("Error loading next race.", e);
        }
        return null;
    }

    @Override
    public Race getRace(Integer season,
            Integer race) {
        try {
            return raceDAO.loadRaceByNumber(season, race, conn);
        } catch (SQLException e) {
            logger.error("Error loading race " + race + " on season " + season, e);
        }
        return null;
    }

    @Override
    public void store(String managerName, Race race) {
        try {
            Season season = seasonDAO.loadSeasonByNumber(managerName, race.getSeasonNumber(), conn);
            if (season != null) {
                raceDAO.createOrUpdate(season.getId(), race, conn);
                commit();
            } else {
                logger.error("Error saving race. Season " + race.getSeasonNumber() + " not found.");
            }
        } catch (SQLException e) {
            logger.error("Error saving race " + race, e);
            rollback();
        }
    }

    @Override
    public Season getSeason(String managerName, Integer number) {
        try {
            Season season = seasonDAO.loadSeasonByNumber(managerName, number, conn);
            if (season != null) {
                List<Race> races = raceDAO.loadRacesForSeasonById(season.getId(), conn);
                season.getRaces().addAll(races);
            }
            return season;
        } catch (SQLException e) {
            logger.error("Error loading season " + number, e);
        }
        return null;
    }

    @Override
    public Season getCurrentSeason(String managerName) {
        Integer currentSeason = getApplicationStatus().getCurrentSeason();
        return currentSeason != null ? getSeason(managerName, currentSeason) : null;
    }

    @Override
    public void store(Season season) {
        try {
            seasonDAO.createOrUpdate(season, conn);
            for (Race race : season.getRaces()) {
                raceDAO.createOrUpdate(season.getId(), race, conn);
            }
            commit();
        } catch (SQLException e) {
            logger.error("Error saving season " + season, e);
            rollback();
        }
    }

    @Override
    public List<Integer> getSeasonsForSetup() {
        try {
            List<Integer> seasons = seasonDAO.loadSeasonsForSetup(conn);
            return seasons;
        } catch (SQLException e) {
            logger.error("Error loading seasons for setup report.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getSeasonsForTelemetry() {
        try {
            List<Integer> seasons = seasonDAO.loadSeasonsForTelemetry(conn);
            return seasons;
        } catch (SQLException e) {
            logger.error("Error loading seasons for telemetry report.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Season> getSeasonsWithNullGroups() {
        try {
            List<Season> seasons = seasonDAO.loadSeasonsWithNullGroups(conn);
            return seasons;
        } catch (SQLException e) {
            logger.error("Error loading seasons with null groups.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getSeasonsForTest() {
        try {
            List<Integer> seasons = seasonDAO.loadSeasonsForTest(conn);
            return seasons;
        } catch (SQLException e) {
            logger.error("Error loading seasons for test report.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Race> getRacesForTest(Integer season) {
        try {
            List<Race> races = raceDAO.loadRacesForTest(season, conn);
            return races;
        } catch (SQLException e) {
            logger.error("Error loading races for test report.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Race> getRacesForSetup(Integer season) {
        try {
            List<Race> races = raceDAO.loadRacesForSetup(season, conn);
            return races;
        } catch (SQLException e) {
            logger.error("Error loading races for test report.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Race> getRacesForTelemetry(Integer season) {
        try {
            List<Race> races = raceDAO.loadRacesForTelemetry(season, conn);
            return races;
        } catch (SQLException e) {
            logger.error("Error loading races for telemetry report.", e);
        }
        return Collections.emptyList();
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
    public boolean isFirstExecutionForSeason() {
        return getNextRace() == null;
    }

    @Override
    public void store(WearPlan plan) {
        try {
            wearPlanDAO.createOrUpdate(plan, conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error saving wear plan " + plan, e);
            rollback();
        }
    }

    @Override
    public void delete(WearPlan plan) {
        try {
            wearPlanDAO.delete(plan.getId(), conn);
            commit();
        } catch (SQLException e) {
            logger.error("Error deleting wear plan " + plan, e);
            rollback();
        }
    }

    @Override
    public List<WearPlan> loadWearPlans() {
        try {
            return wearPlanDAO.loadAllPlans(conn);
        } catch (SQLException e) {
            logger.error("Error loading wear plans.", e);
        }
        return Collections.emptyList();
    }

    public Connection getConnection() {
        return conn;
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
            TyreSupplierAttrs supplier = supplierDAO.loadSupplierBySeasonNumber( seasonNumber, supplierName, conn);
            return supplier;
        } catch (SQLException e) {
            logger.error("Error loading supplier "+supplierName+" for season "+seasonNumber+".", e);
        }
        return null;
    }

    @Override
    public List<TyreSupplierAttrs> getTyreSuppliersForSeason(Integer seasonNumber) {
        try {
            List<TyreSupplierAttrs> suppliers = supplierDAO.loadSuppliersForSeason(seasonNumber, conn);
            return suppliers;
        } catch (SQLException e) {
            logger.error("Error loading suppliers for season "+seasonNumber+".", e);
        }
        return Collections.emptyList();
    }

}
