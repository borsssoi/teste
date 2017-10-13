package org.gproman.scrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Pit;
import org.gproman.model.race.RaceReport;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlStrong;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class RaceAnalysisWorker
        implements
        Callable<RaceReport> {
    private static final Logger logger = LoggerFactory.getLogger( RaceAnalysisWorker.class );

    private final HtmlPage      raceAnalysis;

    public RaceAnalysisWorker(HtmlPage raceAnalysis) {
        this.raceAnalysis = raceAnalysis;
    }

    @Override
    public RaceReport call() {
        try { 
            return parsePage( raceAnalysis );
        } catch( Exception e ) {
            final String fileName = "RaceAnalysis_"+System.currentTimeMillis()+".html";
            logger.error( "Error parsing past setup page: "+fileName, e );
            savePageToFile( fileName );
        }
        return null;
    }

    private void savePageToFile(final String fileName)  {
        logger.error( "Saving downloaded page to file: "+fileName );
        FileOutputStream file = null;
        try {
            file = new FileOutputStream( fileName );
            file.write( raceAnalysis.asXml().getBytes() );
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
        logger.info( "Parsing Race Analysis report..." );
        RaceReport report = new RaceReport();

        // find the race and season numbers
        parseTitle(page, report );
        
        // parse race information
        parseQSettings( page, report.getRaceSettings() );
        report.setQualify1( null );
        report.setQualify2( null );
        logger.info( "Race settings: "+report.getRaceSettings() );
        
        // parse race risks
        parseRisks( page, report );
        
        //parse race Energy
        parseEnergy( page, report );
        
        // parse race laps
        parseLaps( page, report );
        
        // parse pits
        parsePits( page, report );
        
        logger.info( "Race Analysis report parsed." );
        return report;
    }

    private void parseTitle(HtmlPage page,
                            RaceReport report) {
        // Parse season and race info
        String title = page.getTitleText();
        Matcher tm = Pattern.compile( ".*S (\\d+) R (\\d+) .*" ).matcher( title );
        if ( tm.matches() ) {
            Integer seasonNumber = Integer.valueOf( tm.group( 1 ) );
            Integer raceNumber = Integer.valueOf( tm.group( 2 ) );
            logger.info( "Report for Season " + seasonNumber + " Race " + raceNumber );
            report.setSeason( seasonNumber );
            report.setRace( raceNumber );
        } else {
            logger.error( "Failed to parse title '"+title+"'" );
        }
    }

    private void parseRisks(HtmlPage page,
                            RaceReport report ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Risks used')]/ancestor::table" );
        HtmlTableRow raceRisks = table.getRow( 2 );
        int index = 0;
        report.setRiskOvertake( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        report.setRiskDefend( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        report.setRiskClear( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        if( raceRisks.getCells().size() >= 5 ) {
            report.setRiskClearWet( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        }
        report.setRiskMalfunction( Integer.parseInt( raceRisks.getCell( index++ ).getTextContent().trim() ) );
        logger.info( "Risks parsed: "+report.getRiskOvertake()+" / "+report.getRiskDefend()+" / "+report.getRiskClear()+ " / "+report.getRiskClearWet()+ " / "+report.getRiskMalfunction() );
    }
    
    private void parseEnergy(HtmlPage page,
                            RaceReport report ) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Driver energy')]/ancestor::table" );
        HtmlTableRow raceEnergy= table.getRow( 1 );
        report.setEnergiaInicial(raceEnergy.getCell( 0 ).getTextContent().trim() );
        report.setEnergiaFinal(raceEnergy.getCell( 2 ).getTextContent().trim() );
    }

    private void parseQSettings(HtmlPage page,
                                CarSettings race) {
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(), 'Setup used')]/ancestor::table" );
        // Race setup
        if( table != null && table.getRowCount() > 2 ) {
            parseSettings( table.getRow( 2 ).getCells(), 0, race );
        } else {
            race.setFrontWing( 0 );
            race.setRearWing( 0 );
            race.setEngine( 0 );
            race.setBrakes( 0 );
            race.setGear( 0 );
            race.setSuspension( 0 );
        }
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
                lap.setNumber( Integer.parseInt( cells.get( 0 ).asText().trim() ) );
                String lapTime = cells.get( 1 ).getTextContent().trim();
                if( "Start Accident".equalsIgnoreCase( lapTime ) ) {
                    lap.setTime( 0 );
                    lap.setEvents( lapTime );
                } else {
                    lap.setTime( parseTime( lapTime ) );
                    lap.setEvents( cells.get( 7 ).asText().trim() );
                }
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
        HtmlDivision div = page.getFirstByXPath( "//p[contains( text(), 'Start fuel')]/ancestor::div" );
        if( div == null ) {
            logger.info( "No pit and fuel info found." );
            return;
        }
        HtmlStrong startingFuel = div.getFirstByXPath( ".//strong[contains( text(), 'liters')]" );
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
}