/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:50 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.model.orm;

import static org.gproman.db.DBUtil.getIntOrNull;
import static org.gproman.db.DBUtil.setIntParameter;
import static org.gproman.db.DBUtil.setStringParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gproman.db.model.dao.IdGeneratorDAO;
import org.gproman.db.model.dao.PitDAO;
import org.gproman.model.race.Pit;

/**
 * This class provides methods to populate DB Table of PIT
 */
public class PitDAOImpl implements PitDAO {
    
    public static final PitDAO INSTANCE = new PitDAOImpl();
    
    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO PIT ("
        + "ID, RACE_ID, NUMBER, FUEL, LAP, REASON, REFUELED_TO, "
        + "TIME, TYRES"
        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "ID, RACE_ID, NUMBER, FUEL, LAP, REASON, REFUELED_TO, "
        + "TIME, TYRES "
        + "FROM PIT WHERE "
        + "ID = ?";

    private static final String SQL_SELECT_BY_RACE_ID =
            "SELECT "
            + "ID, RACE_ID, NUMBER, FUEL, LAP, REASON, REFUELED_TO, "
            + "TIME, TYRES "
            + "FROM PIT WHERE "
            + "RACE_ID = ?";

    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE PIT SET "
        + "RACE_ID = ?, NUMBER = ?, FUEL = ?, LAP = ?, REASON = ?, REFUELED_TO = ?, TIME = ?,  "
        + "TYRES = ? "
        + "WHERE "
        + "ID = ?";

    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM PIT WHERE "
        + "ID = ?";
    
    private PitDAOImpl() {}

    /**
     * Create a new record in Database.
     * @param bean   The Object to be inserted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void create(Integer raceId, Pit bean, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            bean.setId( IdGeneratorDAO.INSTANCE.getNextId( conn ) );

            ps = conn.prepareStatement(SQL_INSERT);
            ps.setInt(1, bean.getId());
            setIntParameter( ps, 2, raceId);
            setIntParameter( ps, 3, bean.getNumber());
            setIntParameter( ps, 4, bean.getFuel());
            setIntParameter( ps, 5, bean.getLap());
            setStringParameter( ps, 6, bean.getReason());
            setIntParameter( ps, 7, bean.getRefueledTo());
            setIntParameter( ps, 8, bean.getTime());
            setIntParameter( ps, 9, bean.getTyres());
            ps.executeUpdate();
        }finally {
            close(ps);
        }
    }

    /**
     * Retrive a record from Database.
     * @param beanKey   The PK Object to be retrived.
     * @param conn      JDBC Connection.
     * @exception       SQLException if something is wrong.
     */
    public Pit load(Integer key, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(SQL_SELECT);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            List<Pit> results = getResults(rs);
            if (results.size() > 0)
                return (Pit) results.get(0);
            else
                return null;
        }finally {
            close(rs);
            close(ps);
        }
    }
    
    @Override
    public List<Pit> loadPitsForRace(Integer raceId,
                                     Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(SQL_SELECT_BY_RACE_ID);
            ps.setInt(1, raceId);
            rs = ps.executeQuery();
            return getResults(rs);
        } finally {
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
    public void update(Integer raceId, Pit bean, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setInt(1, raceId);
            setIntParameter( ps, 2, bean.getNumber());
            setIntParameter( ps, 3, bean.getFuel());
            setIntParameter( ps, 4, bean.getLap());
            setStringParameter( ps, 5, bean.getReason());
            setIntParameter( ps, 6, bean.getRefueledTo());
            setIntParameter( ps, 7, bean.getTime());
            setIntParameter( ps, 8, bean.getTyres());
            setIntParameter( ps, 9, bean.getId());
            ps.executeUpdate();
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
    private List<Pit> getResults(ResultSet rs) throws SQLException {
        List<Pit> results = new ArrayList<Pit>();
        while (rs.next()) {
            Pit bean = new Pit();
            bean.setId(rs.getInt("ID"));
            bean.setNumber( getIntOrNull( rs, "NUMBER"));
            bean.setFuel( getIntOrNull( rs, "FUEL"));
            bean.setLap( getIntOrNull( rs, "LAP"));
            bean.setReason(rs.getString("REASON"));
            bean.setRefueledTo( getIntOrNull( rs, "REFUELED_TO"));
            bean.setTime( getIntOrNull( rs, "TIME"));
            bean.setTyres( getIntOrNull( rs, "TYRES"));
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