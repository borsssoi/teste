/*
 * This java source file is generated by DAO4J v1.18
 * Generated on Fri Jan 04 19:21:51 EST 2013
 * For more information, please contact b-i-d@163.com
 * Please check http://members.lycos.co.uk/dao4j/ for the latest version.
 */

package org.gproman.db.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.gproman.model.track.Track;

/**
 * This interface provides methods to populate DB Table of TRACK
 */
public interface TrackDAO {
    /**
     * Create a new record in Database.
     * @param bean   The Object to be inserted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void create(Track bean, Connection conn) throws SQLException;

    /**
     * Retrive a record from Database.
     * @param beanKey   The PK Object to be retrived.
     * @param conn      JDBC Connection.
     * @exception       SQLException if something is wrong.
     */
    public Track load(Integer key, Connection conn) throws SQLException;

    /**
     * Update a record in Database.
     * @param bean   The Object to be saved.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public int update(Track bean, Connection conn) throws SQLException;

    /**
     * Create a new record in Database.
     * @param bean   The PK Object to be deleted.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void delete(Integer key, Connection conn) throws SQLException;
    
    /**
     * Update a record in Database.
     * @param track   The Object to be saved.
     * @param conn   JDBC Connection.
     * @exception    SQLException if something is wrong.
     */
    public void createOrUpdate(Track track, Connection conn) throws SQLException;

    public Track loadByName(String name, Connection conn) throws SQLException;
    
    public List<Track> loadAllTracks(Connection conn) throws SQLException;

}