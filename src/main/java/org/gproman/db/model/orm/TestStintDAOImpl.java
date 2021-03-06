/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:50 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.model.orm;

import static org.gproman.db.DBUtil.setIntParameter;
import static org.gproman.db.DBUtil.setParameter;
import static org.gproman.db.DBUtil.setStringParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gproman.db.DBUtil;
import org.gproman.db.model.dao.CarDAO;
import org.gproman.db.model.dao.CarSettingsDAO;
import org.gproman.db.model.dao.TestStintDAO;
import org.gproman.model.race.TestPriority;
import org.gproman.model.race.TestStint;

/**
 * This class provides methods to populate DB Table of TEST_SESSION
 */
public class TestStintDAOImpl
        implements
        TestStintDAO {

    public static final TestStintDAO INSTANCE           = new TestStintDAOImpl();

    /* SQL to insert data */
    private static final String      SQL_INSERT         =
                                                                "INSERT INTO TEST_STINT ("
                                                                        + "TEST_SESSION_ID, NUMBER, LAPS_DONE, LAPS_PLANNED, BEST_TIME, MEAN_TIME, "
                                                                        + "SETTINGS_ID, FUEL_START, FUEL_END, TYRES_END, PRIORITY, COMMENTS, "
                                                                        + "CAR_START_ID, CAR_FINISH_ID "
                                                                        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /* SQL to select data */
    private static final String      SQL_SELECT         =
                                                                "SELECT "
                                                                        + "ID, TEST_SESSION_ID, NUMBER, LAPS_DONE, LAPS_PLANNED, BEST_TIME, MEAN_TIME, "
                                                                        + "SETTINGS_ID, FUEL_START, FUEL_END, TYRES_END, PRIORITY, COMMENTS, "
                                                                        + "CAR_START_ID, CAR_FINISH_ID "
                                                                        + "FROM TEST_STINT WHERE "
                                                                        + "ID = ?";

    private static final String      SQL_SELECT_SESSION =
                                                                "SELECT "
                                                                        + "ID, TEST_SESSION_ID, NUMBER, LAPS_DONE, LAPS_PLANNED, BEST_TIME, MEAN_TIME, "
                                                                        + "SETTINGS_ID, FUEL_START, FUEL_END, TYRES_END, PRIORITY, COMMENTS, "
                                                                        + "CAR_START_ID, CAR_FINISH_ID "
                                                                        + "FROM TEST_STINT WHERE "
                                                                        + "TEST_SESSION_ID = ?";

    /* SQL to update data */
    private static final String      SQL_UPDATE         =
                                                                "UPDATE TEST_STINT SET "
                                                                        + "TEST_SESSION_ID = ?, NUMBER = ?, LAPS_DONE = ?, LAPS_PLANNED = ?, BEST_TIME = ?, MEAN_TIME = ?, "
                                                                        + "SETTINGS_ID = ?, FUEL_START = ?, FUEL_END = ?, TYRES_END = ?, PRIORITY = ?, COMMENTS = ?, "
                                                                        + "CAR_START_ID = ?, CAR_FINISH_ID = ? "
                                                                        + "WHERE "
                                                                        + "ID = ?";

    /* SQL to delete data */
    private static final String      SQL_DELETE         =
                                                                "DELETE FROM TEST_STINT WHERE "
                                                                        + "ID = ?";

    private CarSettingsDAO           settingsDAO        = CarSettingsDAOImpl.INSTANCE;
    private CarDAO                   carDAO             = CarDAOImpl.INSTANCE;

    private TestStintDAOImpl() {
    }

    /**
     * Create a new record in Database.
     * @param td   The Object to be inserted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void create(Integer testSessionId,
                       TestStint td,
                       Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            // create car settings
            if ( td.getSettings() != null ) {
                settingsDAO.create( td.getSettings(), conn );
            }
            if ( td.getCarStart() != null ) {
                carDAO.create( td.getCarStart(), conn );
            }
            if ( td.getCarFinish() != null ) {
                carDAO.create( td.getCarFinish(), conn );
            }

            int index = 1;
            ps = conn.prepareStatement( SQL_INSERT );
            setIntParameter( ps, index++, testSessionId );
            setIntParameter( ps, index++, td.getNumber() );
            setIntParameter( ps, index++, td.getLapsDone() );
            setIntParameter( ps, index++, td.getLapsPlanned() );
            setIntParameter( ps, index++, td.getBestTime() );
            setIntParameter( ps, index++, td.getMeanTime() );
            setParameter( ps, index++, td.getSettings() );
            setIntParameter( ps, index++, td.getFuelStart() );
            setIntParameter( ps, index++, td.getFuelEnd() );
            setIntParameter( ps, index++, td.getTyresEnd() );
            setStringParameter( ps, index++, td.getPriority() != null ? td.getPriority().english : null );
            setStringParameter( ps, index++, td.getComments() );
            setParameter( ps, index++, td.getCarStart() );
            setParameter( ps, index++, td.getCarFinish() );
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if ( generatedKeys.next() ) {
                td.setId( generatedKeys.getInt( 1 ) );
            }
        } finally {
            close( ps );
        }
    }

    /**
     * Retrieve a record from Database.
     * @param beanKey   The PK Object to be retrieved.
     * @param conn      JDBC Connection.
     * @exception       SQLException if something is wrong.
     */
    public TestStint load(Integer key,
                          Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement( SQL_SELECT );
            ps.setInt( 1, key );
            rs = ps.executeQuery();
            List<TestStint> results = getResults( rs, conn );
            if ( results.size() > 0 ) return results.get( 0 );
            else return null;
        } finally {
            close( rs );
            close( ps );
        }
    }

    /**
     * Update a record in Database.
     * @param bean   The Object to be saved.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public int update(Integer testSessionId,
                      TestStint td,
                      Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            if( td.getId() != null ) {
                TestStint saved = load( td.getId(), conn );
                int result = 0;
                if ( saved != null ) {
                    // create car settings
                    if ( td.getSettings() != null ) {
                        settingsDAO.createOrUpdate( td.getSettings(), conn );
                    }
                    if ( td.getCarStart() != null ) {
                        carDAO.createOrUpdate( td.getCarStart(), conn );
                    }
                    if ( td.getCarFinish() != null ) {
                        carDAO.createOrUpdate( td.getCarFinish(), conn );
                    }

                    ps = conn.prepareStatement( SQL_UPDATE );
                    int index = 1;
                    setIntParameter( ps, index++, testSessionId );
                    setIntParameter( ps, index++, td.getNumber() );
                    setIntParameter( ps, index++, td.getLapsDone() );
                    setIntParameter( ps, index++, td.getLapsPlanned() );
                    setIntParameter( ps, index++, td.getBestTime() );
                    setIntParameter( ps, index++, td.getMeanTime() );
                    setParameter( ps, index++, td.getSettings() );
                    setIntParameter( ps, index++, td.getFuelStart() );
                    setIntParameter( ps, index++, td.getFuelEnd() );
                    setIntParameter( ps, index++, td.getTyresEnd() );
                    setStringParameter( ps, index++, td.getPriority() != null ? td.getPriority().english : null );
                    setStringParameter( ps, index++, td.getComments() );
                    setParameter( ps, index++, td.getCarStart() );
                    setParameter( ps, index++, td.getCarFinish() );
                    setIntParameter( ps, index++, td.getId() );
                    result = ps.executeUpdate();
                }
                return result;
            } else {
                return 0;
            }
        } finally {
            close( ps );
        }
    }

    /**
     * Create a new record in Database.
     * @param bean   The PK Object to be deleted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void delete(Integer key,
                       Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            TestStint ts = load( key, conn );

            ps = conn.prepareStatement( SQL_DELETE );
            ps.setInt( 1, key );
            ps.executeUpdate();

            if ( ts.getSettings() != null ) {
                settingsDAO.delete( ts.getId(), conn );
            }
            // Not working at the moment as multiple stints may reference the same car
//            if ( ts.getCarStart() != null ) {
//                carDAO.delete( ts.getCarStart().getId(), conn );
//            }
//            if ( ts.getCarFinish() != null ) {
//                carDAO.delete( ts.getCarFinish().getId(), conn );
//            }
        } finally {
            close( ps );
        }
    }

    /**
     * Populate the ResultSet.
     * @param rs     The ResultSet.
     * @return       The Object to retrieve from DB.
     * @exception    SQLException if something is wrong.
     */
    private List<TestStint> getResults(ResultSet rs,
                                       Connection conn) throws SQLException {
        List<TestStint> results = new ArrayList<TestStint>();
        while ( rs.next() ) {
            TestStint td = new TestStint();
            td.setId( rs.getInt( "ID" ) );
            td.setNumber( DBUtil.getIntOrNull( rs, "NUMBER" ) );
            td.setLapsDone( DBUtil.getIntOrNull( rs, "LAPS_DONE" ) );
            td.setLapsPlanned( DBUtil.getIntOrNull( rs, "LAPS_PLANNED" ) );
            td.setBestTime( DBUtil.getIntOrNull( rs, "BEST_TIME" ) );
            td.setMeanTime( DBUtil.getIntOrNull( rs, "MEAN_TIME" ) );
            td.setFuelStart( DBUtil.getIntOrNull( rs, "FUEL_START" ) );
            td.setFuelEnd( DBUtil.getIntOrNull( rs, "FUEL_END" ) );
            td.setTyresEnd( DBUtil.getIntOrNull( rs, "TYRES_END" ) );
            String priority = rs.getString( "PRIORITY" );
            if ( priority != null ) {
                td.setPriority( TestPriority.determinePriority( priority ) );
            }
            td.setComments( rs.getString( "COMMENTS" ) );
            Integer settingsId = DBUtil.getIntOrNull( rs, "SETTINGS_ID" );
            if ( settingsId != null ) {
                td.setSettings( settingsDAO.load( settingsId, conn ) );
            }
            Integer carStartId = DBUtil.getIntOrNull( rs, "CAR_START_ID" );
            if ( carStartId != null ) {
                td.setCarStart( carDAO.load( carStartId, conn ) );
            }
            Integer carFinishId = DBUtil.getIntOrNull( rs, "CAR_FINISH_ID" );
            if ( carFinishId != null ) {
                td.setCarFinish( carDAO.load( carFinishId, conn ) );
            }

            results.add( td );
        }
        return results;
    }

    @Override
    public void createOrUpdate(Integer testSessionId,
                               TestStint stint,
                               Connection conn) throws SQLException {
        if ( update( testSessionId, stint, conn ) == 0 ) {
            create( testSessionId, stint, conn );
        }
    }

    @Override
    public List<TestStint> loadStintsForSession(Integer testSessionId,
                                                Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement( SQL_SELECT_SESSION );
            ps.setInt( 1, testSessionId );
            rs = ps.executeQuery();
            return getResults( rs, conn );
        } finally {
            close( rs );
            close( ps );
        }
    }

    /**
     * Close JDBC Statement.
     * @param stmt  Statement to be closed.
     */
    private void close(Statement stmt) {
        if ( stmt != null ) {
            try {
                stmt.close();
            } catch ( SQLException e ) {
            }
        }
    }

    /**
     * Close JDBC ResultSet.
     * @param rs  ResultSet to be closed.
     */
    private void close(ResultSet rs) {
        if ( rs != null ) {
            try {
                rs.close();
            } catch ( SQLException e ) {
            }
        }
    }

}