package org.gproman.model.track;

import java.util.HashMap;
import java.util.Map;

public enum SuspensionRigidity {
    
    VERY_SOFT("Very Soft","Muito Macia"), 
    SOFT("Soft","Macia"),
    MEDIUM("Medium","Média"),
    HARD("Hard","Dura"),
    VERY_HARD("Very Hard","Muito Dura");
    
    public final String english;
    public final String portuguese;
    private SuspensionRigidity(String english, String portuguese) {
        this.english = english;
        this.portuguese = portuguese;
    }

    private static final Map<String, SuspensionRigidity> values = new HashMap<String, SuspensionRigidity>();
    static {
        values.put( "VERY SOFT", VERY_SOFT );
        values.put( "V, SOFT", VERY_SOFT );
        values.put( "V. SOFT", VERY_SOFT );
        values.put( "SOFT", SOFT );
        values.put( "MEDIUM", MEDIUM );
        values.put( "HARD", HARD );
        values.put( "VERY HARD", VERY_HARD );
        values.put( "V, HARD", VERY_HARD );
        values.put( "V. HARD", VERY_HARD );
        for( SuspensionRigidity sr : SuspensionRigidity.values() ) {
            values.put(sr.english.toUpperCase(), sr);
            values.put(sr.portuguese.toUpperCase(), sr);
            values.put(sr.toString().toUpperCase(), sr);
        }
    }
    
    public static SuspensionRigidity fromString( String suspension ) {
        return values.get( suspension.toUpperCase() );
    }

}
