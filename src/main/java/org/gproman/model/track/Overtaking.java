package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum Overtaking {
    
    VERY_EASY("Muito Fácil", "Very Easy"), 
    EASY("Fácil", "Easy"), 
    NORMAL("Normal", "Normal"), 
    HARD("Difícil", "Hard"), 
    VERY_HARD("Muito Difícil", "Very Hard");
    
    public final String portuguese;
    public final String english;

    private Overtaking( String portuguese, String english ) {
        this.portuguese = portuguese;
        this.english = english;
    }
    
    private static final Map<String, Overtaking> values = new HashMap<String, Overtaking>();
    static {
        values.put( "VERY EASY", VERY_EASY );
        values.put( "V, EASY", VERY_EASY );
        values.put( "V. EASY", VERY_EASY );
        values.put( "EASY", EASY );
        values.put( "NORMAL", NORMAL );
        values.put( "HARD", HARD );
        values.put( "VERY HARD", VERY_HARD );
        values.put( "V, HARD", VERY_HARD );
        values.put( "V. HARD", VERY_HARD );
        
        values.put( VERY_EASY.portuguese.toUpperCase(), VERY_EASY );
        values.put( EASY.portuguese.toUpperCase(), EASY );
        values.put( NORMAL.portuguese.toUpperCase(), NORMAL );
        values.put( HARD.portuguese.toUpperCase(), HARD );
        values.put( VERY_HARD.portuguese.toUpperCase(), VERY_HARD );
        
        values.put( VERY_EASY.english.toUpperCase(), VERY_EASY );
        values.put( EASY.english.toUpperCase(), EASY );
        values.put( NORMAL.english.toUpperCase(), NORMAL );
        values.put( HARD.english.toUpperCase(), HARD );
        values.put( VERY_HARD.english.toUpperCase(), VERY_HARD );
    }
    
    public static Overtaking fromString( String overtaking ) {
        return values.get( overtaking.toUpperCase() );
    }
    

}
