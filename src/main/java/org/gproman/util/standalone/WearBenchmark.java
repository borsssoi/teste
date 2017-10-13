package org.gproman.util.standalone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gproman.GproManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WearBenchmark {

    private static final String OUTPUT_FILE = "wear_benchmark.csv";
    private static final String DELIMITER   = ",";

    final static Logger         logger      = LoggerFactory.getLogger(WearBenchmark.class);

    private EverestService      everest;
    private UserConfiguration   conf;
    private PrintWriter         out;
    private StringBuilder       line;
    
    public static void main(String[] args) {
        logger.info("Starting export routine");
        new WearBenchmark().run();
        logger.info("Terminating export routine");
    }

    public void run() {
        conf = ConfigurationManager.loadConfiguration();
        if (startEverest() > 0) {
            try {
                Map<Integer, TrackCoef> tracks = loadTracks();
                out = new PrintWriter(OUTPUT_FILE);
                printHeader();
                for (int season = 17; season <= 40; season++) {
                    for (int race = 1; race <= 17; race++) {
                        logger.info("Benchmarking " + season + " race " + race);
                        List<NormalizedRace> races = everest.getRaces(season, race);
                        for (NormalizedRace r : races) {
                            TrackCoef kpart = tracks.get( r.getTrack().getId() );
                            if ( kpart != null &&
                                    r.getRaceStatus().equals(RaceStatus.COMPLETED) && 
                                    r.getDriverStart() != null && 
                                    r.getCarStart() != null && 
                                    r.getCarFinish() != null && 
                                    r.getCarWear() != null ) {
                                try {
                                    line = new StringBuilder();
                                    line.append(season);
                                    fi(race);
                                    fi(r.getTrack().getId());
                                    fs(r.getTrack().getName());
                                    CarPart[] actualWear = r.getCarWear().getParts();
                                    CarPart[] finishWear = r.getCarFinish().getParts();
                                    for( int i = 0; i < actualWear.length; i++ ) {
                                        if( finishWear[i].getWear() >= 99 || finishWear[i].getWear() <= 0 || actualWear[i].getWear() <= 0 ) {
                                            // can't consider telemetry if finish wear >= 99
                                            line = null;
                                            break;
                                        }
                                        double calcWear = calculateWear(kpart.kpart[i], kpart.kdriver, r.getRiskClear(), finishWear[i].getLevel(), r.getDriverStart() );
                                        double delta = actualWear[i].getWear()-calcWear;
                                        if( Math.abs( delta ) >= 8 ) {
                                            // this would be data error
                                            line = null;
                                            break;
                                        }
                                        fd(actualWear[i].getWear());
                                        fd(calcWear);
                                        fd(delta);
                                    }
                                    if( line != null ) {
                                        fs(r.getUrl());
                                        out.println( line );
                                    }
                                } catch (Exception e) {
                                    logger.error("Error processing race " + r.getHeader(), e);
                                }
                            }
                        }
                    }
                }
                everest.shutdown();
            } catch (FileNotFoundException e) {
                logger.error("Error creating output data file: " + OUTPUT_FILE);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private Map<Integer, TrackCoef> loadTracks() {
        Map<Integer, TrackCoef> tracks = new HashMap<Integer, WearBenchmark.TrackCoef>();
        try {
            BufferedReader reader = new BufferedReader( new FileReader("research/wear_coef.csv") );
            String line = null;
            while( ( line = reader.readLine() ) != null ) {
                if( !line.isEmpty() ) {
                    String[] tokens = line.split(",");
                    if( tokens.length == 14 && !tokens[0].equals("ID") && !tokens[13].isEmpty()) {
                        TrackCoef coef = new TrackCoef();
                        coef.id = new Integer(tokens[0].trim());
                        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
                            coef.kpart[i] = Double.parseDouble(tokens[i+2].trim());
                        }
                        coef.kdriver = Double.parseDouble( tokens[13].trim() );
                        tracks.put(coef.id, coef);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tracks;
    }

    private static final double[] K_LEVEL = new double[] { 0.017, 0.0083, 0.0055, 0.0042, 0.0034, 0.0028, 0.0033, 0.0073, 0.0175 };
    
    private double calculateWear( double kpart, double kdriver, double ct, int level, Driver driver ) {
        return Math.round( kpart * (ct * K_LEVEL[level-1] + 1) - 
                           kdriver * (driver.getAttributes().getConcentration()+driver.getAttributes().getTalent()+driver.getAttributes().getExperience())); 
    }
    
    public static class TrackCoef {
        public Integer id;
        public double[] kpart = new double[11];
        public double kdriver;
    }

    private void printHeader() {
        line = new StringBuilder();
        line.append("Season");
        fs("Race");
        fs("TrackId");
        fs("TrackName");
        for( int i = 0; i < Car.PARTS.length; i++ ) {
            fs(Car.PARTS[i]+"_Actual");
            fs(Car.PARTS[i]+"_Calc");
            fs(Car.PARTS[i]+"_Delta");
        }
        fs("URL");
        out.println(line);
    }

    private void fi(int field) {
        delim();
        line.append(field);
    }

    private void fd(double field) {
        delim();
        line.append(String.format("%5.3f", field));
    }

    private void fs(String field) {
        delim();
        line.append("\"" + field + "\"");
    }

    private void delim() {
        line.append(DELIMITER);
    }

    public int startEverest() {
        UserCredentials credentials = CredentialsManager.loadCredentials();
        if (credentials != null && (
                UserCredentials.UserRole.ADVANCED.equals(credentials.getRole()) ||
                UserCredentials.UserRole.ADMIN.equals(credentials.getRole()))) {
            char[] usr = new char[]{'e', 'v', 'e', 'r', 'e', 's', 't', 'd', 'b'};
            char[] pwd = new char[]{'t', 'i', 'a', 'n', 'd', '1', '3', ' ', 't', 'i', 'a', 'n', 'l'};
            String everestURL = String.format(GproManager.EVEREST_DOMAIN, conf.getEverestDir());
            logger.info("Everest URL : " + everestURL);
            this.everest = new JDBCEverestService(everestURL,
                    new String(usr),
                    new String(pwd));
            int previousSchemaVersion = this.everest.start();
            return previousSchemaVersion;
        }
        return 0;
    }

}
