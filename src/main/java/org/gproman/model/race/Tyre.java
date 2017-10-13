package org.gproman.model.race;

import java.util.HashMap;
import java.util.Map;

public enum Tyre {
    
    XSOFT("extra soft","super-macio", "ExtraSoft", "XS", 0, 0.12), 
    SOFT("soft","macio", "Soft", "S", 1, 0.21), 
    MEDIUM("medium","médio", "Medium", "M", 2, 0.3), 
    HARD("hard","duro", "Hard", "H", 3, 0.4), 
    RAIN("rain","chuva", "Rain", "R", 4, 0.42);

    public final String english;
    public final String portuguese;
    public final String symbol;
    public final String gobr;
    public final int diffFactor;
    public final double riskFactor;
    
    private Tyre( String english, String portuguese, String gobr, String symbol, int index, double riskFactor ) {
        this.english = english;
        this.portuguese = portuguese;
        this.gobr = gobr;
        this.symbol = symbol;
        this.diffFactor = index;
        this.riskFactor = riskFactor;
    }
    
    private static Map<String, Tyre> map = new HashMap<String, Tyre>();
    static {
        map.put( XSOFT.toString().toLowerCase(), XSOFT );
        map.put( SOFT.toString().toLowerCase(), SOFT );
        map.put( MEDIUM.toString().toLowerCase(), MEDIUM );
        map.put( HARD.toString().toLowerCase(), HARD );
        map.put( RAIN.toString().toLowerCase(), RAIN );
        map.put( XSOFT.english.toLowerCase(), XSOFT );
        map.put( SOFT.english.toLowerCase(), SOFT );
        map.put( MEDIUM.english.toLowerCase(), MEDIUM );
        map.put( HARD.english.toLowerCase(), HARD );
        map.put( RAIN.english.toLowerCase(), RAIN );
        map.put( XSOFT.portuguese.toLowerCase(), XSOFT );
        map.put( SOFT.portuguese.toLowerCase(), SOFT );
        map.put( MEDIUM.portuguese.toLowerCase(), MEDIUM );
        map.put( HARD.portuguese.toLowerCase(), HARD );
        map.put( RAIN.portuguese.toLowerCase(), RAIN );
        map.put( XSOFT.gobr.toLowerCase(), XSOFT );
        map.put( SOFT.gobr.toLowerCase(), SOFT );
        map.put( MEDIUM.gobr.toLowerCase(), MEDIUM );
        map.put( HARD.gobr.toLowerCase(), HARD );
        map.put( RAIN.gobr.toLowerCase(), RAIN );
    }
    
    public static Tyre determineTyre(String string) {
        return string != null && !string.isEmpty() ? map.get( string.toLowerCase() ) : null;
    }
    
}
