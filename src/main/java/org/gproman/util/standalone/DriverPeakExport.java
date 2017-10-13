package org.gproman.util.standalone;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gproman.GproManager;
import org.gproman.calc.DriverPlanner;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;
import org.gproman.util.CSVHelper;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverPeakExport {

    private static final String OUTPUT_FILE = "driver_peak.csv";
    private static final String DELIMITER   = ",";

    final static Logger logger = LoggerFactory.getLogger(DriverPeakExport.class);

    private EverestService    everest;
    private UserConfiguration conf;
    private PrintWriter       out;
    private CSVHelper         csv;

    public static void main(String[] args) {
        logger.info("Starting export routine");
        new DriverPeakExport().run();
        logger.info("Terminating export routine");
    }

    public void run() {
        conf = ConfigurationManager.loadConfiguration();
        if (startEverest() > 0) {
            try {
                out = new PrintWriter(OUTPUT_FILE);
                this.csv = new CSVHelper();
                writeHeader();
                for (int season = 43; season <= 43; season++) {
                    for (int race = 1; race <= 4; race++) {
                        logger.info("Exporting " + season + " race " + race);
                        List<NormalizedRace> races = everest.getRaces(season, race);
                        for (NormalizedRace r : races) {
                            if (r.getGroup().startsWith("Master") &&
                                    r.getRaceStatus().equals(RaceStatus.COMPLETED) &&
                                    r.getDriverStart() != null &&
                                    r.getCarStart() != null ) {
                                try {
                                    csv.newLine().fi(season, false)
                                        .fi(race)
                                        .fs(r.getTrack().getName())
                                        .fs(r.getGroup())
                                        .fs(r.getManager())
                                        .delim().delim().delim().delim()
                                        .fi(r.getRiskClear())
                                        .fi(r.getRiskClearWet())
                                        .fi(r.getCarStart().getPower())
                                        .fi(r.getCarStart().getHandling())
                                        .fi(r.getCarStart().getAcceleration());

                                    CarPart[] sc = r.getCarStart().getParts();
                                    for( CarPart p : sc ) {
                                        csv.fi(p.getLevel());
                                    }

                                    DriverAttributes attr = r.getDriverStart().getAttributes();
                                    csv.fs(r.getDriverStart().getName())
                                            .fi(attr.getOverall())
                                            .fi(attr.getConcentration())
                                            .fi(attr.getTalent())
                                            .fi(attr.getAggressiveness())
                                            .fi(attr.getExperience())
                                            .fi(attr.getTechInsight())
                                            .fi(attr.getStamina())
                                            .fi(attr.getCharisma())
                                            .fi(attr.getMotivation())
                                            .fi(attr.getReputation())
                                            .fi(attr.getWeight())
                                            .fi(attr.getAge());
                                    String fts = "";
                                    for (Track t : r.getDriverStart().getFavoriteTracks()) {
                                        if( !fts.isEmpty() ) {
                                            fts += ",";
                                        }
                                        fts += t.getName();
                                    }
                                    csv.fs(fts);
                                    TechDirector td = r.getTechDirector();
                                    if( td != null ) {
                                        TDAttributes ta = td.getAttributes();
                                        csv.fs(td.getName())
                                                .fi(ta.getOverall())
                                                .fi(ta.getLeadership())
                                                .fi(ta.getRdMech())
                                                .fi(ta.getRdElect())
                                                .fi(ta.getRdAero())
                                                .fi(ta.getExperience())
                                                .fi(ta.getPitCoord())
                                                .fi(ta.getMotivation())
                                                .fi(ta.getAge());
                                    } else {
                                        for( int i = 0; i < 10; i++ ) csv.delim();
                                    }
                                    csv.fs(r.getSupplier().name);
                                    out.print(csv.endLine());
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

    private void writeHeader() {
        String[] headers = new String[]{
                "Season",
                "Race",
                "Track",
                "Group",
                "Manager",
                "Practice",
                "Q1",
                "Q2",
                "FL",
                "Tyre_Sup",
                "Risk_CT",
                "Risk_CT_Wet",
                "Car_Power",
                "Car_Handling",
                "Car_Acceleration",
                "Car_Cha",
                "Car_Eng",
                "Car_Fwg",
                "Car_Rwg",
                "Car_Und",
                "Car_Sid",
                "Car_Coo",
                "Car_Gea",
                "Car_Bra",
                "Car_Sus",
                "Car_Ele",
                "Driver_Name",
                "Driver_OA",
                "Driver_Con",
                "Driver_Tal",
                "Driver_Agg",
                "Driver_Exp",
                "Driver_TI",
                "Driver_Sta",
                "Driver_Cha",
                "Driver_Mot",
                "Driver_Rep",
                "Driver_Wei",
                "Driver_Age",
                "Driver_FTs",
                "TD_Name",
                "TD_OA",
                "TD_Lead",
                "TD_Mech",
                "TD_Elec",
                "TD_Aero",
                "TD_Exp",
                "TD_Pit",
                "TD_Mot",
                "TD_Age"
        };
        out.print(csv.newLine().printHeader(headers).endLine());
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
