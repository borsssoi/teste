package org.gproman.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.gproman.model.PersistentEntity;

public class DBUtil {
    public static void setParameter(PreparedStatement ps,
                                    int index,
                                    PersistentEntity entity) throws SQLException {
        if ( entity != null ) {
            ps.setInt( index, entity.getId() );
        } else {
            ps.setNull( index, Types.INTEGER );
        }
    }

    public static void setIntParameter(PreparedStatement ps,
                                       int index,
                                       Integer val) throws SQLException {
        if ( val != null ) {
            ps.setInt( index, val );
        } else {
            ps.setNull( index, Types.INTEGER );
        }
    }
    
    public static void setDoubleParameter(PreparedStatement ps,
                                          int index,
                                          Double val) throws SQLException {
        if ( val != null ) {
            ps.setDouble( index, val );
        } else {
            ps.setNull( index, Types.DOUBLE );
        }
    }
    
    public static void setStringParameter(PreparedStatement ps,
                                          int index,
                                          Object val) throws SQLException {
        if ( val != null ) {
            ps.setString( index, val.toString() );
        } else {
            ps.setNull( index, Types.VARCHAR );
        }
    }
    
    public static Integer getIntOrNull( ResultSet rs, String fieldName ) throws SQLException {
        Integer val = rs.getInt( fieldName );
        return rs.wasNull() ? null : val;
    }

    public static Double getDoubleOrNull(ResultSet rs,
                                         String fieldName ) throws SQLException {
        Double val = rs.getDouble( fieldName );
        return rs.wasNull() ? null : val;
    }
    
}
