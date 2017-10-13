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
import org.gproman.model.everest.NormalizedRace.TDStatus;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class GMTTelemetryParser implements TelemetryMinerParser {

    private static final Logger logger       = LoggerFactory.getLogger(GMTTelemetryParser.class);
    private final Pattern       seasonParser = Pattern.compile("S(\\d\\d)R(\\d\\d) - (.*)( -|,) correndo na ((\\w+ - \\d+)|(null)).*");
    private final Pattern       riskParser   = Pattern.compile("Ultrapassagem: (\\d+), Defesa: (\\d+), Pista Livre: (\\d+),( Pista Livre Molhada: (\\d+),)? Defeito: (\\d+)");
    private final Pattern       fuelParser   = Pattern.compile("Início=(.*?) lts, Fim=(.*?) lts, Consumo da corrida=(.*?) lts, Eficiencia=(.*?) Km/lt.");
    private final Pattern       tyreP        = Pattern.compile("(\\d+)% \\(\\s*(.*?)Km, (.*?)Km, (.*?)Km\\)");

    private final DoubleParser  dp           = new DoubleParser();
    private final DataService   db;

    public GMTTelemetryParser(DataService db) {
        this.db = db;
    }

    @Override
    public ParsingResult parse(String managerName, HtmlTableCell postCell, String tool, String url) {
        ParsingResult result = new ParsingResult();
        result.setContent(postCell.asXml());
        HtmlTable table = postCell.getFirstByXPath("./table");
        if (table != null) {
            // post is table based
            parseTable(result, managerName, table, tool, url);
        } else {
            // post is text based
            parseText(result, managerName, postCell.asText(), tool, url);
        }
        
        updateWeatherForStints(result);
        return result;
    }

    private void updateWeatherForStints(ParsingResult result) {
        // update stints with the weather type
        for( NormalizedStint stint : result.getRace().getStints() ) {
            // this loop is not efficient as it could be, but we want to be careful as 
            // there might be missing stints in the report
            int count = 0;
            int dry = 0;
            int wet = 0;
            for( NormalizedLap l : result.getRace().getLaps() ) {
                if( l.getNumber() >= stint.getInitialLap() && l.getNumber() <= stint.getFinalLap() ) {
                    count++;
                    if( l.getWeather() != null && Weather.RAIN.equals(l.getWeather()) ) {
                        wet++;
                    } else if( l.getWeather() != null ) {
                        dry++;
                    }
                }
            }
            if( count > 0 ) {
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
    
    private void parseTable(ParsingResult result, String managerName, HtmlTable content, String tool, String url) {
        logger.info("Parsing table-based telemetry data for manager " + managerName);

        NormalizedRace race = new NormalizedRace();
        race.setManager(managerName);
        race.setTool(tool);
        race.setUrl(url);
        result.setRace(race);

        try {
            // parse header info
            String theader = content.getHeader().asText().trim();
            parseTableHeader(result, theader, race);

            // parse fuel
            parseTableFuel(result, content, race);
            
            // parse Energy
            //parseEnergy(result, content, race);

            // parse risks
            parseTableRisk(result, content, race);

            // parse Setup
            parseTableSetup(result, content, race);

            // parse Tyre Supplier
            parseTableTyreSupplier(result, content, race);

            // parse Car
            parseTableCar(result, content, race);

            // parse Driver
            parseTableDriver(result, content, race);

            // parse Technical Director
            parseTableTechDirector(result, content, race);

            // parse Stints
            parseTableStints(result, content, race);

            // parse Laps
            parseTableLaps(result, content, race);

        } catch (Exception e) {
            result.addError("Error parsing table-based telemetry data for manager " + managerName, e);
        }
    }

    private void parseTableHeader(ParsingResult pr, String theader, NormalizedRace race) {
        try {
            Matcher m = seasonParser.matcher(theader);
            if (m.matches()) {
                race.setSeasonNumber(Integer.valueOf(m.group(1)));
                race.setGroup(m.group(5));
                race.setRaceNumber(Integer.valueOf(m.group(2)));
                Track track = db.getTrackByName(m.group(3));
                race.setTrack(track);
            }
        } catch (Exception e) {
            pr.addError("Error parsing header '" + theader + "'", e);
        }
    }

    private void parseTableFuel(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable fuelTable = content.getFirstByXPath(".//th[contains(text(),'Combustível')]/ancestor::table[1]");
            if (fuelTable != null) {
                String val = fuelTable.getRow(1).getCell(1).asText().replaceAll("lts", "").trim();
                if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                    race.setStartingFuel(Integer.valueOf(val));
                }
                val = fuelTable.getRow(2).getCell(1).asText().replaceAll("lts", "").trim();
                if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                    race.setFinishFuel(Integer.valueOf(val));
                }
                val = fuelTable.getRow(3).getCell(1).asText().replaceAll("lts", "").trim();
                if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                    race.setFuelUsed(dp.parse(val));
                }
            } else {
                pr.addError("Fuel table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing fuel table", e);
        }
    }

    private void parseTableRisk(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable riskTable = content.getFirstByXPath(".//th[contains(text(),'Riscos usados')]/ancestor::table[1]");
            if (riskTable != null) {
                if( riskTable.getRowCount() == 5 ) {
                    race.setRiskOvertake(Integer.valueOf(riskTable.getRow(1).getCell(1).asText().trim()));
                    race.setRiskDefend(Integer.valueOf(riskTable.getRow(2).getCell(1).asText().trim()));
                    race.setRiskClear(Integer.valueOf(riskTable.getRow(3).getCell(1).asText().trim()));
                    race.setRiskClearWet(race.getRiskClear());
                    race.setRiskMalfunction(Integer.valueOf(riskTable.getRow(4).getCell(1).asText().trim()));
                } else {
                    race.setRiskOvertake(Integer.valueOf(riskTable.getRow(1).getCell(1).asText().trim()));
                    race.setRiskDefend(Integer.valueOf(riskTable.getRow(2).getCell(1).asText().trim()));
                    race.setRiskClear(Integer.valueOf(riskTable.getRow(3).getCell(1).asText().trim()));
                    race.setRiskClearWet(Integer.valueOf(riskTable.getRow(4).getCell(1).asText().trim()));
                    race.setRiskMalfunction(Integer.valueOf(riskTable.getRow(5).getCell(1).asText().trim()));
                }
            } else {
                pr.addError("Risk table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing risk table", e);
        }
    }

    private void parseTableSetup(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable setupTable = content.getFirstByXPath(".//th[contains(text(),'Ajuste usado')]/ancestor::table[1]");
            if (setupTable != null) {
                CarSettings settings = new CarSettings();
                settings.setFrontWing(Integer.valueOf(setupTable.getRow(2).getCell(0).asText().trim()));
                settings.setRearWing(Integer.valueOf(setupTable.getRow(2).getCell(1).asText().trim()));
                settings.setEngine(Integer.valueOf(setupTable.getRow(2).getCell(2).asText().trim()));
                settings.setBrakes(Integer.valueOf(setupTable.getRow(2).getCell(3).asText().trim()));
                settings.setGear(Integer.valueOf(setupTable.getRow(2).getCell(4).asText().trim()));
                settings.setSuspension(Integer.valueOf(setupTable.getRow(2).getCell(5).asText().trim()));
                race.setRaceSettings(settings);
            } else {
                pr.addError("Setup table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing setup table", e);
        }
    }

    private void parseTableTyreSupplier(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable supplierTable = content.getFirstByXPath(".//th[contains(text(),'Fornecedor de Pneu')]/ancestor::table[1]");
            if (supplierTable != null) {
                race.setSupplier(TyreSupplier.determineTyre(supplierTable.getRow(1).getCell(0).getTextContent().split("\\s+")[0].trim()));
            } else {
                pr.addError("Supplier table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing supplier table", e);
        }
    }

    private void parseTableCar(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable carTable = content.getFirstByXPath(".//th[contains(text(),'Carro')]/ancestor::table[1]");
            if (carTable != null) {
                Car start = new Car();
                Car finish = new Car();
                Car wear = new Car();

                for (int i = 0; i < Car.PARTS_COUNT; i++) {
                    CarPart[] parts = parseCarPartRow(pr, i, carTable.getRow(i + 2));
                    start.setPart(i, parts[0]);
                    finish.setPart(i, parts[1]);
                    wear.setPart(i, parts[2]);
                }

                HtmlTable phaTable = carTable.getFirstByXPath("./following-sibling::table");
                if (phaTable != null && phaTable.asText().contains("* Desgaste da peça")) {
                    phaTable = phaTable.getFirstByXPath("./following-sibling::table");
                }
                if (phaTable != null) {
                    PHA pha = new PHA();
                    pha.setP(Double.parseDouble(phaTable.getRow(1).getCell(0).getTextContent()));
                    pha.setH(Double.parseDouble(phaTable.getRow(1).getCell(1).getTextContent()));
                    pha.setA(Double.parseDouble(phaTable.getRow(1).getCell(2).getTextContent()));
                    start.setPHA(pha);
                    finish.setPHA(pha);
                    wear.setPHA(pha);
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
            } else {
                pr.addError("Car table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing car table", e);
        }
    }

    private CarPart[] parseCarPartRow(ParsingResult pr, int index, HtmlTableRow row) {
        CarPart[] parts = new CarPart[3];
        try {
            String name = Car.PARTS[index];
            int level = 0;
            int i = 0;
            if (row.getCells().size() == 4) {
                String[] vals = row.getCell(0).getTextContent().trim().split("\\s+");
                level = Integer.parseInt(vals[vals.length - 1]);
                i = 1;
            } else {
                level = Integer.parseInt(row.getCell(1).getTextContent().trim());
                i = 2;
            }

            String wear = row.getCell(i++).getTextContent().trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[0] = new CarPart();
                parts[0].setName(name);
                parts[0].setLevel(level);
                parts[0].setWear(Double.parseDouble(wear));
            }

            wear = row.getCell(i++).getTextContent().trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[1] = new CarPart();
                parts[1].setName(name);
                parts[1].setLevel(level);
                parts[1].setWear(Double.parseDouble(wear));
            }

            wear = row.getCell(i++).getTextContent().trim().replaceAll("%", "");
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
            pr.addError("Error parsing car part for row: " + row.asText(), null);
        }
        return parts;
    }

    private void parseTableDriver(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable driverTable = content.getFirstByXPath(".//th[contains(text(),'Piloto')]/ancestor::table[1]");
            if (driverTable != null) {
                boolean skipFirst = true;
                Driver s = new Driver();
                Driver f = new Driver();
                race.setDriverStart(s);
                race.setDriverFinish(f);
                for (HtmlTableRow row : driverTable.getRows()) {
                    if (skipFirst) {
                        skipFirst = false;
                        continue;
                    }
                    String attr = row.getCell(0).getTextContent().trim();
                    String val = row.getCell(1).getTextContent().trim();
                    if ("Nome".equalsIgnoreCase(attr)) {
                        s.setName(val);
                        f.setName(val);
                    } else if ("Total".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setOverall(vals[0]);
                        f.getAttributes().setOverall(vals[1]);
                    } else if ("Concentração".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setConcentration(vals[0]);
                        f.getAttributes().setConcentration(vals[1]);
                    } else if ("Talento".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setTalent(vals[0]);
                        f.getAttributes().setTalent(vals[1]);
                    } else if ("Agressividade".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setAggressiveness(vals[0]);
                        f.getAttributes().setAggressiveness(vals[1]);
                    } else if ("Experiência".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setExperience(vals[0]);
                        f.getAttributes().setExperience(vals[1]);
                    } else if ("Conhec. Técnico".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setTechInsight(vals[0]);
                        f.getAttributes().setTechInsight(vals[1]);
                    } else if ("Resistência".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setStamina(vals[0]);
                        f.getAttributes().setStamina(vals[1]);
                    } else if ("Carisma".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setCharisma(vals[0]);
                        f.getAttributes().setCharisma(vals[1]);
                    } else if ("Motivação".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setMotivation(vals[0]);
                        f.getAttributes().setMotivation(vals[1]);
                    } else if ("Reputação".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setReputation(vals[0]);
                        f.getAttributes().setReputation(vals[1]);
                    } else if ("Peso".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setWeight(vals[0]);
                        f.getAttributes().setWeight(vals[1]);
                    } else if ("Idade".equalsIgnoreCase(attr)) {
                        int[] vals = parseDriverAttr(val);
                        s.getAttributes().setAge(vals[0]);
                        f.getAttributes().setAge(vals[1]);
                    } else if ("Pistas Favoritas".equalsIgnoreCase(attr)) {
                        String[] tracks = val.replaceAll("[\\[\\]]", "").split(",");
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
                            f.setFavoriteTracks(favorites);
                        }
                    }
                }
            } else {
                pr.addError("Driver table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing driver table", e);
        }
    }

    private int[] parseDriverAttr(String val) {
        int[] result = new int[2];
        if (val.contains("=")) {
            String[] vals = val.split("[\\s\\+=]+");
            result[0] = Integer.parseInt(vals[0]);
            result[1] = Integer.parseInt(vals[2]);
        } else {
            result[0] = Integer.parseInt(val);
            result[1] = result[0];
        }
        return result;
    }

    private void parseTableTechDirector(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable tdTable = content.getFirstByXPath(".//big[contains(text(),'Liderança')]/ancestor::table[1]");
            if (tdTable != null) {
                if ("Não contratado".equalsIgnoreCase(tdTable.getRow(1).getCell(1).getTextContent().trim())) {
                    race.setTdStatus(TDStatus.NOT_HIRED);
                } else {
                    boolean skipFirst = true;
                    TechDirector s = new TechDirector();
                    race.setTechDirector(s);
                    race.setTdStatus(TDStatus.HIRED);
                    for (HtmlTableRow row : tdTable.getRows()) {
                        if (skipFirst) {
                            skipFirst = false;
                            continue;
                        }
                        String attr = row.getCell(0).getTextContent().trim();
                        String val = row.getCell(1).getTextContent().trim();
                        if ("Nome".equalsIgnoreCase(attr)) {
                            s.setName(val);
                        } else if ("Total".equalsIgnoreCase(attr)) {
                            s.getAttributes().setOverall(Integer.parseInt(val));
                        } else if ("Liderança".equalsIgnoreCase(attr)) {
                            s.getAttributes().setLeadership(Integer.parseInt(val));
                        } else if ("P&D Mecânico".equalsIgnoreCase(attr)) {
                            s.getAttributes().setRdMech(Integer.parseInt(val));
                        } else if ("P&D Eletrônico".equalsIgnoreCase(attr)) {
                            s.getAttributes().setRdElect(Integer.parseInt(val));
                        } else if ("P&D Aerodinâmico".equalsIgnoreCase(attr)) {
                            s.getAttributes().setRdAero(Integer.parseInt(val));
                        } else if ("Experiência".equalsIgnoreCase(attr)) {
                            s.getAttributes().setExperience(Integer.parseInt(val));
                        } else if ("Coord. de Pit".equalsIgnoreCase(attr)) {
                            s.getAttributes().setPitCoord(Integer.parseInt(val));
                        } else if ("Motivação".equalsIgnoreCase(attr)) {
                            s.getAttributes().setMotivation(Integer.parseInt(val));
                        } else if ("Idade".equalsIgnoreCase(attr)) {
                            s.getAttributes().setAge(Integer.parseInt(val));
                        }
                    }
                }
            } else {
                // this is not an error because old reports did not have TD information
                race.setTdStatus(TDStatus.UNKNOWN);
            }
        } catch (Exception e) {
            pr.addError("Error parsing TD table", e);
        }
    }

    private void parseTableStints(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            HtmlTable stintsTable = content.getFirstByXPath(".//th[contains(text(),'Stints')]/ancestor::table[1]");
            if (stintsTable != null) {
                Double fuelStart = race.getStartingFuel().doubleValue();
                for (int i = 2; i < stintsTable.getRowCount(); i++) {
                    HtmlTableRow row = stintsTable.getRow(i);
                    NormalizedStint stint = new NormalizedStint();
                    stint.setNumber(i - 2);
                    String[] laps = row.getCell(0).getTextContent().trim().split("-");
                    stint.setInitialLap(Integer.parseInt(laps[0].trim()));
                    stint.setFinalLap(Integer.parseInt(laps[1].trim()));
                    if (stint.getInitialLap() > stint.getFinalLap()) {
                        // error, unable to continue parsing stints
                        break;
                    }
                    stint.setPitReason(row.getCell(1).getTextContent().trim());
                    stint.setTyre(Tyre.determineTyre(row.getCell(2).getTextContent().trim().replaceAll(":", "")));

                    // tyres
                    Matcher m = tyreP.matcher(row.getCell(3).getTextContent().trim());
                    if (m.matches()) {
                        stint.setTyreLeft(Integer.parseInt(m.group(1)));
                        stint.setTyreUsed("Infinity".equalsIgnoreCase(m.group(2)) || "NaN".equalsIgnoreCase(m.group(2)) ? null : dp.parse(m.group(2)));
                        stint.setTyreNoBad("Infinity".equalsIgnoreCase(m.group(3)) || "NaN".equalsIgnoreCase(m.group(3)) ? null : dp.parse(m.group(3)));
                        stint.setTyreDurability("Infinity".equalsIgnoreCase(m.group(4)) || "NaN".equalsIgnoreCase(m.group(4)) ? null : dp.parse(m.group(4)));
                    } else {
                        pr.addError("Error parsing tyre durability stats... regexp did not match: " + row.getCell(3).getTextContent().trim(), null);
                    }

                    // temp
                    String temp = row.getCell(4).getTextContent().trim();
                    stint.setAvgTemp(dp.parse(temp.substring(0, temp.indexOf('°'))));
                    // Humidity
                    String hum = row.getCell(5).getTextContent().trim();
                    stint.setAvgHum(dp.parse(hum.substring(0, hum.indexOf('%'))));
                    // fuel
                    String fuel = row.getCell(6).getTextContent().trim();
                    stint.setFuelLeft(dp.parse(fuel.substring(0, fuel.indexOf('l'))));
                    stint.setFuelStart(fuelStart);
                    String refuel = row.getCell(7).getTextContent().trim();
                    if (refuel.contains(" lts")) {
                        stint.setRefueledTo(Integer.valueOf(refuel.substring(0, refuel.indexOf(" lts"))));
                        fuelStart = stint.getRefueledTo().doubleValue();
                    } else {
                        fuelStart = stint.getFuelLeft();
                    }
                    // time
                    String time = row.getCell(8).getTextContent().trim();
                    if (!"-".equals(time)) {
                        stint.setPitTime(Integer.valueOf(time.replaceAll("[\\.,s]", "")));
                    }
                    race.getStints().add(stint);
                }
            } else {
                pr.addError("Stints table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing stints table", e);
        }
    }

    private void parseTableLaps(ParsingResult pr, HtmlTable content, NormalizedRace race) {
        try {
            // we can't use "Voltas" as the table header because stints also have the same header
            HtmlTable lapsTable = content.getFirstByXPath(".//th[contains(text(),'Voltas') and @colspan=10]/ancestor::table[1]");
            if (lapsTable != null) {
                RaceStatus status = RaceStatus.UNKNOWN;
                double avgTemp = 0;
                double avgHum = 0;
                int lastLap = 0;
                for (int i = 2; i < lapsTable.getRowCount(); i++) {
                    HtmlTableRow row = lapsTable.getRow(i);
                    NormalizedLap lap = new NormalizedLap();
                    race.getLaps().add(lap);
                    lap.setNumber(Integer.parseInt(row.getCell(0).getTextContent().trim()));
                    lap.setTime(parseTime(row.getCell(1).getTextContent().trim()));
                    lap.setPosition(Integer.parseInt(row.getCell(2).getTextContent().trim()));
                    lap.setTyre(Tyre.determineTyre(row.getCell(3).getTextContent().trim()));
                    lap.setWeather(Weather.determineWeather(row.getCell(4).getTextContent().trim()));
                    String temp = row.getCell(5).getTextContent().trim();
                    lap.setTemperature(Integer.valueOf(temp.substring(0, temp.indexOf('°'))));
                    String hum = row.getCell(6).getTextContent().trim();
                    lap.setHumidity(Integer.valueOf(hum.substring(0, hum.indexOf('%'))));
                    lap.setEvents(row.getCell(9).getTextContent().trim());
                    if (lap.getEvents().contains("Car problem") && status == RaceStatus.UNKNOWN) {
                        status = RaceStatus.CAR_PROBLEM;
                    }
                    avgTemp += lap.getTemperature();
                    avgHum += lap.getHumidity();
                    if (i > 2 && lastLap == 0 && (lap.getTime() == null || lap.getTime() <= 0)) {
                        lastLap = lap.getNumber();
                        status = RaceStatus.DROPPED_OUT;
                    }
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
            } else {
                pr.addError("Laps table not found!", null);
            }
        } catch (Exception e) {
            pr.addError("Error parsing laps table", e);
        }
    }

    public void parseText(ParsingResult result, String managerName, String content, String tool, String url) {
        logger.info("Parsing text-based telemetry data for manager " + managerName);

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

            // parse Tyre Supplier
            parseTextTyreSupplier(result, reader, race);

            // parse fuel
            parseTextFuel(result, reader, race);

            // parse Stints
            parseTextStints(result, reader, race);

            // parse Car
            parseTextCar(result, reader, race);

            // parse Driver
            parseTextDriver(result, reader, race);

            // parse Technical Director
            parseTextTechDirector(result, reader, race);

            // parse Laps
            parseTextLaps(result, reader, race);

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
                    String wet = m.group(5);
                    race.setRiskClearWet( wet != null ? Integer.valueOf(wet) : race.getRiskClear() );
                    race.setRiskMalfunction(Integer.valueOf(m.group(6)));
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
                if (line.startsWith("Ajuste usado:")) {
                    reader.readLine(); // skip header
                    String[] vals = reader.readLine().trim().split("\\|"); // thats the data
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

    private void parseTextTyreSupplier(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Fornecedor de Pneu:")) {
                    String[] vals = line.split("\\s+");
                    race.setSupplier(TyreSupplier.determineTyre(vals[3].trim()));
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Tyre supplier data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing tyre supplier data", e);
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
                    if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                        race.setStartingFuel(Integer.valueOf(val));
                    }
                    val = m.group(2).trim();
                    if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                        race.setFinishFuel(Integer.valueOf(val));
                    }
                    val = m.group(3).trim();
                    if (!"<não disponível>".equalsIgnoreCase(val) && !"<unknown>".equalsIgnoreCase(val)) {
                        race.setFuelUsed(dp.parse(val));
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

    private void parseTextCar(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Carro:")) {
                    // parse PHA first
                    reader.readLine(); // skip header
                    String[] vals = reader.readLine().trim().split("\\|");
                    PHA pha = new PHA();
                    pha.setP(Double.parseDouble(vals[0].trim()));
                    pha.setH(Double.parseDouble(vals[1].trim()));
                    pha.setA(Double.parseDouble(vals[2].trim()));

                    // find the parts
                    line = reader.readLine();
                    while (line != null) {
                        line = line.trim();
                        if (line.startsWith("Parte")) {
                            // parse car parts
                            Car start = new Car();
                            Car finish = new Car();
                            Car wear = new Car();
                            start.setPHA(pha);
                            finish.setPHA(pha);
                            wear.setPHA(pha);

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
                            return;
                        }
                        line = reader.readLine();
                    }
                    pr.addError("Car parts data not found", null);
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
            int level = 0;
            int i = 0;
            line = line.endsWith("|") ? line+" " : line; // have to do this or it won't split correctly
            String[] vals = line.split("\\|");
            if (vals.length == 4) {
                String[] l = vals[0].trim().split("\\s+");
                level = Integer.parseInt(l[l.length - 1]);
                i = 1;
            } else {
                level = Integer.parseInt(vals[1].trim());
                i = 2;
            }

            String wear = vals[i++].trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[0] = new CarPart();
                parts[0].setName(name);
                parts[0].setLevel(level);
                parts[0].setWear(Double.parseDouble(wear));
            }

            wear = vals[i++].trim().replaceAll("%", "");
            if (!wear.isEmpty()) {
                parts[1] = new CarPart();
                parts[1].setName(name);
                parts[1].setLevel(level);
                parts[1].setWear(Double.parseDouble(wear));
            }

            wear = vals[i++].trim().replaceAll("%", "");
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
                    Driver f = new Driver();
                    race.setDriverStart(s);
                    race.setDriverFinish(f);

                    line = reader.readLine();
                    while (line != null && !line.trim().isEmpty()) {
                        String[] cells = line.split(":");
                        String attr = cells[0].trim();
                        String val = cells[1].trim();
                        if ("Nome".equalsIgnoreCase(attr)) {
                            s.setName(val);
                            f.setName(val);
                        } else if ("Total".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setOverall(vals[0]);
                            f.getAttributes().setOverall(vals[1]);
                        } else if ("Concentração".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setConcentration(vals[0]);
                            f.getAttributes().setConcentration(vals[1]);
                        } else if ("Talento".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setTalent(vals[0]);
                            f.getAttributes().setTalent(vals[1]);
                        } else if ("Agressividade".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setAggressiveness(vals[0]);
                            f.getAttributes().setAggressiveness(vals[1]);
                        } else if ("Experiência".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setExperience(vals[0]);
                            f.getAttributes().setExperience(vals[1]);
                        } else if ("Conhec. Técnico".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setTechInsight(vals[0]);
                            f.getAttributes().setTechInsight(vals[1]);
                        } else if ("Resistência".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setStamina(vals[0]);
                            f.getAttributes().setStamina(vals[1]);
                        } else if ("Carisma".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setCharisma(vals[0]);
                            f.getAttributes().setCharisma(vals[1]);
                        } else if ("Motivação".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setMotivation(vals[0]);
                            f.getAttributes().setMotivation(vals[1]);
                        } else if ("Reputação".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setReputation(vals[0]);
                            f.getAttributes().setReputation(vals[1]);
                        } else if ("Peso".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setWeight(vals[0]);
                            f.getAttributes().setWeight(vals[1]);
                        } else if ("Idade".equalsIgnoreCase(attr)) {
                            int[] vals = parseDriverAttr(val);
                            s.getAttributes().setAge(vals[0]);
                            f.getAttributes().setAge(vals[1]);
                        } else if ("Pistas Favoritas".equalsIgnoreCase(attr)) {
                            String[] tracks = val.replaceAll("[\\[\\]]", "").split(",");
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
                                f.setFavoriteTracks(favorites);
                            }
                        }
                        line = reader.readLine();
                    }
                    return;
                }
                line = reader.readLine();
            }
            pr.addError("Driver data not found", null);
        } catch (Exception e) {
            pr.addError("Error parsing driver data", e);
        }
    }

    private void parseTextTechDirector(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null && !line.trim().equals("Voltas:")) {
                line = line.trim();
                if (line.startsWith("Diretor Técnico")) {
                    line = reader.readLine();
                    if (line != null && line.trim().equalsIgnoreCase("Não contratado")) {
                        race.setTdStatus(TDStatus.NOT_HIRED);
                    } else {
                        TechDirector s = new TechDirector();
                        race.setTechDirector(s);
                        race.setTdStatus(TDStatus.HIRED);
                        while (line != null && !line.trim().isEmpty()) {
                            String[] cells = line.split(":");
                            String attr = cells[0].trim();
                            String val = cells[1].trim();
                            if ("Nome".equalsIgnoreCase(attr)) {
                                s.setName(val);
                            } else if ("Total".equalsIgnoreCase(attr)) {
                                s.getAttributes().setOverall(Integer.parseInt(val));
                            } else if ("Liderança".equalsIgnoreCase(attr)) {
                                s.getAttributes().setLeadership(Integer.parseInt(val));
                            } else if ("P&D Mecânico".equalsIgnoreCase(attr)) {
                                s.getAttributes().setRdMech(Integer.parseInt(val));
                            } else if ("P&D Eletrônico".equalsIgnoreCase(attr)) {
                                s.getAttributes().setRdElect(Integer.parseInt(val));
                            } else if ("P&D Aerodinâmico".equalsIgnoreCase(attr)) {
                                s.getAttributes().setRdAero(Integer.parseInt(val));
                            } else if ("Experiência".equalsIgnoreCase(attr)) {
                                s.getAttributes().setExperience(Integer.parseInt(val));
                            } else if ("Coord. de Pit".equalsIgnoreCase(attr)) {
                                s.getAttributes().setPitCoord(Integer.parseInt(val));
                            } else if ("Motivação".equalsIgnoreCase(attr)) {
                                s.getAttributes().setMotivation(Integer.parseInt(val));
                            } else if ("Idade".equalsIgnoreCase(attr)) {
                                s.getAttributes().setAge(Integer.parseInt(val));
                            }
                            line = reader.readLine();
                        }
                    }
                    return;
                }
                line = reader.readLine();
            }
            if (line.trim().equalsIgnoreCase("Voltas:")) {
                // this is not an error because old reports did not have TD information
                race.setTdStatus(TDStatus.UNKNOWN);
            }
        } catch (Exception e) {
            pr.addError("Error parsing TD data", e);
        }
    }

    private void parseTextStints(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("Stints:")) {
                    reader.readLine(); // skip header

                    // parse stints
                    Double fuelStart = race.getStartingFuel().doubleValue();
                    line = reader.readLine();
                    int c = 1;
                    while (line != null && !line.trim().isEmpty() ) {
                        NormalizedStint stint = new NormalizedStint();
                        stint.setNumber(c++);
                        String[] cells = line.split("\\|");
                        
                        String[] laps = cells[0].trim().split("-");
                        stint.setInitialLap(Integer.parseInt(laps[0].trim()));
                        stint.setFinalLap(Integer.parseInt(laps[1].trim()));
                        if (stint.getInitialLap() > stint.getFinalLap()) {
                            // error, unable to continue parsing stints
                            return;
                        }
                        stint.setPitReason(cells[1].trim());
                        stint.setTyre(Tyre.determineTyre(cells[2].trim().replaceAll(":", "")));

                        // tyres
                        Matcher m = tyreP.matcher(cells[3].trim());
                        if (m.matches()) {
                            stint.setTyreLeft(Integer.parseInt(m.group(1)));
                            stint.setTyreUsed("Infinity".equalsIgnoreCase(m.group(2)) || "NaN".equalsIgnoreCase(m.group(2)) ? null : dp.parse(m.group(2)));
                            stint.setTyreNoBad("Infinity".equalsIgnoreCase(m.group(3)) || "NaN".equalsIgnoreCase(m.group(3)) ? null : dp.parse(m.group(3)));
                            stint.setTyreDurability("Infinity".equalsIgnoreCase(m.group(4)) || "NaN".equalsIgnoreCase(m.group(4)) ? null : dp.parse(m.group(4)));
                        } else {
                            pr.addError("Error parsing tyre durability stats... regexp did not match: " + cells[3].trim(), null);
                        }

                        // temp
                        String temp = cells[4].trim();
                        stint.setAvgTemp(dp.parse(temp.replaceAll("[°C\\s]", "")));
                        // Humidity
                        String hum = cells[5].trim();
                        stint.setAvgHum(dp.parse(hum.replaceAll("%","")));
                        // fuel
                        String fuel = cells[6].trim();
                        stint.setFuelLeft(dp.parse(fuel.substring(0, fuel.indexOf('l'))));
                        stint.setFuelStart(fuelStart);
                        String refuel = cells[7].trim();
                        if (refuel.contains(" lts")) {
                            stint.setRefueledTo(Integer.valueOf(refuel.substring(0, refuel.indexOf(" lts"))));
                            fuelStart = stint.getRefueledTo().doubleValue();
                        } else {
                            fuelStart = stint.getFuelLeft();
                        }
                        // time
                        String time = cells[8].trim();
                        if (!"-".equals(time)) {
                            stint.setPitTime(Integer.valueOf(time.replaceAll("[\\.,s]", "")));
                        }
                        race.getStints().add(stint);
                        line = reader.readLine();
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

    private void parseTextLaps(ParsingResult pr, BufferedReader reader, NormalizedRace race) {
        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("# | Tempo | Pos")) {
                    // parse laps
                    RaceStatus status = RaceStatus.UNKNOWN;
                    double avgTemp = 0;
                    double avgHum = 0;
                    int lastLap = 0;

                    line = reader.readLine();
                    while (line != null && !line.trim().isEmpty() ) {
                        String[] cells = line.split("\\|");
                        
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
                        lap.setHumidity(Integer.valueOf(hum.replaceAll("[%\\s]","")));
                        lap.setEvents(cells[9].trim());
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
            // 'm:ss:SSS'
            String[] parts = timeStr.split(":");
            return (Integer.parseInt(parts[0]) * 60000) + (Integer.parseInt(parts[1]) * 1000) + Integer.parseInt(parts[2]);
        }
        return null;
    }

}