package org.gproman.model.race;

import java.util.HashMap;
import java.util.Map;

public enum StartingRisk {

    AVOID_TROUBLE("evitar problemas","avoid trouble"), 
    MAINTAIN_POSITION("mantenha sua posição","maintain his position"), 
    OVERTAKE("ultrapasse onde for possível","overtake where possible"), 
    FORCE_TO_THE_FRONT("forçar seu caminho à frente","force his way to the front");
    
    public final String portuguese;
    public final String english;
    
    private StartingRisk( String portuguese, String english ) {
        this.portuguese = portuguese;
        this.english = english;
    }
    
    @SuppressWarnings("serial")
    private static final Map<String, StartingRisk> TRANSLATIONS = new HashMap<String, StartingRisk>() {{
        put( AVOID_TROUBLE.english, AVOID_TROUBLE );    
        put( MAINTAIN_POSITION.english, MAINTAIN_POSITION );    
        put( OVERTAKE.english, OVERTAKE );    
        put( FORCE_TO_THE_FRONT.english, FORCE_TO_THE_FRONT );    
        put( AVOID_TROUBLE.portuguese, AVOID_TROUBLE );    
        put( MAINTAIN_POSITION.portuguese, MAINTAIN_POSITION );    
        put( OVERTAKE.portuguese, OVERTAKE );    
        put( FORCE_TO_THE_FRONT.portuguese, FORCE_TO_THE_FRONT );    
    }};
    
    public static StartingRisk determineRisk( String desc ) {
        return TRANSLATIONS.get( desc.toLowerCase() );
    }
}
