package org.gproman.scrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Comment;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Pit;
import org.gproman.model.race.Practice;
import org.gproman.model.race.RaceReport;
import org.gproman.model.race.StartingRisk;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.util.CommentsTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class PastSetupWorker
        implements
        Callable<RaceReport> {
    private static final Logger logger = LoggerFactory.getLogger( PastSetupWorker.class );

    private final HtmlPage      pastSetupPage;

    public PastSetupWorker(HtmlPage pastSetupPage) {
        this.pastSetupPage = pastSetupPage;
    }

    @Override
    public RaceReport call() {
        try { 
            return parsePage( pastSetupPage );
        } catch( Exception e ) {
            final String fileName = "PastSetup_"+System.currentTimeMillis()+".html";
            logger.error( "Error parsing past setup page: ", e );
            savePageToFile( fileName );
        }
        return null;
    }

    private void savePageToFile(final String fileName)  {
        logger.error( "Saving downloaded page to file: "+fileName );
        FileOutputStream file = null;
        try {
            file = new FileOutputStream( fileName );
            file.write( pastSetupPage.asXml().getBytes() );
        } catch ( Exception e ) {
            logger.error( "Error saving page to file "+fileName, e );
        } finally { 
            if( file != null ) {
                try {
                    file.close();
                } catch ( IOException e ) {
                    // intentionally left blank... nothing to do.
                }
            }
        }
    }

    public RaceReport parsePage(HtmlPage page) {
        logger.info( "Parsing Past Race report..." );
        RaceReport report = new RaceReport();

        // find the race and season numbers
        if( ! parseTitle(page, report ) ) {
            return null;
        }
        
        // parse the forecast
        Forecast[] forecast = parseForecast( page );
        
        // parse practice laps
        Practice practice = parsePractice( page, forecast[0] );
        report.setPractice( practice );
        
        // parse race information
        parseQTimes( page, report.getQualify1().getLap(), report.getQualify2().getLap() );
        parseQSettings( page, report.getQualify1().getLap(), report.getQualify2().getLap(), report.getRaceSettings() );
        populateWeather(forecast[0], report.getQualify1().getLap() );
        populateWeather(forecast[1], report.getQualify2().getLap() );
        
        logger.info( "Qualify 1 parsed: "+report.getQualify1() );
        logger.info( "Qualify 2 parsed: "+report.getQualify2() );
        logger.info( "Race settings: "+report.getRaceSettings() );
        
        
       // parse
        parseEnergy(page, report );
        
        // parse race risks
        parseRisks( page, report );
        
        // parse driver
        report.setDriver( parseDriver( page ) );
        
        // parse car
        Car start = new Car();
        parseCarPHA( page, start );
        Car finish = start.clone();
        parseCarParts( page, start, finish );
        report.setCarStart( start );
        report.setCarFinish( finish );
        logger.info( "Parsed car at start   = "+start );
        logger.info( "Parsed car at the end = "+finish );
        
        // parse race laps
        parseLaps( page, report );
        
        // parse pits
        parsePits( page, report );
        
        //        parseForecast( practice, practicePage );
        logger.info( "Past race report parsed." );
        return report;
    }

    private void parseQTimes(HtmlPage page,
                             Lap q1lap,
                             Lap q2lap ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Lap times')]/ancestor::table" );
        HtmlTableRow times = table.getRow( 2 );
        HtmlTableCell q1time = times.getCell( 0 );
        parseQTime( q1lap, q1time );
        HtmlTableCell q2time = times.getCell( 1 );
        parseQTime( q2lap, q2time );
    }

    private void parseRisks(HtmlPage page,
                            RaceReport report ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Risks used')]/ancestor::table" );
        HtmlTableRow starting = table.getRow( 2 );
        report.setRiskStarting( StartingRisk.determineRisk( starting.getCell( 0 ).getTextContent().trim() ) );
        HtmlTableRow raceRisks = table.getRow( 4 );
        int index = 0;
        report.setRiskOvertake( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        report.setRiskDefend( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        report.setRiskClear( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        if( raceRisks.getCells().size() >= 5 ) {
            report.setRiskClearWet( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        }
        report.setRiskMalfunction( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        logger.info( "Risks parsed: "+report.getRiskStarting()+" / "+report.getRiskOvertake()+" / "+report.getRiskDefend()+" / "+report.getRiskClear()+ " / "+report.getRiskClearWet()+ " / "+report.getRiskMalfunction() );
    }
    
    private void parseEnergy(HtmlPage page,
                            RaceReport report ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Driver energy')]/ancestor::table" );
        HtmlTableRow raceEnergy= table.getRow( 1 );
        report.setEnergiaInicial(raceEnergy.getCell( 0 ).getTextContent().trim() );
        report.setEnergiaFinal(raceEnergy.getCell( 2 ).getTextContent().trim() );
    }

    private Driver parseDriver(HtmlPage page ) {
        Driver driver = new Driver();
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Driver attributes')]/ancestor::table" );
        HtmlTableRow headerRow = table.getRow( 1 );
        HtmlTableRow valueRow = table.getRow( 2 );
        
        HtmlAnchor href = (HtmlAnchor) valueRow.getCell( 0 ).getHtmlElementsByTagName( "a" ).get( 0 );
        driver.setName( href.getTextContent().trim() );
        driver.setNumber( Integer.parseInt( href.getHrefAttribute().substring( href.getHrefAttribute().indexOf( '=' )+1 ) ) );
        
        DriverAttributes attributes = driver.getAttributes();
        List<HtmlTableCell> headers = headerRow.getCells();
        List<HtmlTableCell> values = valueRow.getCells();
        for( int i = 1; i < values.size(); i++ ) {
            try {
                String header = headers.get(i).getTextContent().trim();
                int value = Integer.parseInt( values.get(i).getTextContent().trim() );
                if( "OA".equalsIgnoreCase(header) ) {
                    attributes.setOverall( value );
                } else if( "Con".equalsIgnoreCase(header) ) {
                    attributes.setConcentration( value );
                } else if( "Tal".equalsIgnoreCase(header) ) {
                    attributes.setTalent( value );
                } else if( "Agr".equalsIgnoreCase(header) ) {
                    attributes.setAggressiveness( value );
                } else if( "Exp".equalsIgnoreCase(header) ) {
                    attributes.setExperience( value );
                } else if( "TeI".equalsIgnoreCase(header) ) {
                    attributes.setTechInsight( value );
                } else if( "Sta".equalsIgnoreCase(header) ) {
                    attributes.setStamina( value );
                } else if( "Cha".equalsIgnoreCase(header) ) {
                    attributes.setCharisma( value );
                } else if( "Mot".equalsIgnoreCase(header) ) {
                    attributes.setMotivation( value );
                } else if( "Rep".equalsIgnoreCase(header) ) {
                    attributes.setReputation( value );
                } else if( "Wei".equalsIgnoreCase(header) ) {
                    attributes.setWeight( value );
                } else if( "Age".equalsIgnoreCase(header) ) {
                    attributes.setAge( value );
                }
            } catch (Exception e) {
                logger.error("Error parsing driver attribute '"+headers.get(i).getTextContent().trim()+"' with value '"+values.get(i).getTextContent().trim()+"'");
            }
        }
        logger.info( "Driver parsed: "+driver);
        return driver;
    }

    private void parseCarPHA(HtmlPage page, Car car ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Overall car character')]/ancestor::table[1]" );
        HtmlTableRow row = table.getRow( 2 );
        
        car.setPower( Integer.parseInt( row.getCell( 0 ).getTextContent().trim() ) );
        car.setHandling( Integer.parseInt( row.getCell( 1 ).getTextContent().trim() ) );
        car.setAcceleration( Integer.parseInt( row.getCell( 2 ).getTextContent().trim() ) );
    }

    private void parseCarParts(HtmlPage page, Car start, Car finish ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Car parts level')]/ancestor::table" );
        HtmlTableRow level = table.getRow( 2 );
        HtmlTableRow wearStart = table.getRow( 4 );
        HtmlTableRow wearFinish = table.getRow( 6 );
        
        CarPart[] parts = new CarPart[Car.PARTS_COUNT];
        for( int i = 0; i < parts.length; i++ ) {
            CarPart partS = new CarPart();
            CarPart partF = new CarPart();
            partS.setLevel( Integer.parseInt(  level.getCell( i ).getTextContent().trim() ) );
            partF.setLevel( partS.getLevel() );
            String wearS = wearStart.getCell( i ).getTextContent().trim();
            partS.setWear( Integer.parseInt( wearS.substring( 0, wearS.indexOf( '%' ) ) ) );
            String wearF = wearFinish.getCell( i ).getTextContent().trim();
            partF.setWear( Integer.parseInt( wearF.substring( 0, wearF.indexOf( '%' ) ) ) );
            start.setPart( i, partS );
            finish.setPart( i, partF );
        }
    }

    private void parseQTime(Lap qlap,
                            HtmlTableCell qtime) {
        try {
            qlap.setTime( parseTime( qtime.getTextContent().trim() ) );
        } catch ( ParseException e ) {
            logger.error( "Unable to parse Q time: "+qtime.getTextContent(), e );
        }
    }

    private void parseQSettings(HtmlPage page,
                                Lap q1lap,
                                Lap q2lap,
                                CarSettings race) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Setups used')]/ancestor::table" );
        // Q1 setup
        parseSettings( table.getRow( 2 ).getCells(), 1, q1lap.getSettings() );
        // Q2 setup
        parseSettings( table.getRow( 3 ).getCells(), 1, q2lap.getSettings() );
        // Race setup
        parseSettings( table.getRow( 4 ).getCells(), 1, race );
    }

    private boolean parseTitle(HtmlPage page,
                               RaceReport report) {
        // Parse season and race info
        String title = page.getTitleText();
        Matcher tm = Pattern.compile( ".*Season (\\d+) - Race (\\d+) .*" ).matcher( title );
        if ( tm.matches() ) {
            Integer seasonNumber = Integer.valueOf( tm.group( 1 ) );
            Integer raceNumber = Integer.valueOf( tm.group( 2 ) );
            logger.info( "Report for Season " + seasonNumber + " Race " + raceNumber );
            report.setSeason( seasonNumber );
            report.setRace( raceNumber );
            return true;
        } else {
            logger.error( "Failed to parse title '"+title+"'" );
            return false;
        }
    }

    /**
     * Parses practice laps data
     * @param page
     * @param forecast 
     * @return
     */
    private Practice parsePractice(HtmlPage page, Forecast forecast) {
        // Parse practice info
        logger.info( "Parsing practice info..." );
        Practice practice = new Practice();
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(),'Practice laps data')]/ancestor::table//table" );

        if( table != null ) {
            Pattern commentsFinder = Pattern.compile( ".*?innerHTML='(.+?)';.*" );
            Pattern commentsSplitter = Pattern.compile( "<br>(.+?)(?=<br>|$|\")" );
            for ( HtmlTableRow tr : table.getRows() ) {
                List<HtmlTableCell> cells = tr.getCells();
                if ( cells.size() != 12 ) {
                    // skip header rows
                    continue;
                }

                Lap lap = new Lap();
                try {
                    lap.setNumber( Integer.parseInt( cells.get( 0 ).getTextContent().trim() ) );
                    lap.setTime( parseTime( cells.get( 1 ).getTextContent().trim() ) );
                    lap.setMistake( parseTime( cells.get( 2 ).getTextContent().trim() ) );
                    lap.setNetTime( parseTime( cells.get( 3 ).getTextContent().trim() ) );
                    parseSettings( cells, 4, lap.getSettings() );

                    populateWeather( forecast, lap );

                    // the following pattern extracts the list of comments for the given lap
                    Matcher matcher = commentsFinder.matcher( ((HtmlInput) cells.get( 11 ).getFirstChild()).getOnClickAttribute() );
                    if( matcher.matches() ) {
                        // all the comments for the lap
                        String comments = matcher.group( 1 );
                        List<Comment> cl = new ArrayList<Comment>();

                        // the following pattern extracts the individual comment for each setting
                        Matcher cm = commentsSplitter.matcher( comments );
                        while ( cm.find() ) {
                            // specific comment
                            String comment = cm.group(1);
                            Comment normalizedComment = CommentsTranslator.getTranslation( comment );
                            if ( normalizedComment != null ) {
                                cl.add( normalizedComment );
                            } else {
                                if ( !comment.contains( "I am satisfied" ) && !comment.contains( "Estou satisfeito" ) ) {
                                    logger.error( "Error parsing comment. Pre-defined comment not found for string '" + comment + "'" );
                                }
                            }
                        }

                        lap.setComments( cl.toString() );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error parsing lap '" + tr.asText() + "'", e );
                }
                logger.info( "Lap parsed: " + lap );

                practice.getLaps().add( lap );
            }
            logger.info( "Practice parsing finished." );
        } else {
            logger.info( "Not practice lap information found." );
        }
        return practice;
    }

    private void populateWeather(Forecast forecast,
                                 Lap lap) {
        lap.setWeather( forecast.weather );
        lap.setTemperature( forecast.temperature );
        lap.setHumidity( forecast.humidity );
    }

    private void parseSettings(List<HtmlTableCell> cells,
                               int offset,
                               CarSettings settings) {
        settings.setFrontWing( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setRearWing( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setEngine( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setBrakes( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setGear( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setSuspension( Integer.parseInt( cells.get( offset++ ).getTextContent().trim() ) );
        settings.setTyre( Tyre.determineTyre( cells.get( offset++ ).getTextContent().trim() ) );
    }

    private Forecast[] parseForecast(HtmlPage page) {
        Forecast[] ret = new Forecast[2];
        HtmlTable forecast = page.getFirstByXPath( "//th[contains(text(),'Sessions weather')]/ancestor::table" );
        HtmlTableRow row = forecast.getRow( 2 );
        Pattern fp = Pattern.compile( ".*Temp: (\\d+).*Humidity: (\\d+).*", Pattern.MULTILINE|Pattern.DOTALL );
        ret[0] = parseForcastCell( row.getCell( 0 ), fp );
        ret[1] = parseForcastCell( row.getCell( 1 ), fp );
        return ret;
    }

    private Forecast parseForcastCell( HtmlTableCell cell,
                                       Pattern fp) {
        Matcher fm = fp.matcher( cell.asText() );
        if( fm.matches() ) {
            Forecast ret = new Forecast();
            HtmlImage weather = cell.getFirstByXPath( "./img" );
            ret.weather = Weather.determineWeather( weather.getAttribute( "title" ) );
            ret.temperature = Integer.parseInt( fm.group( 1 ) );
            ret.humidity = Integer.parseInt( fm.group( 2 ) );
            return ret;
        } else {
            logger.error( "Unable to parse weather forecast for: '"+cell.asXml()+"'" );
        }
        return null;
    }

    private int parseTime(String timeStr) throws ParseException {
        if ( "-".equals( timeStr ) || "".equals( timeStr ) ) {
            return 0;
        } else if ( timeStr.indexOf( ':' ) > 0 ) {
            // 'm:ss.SSS'
            String[] parts = timeStr.split( "[:\\.s]" );
            return (Integer.parseInt( parts[0] ) * 60000) + (Integer.parseInt( parts[1] ) * 1000) + Integer.parseInt( parts[2] );
        } else {
            // 'ss.SSS'
            String[] parts = timeStr.split( "[\\.s]" );
            return (Integer.parseInt( parts[0] ) * 1000) + Integer.parseInt( parts[1] );
        }
    }
    
    /**
     * Parses race laps
     */
    private void parseLaps(HtmlPage page, RaceReport report ) {
        // Parse practice info
        logger.info( "Parsing race laps..." );
        HtmlTable table = page.getFirstByXPath( "//b[contains(text(), 'Events')]/ancestor::table" );
        
        boolean skipFirst = true;
        for ( HtmlTableRow tr : table.getRows() ) {
            if ( skipFirst ) {
                // skip header row
                skipFirst = false;
                continue;
            }
            List<HtmlTableCell> cells = tr.getCells();

            Lap lap = new Lap();
            try {
                String lapTime = cells.get( 1 ).getTextContent().trim();
                if( "Start Accident".equalsIgnoreCase( lapTime ) ) {
                    lap.setTime( 0 );
                    lap.setEvents( lapTime );
                } else {
                    lap.setTime( parseTime( lapTime ) );
                    lap.setEvents( cells.get( 7 ).asText().trim() );
                }
                lap.setNumber( Integer.parseInt( cells.get( 0 ).asText().trim() ) );
                lap.setPosition( Integer.parseInt(  cells.get( 2 ).asText().trim() ) );
                lap.getSettings().setTyre( Tyre.determineTyre( cells.get( 3 ).asText().trim() ) );
                lap.setWeather( Weather.determineWeather( cells.get( 4 ).asText().trim() ) );
                lap.setTemperature( Integer.parseInt( cells.get( 5 ).asText().trim().replaceAll( "\\D", "" ) ) );
                String hum = cells.get( 6 ).asText().trim();
                lap.setHumidity( Integer.parseInt( hum.substring( 0, hum.indexOf( '%' ) ) ) );
                
            } catch ( Exception e ) {
                logger.error( "Error parsing lap '" + tr.asText() + "'", e );
            }
            logger.info( "Lap parsed: " + lap );

            report.getLaps().add( lap );
        }
        logger.info( "Race laps parsed." );
    }
 
    private void parsePits(HtmlPage page, RaceReport report ) {
        // Parse pits
        logger.info( "Parsing race information and pits..." );
        HtmlDivision div = page.getFirstByXPath( "//th[contains( text(), 'Lap times')]/ancestor::div" );
        if( div == null ) {
            logger.info( "No pit and fuel info found." );
            return;
        }
        HtmlBold startingFuel = div.getFirstByXPath( ".//b[contains( text(), 'liters')]" );
        if( startingFuel != null ) {
            String fs = startingFuel.getTextContent().trim();
            // due to some funky white space character, have to use this split to properly extract the number
            report.setStartingFuel( Integer.valueOf( fs.split( "\\W" )[0] ) );
        }
        
        HtmlBold tyresEnd = div.getFirstByXPath( ".//p[contains( text(), 'Tyres condition after finish')]/b" );
        if( tyresEnd != null ) {
            String ts = tyresEnd.asText();
            report.setFinishTyre( Integer.valueOf( ts.substring( 0, ts.indexOf( '%' ) ) ) );
        }
        
        HtmlBold fuelEnd = div.getFirstByXPath( ".//p[contains( text(), 'Fuel left in the car after finish')]/b" );
        if( fuelEnd != null ) {
            String fe = fuelEnd.asText();
            report.setFinishFuel( Integer.valueOf( fe.split( "\\W" )[0] ) );
        }
        
        logger.info( String.format( "Fuel at start=%s end=%s / tyres at end=%s%%", 
                                    report.getStartingFuel() != null ? report.getStartingFuel().toString() : "<unknown>", 
                                    report.getFinishFuel() != null ? report.getFinishFuel().toString() : "<unknown>", 
                                    report.getFinishTyre() != null ? report.getFinishTyre().toString() : "<unknown>" ) );
        
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Pitstop reason')]/ancestor::table" );
        if( table != null ) {
            Pattern pp = Pattern.compile( "Stop.*?(\\d+).*Lap.*?(\\d+).*", Pattern.DOTALL|Pattern.MULTILINE );
            boolean skipFirst = true;
            for ( HtmlTableRow tr : table.getRows() ) {
                if ( skipFirst ) {
                    // skip header row
                    skipFirst = false;
                    continue;
                }
                List<HtmlTableCell> cells = tr.getCells();

                Matcher pm = pp.matcher( cells.get( 0 ).asText().trim() );
                if( pm.matches() ) {
                    Pit pit = new Pit();
                    pit.setNumber( Integer.valueOf( pm.group( 1 ) ) );
                    pit.setLap( Integer.valueOf( pm.group( 2 ) ) );
                    pit.setReason( cells.get( 1 ).asText().trim() );
                    String tyres = cells.get( 2 ).asText().trim();
                    pit.setTyres( Integer.valueOf( tyres.substring( 0, tyres.indexOf( '%' ) ) ) );
                    String fuel = cells.get( 3 ).asText().trim();
                    pit.setFuel( Integer.valueOf( fuel.substring( 0, fuel.indexOf( '%' ) ) ) );
                    String refill = cells.get( 4 ).asText().trim();
                    if( "No refill".equalsIgnoreCase( refill ) ) {
                        pit.setRefueledTo( null );
                    } else {
                        pit.setRefueledTo( Integer.valueOf( refill.split( "\\W" )[0] ) );
                    }
                    try {
                        pit.setTime( parseTime( cells.get( 5 ).asText().trim() ) );
                    } catch ( ParseException e ) {
                        logger.error( "Error parsing pit time for pit '" + tr.asText() + "'", e );
                    }
                    
                    logger.info( "Pit parsed: " + pit );

                    report.getPits().add( pit );
                } else {
                    logger.error( "Error parsing pit '" + tr.asText() + "'" );
                }
            }
        } else {
            logger.info( "No pit information found." );
        }
    }
    
    private static class Forecast { 
        public Weather weather;
        public int temperature;
        public int humidity;
    }
    
}