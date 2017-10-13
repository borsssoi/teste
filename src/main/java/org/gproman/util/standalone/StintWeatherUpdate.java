package org.gproman.util.standalone;

import java.util.List;

import org.gproman.GproManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.everest.NormalizedLap;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Weather;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StintWeatherUpdate {
    
    final static Logger          logger          = LoggerFactory.getLogger( StintWeatherUpdate.class );
    
    private EverestService       everest;
    private UserConfiguration    conf;

    public static void main(String[] args) {
        new StintWeatherUpdate().runUpdate( 17, 42 );
    }
    
    public void runUpdate(int startSeason, int endSeason ) {
        conf = ConfigurationManager.loadConfiguration();
        if( startEverest() > 0 ) {
            for( int season = startSeason; season <= endSeason; season++ ) {
                for( int race = 1; race <= 17; race++ ) {
                    List<NormalizedRace> races = everest.getRaces(season, race);
                    System.out.println("Updating season "+season+" race "+race+" with "+races.size()+" telemetries");
                    for( NormalizedRace nr : races ) {
                        updateWeatherForStints(nr);
                        everest.store(nr);
                    }
                }
            }
            everest.shutdown();
        }
    }
    private void updateWeatherForStints(NormalizedRace race) {
        // update stints with the weather type
        for( NormalizedStint stint :race.getStints() ) {
            // this loop is not efficient as it could be, but we want to be careful as 
            // there might be missing stints in the report
            int count = 0;
            int dry = 0;
            int wet = 0;
            double avgTemp = 0;
            double avgHum = 0;
            for( NormalizedLap l : race.getLaps() ) {
                if( l.getNumber() >= stint.getInitialLap() && l.getNumber() <= stint.getFinalLap() ) {
                    count++;
                    avgTemp += l.getTemperature().intValue();
                    avgHum += l.getHumidity().intValue();
                    if( l.getWeather() != null && Weather.RAIN.equals(l.getWeather()) ) {
                        wet++;
                    } else if( l.getWeather() != null ) {
                        dry++;
                    }
                }
            }
            if( count > 0 ) {
                stint.setAvgTemp(avgTemp/(double)count);
                stint.setAvgHum(avgHum/(double)count);
                if( wet > 0 && dry == 0 ) {
                    stint.setWeatherType(WeatherType.WET);
                } else if( wet == 0 && dry > 0 ) {
                    stint.setWeatherType(WeatherType.DRY);
                } else if( wet <= 2 && dry > 2 ) {
                    stint.setWeatherType(WeatherType.MOSTLY_DRY);
                } else if( wet > 2 && dry <= 2 ) {
                    stint.setWeatherType(WeatherType.MOSTLY_WET);
                } else {
                    stint.setWeatherType(WeatherType.MIXED);
                }
            }
        }
    }
    
    public int startEverest() {
        UserCredentials credentials = CredentialsManager.loadCredentials();
        if ( credentials != null && (
                UserCredentials.UserRole.ADVANCED.equals(credentials.getRole()) ||
                UserCredentials.UserRole.ADMIN.equals(credentials.getRole())) ) {
            char[] usr = new char[]{'e', 'v', 'e', 'r', 'e', 's', 't', 'd', 'b'};
            char[] pwd = new char[]{'t', 'i', 'a', 'n', 'd', '1', '3',' ', 't', 'i', 'a', 'n', 'l'};
            String everestURL = String.format( GproManager.EVEREST_DOMAIN, conf.getEverestDir() );
            logger.info("Everest URL : "+everestURL );
            this.everest = new JDBCEverestService( everestURL,
                                                    new String( usr ),
                                                    new String( pwd ) );
            int previousSchemaVersion = this.everest.start();
            return previousSchemaVersion;
        }
        return 0;
    }

    

}
