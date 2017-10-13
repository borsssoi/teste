package org.gproman.util.standalone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gproman.GproManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Tyre;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyreWearBenchmark {

    private static final String OUTPUT_FILE = "research/tyre_wear_benchmark.csv";
    private static final String DELIMITER   = ",";

    final static Logger         logger      = LoggerFactory.getLogger(TyreWearBenchmark.class);

    private EverestService      everest;
    private UserConfiguration   conf;
    private PrintWriter         out;
    private StringBuilder       line;

    public static void main(String[] args) {
        logger.info("Starting export routine");
        new TyreWearBenchmark().run();
        logger.info("Terminating export routine");
    }

    public void run() {
        conf = ConfigurationManager.loadConfiguration();
        if (startEverest() > 0) {
            try {
                Map<Integer, TrackCoef> tracks = loadTracks();
                out = new PrintWriter(OUTPUT_FILE);
                printHeader();
                for (int season = 17; season <= 50; season++) {
                    for (int race = 1; race <= 17; race++) {
                        logger.info("Benchmarking " + season + " race " + race);
                        List<NormalizedRace> races = everest.getRaces(season, race);
//                        if( races.isEmpty() || races.get(0).getTrack().getId() != 6 ) {
//                            continue;
//                        }
                        for (NormalizedRace r : races) {
//                            TrackCoef tcoef = tracks.get( r.getTrack().getId() );
//                            if( tcoef == null ) {
//                                logger.error("No track coefficients found for track: "+r.getTrack().getName());
//                                break;
//                            }
                            if (r.getRaceStatus().equals(RaceStatus.COMPLETED) && 
                                r.getDriverStart() != null &&
                                r.getDriverStart().getAttributes().getOverall() > 0 &&
                                r.getCarStart() != null && 
                                r.getCarFinish() != null && 
                                r.getCarWear() != null ) {
                                try {
                                    for( NormalizedStint s : r.getStints() ) {
                                        if( s.getLaps() > 10 &&
                                            ( (s.getWeatherType().equals(WeatherType.DRY) && !s.getTyre().equals(Tyre.RAIN) ) || 
                                            (s.getWeatherType().equals(WeatherType.WET) && s.getTyre().equals(Tyre.RAIN) ) ) ) {
                                            line = new StringBuilder();
                                            line.append(season);
                                            fi(race);
                                            fs(r.getTrack().getName());
                                            fi(r.getTrack().getLaps());
                                            fd(r.getTrack().getDistance());
                                            fi(s.getNumber());
                                            fi(s.getLaps());
                                            fi(r.getRiskClear());
                                            fs(s.getTyre().symbol);
                                            fi(everest.getTyreSupplier(season, r.getSupplier().name ).getDurability() );
                                            fi(r.getDriverStart().getAttributes().getTalent());
                                            fi(r.getDriverStart().getAttributes().getTechInsight());
                                            fi(r.getDriverStart().getAttributes().getConcentration());
                                            fi(r.getDriverStart().getAttributes().getAggressiveness());
                                            fi(r.getDriverStart().getAttributes().getExperience());
                                            fi(r.getDriverStart().getAttributes().getWeight());
                                            fi(r.getCarStart().getChassis().getLevel());
                                            fd(r.getCarStart().getChassis().getWear());
                                            fi(r.getCarStart().getGearbox().getLevel());
                                            fd(r.getCarStart().getGearbox().getWear());
                                            fi(r.getCarStart().getEngine().getLevel());
                                            fd(r.getCarStart().getEngine().getWear());
                                            fi(r.getCarStart().getSuspension().getLevel());
                                            fd(r.getCarStart().getSuspension().getWear());
                                            fi(r.getCarStart().getElectronics().getLevel());
                                            fd(r.getCarStart().getElectronics().getWear());
                                            fi(r.getCarStart().getFrontWing().getLevel());
                                            fd(r.getCarStart().getFrontWing().getWear());
                                            fi(r.getCarStart().getRearWing().getLevel());
                                            fd(r.getCarStart().getRearWing().getWear());
                                            
                                            fd(s.getAvgTemp());
                                            fd(s.getAvgHum());
                                            
                                            double actual = 1-(s.getTyreLeft()/100.0);
                                            fd(actual);
//                                            double fj = calcWearJ(r, s, tcoef);
//                                            fd(fj);
//                                            double dfj = actual - fj;
//                                            fd(dfj);
                                            fs(r.getUrl());
                                            out.println( line );
                                            //break; // one stint per telemetry
                                        }
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

    private double calcWearJ(NormalizedRace r, NormalizedStint s, TrackCoef tcoef) {
        double k_tyre = tcoef.ktyre;
        double k_risk = tcoef.krisk;
        
        double k_compound = getCompoundCoef(s.getTyre());
        double k_durability = 0.968;
        double tyre_durab = everest.getTyreSupplier(39, r.getSupplier().name ).getDurability();
        double k_aggr = 1.00015;
        double k_exp  = 0.99985;
        double k_weight = 1.0003;
        double k_eng = 0.997;
        double k_sus = 0.998;
        double k_ele = 0.998;
        
        double total_wear = (k_tyre * (1+(r.getRiskClear().doubleValue()/100.0)*k_risk) / k_compound *
                Math.pow(k_durability, tyre_durab) * Math.pow(k_aggr, r.getDriverStart().getAttributes().getAggressiveness()) *
                Math.pow(k_exp, r.getDriverStart().getAttributes().getExperience()) * 
                Math.pow(k_weight, r.getDriverStart().getAttributes().getWeight()) *
                Math.pow(k_eng, r.getCarStart().getEngine().getLevel() ) *
                Math.pow(k_sus, r.getCarStart().getSuspension().getLevel() ) *
                Math.pow(k_ele, r.getCarStart().getElectronics().getLevel() ) *
                (1.0 + s.getAvgTemp() / 75.0) - 0.0016 ) * r.getTrack().getDistance();
                   
        double wear = s.getLaps().doubleValue() * total_wear / (double) r.getTrack().getLaps();
        return wear;
    }
    
//    private double calcWearB(NormalizedRace r, NormalizedStint s) {
        // austin
//        double wtrack = 0.719020817835503;
        // montreal
//        double wtrack = 0.927232122529964;
//
//        double ct = r.getRiskClear().doubleValue();
//        double s1_100CT = getCompoundCoef100CT( s.getTyre() );
//        double s1_base1 = getCoefBySupplier( r.getSupplier(), s.getTyre() );
//        double temp_mod = Math.pow(100.0/101.5, s.getAvgTemp());
//        double humi_mod = Math.pow(1.002, s.getAvgHum());
//        double wear_mod = getWearMod( r.getTrack().getTyreWear() );
//        
//        double ct1 = wtrack * temp_mod * humi_mod * s1_base1;
//        double ct2 = wear_mod * temp_mod * humi_mod * s1_base1;
//        
//        double max1 = ct1 - (ct1 * s1_100CT * ct / 100.0);
//        double max2 = ct2 - (ct2 * s1_100CT * ct / 100.0);
//        
//        double durability = Math.floor( (max1+max2)/2.0 );
//        
//        // returns the percentage of the tyre that was used in the stint
//        return s.getTyreUsed().doubleValue() / durability;
//    }
//    
//
//    private double getWearMod(TyreWear tyreWear) {
//        switch( tyreWear ) {
//            case VERY_LOW: return 1.0;
//            case LOW: return 10.0/11.0;
//            case MEDIUM: return Math.pow(10.0/11.0, 2);
//            case HIGH: return Math.pow(10.0/11.0, 3);
//            case VERY_HIGH: return Math.pow(10.0/11.0, 4);
//        }
//        return 0;
//    }
//
//    private double getCoefBySupplier(TyreSupplier supplier, Tyre tyre) {
//        switch( supplier ) {
//            case AVONN:
//                switch( tyre ) {
//                    case XSOFT : return 197.064;
//                    case SOFT : return 272.35;
//                    case MEDIUM : return 370.83;
//                    case HARD : return 505;
//                    case RAIN : return 548.82;
//                }
//            case BADYEAR:
//                switch( tyre ) {
//                    case XSOFT : return 193.20;
//                    case SOFT : return 256.935;
//                    case MEDIUM : return 341.775;
//                    case HARD : return 454.545;
//                    case RAIN : return 522.69;
//                }
//            case BRIDGEROCK:
//                switch( tyre ) {
//                    case XSOFT : return 184;
//                    case SOFT : return 244.70;
//                    case MEDIUM : return 325.50;
//                    case HARD : return 432.90;
//                    case RAIN : return 497.8;
//                }
//            case CONTIMENTAL:
//                switch( tyre ) {
//                    case XSOFT : return 197.064;
//                    case SOFT : return 272.35;
//                    case MEDIUM : return 370.83;
//                    case HARD : return 505;
//                    case RAIN : return 548.82;
//                }
//            case DUNNOLOP:
//                switch( tyre ) {
//                    case XSOFT : return 167.235;
//                    case SOFT : return 222.42;
//                    case MEDIUM : return 295.885;
//                    case HARD : return 393.38;
//                    case RAIN : return 452.42;
//                }
//            case HANCOCK:
//                switch( tyre ) {
//                    case XSOFT : return 143.70;
//                    case SOFT : return 191.10;
//                    case MEDIUM : return 254.20;
//                    case HARD : return 338;
//                    case RAIN : return 388.7;
//                }
//            case MICHELINI:
//                switch( tyre ) {
//                    case XSOFT : return 175.5816;
//                    case SOFT : return 233.42;
//                    case MEDIUM : return 310.51;
//                    case HARD : return 412.88;
//                    case RAIN : return 474.82;
//                }
//            case PIPIRELLI:
//                switch( tyre ) {
//                    case XSOFT : return 143.70;
//                    case SOFT : return 191.10;
//                    case MEDIUM : return 254.20;
//                    case HARD : return 338;
//                    case RAIN : return 388.7;
//                }
//            case YOKOMAMA:
//                switch( tyre ) {
//                    case XSOFT : return 150.9;
//                    case SOFT : return 200.7;
//                    case MEDIUM : return 267;
//                    case HARD : return 354.90;
//                    case RAIN : return 408.2;
//                }
//        }
//        return 0;
//    }

    private double getCompoundCoef(Tyre tyre) {
        switch( tyre ) {
            case XSOFT : return 1.0;
            case SOFT : return 1.265;
            case MEDIUM : return 1.6;
            case HARD : return 2.024;
            case RAIN : return 2.5;
        }
        return 0;
    }

    private double getCompoundCoef100CT(Tyre tyre) {
        switch( tyre ) {
            case XSOFT : return 0.12;
            case SOFT : return 0.21;
            case MEDIUM : return 0.3;
            case HARD : return 0.4;
            case RAIN : return 0.42;
        }
        return 0;
    }

    private void printHeader() {
        line = new StringBuilder();
        line.append("season");
        fs("race");
        fs("track");
        fs("rlaps");
        fs("dist");
        fs("stint");
        
        fs("laps");
        fs("ct");
        fs("comp");
        fs("dur");
        fs("tal");
        fs("ti");
        fs("con");
        fs("agg");
        fs("exp");
        fs("wei");
        fs("cha");
        fs("chaW");
        fs("gea");
        fs("geaW");
        fs("eng");
        fs("engW");
        fs("sus");
        fs("susW");
        fs("ele");
        fs("eleW");
        fs("fwg");
        fs("fwgW");
        fs("rwg");
        fs("rwgW");
        fs("temp");
        fs("hum");
        
        fs("wear");
//        fs("calc");
//        fs("delta");

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
    
    private Map<Integer, TrackCoef> loadTracks() {
        Map<Integer, TrackCoef> tracks = new HashMap<Integer, TrackCoef>();
        try {
            BufferedReader reader = new BufferedReader( new FileReader("research/tyre_wear_coefs.csv") );
            String line = null;
            while( ( line = reader.readLine() ) != null ) {
                if( !line.isEmpty() ) {
                    String[] tokens = line.split(",");
                    if( tokens.length == 4 && !tokens[0].equalsIgnoreCase("ID") ) {
                        TrackCoef coef = new TrackCoef();
                        coef.id = new Integer(tokens[0].trim());
                        coef.ktyre = Double.parseDouble(tokens[2].trim());
                        coef.krisk = Double.parseDouble(tokens[3].trim() );
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

    public static class TrackCoef {
        public Integer id;
        public double ktyre;
        public double krisk;
    }
    

}
