package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum TyreWear {
    VERY_LOW(1, "Muito Baixo", "Very Low"), 
    LOW(0.909090909, "Baixo", "Low"), 
    MEDIUM(0.826446281, "Médio", "Medium"), 
    HIGH(0.751314801, "Alto", "High"), 
    VERY_HIGH(0.683013455, "Muito Alto", "Very High");
    
    private final double wearFactor;
    public final String portuguese;
    public final String english;

    private TyreWear( double wearFactor, String portuguese, String english ) {
        this.wearFactor = wearFactor;
        this.portuguese = portuguese;
        this.english = english;
    }
    
    public double getWearFactor() {
        return wearFactor;
    }
    
    private static final Map<String, TyreWear> values = new HashMap<String, TyreWear>();
    static {
        values.put( "VERY LOW", VERY_LOW );
        values.put( "V, LOW", VERY_LOW );
        values.put( "V. LOW", VERY_LOW );
        values.put( "LOW", LOW );
        values.put( "MEDIUM", MEDIUM );
        values.put( "HIGH", HIGH );
        values.put( "VERY HIGH", VERY_HIGH );
        values.put( "V, HIGH", VERY_HIGH );
        values.put( "V. HIGH", VERY_HIGH );
        
        values.put( VERY_LOW.portuguese.toUpperCase(), VERY_LOW );
        values.put( LOW.portuguese.toUpperCase(), LOW );
        values.put( MEDIUM.portuguese.toUpperCase(), MEDIUM );
        values.put( HIGH.portuguese.toUpperCase(), HIGH );
        values.put( VERY_HIGH.portuguese.toUpperCase(), VERY_HIGH );
        
        values.put( VERY_LOW.english.toUpperCase(), VERY_LOW );
        values.put( LOW.english.toUpperCase(), LOW );
        values.put( MEDIUM.english.toUpperCase(), MEDIUM );
        values.put( HIGH.english.toUpperCase(), HIGH );
        values.put( VERY_HIGH.english.toUpperCase(), VERY_HIGH );
        
    }
    
    public static TyreWear fromString( String tyreWear ) {
        return tyreWear != null ? values.get( tyreWear.toUpperCase() ) : null;
    }
}
