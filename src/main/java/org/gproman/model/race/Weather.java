package org.gproman.model.race;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.gproman.ui.UIUtils;

public enum Weather {
    RAIN("rain", "chuvoso", "Rain", "WR"), 
    VERY_CLOUDY("very cloudy", "muito nublado", "VeryCloudy", "WVC"), 
    CLOUDY("cloudy", "nublado", "Cloudy", "WC"), 
    PARTIALLY_CLOUDY("partially cloudy", "parcialmente nublado", "PartiallyCloudy", "WPC"), 
    SUNNY("sunny", "ensolarado", "Sunny", "WS");
             
    private ImageIcon icon;
    public String portuguese;
    public String english;
    public String gobr;
    public String bbCode;
    
    private Weather( String english, String portuguese, String gobr, String bbCode ) {
        this.english = english;
        this.portuguese = portuguese;
        this.gobr = gobr;
        this.bbCode = bbCode;
    }

    public ImageIcon getIcon() {
        if( icon == null ) {
            icon = UIUtils.createImageIcon( "/icons/"+this.toString().toLowerCase()+".gif" ); 
        }
        return icon;
    }
    
    public String getToolTip() {
        switch( this ) {
            case RAIN: return "Chuva";
            case VERY_CLOUDY: return "Muito Nublado";
            case CLOUDY: return "Nublado";
            case PARTIALLY_CLOUDY: return "Parcialmente Nublado";
            case SUNNY: return "Ensolarado";
        }
        return "";
    }
    
    
    @SuppressWarnings("serial")
    private static final Map<String, Weather> TRANSLATIONS = new HashMap<String, Weather>() {{
        put( RAIN.english.toLowerCase(), RAIN );    
        put( VERY_CLOUDY.english.toLowerCase(), VERY_CLOUDY );    
        put( CLOUDY.english.toLowerCase(), CLOUDY );    
        put( PARTIALLY_CLOUDY.english.toLowerCase(), PARTIALLY_CLOUDY );    
        put( SUNNY.english.toLowerCase(), SUNNY );    
        put( RAIN.portuguese.toLowerCase(), RAIN );    
        put( VERY_CLOUDY.portuguese.toLowerCase(), VERY_CLOUDY );    
        put( CLOUDY.portuguese.toLowerCase(), CLOUDY );    
        put( PARTIALLY_CLOUDY.portuguese.toLowerCase(), PARTIALLY_CLOUDY );    
        put( SUNNY.portuguese.toLowerCase(), SUNNY );    
        put( RAIN.gobr.toLowerCase(), RAIN );    
        put( VERY_CLOUDY.gobr.toLowerCase(), VERY_CLOUDY );    
        put( CLOUDY.gobr.toLowerCase(), CLOUDY );    
        put( PARTIALLY_CLOUDY.gobr.toLowerCase(), PARTIALLY_CLOUDY );    
        put( SUNNY.gobr.toLowerCase(), SUNNY );    
        put( RAIN.toString().toLowerCase(), RAIN );    
        put( VERY_CLOUDY.toString().toLowerCase(), VERY_CLOUDY );    
        put( CLOUDY.toString().toLowerCase(), CLOUDY );    
        put( PARTIALLY_CLOUDY.toString().toLowerCase(), PARTIALLY_CLOUDY );    
        put( SUNNY.toString().toLowerCase(), SUNNY );    
    }};
    
    public static Weather determineWeather( String desc ) {
        return TRANSLATIONS.get( desc.toLowerCase() );
    }
    

}
