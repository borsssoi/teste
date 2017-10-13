package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum FuelConsumption {

    VERY_LOW("Muito Baixo", "Very Low"), 
    LOW("Baixo", "Low"), 
    MEDIUM("Médio", "Medium"), 
    HIGH("Alto", "High"), 
    VERY_HIGH("Muito Alto", "Very High");
    
    public final String portuguese;
    public final String english;

    private FuelConsumption( String portuguese, String english ) {
        this.portuguese = portuguese;
        this.english = english;
    }
    
    private static final Map<String, FuelConsumption> values = new HashMap<String, FuelConsumption>();
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
    
    public static FuelConsumption fromString( String fuelConsumption ) {
        return fuelConsumption != null ? values.get( fuelConsumption.toUpperCase() ) : null;
    }
    
}
