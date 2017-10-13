package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum GripLevel {
    VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH;

    private static final Map<String, GripLevel> values = new HashMap<String, GripLevel>();
    static {
        values.put( "VERY LOW", VERY_LOW );
        values.put( "V, LOW", VERY_LOW );
        values.put( "V. LOW", VERY_LOW );
        values.put( "LOW", LOW );
        values.put( "NORMAL", NORMAL );
        values.put( "HIGH", HIGH );
        values.put( "VERY HIGH", VERY_HIGH );
        values.put( "V, HIGH", VERY_HIGH );
        values.put( "V. HIGH", VERY_HIGH );
    }
    
    public static GripLevel fromString( String grip ) {
        return values.get( grip.toUpperCase() );
    }
    

}
