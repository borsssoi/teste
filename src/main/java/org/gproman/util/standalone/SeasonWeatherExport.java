package org.gproman.util.standalone;

import org.gproman.GproManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.everest.NormalizedLap;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Weather;
import org.gproman.util.CSVHelper;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class SeasonWeatherExport {

    final static         Logger logger      = LoggerFactory.getLogger( SeasonWeatherExport.class );
    private static final String OUTPUT_FILE = "research/weather_history.csv";

    private EverestService    everest;
    private UserConfiguration conf;
    private PrintWriter       out;

    public static void main(String[] args) {
        new SeasonWeatherExport().runUpdate( 17, 49 );
    }

    public void runUpdate(int startSeason, int endSeason) {
        conf = ConfigurationManager.loadConfiguration();
        if ( startEverest() > 0 ) {
            try {
                out = new PrintWriter( OUTPUT_FILE );
                CSVHelper csv = new CSVHelper();
                printHeader( csv );
                for ( int season = startSeason; season <= endSeason; season++ ) {
                    for ( int race = 1; race <= 17; race++ ) {
                        List<NormalizedRace> races = everest.getRaces( season, race );
                        System.out.print( "Export season " + season + " race " + race + " from " + races.size() + " telemetries... " );
                        boolean success = false;
                        for ( NormalizedRace nr : races ) {
                            if ( nr.getRaceStatus().equals( NormalizedRace.RaceStatus.COMPLETED ) && nr.getLaps().size() >= nr.getTrack().getLaps() ) {
                                if( exportRace( csv, nr ) ) {
                                    System.out.println("success.");
                                    success = true;
                                    break;
                                }
                            }
                        }
                        if( !success ) {
                            System.out.println("FAILED.");
                        }
                    }
                }
            } catch ( FileNotFoundException e ) {
                logger.error( "Error creating output data file: " + OUTPUT_FILE, e );
            } finally {
                if ( out != null ) {
                    try {
                        out.close();
                    } catch ( Exception e ) {
                    }
                }
            }
            everest.shutdown();
        }
    }

    private void printHeader(CSVHelper csv) {
        String[] headers = new String[]{
                "season",
                "race",
                "type",
                "dry",
                "wet",
                "temp",
                "hum"};
        out.print( csv.newLine().printHeader( headers ).endLine() );
    }

    private boolean exportRace(CSVHelper csv, NormalizedRace race) {
        int count = 0;
        int dry = 0;
        int wet = 0;
        double avgTemp = 0;
        double avgHum = 0;
        for ( NormalizedLap l : race.getLaps() ) {
            if ( l.getNumber() >= 1 ) {
                count++;
                avgTemp += l.getTemperature().intValue();
                avgHum += l.getHumidity().intValue();
                if ( l.getWeather() != null && Weather.RAIN.equals( l.getWeather() ) ) {
                    wet++;
                } else if ( l.getWeather() != null ) {
                    dry++;
                }
            }
        }
        if ( count > 0 ) {
            csv.newLine()
                    .fi( race.getSeasonNumber(), false )
                    .fi( race.getRaceNumber() );
            if ( wet > 0 && dry == 0 ) {
                csv.fs( WeatherType.WET.english );
            } else if ( wet == 0 && dry > 0 ) {
                csv.fs( WeatherType.DRY.english );
            } else {
                csv.fs( WeatherType.MIXED.english );
            }
            csv.fi( dry )
                    .fi( wet )
                    .fd( avgTemp / (double) count )
                    .fd( avgHum / (double) count );
            out.print(csv.endLine());
            return true;
        }
        return false;
    }

    public int startEverest() {
        UserCredentials credentials = CredentialsManager.loadCredentials();
        if ( credentials != null && (
                UserCredentials.UserRole.ADVANCED.equals( credentials.getRole() ) ||
                UserCredentials.UserRole.ADMIN.equals( credentials.getRole() )) ) {
            char[] usr = new char[]{'e', 'v', 'e', 'r', 'e', 's', 't', 'd', 'b'};
            char[] pwd = new char[]{'t', 'i', 'a', 'n', 'd', '1', '3', ' ', 't', 'i', 'a', 'n', 'l'};
            String everestURL = String.format( GproManager.EVEREST_DOMAIN, conf.getEverestDir() );
            logger.info( "Everest URL : " + everestURL );
            this.everest = new JDBCEverestService(
                    everestURL,
                    new String( usr ),
                    new String( pwd ) );
            int previousSchemaVersion = this.everest.start();
            return previousSchemaVersion;
        }
        return 0;
    }

}
