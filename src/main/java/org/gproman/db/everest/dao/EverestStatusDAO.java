/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:50 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.everest.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.EverestStatus;

/**
 * This interface provides methods to populate DB Table of APPLICATION_STATUS
 */
public interface EverestStatusDAO {

    /**
     * Retrive a record from Database.
     * @param beanKey   The PK Object to be retrived.
     * @param conn      JDBC Connection.
     * @exception       SQLException if something is wrong.
     */
    public EverestStatus load(Integer key, Connection conn) throws SQLException;

    /**
     * Update a record in Database.
     * @param bean   The Object to be saved.
     * @param conn   JDBC Connection.
     * @return the row count for the updated rows, i.e., 1 if successful
     * @exception    SQLException if something is wrong.
     */
    public int update(EverestStatus bean, Connection conn) throws SQLException;
    
    public EverestMetrics loadMetrics( Connection conn ) throws SQLException;

}