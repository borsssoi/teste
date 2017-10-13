package org.gproman.miner.parsers;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.db.DataService;
import org.gproman.miner.ParsingResult;
import org.gproman.miner.TelemetryMinerParser;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.gproman.model.driver.Driver;
import org.gproman.model.everest.NormalizedLap;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

public class GoBRTelemetryParser implements TelemetryMinerParser {

    private static final Logger logger        = LoggerFactory.getLogger(GoBRTelemetryParser.class);
    private final Pattern       seasonParser  = Pattern.compile("S(\\d\\d)R(\\d\\d) - (.+?)(( -|,) correndo na (\\w+ - \\d+)|(null)|\\s*$).*");
    private final Pattern       riskParser    = Pattern.compile("overtake (\\d+), defend (\\d+), clear (\\d+), mal func (\\d+)");
    private final Pattern       fuelParser    = Pattern.compile("Fuel Start: (\\d+) lts, ((End (\\d+) lts, Race Consumption (.*?) lts.*)|(DNF))");
    private final Pattern       pitPattern    = Pattern.compile(".*?L(\\d+), (.*?), (.*?): (\\d+)% \\((\\d+[,\\.]\\d+), ((\\d+[,\\.]\\d+)|(noBAD)), (\\d+[,\\.]\\d+)\\), fuel left (\\d+)% \\((\\d+[,\\.]\\d+) lts\\), ((refill (\\d+) lts)|((NOT) refilled)), pit time (\\d+[,\\.]\\d+)");
    private final Pattern       pitEndPattern = Pattern.compile("end L(\\d+), (.*?): (\\d+)% \\((\\d+[,\\.]\\d+), ((\\d+[,\\.]\\d+)|(noBAD)), (\\d+[,\\.]\\d+)\\)");

    private final DoubleParser  dp            = new DoubleParser();
    private final DataService   db;

    public GoBRTelemetryParser(DataService db) {
        this.db = db;
    }

    @Override
    public ParsingResult parse(String managerName, HtmlTableCell postCell, String tool, String url) {
        ParsingResult result = new ParsingResult();
        result.setContent(postCell.asText());
        // post is text based
        parseText(result, managerName, result.getContent(), tool, url);
        return result;
    }

    public void parseText(ParsingResult result, String managerName, String content, String tool, String url) {
        logger.info("Parsing goBR telemetry data for manager " + managerName);

        NormalizedRace race = new NormalizedRace();
        race.setManager(managerName);
        race.setTool(tool);
        race.setUrl(url);
        result.setRace(race);

        try {
            // parse header info
            BufferedReader reader = new BufferedReader(new StringReader(content));
            parseTextHeader(result, reader, race);

            // parse risks
            parseTextRisk(result, reader, race);

            // parse Setup
            parseTextSetup(result, reader, race);

            // parse fuel
            parseTextFuel(result, reader, race);

            // parse Stints
            parseTextStints(result, reader, race);

            // parse Tyre Supplier
            parseTextTyreSupplier(result, reader, race);

            // parse Car
            parseTextCar(result, reader, race);

            // parse Driver
            parseTextDriver(result, reader, race);

            // parse Laps
            parseTextLaps(result, reader, race);
            
            // update stints with the average temperature and humidity
            for( NormalizedStint stint : race.getStints() ) {
                // this loop is not efficient as it could be, but we want to be careful as 
                // there might be missing stints in the report
                double avgtemp = 0;
                double avghum = 0;
                int count = 0;
                int dry = 0;
                int wet = 0;
                for( NormalizedLap l : race.getLaps() ) {
                    if( l.getNumber() >= stint.getInitialLap() && l.getNumber() <= stint.getFinalLap() ) {
                        count++;
                        avgtemp += l.getTemperature();
                        avghum += l.getHumidity();
                        if( l.getWeather() != null && Weather.RAIN.equals(l.getWeather()) ) {
                            wet++;
                        } else if( l.getWeather() != null ) {
                            dry++;
                        }
                    }
                }
                if( count > 0 ) {
                    stint.setAvgTemp( avgtemp / count );
                    stint.setAvgHum( avghum / count );
                    if( wet > 0 && dry == 0 ) {
                        stint.setWeatherType(WeatherType.WET);
                    } else if( wet == 0 && dry > 0 ) {
                        stint.setWeatherType(WeatherType.WET);
                    } else if( wet <= 2 && dry > 2 ) {
                        stint.setWeatherType(WeatherType.MOSTLY_DRY);
                    } else if( wet > 2 && dry <= 2 ) {
                        stint.setWeatherType(WeatherType.MOSTLY_WET);
                    } else {
                        stint.setWeatherType(WeatherType.MIXED);
                    }
                }
            }

        } catch (Exception e) {
            result.addError("Error parsing table-based telemetry data for manager " + managerName, e);
        }
    }

    private void parseTextHeader(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                Matcher m = seasonParser.matcher(line);
                if (m.matches()) {
                    race.setSeasonNumber(Integer.valueOf(m.group(1)));
                    race.setGroup(m.group(5));
                    race.setRaceNumber(Integer.valueOf(m.group(2)));
                    Track track = db.getTrackByName(m.group(3));
                    race.setTrack(track);
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Header not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing header", e);
        }
    }

    private void parseTextRisk(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                Matcher m = riskParser.matcher(line);
                if (m.matches()) {
                    race.setRiskOvertake(Integer.valueOf(m.group(1)));
                    race.setRiskDefend(Integer.valueOf(m.group(2)));
                    race.setRiskClear(Integer.valueOf(m.group(3)));
                    race.setRiskClearWet(race.getRiskClear());
                    race.setRiskMalfunction(Integer.valueOf(m.group(4)));
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Risk data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing risk data", e);
        }
    }

    private void parseTextSetup(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Setup Used")) {
                    reader.readLine(); // skip header
                    String[] vals = reader.readLine().trim().split("\\s+"); // thats the data
                    if (vals.length == 6) {
                        CarSettings settings = new CarSettings();
                        settings.setFrontWing(Integer.valueOf(vals[0].trim()));
                        settings.setRearWing(Integer.valueOf(vals[1].trim()));
                        settings.setEngine(Integer.valueOf(vals[2].trim()));
                        settings.setBrakes(Integer.valueOf(vals[3].trim()));
                        settings.setGear(Integer.valueOf(vals[4].trim()));
                        settings.setSuspension(Integer.valueOf(vals[5].trim()));
                        race.setRaceSettings(settings);
                    } else {
                        pr.addError("Error parsing setup data: '" + Arrays.toString(vals) + "'", null);
                    }
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Setup data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing setup data", e);
        }
    }

    private void parseTextFuel(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                Matcher m = fuelParser.matcher(line);
                if (m.matches()) {
                    String val = m.group(1).trim();
                    race.setStartingFuel(Integer.valueOf(val));
                    val = m.group(4);
                    if( val != null ) {
                        race.setFinishFuel(Integer.valueOf(val.trim()));
                    }
                    val = m.group(5);
                    if( val != null ) {
                        race.setFuelUsed(dp.parse(val.trim()));
                    }
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Fuel data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing fuel data", e);
        }
    }

    private void parseTextStints(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Pit Stops")) {
                    // parse stints
                    Double fuelStart = race.getStartingFuel().doubleValue();
                    int startingLap = 1;
                    line = reader.readLine();
                    int c = 1;
                    while (line != null && !line.trim().isEmpty() && !line.startsWith("end")) {
                        NormalizedStint stint = new NormalizedStint();
                        stint.setNumber(c++);
                        Matcher m = pitPattern.matcher(line);
                        if (m.matches()) {
                            stint.setInitialLap(startingLap);
                            stint.setFinalLap(Integer.parseInt(m.group(1)));
                            startingLap = stint.getFinalLap() + 1;
                            stint.setPitReason(m.group(2));
                            stint.setTyre(Tyre.determineTyre(m.group(3)));

                            // tyres
                            stint.setTyreLeft(Integer.parseInt(m.group(4)));
                            stint.setTyreUsed(dp.parse(m.group(5)));
                            stint.setTyreDurability(dp.parse(m.group(9)));
                            String nobad = m.group(6);
                            if ("noBAD".equalsIgnoreCase(nobad)) {
                                stint.setTyreNoBad(stint.getTyreDurability() * .82);
                            } else {
                                stint.setTyreNoBad(dp.parse(nobad));
                            }

                            // fuel
                            stint.setFuelLeft(dp.parse(m.group(11)));
                            stint.setFuelStart(fuelStart);
                            String refuel = m.group(12);
                            if (!refuel.contains("NOT")) {
                                stint.setRefueledTo(Integer.valueOf(m.group(14)));
                                fuelStart = stint.getRefueledTo().doubleValue();
                            } else {
                                fuelStart = stint.getFuelLeft();
                            }
                            // time
                            String time = m.group(17);
                            stint.setPitTime(Integer.valueOf(time.replaceAll("[\\.,s]", "")));
                            race.getStints().add(stint);
                        }
                        line = reader.readLine();
                    }
                    if (line != null && line.startsWith("end")) {
                        Matcher m = pitEndPattern.matcher(line);
                        if (m.matches()) {
                            NormalizedStint stint = new NormalizedStint();
                            stint.setNumber(c++);

                            stint.setInitialLap(startingLap);
                            stint.setFinalLap(Integer.parseInt(m.group(1)));
                            startingLap = stint.getFinalLap() + 1;
                            stint.setPitReason("Race end");
                            stint.setTyre(Tyre.determineTyre(m.group(2)));

                            // tyres
                            stint.setTyreLeft(Integer.parseInt(m.group(3)));
                            stint.setTyreUsed(dp.parse(m.group(4)));
                            stint.setTyreDurability(dp.parse(m.group(8)));
                            String nobad = m.group(5);
                            if ("noBAD".equalsIgnoreCase(nobad)) {
                                stint.setTyreNoBad(stint.getTyreDurability() * .82);
                            } else {
                                stint.setTyreNoBad(dp.parse(nobad));
                            }

                            // fuel
                            stint.setFuelLeft(race.getFinishFuel().doubleValue());
                            stint.setFuelStart(fuelStart);
                            race.getStints().add(stint);
                        }
                    }
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Stints data not found!", null);
        } catch (Exception e) {
            pr.addError("Error parsing stints data", e);
        }
    }

    private void parseTextTyreSupplier(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Tyre Supplier:")) {
                    String[] vals = line.split("\\s+");
                    if( vals.length >= 3) {
                        race.setSupplier(TyreSupplier.determineTyre(vals[2].trim()));
                    }
                    return;
                } else if( line.startsWith("Desgaste ao fim da corrida") ) {
                    // tyre supplier not available
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Tyre supplier data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing tyre supplier data", e);
        }
    }

    private void parseTextCar(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Part Lv Inicio Fim Wear")) {
                    // parse car parts
                    Car start = new Car();
                    Car finish = new Car();
                    Car wear = new Car();

                    for (int i = 0; i < Car.PARTS_COUNT; i++) {
                        line = reader.readLine();
                        if (line != null) {
                            line = line.trim();
                            CarPart[] parts = parseCarPartLine(pr, i, line);
                            start.setPart(i, parts[0]);
                            finish.setPart(i, parts[1]);
                            wear.setPart(i, parts[2]);
                        }
                    }

                    if (start.isCarPartDataAvailable()) {
                        race.setCarStart(start);
                    }
                    if (finish.isCarPartDataAvailable()) {
                        race.setCarFinish(finish);
                    }
                    if (wear.isCarPartDataAvailable()) {
                        race.setCarWear(wear);
                    }
                    
                    line = reader.readLine();
                    while (line != null) {
                        line = line.trim();
                        if (line.startsWith("P H A")) {
                            // parse PHA
                            String[] vals = reader.readLine().trim().split("\\s+");
                            PHA pha = new PHA();
                            pha.setP(Double.parseDouble(vals[0].trim()));
                            pha.setH(Double.parseDouble(vals[1].trim()));
                            pha.setA(Double.parseDouble(vals[2].trim()));
                            start.setPHA(pha);
                            finish.setPHA(pha);
                            wear.setPHA(pha);
                            return;
                        }
                        line = reader.readLine();
                    }

                    pr.addError("Car PHA data not found", null);
                }
                line = reader.readLine();
            }
            pr.addError("Car data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing car data", e);
        }
    }

    private CarPart[] parseCarPartLine(ParsingResult pr, int index, String line) {
        CarPart[] parts = new CarPart[3];
        try {
            String name = Car.PARTS[index];
            String[] vals = line.split("\\s+");
            int level = Integer.parseInt(vals[1].trim());

            String wear = vals[2].trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[0] = new CarPart();
                parts[0].setName(name);
                parts[0].setLevel(level);
                parts[0].setWear(Double.parseDouble(wear));
            }

            wear = vals[3].trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[1] = new CarPart();
                parts[1].setName(name);
                parts[1].setLevel(level);
                parts[1].setWear(Double.parseDouble(wear));
            }

            wear = vals[4].trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[2] = new CarPart();
                parts[2].setName(name);
                parts[2].setLevel(level);
                if ("*".equalsIgnoreCase(wear)) {
                    parts[2].setWear(99);
                } else {
                    parts[2].setWear(Double.parseDouble(wear));
                }
            }
            return parts;
        } catch (Exception e) {
            pr.addError("Error parsing car part for line: " + line, e);
        }
        return parts;
    }

    private void parseTextDriver(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Piloto:")) {
                    Driver s = new Driver();
                    race.setDriverStart(s);
                    String[] name = line.split(":");
                    s.setName(name.length==2 ? name[1].trim() : null);
                    
                    line = reader.readLine();
                    while (line != null && !line.trim().isEmpty()) {
                        String[] cells = line.split(":");
                        String attr = cells[0].trim();
                        String val = cells[1].trim();
                        if ("Overall".equalsIgnoreCase(attr)) {
                            s.getAttributes().setOverall(Integer.valueOf(val));
                        } else if ("Concentration".equalsIgnoreCase(attr)) {
                            s.getAttributes().setConcentration(Integer.valueOf(val));
                        } else if ("Talent".equalsIgnoreCase(attr)) {
                            s.getAttributes().setTalent(Integer.valueOf(val));
                        } else if ("Aggresiveness".equalsIgnoreCase(attr)) {
                            s.getAttributes().setAggressiveness(Integer.valueOf(val));
                        } else if ("Experience".equalsIgnoreCase(attr)) {
                            s.getAttributes().setExperience(Integer.valueOf(val));
                        } else if ("Tech. Insight".equalsIgnoreCase(attr)) {
                            s.getAttributes().setTechInsight(Integer.valueOf(val));
                        } else if ("Stamina".equalsIgnoreCase(attr)) {
                            s.getAttributes().setStamina(Integer.valueOf(val));
                        } else if ("Charisma".equalsIgnoreCase(attr)) {
                            s.getAttributes().setCharisma(Integer.valueOf(val));
                        } else if ("Motivation".equalsIgnoreCase(attr)) {
                            s.getAttributes().setMotivation(Integer.valueOf(val));
                        } else if ("Reputation".equalsIgnoreCase(attr)) {
                            s.getAttributes().setReputation(Integer.valueOf(val));
                        } else if ("Weight".equalsIgnoreCase(attr)) {
                            s.getAttributes().setWeight(Integer.valueOf(val));
                        } else if ("Age".equalsIgnoreCase(attr)) {
                            s.getAttributes().setAge(Integer.valueOf(val));
                        } else if ("Favorite Tracks".equalsIgnoreCase(attr)) {
                            if( ! val.isEmpty() ) {
                                String[] tracks = val.split(",");
                                if (tracks.length > 0) {
                                    List<Track> favorites = new ArrayList<Track>();
                                    for (String t : tracks) {
                                        if (t != null && !t.trim().isEmpty()) {
                                            Track track = db.getTrackByName(t.trim());
                                            if (track != null) {
                                                favorites.add(track);
                                            } else {
                                                pr.addError("Favorite track not found: " + t, null);
                                            }
                                        }
                                    }
                                    s.setFavoriteTracks(favorites);
                                }
                            }
                        }
                        line = reader.readLine();
                    }
                    return;
                } else if( line.startsWith("Voltas") ) {
                    // Driver data not available
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Driver data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing driver data", e);
        }
    }

    private void parseTextLaps(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Lap Time Pos")) {
                    // parse laps
                    RaceStatus status = RaceStatus.UNKNOWN;
                    double avgTemp = 0;
                    double avgHum = 0;
                    int lastLap = 0;

                    line = reader.readLine();
                    while (line != null && !line.trim().isEmpty()) {
                        String[] cells = line.trim().split("\\s+");

                        NormalizedLap lap = new NormalizedLap();
                        race.getLaps().add(lap);
                        lap.setNumber(Integer.parseInt(cells[0].trim()));
                        lap.setTime(parseTime(cells[1].trim()));
                        lap.setPosition(Integer.parseInt(cells[2].trim()));
                        lap.setTyre(Tyre.determineTyre(cells[3].trim()));
                        lap.setWeather(Weather.determineWeather(cells[4].trim()));
                        String temp = cells[5].trim();
                        lap.setTemperature(Integer.valueOf(temp.replaceAll("[°C\\s]", "")));
                        String hum = cells[6].trim();
                        lap.setHumidity(Integer.valueOf(hum.replaceAll("[%\\s]", "")));
                        lap.setEvents(cells[9].trim());
                        for( int i = 10; i < cells.length; i++ ) {
                            // have to concatenate all the remaining cells
                            lap.setEvents(lap.getEvents()+" "+cells[i]);
                        }
                        if (lap.getEvents().contains("Car problem") && status == RaceStatus.UNKNOWN) {
                            status = RaceStatus.CAR_PROBLEM;
                        }
                        avgTemp += lap.getTemperature();
                        avgHum += lap.getHumidity();
                        if (lap.getNumber() > 0 && lastLap == 0 && (lap.getTime() == null || lap.getTime() <= 0)) {
                            lastLap = lap.getNumber();
                            status = RaceStatus.DROPPED_OUT;
                        }
                        line = reader.readLine();
                    }
                    if (race.getLaps().size() > 0) {
                        avgTemp /= race.getLaps().size();
                        avgHum /= race.getLaps().size();
                        race.setAvgTemp(avgTemp);
                        race.setAvgHum(avgHum);
                        race.setDistance(lastLap == 0 ? race.getTrack().getDistance() : race.getTrack().getLapDistance() * lastLap);
                        if (status == RaceStatus.UNKNOWN && lastLap == 0) {
                            status = RaceStatus.COMPLETED;
                        }
                    } else {
                        status = RaceStatus.ERROR;
                    }
                    race.setRaceStatus(status);
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Laps data not found!", null);
        } catch (Exception e) {
            pr.addError("Error parsing laps data", e);
        }
    }

    private Integer parseTime(String timeStr) throws ParseException {
        if ("-".equals(timeStr) || "".equals(timeStr)) {
            return null;
        } else if (timeStr.indexOf(':') > 0) {
            // 'm:ss.SSS'
            String[] parts = timeStr.split("[:\\.]");
            return (Integer.parseInt(parts[0]) * 60000) + (Integer.parseInt(parts[1]) * 1000) + Integer.parseInt(parts[2]);
        }
        return null;
    }

}