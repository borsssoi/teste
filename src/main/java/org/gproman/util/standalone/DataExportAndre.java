package org.gproman.util.standalone;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.gproman.GproManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.car.Car;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.everest.NormalizedLap;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExportAndre {

    private static final String OUTPUT_FILE = "dados.csv";
    private static final String DELIMITER   = "\t";

    final static Logger         logger      = LoggerFactory.getLogger(DataExportAndre.class);

    private EverestService      everest;
    private UserConfiguration   conf;
    private PrintWriter         out;
    private StringBuilder       line;

    public static void main(String[] args) {
        logger.info("Starting export routine");
        new DataExportAndre().export();
        logger.info("Terminating export routine");
    }

    public void export() {
        conf = ConfigurationManager.loadConfiguration();
        if (startEverest() > 0) {
            try {
                out = new PrintWriter(OUTPUT_FILE);
                printHeader();
                for (int season = 17; season <= 39; season++) {
                    for (int race = 1; race <= 17; race++) {
                        logger.info("Exporting races for season " + season + " race " + race);
                        List<NormalizedRace> races = everest.getRaces(season, race);
                        for (NormalizedRace r : races) {
                            if (r.getRaceStatus().equals(RaceStatus.COMPLETED)) {
                                try {
                                    line = new StringBuilder();
                                    // row header
                                    printRowHeader(season, race);
                                    // track
                                    printTrack(r);
                                    // clima
                                    printWeather(r);
                                    // voltas
                                    if( !printLaps(r) ) continue;
                                    // desgaste pneus
                                    if( !printTyreWear(r) ) continue;
                                    // combustível
                                    printFuelConsumption(r);
                                    // setup
                                    printSetup(r);
                                    // car parts
                                    if( !printCarParts(r) ) continue;
                                    // risks
                                    printRisks(r);
                                    // PHA
                                    printPHA(r);
                                    // Driver
                                    if( !printDriver(r) ) continue;
                                    // URL
                                    fs(r.getUrl());
                                    out.println( line );
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

    private boolean printDriver(NormalizedRace r) {
        if( r.getDriverStart() == null || r.getDriverStart().getAttributes() == null ) {
            return false;
        }
        DriverAttributes d = r.getDriverStart().getAttributes();
        fi( d.getOverall() );
        fi( d.getConcentration() );
        fi( d.getTalent() );
        fi( d.getAggressiveness() );
        fi( d.getExperience() );
        fi( d.getTechInsight() );
        fi( d.getStamina() );
        fi( d.getCharisma() );
        fi( d.getMotivation() );
        fi( d.getReputation() );
        fi( d.getWeight() );
        fi( d.getAge() );
        return true;
    }

    private void printPHA(NormalizedRace r) {
        fi( r.getCarStart().getPower() );
        fi( r.getCarStart().getHandling() );
        fi( r.getCarStart().getAcceleration() );
    }

    private void printRisks(NormalizedRace r) {
        fi( r.getRiskOvertake() );
        fi( r.getRiskDefend() );
        fi( r.getRiskClear() );
        fi( r.getRiskMalfunction() );
    }

    private boolean printCarParts(NormalizedRace r) {
        if( r.getCarStart() == null || r.getCarFinish() == null || r.getCarWear() == null ) {
            return false;
        }
        for(int i = 0; i < Car.PARTS_COUNT; i++) {
            fi( r.getCarStart().getParts()[i].getLevel() );
        }
        for(int i = 0; i < Car.PARTS_COUNT; i++) {
            fi( (int) r.getCarStart().getParts()[i].getWear() );
        }
        for(int i = 0; i < Car.PARTS_COUNT; i++) {
            fi( (int) r.getCarFinish().getParts()[i].getWear() );
        }
        for(int i = 0; i < Car.PARTS_COUNT; i++) {
            fi( (int) r.getCarWear().getParts()[i].getWear() );
        }
        return true;
    }

    private void printSetup(NormalizedRace r) {
        CarSettings rs = r.getRaceSettings();
        fi( rs.getFrontWing() );
        fi( rs.getRearWing() );
        fi( rs.getEngine() );
        fi( rs.getBrakes() );
        fi( rs.getGear() );
        fi( rs.getSuspension() );
    }

    private void printFuelConsumption(NormalizedRace r) {
        fi( r.getStartingFuel() );
        fd( r.getFinishFuel().doubleValue()*1.8 );
        fd( r.getFuelUsed().doubleValue()/r.getTrack().getLaps() );
        fd( r.getFuelUsed().doubleValue()/r.getTrack().getDistance());
    }

    private boolean printTyreWear(NormalizedRace r) {
        fi(convertSupplier(r.getSupplier()));
        NormalizedStint st = null;
        for (NormalizedStint ns : r.getStints()) {
            if (ns.getLaps() >= 10 && ns.getTyreDurability() != null) {
                st = ns;
                break;
            }
        }
        if( st != null ) {
            fi(st.getTyre().ordinal());
            fi(st.getTyreLeft());
            fd(r.getTrack().getLapDistance() / st.getTyreDurability() * 100);
            fd(1.0 / st.getTyreDurability() * 100.0);
            fd(st.getTyreDurability());
            return true;
        }
        return false;
    }

    private int convertSupplier(TyreSupplier supplier) {
        if( supplier == null )
            return 0;
        switch (supplier) {
            case PIPIRELLI:
                return 1;
            case YOKOMAMA:
                return 2;
            case DUNNOLOP:
                return 3;
            case BADYEAR:
                return 4;
            case BRIDGEROCK:
                return 5;
            case MICHELINI:
                return 6;
            case AVONN:
                return 7;
            case CONTIMENTAL:
                return 8;
            case HANCOCK:
                return 9;
        }
        return 0;
    }

    private boolean printLaps(NormalizedRace r) {
        int dry = 0;
        int wet = 0;
        for (NormalizedLap l : r.getLaps()) {
            if (l.getWeather() == null ) {
                return false;
            }
            if (l.getWeather().equals(Weather.RAIN)) {
                wet++;
            } else {
                dry++;
            }
        }
        fi(dry);
        fi(wet);
        return true;
    }

    private void printWeather(NormalizedRace r) {
        fi(r.getAvgTemp().intValue());
        fi(r.getAvgHum().intValue());
    }

    private void printTrack(NormalizedRace r) {
        fi(r.getTrack().getId());
        fd(r.getTrack().getLapDistance());
        fi(r.getTrack().getTyreWear().ordinal());
        fi(r.getTrack().getFuelConsumption().ordinal());
    }

    private void printRowHeader(int season, int race) {
        line.append(season);
        fi(race);
    }

    private void printHeader() {
        line = new StringBuilder();
        line.append("Season");
        fs("Race");
        // circuito
        fs("Número");
        fs("Dist/volta");
        fs("Desg.Pneu");
        fs("Desg.Comb");
        // clima
        fs("Temp");
        fs("Hum");
        // voltas
        fs("Seco");
        fs("Chuva");
        // desgaste pneu
        fs("Fabricante");
        fs("Modelo");
        fs("Est.Final");
        fs("Desg./volta");
        fs("Desg./Km");
        fs("Durab");
        // consumo de combustível
        fs("Comb.Ini.");
        fs("Comb.Fin.");
        fs("Cons./Volta");
        fs("Cons./Km");
        // setup
        fs("Asa D.");
        fs("Asa T.");
        fs("Eng.");
        fs("Brak.");
        fs("Gear");
        fs("Susp.");
        // parts level
        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
            fs("lvl_"+Car.MNEM_PTBR[i]);
        }
        // wear at start
        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
            fs("desgI_"+Car.MNEM_PTBR[i]);
        }
        // wear at the end
        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
            fs("desgF_"+Car.MNEM_PTBR[i]);
        }
        // wear during the race
        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
            fs("desgT_"+Car.MNEM_PTBR[i]);
        }
        // risk
        fs("Ove");
        fs("Def");
        fs("Clear");
        fs("Malf");
        // PHA
        fs("P");
        fs("H");
        fs("A");
        // Driver
        fs("Overall");
        fs("Conc.");
        fs("Tal.");
        fs("Agg.");
        fs("Exp.");
        fs("Tech.");
        fs("Sta.");
        fs("Char.");
        fs("Mot.");
        fs("Rep.");
        fs("Weig.");
        fs("Age");
        //
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
