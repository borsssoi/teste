package org.gproman.util.standalone;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.gproman.GproManager;
import org.gproman.calc.FuelCalculator;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.util.CSVHelper;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuelBenchmark {

    private static final String OUTPUT_FILE = "research/fuel_benchmark.csv";

    final static Logger logger = LoggerFactory.getLogger(FuelBenchmark.class);

    private EverestService everest;
    private UserConfiguration conf;
    private PrintWriter out;

    public static void main(String[] args) {
        logger.info("Starting export routine");
        new FuelBenchmark().run();
        logger.info("Terminating export routine");
    }

    public void run() {
        conf = ConfigurationManager.loadConfiguration();
        if (startEverest() > 0) {
            try {
                out = new PrintWriter(OUTPUT_FILE);
                CSVHelper csv = new CSVHelper();
                printHeader(csv);
                for (int season = 17; season <= 46; season++) {
                    for (int race = 1; race <= 17; race++) {
                        logger.info("Benchmarking " + season + " race " + race);
                        List<NormalizedRace> races = everest.getRaces(season, race);
                        for (NormalizedRace r : races) {
                            if (r.getRaceStatus().equals(RaceStatus.COMPLETED) &&
                                    r.getDriverStart() != null &&
                                    r.getCarStart() != null &&
                                    r.getCarFinish() != null &&
                                    r.getCarWear() != null) {
                                try {
                                    for (NormalizedStint s : r.getStints()) {
                                        if (s.getLaps() > 6 &&
                                                ((s.getWeatherType().equals(WeatherType.DRY) && !s.getTyre().equals(
                                                        Tyre.RAIN)) ||
                                                        (s.getWeatherType().equals(
                                                                WeatherType.WET) && s.getTyre().equals(Tyre.RAIN)))) {
                                            String group = ! r.getGroup().isEmpty() && r.getGroup().indexOf( "-" ) > 0 ? r.getGroup().substring( 0, r.getGroup().indexOf( "-" ) ).trim() :
                                                    r.getGroup().length() > 1 ? r.getGroup().trim() : "";
                                            csv.newLine().
                                            fi(season, false).
                                            fi(race).
                                            fs(r.getTrack().getName()).
                                            fs(group).
                                            fi(r.getTrack().getLaps()).
                                            fd(r.getTrack().getDistance()).
                                            fd(r.getFuelUsed()).
                                            fi(s.getNumber()).
                                            fi(s.getLaps()).
                                            fi(r.getDriverStart().getAttributes().getTalent()).
                                            fi(r.getDriverStart().getAttributes().getTechInsight()).
                                            fi(r.getDriverStart().getAttributes().getConcentration()).
                                            fi(r.getDriverStart().getAttributes().getAggressiveness()).
                                            fi(r.getDriverStart().getAttributes().getExperience()).
                                            fi(r.getDriverStart().getAttributes().getWeight()).
                                            fi(r.getCarStart().getGearbox().getLevel()).
                                            fd(r.getCarStart().getGearbox().getWear()).
                                            fi(r.getCarStart().getEngine().getLevel()).
                                            fd(r.getCarStart().getEngine().getWear()).
                                            fi(r.getCarStart().getElectronics().getLevel()).
                                            fd(r.getCarStart().getElectronics().getWear()).
                                            fd(s.getAvgTemp()).
                                            fd(s.getAvgHum());

                                            Weather weather = s.getWeatherType().equals(WeatherType.DRY) ? Weather.SUNNY : Weather.RAIN;
                                            double f1 = FuelCalculator.predictConsumption( weather,
                                                                                           r.getCarStart().getEngine().getLevel(),
                                                                                           r.getCarStart().getElectronics().getLevel(),
                                                                                           r.getTrack() ).doubleValue() *
                                                                                           r.getTrack().getLapDistance() * s.getLaps();
                                            double f2 = FuelCalculator.predictConsumption2( weather,
                                                                                            r.getCarStart().getEngine().getLevel(),
                                                                                            r.getCarStart().getElectronics().getLevel(),
                                                                                            s.getAvgHum(),
                                                                                            r.getTrack(),
                                                                                            r.getDriverStart() ) /
                                                                                            r.getTrack().getLaps() * s.getLaps();
                                            double f3 = FuelCalculator.predictConsumption3( weather,
                                                                                            r.getCarStart().getEngine().getLevel(),
                                                                                            r.getCarStart().getElectronics().getLevel(),
                                                                                            r.getTrack(),
                                                                                            r.getDriverStart() ) /
                                                                                            r.getTrack().getLaps() * s.getLaps();
                                            double actual = s.getFuelStart()-s.getFuelLeft();


                                            double f4 = 0;
                                            if( "Estoril".equalsIgnoreCase( r.getTrack().getName() ) ) {
                                                f4 = 1.1954
                                                     + 0.028 * r.getCarStart().getEngine().getLevel()
                                                     - 0.00015 * r.getCarStart().getEngine().getWear()
                                                     + 0.012 * r.getCarStart().getElectronics().getLevel()
                                                     + 0.00025 * r.getDriverStart().getAttributes().getExperience()
                                                     + 0.0005 * r.getDriverStart().getAttributes().getTechInsight();
                                                f4 = r.getTrack().getLapDistance() * s.getLaps() / f4;
                                            }

                                            if( actual <= 20 ) continue;

                                            csv.fs(s.getWeatherType().english)
                                                    .fd(actual)
                                                    .fd(f1)
                                                    .fd(f2)
                                                    .fd(f3)
                                                    .fd(f4)
                                                    .fd((f1-actual)/actual)
                                                    .fd((f2-actual)/actual)
                                                    .fd((f3-actual)/actual)
                                                    .fd((f4-actual)/actual);
                                            csv.fs(r.getUrl());
                                            out.print(csv.endLine());
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

    private void printHeader(CSVHelper csv) {
        String[] headers = new String[]{
                "season",
                "race",
                "track",
                "group",
                "rlaps",
                "dist",
                "rfuel",
                "stint",
                "laps",
                "tal",
                "ti",
                "con",
                "agg",
                "exp",
                "wei",
                "gea",
                "geaW",
                "eng",
                "engW",
                "ele",
                "eleW",
                "temp",
                "hum",
                "weather",
                "fuel",
                "f1",
                "f2",
                "f3",
                "f4",
                "df1",
                "df2",
                "df3",
                "df4",
                "URL"};
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
