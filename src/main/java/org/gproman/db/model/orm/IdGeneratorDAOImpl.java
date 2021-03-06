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

import org.gproman.db.model.dao.IdGeneratorDAO;

/**
 * This class provides methods to populate DB Table of APPLICATION_STATUS
 */
public class IdGeneratorDAOImpl implements IdGeneratorDAO {
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT SEQ_ID.NEXTVAL FROM DUAL";

    @Override
    public Integer getNextId(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(SQL_SELECT);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt( 1 );
        } finally {
            close(rs);
            close(ps);
        }
    }

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