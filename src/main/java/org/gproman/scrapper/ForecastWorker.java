package org.gproman.scrapper;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.race.Forecast;
import org.gproman.model.race.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class ForecastWorker
        implements
        Callable<Forecast[]> {
    private static final Logger logger = LoggerFactory.getLogger( ForecastWorker.class );

    private final HtmlPage      practicePage;

    public ForecastWorker(HtmlPage practicePage) {
        this.practicePage = practicePage;
    }

    @Override
    public Forecast[] call() {
        return parsePage( practicePage );
    }

    public Forecast[] parsePage(HtmlPage practicePage) {
        logger.info( "Parsing forecast..." );

        Forecast[] forecast = new Forecast[6];
        
        HtmlTable fcTable = practicePage.getFirstByXPath( "//th[contains(text(),'Practice / Qualify 1')]/ancestor::table" );
        HtmlTableRow row = fcTable.getRow( 1 );
        Pattern fp = Pattern.compile( ".*Temp: (\\d+).*Humidity: (\\d+).*", Pattern.MULTILINE|Pattern.DOTALL );
        forecast[0] = parseForecastQCell( row.getCell( 0 ), fp );
        forecast[1] = parseForecastQCell( row.getCell( 1 ), fp );

        fp = Pattern.compile( ".*Temp:\\s+(\\d+)(.*-(\\d+))?.*Humidity:\\s+(\\d+)%(-(\\d+)%)?.*Rain probability:\\s+(\\d+)%(-(\\d+)%)?.*", Pattern.MULTILINE|Pattern.DOTALL );
        fcTable = practicePage.getFirstByXPath( "//th[contains(text(),'Start - 0h30m')]/ancestor::table" );
        row = fcTable.getRow( 1 );
        forecast[2] = parseForecastRCell( row.getCell( 0 ), fp );
        forecast[3] = parseForecastRCell( row.getCell( 1 ), fp );
        row = fcTable.getRow( 3 );
        forecast[4] = parseForecastRCell( row.getCell( 0 ), fp );
        forecast[5] = parseForecastRCell( row.getCell( 1 ), fp );
        
        logger.info( "Forecast parsed... " + Arrays.toString( forecast ) );
        return forecast;
    }

    private Forecast parseForecastQCell( HtmlTableCell cell,
                                         Pattern fp) {
        Matcher fm = fp.matcher( cell.asText() );
        if( fm.matches() ) {
            Forecast ret = new Forecast();
            HtmlImage weather = cell.getFirstByXPath( "./img" );
            ret.setWeather( Weather.determineWeather( weather.getAttribute( "title" ) ) );
            ret.setTempMin( Integer.parseInt( fm.group( 1 ) ) );
            ret.setTempMax( ret.getTempMin() );
            ret.setHumidityMin( Integer.parseInt( fm.group( 2 ) ) );
            ret.setHumidityMax( ret.getHumidityMin() );
            if( ret.getWeather().equals( Weather.RAIN ) ) {
                ret.setRainMin( 100 );
                ret.setRainMax( 100 );
            } else {
                ret.setRainMin( 0 );
                ret.setRainMax( 0 );
            }
            return ret;
        } else {
            logger.error( "Unable to parse weather forecast for: '"+cell.asXml()+"'" );
        }
        return null;
    }

    private Forecast parseForecastRCell( HtmlTableCell cell,
                                         Pattern fp) {
        Matcher fm = fp.matcher( cell.asText() );
        if( fm.matches() ) {
            Forecast ret = new Forecast();
            ret.setTempMin( Integer.parseInt( fm.group( 1 ) ) );
            if( fm.group(3) != null ) {
                ret.setTempMax( Integer.parseInt( fm.group( 3 ) ) );
            } else {
                ret.setTempMax( ret.getTempMin() );
            }
            ret.setHumidityMin( Integer.parseInt( fm.group( 4 ) ) );
            if( fm.group(6) != null ) {
                ret.setHumidityMax( Integer.parseInt( fm.group( 6 ) ) );
            } else {
                ret.setHumidityMax( ret.getHumidityMin() );
            }
            ret.setRainMin( Integer.parseInt( fm.group( 7 ) ) );
            if( fm.group(9) != null ) {
                ret.setRainMax( Integer.parseInt( fm.group( 9 ) ) );
            } else {
                ret.setRainMax( ret.getRainMin() );
            }
            return ret;
        } else {
            logger.error( "Unable to parse weather forecast for: '"+cell.asXml()+"'" );
        }
        return null;
    }


}