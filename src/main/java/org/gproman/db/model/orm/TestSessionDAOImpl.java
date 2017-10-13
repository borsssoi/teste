/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:50 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.model.orm;

import static org.gproman.db.DBUtil.setDoubleParameter;
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
import org.gproman.db.model.dao.TestSessionDAO;
import org.gproman.db.model.dao.TestStintDAO;
import org.gproman.db.model.dao.TrackDAO;
import org.gproman.model.car.PHA;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.race.Weather;

/**
 * This class provides methods to populate DB Table of TEST_SESSION
 */
public class TestSessionDAOImpl
        implements
        TestSessionDAO {

    public static final TestSessionDAO INSTANCE   = new TestSessionDAOImpl();

    /* SQL to insert data */
    private static final String   SQL_INSERT =
                                                     "INSERT INTO TEST_SESSION ("
                                                             + "TRACK_ID, WEATHER, TEMPERATURE, HUMIDITY, LAPS_DONE, STINTS_DONE, TP_P, TP_H, TP_A, "
                                                             + "RDP_P, RDP_H, RDP_A, EP_P, EP_H, EP_A, CCP_P, CCP_H, CCP_A, CURRENT_CAR_ID "
                                                             + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    /* SQL to select data */
    private static final String   SQL_SELECT =
                                                     "SELECT "
                                                             + "ID, TRACK_ID, WEATHER, TEMPERATURE, HUMIDITY, LAPS_DONE, STINTS_DONE, TP_P, TP_H, TP_A, " 
                                                             + "RDP_P, RDP_H, RDP_A, EP_P, EP_H, EP_A, CCP_P, CCP_H, CCP_A, CURRENT_CAR_ID "
                                                             + "FROM TEST_SESSION WHERE "
                                                             + "ID = ?";

    /* SQL to update data */
    private static final String   SQL_UPDATE =
                                                     "UPDATE TEST_SESSION SET "
                                                             + "TRACK_ID = ?, WEATHER = ?, TEMPERATURE = ?, HUMIDITY = ?, LAPS_DONE = ?, STINTS_DONE = ?, TP_P = ?, TP_H = ?, TP_A = ?, " 
                                                             + "RDP_P = ?, RDP_H = ?, RDP_A = ?, EP_P = ?, EP_H = ?, EP_A = ?, CCP_P = ?, CCP_H = ?, CCP_A = ?, CURRENT_CAR_ID = ? "
                                                             + "WHERE "
                                                             + "ID = ?";

    /* SQL to delete data */
    private static final String   SQL_DELETE =
                                                     "DELETE FROM TEST_SESSION WHERE "
                                                             + "ID = ?";
    
    private CarDAO carDAO = CarDAOImpl.INSTANCE;
    private TrackDAO trackDAO = TrackDAOImpl.INSTANCE;
    private TestStintDAO stintDAO = TestStintDAOImpl.INSTANCE;
    

    private TestSessionDAOImpl() {
    }

    /**
     * Create a new record in Database.
     * @param td   The Object to be inserted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void create(TestSession td,
                       Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            // create car
            if ( td.getCurrentCar() != null ) {
                carDAO.create( td.getCurrentCar(), conn );
            }
            
            int index = 1;
            ps = conn.prepareStatement( SQL_INSERT );
            setParameter( ps, index++, td.getTrack() );
            setStringParameter( ps, index++, td.getWeather() );
            setIntParameter( ps, index++, td.getTemperature() );
            setIntParameter( ps, index++, td.getHumidity() );
            setIntParameter( ps, index++, td.getLapsDone() );
            setIntParameter( ps, index++, td.getStintsDone() );
            setDoubleParameter( ps, index++, td.getTestPoints().getPd() );
            setDoubleParameter( ps, index++, td.getTestPoints().getHd() );
            setDoubleParameter( ps, index++, td.getTestPoints().getAd() );
            setDoubleParameter( ps, index++, td.getRdPoints().getPd() );
            setDoubleParameter( ps, index++, td.getRdPoints().getHd() );
            setDoubleParameter( ps, index++, td.getRdPoints().getAd() );
            setDoubleParameter( ps, index++, td.getEngPoints().getPd() );
            setDoubleParameter( ps, index++, td.getEngPoints().getHd() );
            setDoubleParameter( ps, index++, td.getEngPoints().getAd() );
            setDoubleParameter( ps, index++, td.getCcPoints().getPd() );
            setDoubleParameter( ps, index++, td.getCcPoints().getHd() );
            setDoubleParameter( ps, index++, td.getCcPoints().getAd() );
            setParameter( ps, index++, td.getCurrentCar() );
            ps.executeUpdate();
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if ( generatedKeys.next() ) {
                td.setId( generatedKeys.getInt( 1 ) );
            }            
            
            for( TestStint stint : td.getStints() ) {
                // create stints
                stintDAO.create( td.getId(), stint, conn );
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
    public TestSession load(Integer key,
                       Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement( SQL_SELECT );
            ps.setInt( 1, key );
            rs = ps.executeQuery();
            List<TestSession> results = getResults( rs, conn );
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
    public int update(TestSession td,
                      Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            if( td.getCurrentCar() != null ) {
                carDAO.createOrUpdate( td.getCurrentCar(), conn );
            }
            
            TestSession saved = load( td.getId(), conn );
            int result = 0;
            if ( saved != null ) {
                ps = conn.prepareStatement( SQL_UPDATE );
                int index = 1;
                setParameter( ps, index++, td.getTrack() );
                setStringParameter( ps, index++, td.getWeather() );
                setIntParameter( ps, index++, td.getTemperature() );
                setIntParameter( ps, index++, td.getHumidity() );
                setIntParameter( ps, index++, td.getLapsDone() );
                setIntParameter( ps, index++, td.getStintsDone() );
                setDoubleParameter( ps, index++, td.getTestPoints().getPd() );
                setDoubleParameter( ps, index++, td.getTestPoints().getHd() );
                setDoubleParameter( ps, index++, td.getTestPoints().getAd() );
                setDoubleParameter( ps, index++, td.getRdPoints().getPd() );
                setDoubleParameter( ps, index++, td.getRdPoints().getHd() );
                setDoubleParameter( ps, index++, td.getRdPoints().getAd() );
                setDoubleParameter( ps, index++, td.getEngPoints().getPd() );
                setDoubleParameter( ps, index++, td.getEngPoints().getHd() );
                setDoubleParameter( ps, index++, td.getEngPoints().getAd() );
                setDoubleParameter( ps, index++, td.getCcPoints().getPd() );
                setDoubleParameter( ps, index++, td.getCcPoints().getHd() );
                setDoubleParameter( ps, index++, td.getCcPoints().getAd() );
                setParameter( ps, index++, td.getCurrentCar() );
                setIntParameter( ps, index++, td.getId() );
                result = ps.executeUpdate();
            }
            
            for( TestStint stint : td.getStints() ) {
                stintDAO.createOrUpdate( td.getId(), stint, conn );
            }
            return result;
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
            TestSession ts = load( key, conn );
            
            for( TestStint stint : ts.getStints() ) {
                stintDAO.delete( stint.getId(), conn );
            }
            
            ps = conn.prepareStatement( SQL_DELETE );
            ps.setInt( 1, key );
            ps.executeUpdate();
            
            if( ts.getCurrentCar() != null ) {
                carDAO.delete( ts.getCurrentCar().getId(), conn );
            }
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
    private List<TestSession> getResults(ResultSet rs,
                                         Connection conn) throws SQLException {
        List<TestSession> results = new ArrayList<TestSession>();
        while ( rs.next() ) {
            TestSession td = new TestSession();
            td.setId( rs.getInt( "ID" ) );
            
            Integer trackId = DBUtil.getIntOrNull( rs, "TRACK_ID" );
            if( trackId != null ) {
                td.setTrack( trackDAO.load( trackId, conn ) );
            }
            
            String weather = rs.getString( "WEATHER" );
            if( weather != null ) {
                td.setWeather( Weather.valueOf( weather ) );
            }
            
            td.setTemperature( DBUtil.getIntOrNull( rs, "TEMPERATURE" ) );
            td.setHumidity( DBUtil.getIntOrNull( rs, "HUMIDITY" ) );
            td.setLapsDone( DBUtil.getIntOrNull( rs, "LAPS_DONE" ) );
            td.setStintsDone( DBUtil.getIntOrNull( rs, "STINTS_DONE" ) );
            PHA pha = new PHA();
            pha.setP( DBUtil.getDoubleOrNull( rs, "TP_P" ) );
            pha.setH( DBUtil.getDoubleOrNull( rs, "TP_H" ) );
            pha.setA( DBUtil.getDoubleOrNull( rs, "TP_A" ) );
            td.setTestPoints( pha );
            pha = new PHA();
            pha.setP( DBUtil.getDoubleOrNull( rs, "RDP_P" ) );
            pha.setH( DBUtil.getDoubleOrNull( rs, "RDP_H" ) );
            pha.setA( DBUtil.getDoubleOrNull( rs, "RDP_A" ) );
            td.setRdPoints( pha );
            pha = new PHA();
            pha.setP( DBUtil.getDoubleOrNull( rs, "EP_P" ) );
            pha.setH( DBUtil.getDoubleOrNull( rs, "EP_H" ) );
            pha.setA( DBUtil.getDoubleOrNull( rs, "EP_A" ) );
            td.setEngPoints( pha );
            pha = new PHA();
            pha.setP( DBUtil.getDoubleOrNull( rs, "CCP_P" ) );
            pha.setH( DBUtil.getDoubleOrNull( rs, "CCP_H" ) );
            pha.setA( DBUtil.getDoubleOrNull( rs, "CCP_A" ) );
            td.setCcPoints( pha );
            
            Integer carId = DBUtil.getIntOrNull( rs, "CURRENT_CAR_ID" );
            if( carId != null ) {
                td.setCurrentCar( carDAO.load( carId, conn ) );
            }
            
            List<TestStint> stints = stintDAO.loadStintsForSession( td.getId(), conn );
            td.setStints( stints );

            results.add( td );
       }
        return results;
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