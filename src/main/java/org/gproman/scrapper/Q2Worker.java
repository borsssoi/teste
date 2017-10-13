package org.gproman.scrapper;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.race.Lap;
import org.gproman.model.race.Qualify;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class Q2Worker
        implements
        Callable<Qualify> {
    private static final Logger logger = LoggerFactory.getLogger( Q2Worker.class );

    private final HtmlPage      q1Page;

    public Q2Worker(HtmlPage q1Page) {
        this.q1Page = q1Page;
    }

    @Override
    public Qualify call() {
        return parsePage( q1Page );
    }

    public Qualify parsePage(HtmlPage q1Page) {
        logger.info( "Parsing qualify 2..." );

        HtmlTable table = q1Page.getFirstByXPath( "//th[contains(text(),'Qualify 2 lap data')]/ancestor::table" );
        if( table != null ) {
            Qualify q2 = new Qualify();
            q2.setNumber( 2 );

            Forecast[] forecast = parseForecast( q1Page );

            HtmlTableRow row = table.getRow( 2 );
            if( ! "No qualify laps done".equals( row.getTextContent() ) ) {
                List<HtmlTableCell> cells = row.getCells();

                Lap lap = new Lap();
                q2.setLap( lap );
                lap.setNumber( 1 );
                lap.setWeather( forecast[1].weather );
                lap.setTemperature( forecast[1].temperature );
                lap.setHumidity( forecast[1].humidity );
                try {
                    lap.setTime( parseTime( cells.get( 0 ).getTextContent().trim() ) );
                    lap.getSettings().setFrontWing( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    lap.getSettings().setRearWing( Integer.parseInt( cells.get( 2 ).getTextContent().trim() ) );
                    lap.getSettings().setEngine( Integer.parseInt( cells.get( 3 ).getTextContent().trim() ) );
                    lap.getSettings().setBrakes( Integer.parseInt( cells.get( 4 ).getTextContent().trim() ) );
                    lap.getSettings().setGear( Integer.parseInt( cells.get( 5 ).getTextContent().trim() ) );
                    lap.getSettings().setSuspension( Integer.parseInt( cells.get( 6 ).getTextContent().trim() ) );
                    lap.getSettings().setTyre( Tyre.determineTyre( cells.get( 9 ).getTextContent().trim() ) );
                    q2.setRiskDescr( cells.get( 10 ).getTextContent().trim() );
                } catch ( Exception e ) {
                    logger.error( "Error parsing q2 lap'" + row.asText() + "'", e );
                }
                logger.info( "Q2 parsed: " + q2 );
                return q2;
            } else {
                logger.error( "Unable to find Q2 data." );
                return null;
            }
        } else {
            logger.error( "Unable to find Q2 data." );
            return null;
        }
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