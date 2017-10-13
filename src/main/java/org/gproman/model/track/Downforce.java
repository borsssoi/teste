package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum Downforce {
    
    VERY_LOW("Very Low","Muito Baixo"), 
    LOW("Low","Baixo"),
    MEDIUM("Medium","Médio"),
    HIGH("High","Alto"),
    VERY_HIGH("Very High","Muito Alto");
    
    public final String english;
    public final String portuguese;
    private Downforce(String english, String portuguese) {
        this.english = english;
        this.portuguese = portuguese;
    }
    
    private static final Map<String, Downforce> values = new HashMap<String, Downforce>();
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
        for( Downforce df : Downforce.values() ) {
            values.put(df.english.toUpperCase(), df);
            values.put(df.portuguese.toUpperCase(), df);
            values.put(df.toString().toUpperCase(), df);
        }
    }
    
    public static Downforce fromString( String downforce ) {
        return values.get( downforce.toUpperCase() );
    }
    
}
