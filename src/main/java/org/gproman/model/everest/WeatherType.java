package org.gproman.model.everest;

import java.util.HashMap;
import java.util.Map;

public enum WeatherType {
    DRY("Dry","Seco"), 
    WET("Wet","Molhado"), 
    MIXED("Mixed","Misto"), 
    MOSTLY_DRY("Mostly Dry","Praticamente seco"), 
    MOSTLY_WET("Mostly Wet","Praticamente molhado"), 
    UNKNOWN("Unknown","Desconhecido");
    
    public String portuguese;
    public String english;
    
    private WeatherType( String english, String portuguese ) {
        this.english = english;
        this.portuguese = portuguese;
    }

    @SuppressWarnings("serial")
    private static final Map<String, WeatherType> TRANSLATIONS = new HashMap<String, WeatherType>() {{
        put( DRY.toString().toLowerCase(), DRY );    
        put( WET.toString().toLowerCase(), WET );    
        put( MIXED.toString().toLowerCase(), MIXED );    
        put( MOSTLY_DRY.toString().toLowerCase(), MOSTLY_DRY );    
        put( MOSTLY_WET.toString().toLowerCase(), MOSTLY_WET );    
        put( UNKNOWN.toString().toLowerCase(), UNKNOWN );    
        put( DRY.english.toLowerCase(), DRY );    
        put( WET.english.toLowerCase(), WET );    
        put( MIXED.english.toLowerCase(), MIXED );    
        put( MOSTLY_DRY.english.toLowerCase(), MOSTLY_DRY );    
        put( MOSTLY_WET.english.toLowerCase(), MOSTLY_WET );    
        put( UNKNOWN.english.toLowerCase(), UNKNOWN );    
        put( DRY.portuguese.toLowerCase(), DRY );    
        put( WET.portuguese.toLowerCase(), WET );    
        put( MIXED.portuguese.toLowerCase(), MIXED );    
        put( MOSTLY_DRY.portuguese.toLowerCase(), MOSTLY_DRY );    
        put( MOSTLY_WET.portuguese.toLowerCase(), MOSTLY_WET );    
        put( UNKNOWN.portuguese.toLowerCase(), UNKNOWN );    
    }};
    
    public static WeatherType determineWeather( String desc ) {
        if( desc != null ) {
            return TRANSLATIONS.get( desc.toLowerCase() );
        }
        return null;
    }
}