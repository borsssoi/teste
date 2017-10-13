/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:50 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gproman.db.model.dao.IdGeneratorDAO;
import org.gproman.db.model.dao.LapDAO;
import org.gproman.db.model.dao.PracticeDAO;
import org.gproman.db.model.dao.PracticeLapDAO;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;

/**
 * This class provides methods to populate DB Table of PRACTICE
 */
public class PracticeDAOImpl implements PracticeDAO {
    
    public static final PracticeDAO INSTANCE = new PracticeDAOImpl();
    
    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO PRACTICE ("
        + "ID"
        + ") VALUES (?)";

    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "ID "
        + "FROM PRACTICE WHERE "
        + "ID = ?";

    /* SQL to update data */
//    private static final String SQL_UPDATE =
//        "UPDATE PRACTICE SET "
//        + "WHERE "
//        + "ID = ?";

    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM PRACTICE WHERE "
        + "ID = ?";
    
    private PracticeLapDAO plDAO = PracticeLapDAOImpl.INSTANCE;
    private LapDAO lapsDAO = LapDAOImpl.INSTANCE;

    private PracticeDAOImpl() {} 
    
    /**
     * Create a new record in Database.
     * @param bean   The Object to be inserted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void create(Practice bean, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            bean.setId( IdGeneratorDAO.INSTANCE.getNextId( conn ) );
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setInt(1, bean.getId());
            ps.executeUpdate();
            for( Lap lap : bean.getLaps() ) {
                lapsDAO.create( lap, conn );
                plDAO.create( bean.getId(), lap.getId(), conn );
            }
        }finally {
            close(ps);
        }
    }

    /**
     * Retrieve a record from Database.
     * @param beanKey   The PK Object to be retrieved.
     * @param conn      JDBC Connection.
     * @exception       SQLException if something is wrong.
     */
    public Practice load(Integer key, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(SQL_SELECT);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            List<Practice> results = getResults(rs, conn);
            if (results.size() > 0)
                return (Practice) results.get(0);
            else
                return null;
        }finally {
            close(rs);
            close(ps);
        }
    }

    /**
     * Update a record in Database.
     * @param bean   The Object to be saved.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void update(Practice bean, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            List<Lap> oldLaps = plDAO.loadLapsForPractice( bean.getId(), conn );
            for( Lap lap : bean.getLaps() ) {
                if( lap.getId() == null ) {
                    lapsDAO.create( lap, conn );
                    plDAO.create( bean.getId(), lap.getId(), conn );
                } else if( oldLaps.remove( lap ) ) {
                    // nothing to do as element was removed from oldLaps list
                }
            }
            for( Lap lap : oldLaps ) {
                plDAO.delete( bean.getId(), lap.getId(), conn );
                lapsDAO.delete( lap.getId(), conn );
            }
//            ps = conn.prepareStatement(SQL_UPDATE);
//            ps.setInt(1, bean.getId());
//            ps.executeUpdate();
        }finally {
            close(ps);
        }
    }

    /**
     * Create a new record in Database.
     * @param bean   The PK Object to be deleted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void delete(Integer key, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            plDAO.deleteLapsForPractice( key, conn );
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, key);
            ps.executeUpdate();
        }finally {
            close(ps);
        }
    }
    
    /**
     * Populate the ResultSet.
     * @param rs     The ResultSet.
     * @return       The Object to retrieve from DB.
     * @exception    SQLException if something is wrong.
     */
    private List<Practice> getResults(ResultSet rs, Connection conn) throws SQLException {
        List<Practice> results = new ArrayList<Practice>();
        while (rs.next()) {
            Practice bean = new Practice();
            bean.setId(rs.getInt("ID"));
            bean.setLaps( plDAO.loadLapsForPractice( bean.getId(), conn ) );
            results.add(bean);
        }
        return results;
    }

    /**
     * Close JDBC Statement.
     * @param stmt  Statement to be closed.
     */
    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }catch(SQLException e){}
        }
    }

    /**
     * Close JDBC ResultSet.
     * @param rs  ResultSet to be closed.
     */
    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }catch(SQLException e){}
        }
    }
}