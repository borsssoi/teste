package org.gproman.scrapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.race.Comment;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.util.CommentsTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class PracticeWorker
        implements
        Callable<Practice> {
    public static final String   PRACTICE_URL_SUFFIX      = "/gb/Qualify.asp";

    private static final Logger logger = LoggerFactory.getLogger( PracticeWorker.class );

    private final HtmlPage      practicePage;

    public PracticeWorker(HtmlPage practicePage) {
        this.practicePage = practicePage;
    }

    @Override
    public Practice call() {
        return parsePage( practicePage );
    }

    public Practice parsePage(HtmlPage practicePage) {
        logger.info( "Parsing practice..." );
        Practice practice = new Practice();

        Forecast[] forecast = parseForecast( practicePage );

        Map<Integer, List<Comment>> comments = parseLapComments( practicePage );
        HtmlTable table = practicePage.getFirstByXPath( "//th[starts-with(text(),'Practice laps data')]/ancestor::table" );

        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            if ( cells.size() != 12 ) {
                // skip header rows
                continue;
            }

            Lap lap = new Lap();
            lap.setWeather( forecast[0].weather );
            lap.setTemperature( forecast[0].temperature );
            lap.setHumidity( forecast[0].humidity );
            try {
                lap.setNumber( Integer.parseInt( cells.get( 0 ).getTextContent().trim() ) );
                lap.setTime( parseTime( cells.get( 1 ).getTextContent().trim() ) );
                lap.setMistake( parseTime( cells.get( 2 ).getTextContent().trim() ) );
                lap.setNetTime( parseTime( cells.get( 3 ).getTextContent().trim() ) );
                lap.getSettings().setFrontWing( Integer.parseInt( cells.get( 4 ).getTextContent().trim() ) );
                lap.getSettings().setRearWing( Integer.parseInt( cells.get( 5 ).getTextContent().trim() ) );
                lap.getSettings().setEngine( Integer.parseInt( cells.get( 6 ).getTextContent().trim() ) );
                lap.getSettings().setBrakes( Integer.parseInt( cells.get( 7 ).getTextContent().trim() ) );
                lap.getSettings().setGear( Integer.parseInt( cells.get( 8 ).getTextContent().trim() ) );
                lap.getSettings().setSuspension( Integer.parseInt( cells.get( 9 ).getTextContent().trim() ) );
                lap.getSettings().setTyre( Tyre.determineTyre( cells.get( 10 ).getTextContent().trim() ) );
                lap.setComments( comments.get( lap.getNumber() ).toString() );
            } catch ( Exception e ) {
                logger.error( "Error parsing lap '" + tr.asText() + "'", e );
            }
            logger.debug( "Lap parsed: " + lap );

            practice.getLaps().add( lap );
        }
        logger.info( "Practice parsed: " + practice );
        return practice;
    }

    private Forecast[] parseForecast(HtmlPage page) {
        Forecast[] ret = new Forecast[2];
        HtmlTable forecast = page.getFirstByXPath( "//th[contains(text(),'Practice / Qualify 1')]/ancestor::table" );
        HtmlTableRow row = forecast.getRow( 1 );
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

    

    private Map<Integer, List<Comment>> parseLapComments(HtmlPage practicePage) {
        Map<Integer, List<Comment>> ret = new HashMap<Integer, List<Comment>>();
        Pattern findComments = Pattern.compile( "function getLapComment.*(comments\\[1\\] =.*)return comments", Pattern.MULTILINE | Pattern.DOTALL );
        Matcher commentsMatcher = findComments.matcher( practicePage.asXml() );
        if ( commentsMatcher.find() ) {
            String[] comments = commentsMatcher.group( 1 ).split( ";" );
            Pattern commentFinder = Pattern.compile( "<br>(.+?)(?=<br>|$|\")" );
            for ( String c : comments ) {
                if ( !c.trim().isEmpty() ) {
                    Integer lap = Integer.parseInt( c.trim().substring( 9, 10 ) );
                    List<Comment> cl = new ArrayList<Comment>();
                    ret.put( lap, cl );
                    Matcher cm = commentFinder.matcher( c );
                    while ( cm.find() ) {
                        String cleanComment = cm.group( 0 ).trim().replaceAll( "<.*?>", "" ).replaceAll( "\\s+", " " );
                        Comment normalizedComment = CommentsTranslator.getTranslation( cleanComment );
                        if ( normalizedComment != null ) {
                            cl.add( normalizedComment );
                        } else {
                            if ( !cleanComment.startsWith( "I am satisfied" ) && !cleanComment.startsWith( "Estou satisfeito" ) ) {
                                logger.error( "Error parsing comment. Pre-defined comment not found for string '" + cleanComment + "'" );
                            }
                        }
                    }
                }
            }
        } else {
            logger.error( "Unable to extract comments." );
        }
        return ret;
    }

    private int parseTime(String timeStr) throws ParseException {
        if ( timeStr.indexOf( ':' ) > 0 ) {
            // 'm:ss.SSS'
            String[] parts = timeStr.split( "[:\\.s]" );
            return (Integer.parseInt( parts[0] ) * 60000) + (Integer.parseInt( parts[1] ) * 1000) + Integer.parseInt( parts[2] );
        } else {
            // 'ss.SSS'
            String[] parts = timeStr.split( "[\\.s]" );
            return (Integer.parseInt( parts[0] ) * 1000) + Integer.parseInt( parts[1] );
        }
    }

    private static class Forecast {
        public Weather weather;
        public int     temperature;
        public int     humidity;
    }

}