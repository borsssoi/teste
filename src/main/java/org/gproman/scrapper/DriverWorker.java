package org.gproman.scrapper;

import java.util.List;
import java.util.concurrent.Callable;

import org.gproman.db.DataService;
import org.gproman.model.driver.Driver;
import org.gproman.model.track.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class DriverWorker
        implements
        Callable<Driver> {
    private static final Logger logger = LoggerFactory.getLogger( DriverWorker.class );
    private final HtmlPage      driverPage;
    private final DataService   db;

    public DriverWorker(DataService db,
                        HtmlPage driverPage) {
        this.db = db;
        this.driverPage = driverPage;
    }

    @Override
    public Driver call() {
        try {
            Driver driver = parsePage( driverPage );
            logger.info( "Driver retrieved = " + driver );
            return driver;
        } catch ( Exception e1 ) {
            logger.info( "Error retrieving driver page. Impossible to recover data.", e1 );
        }
        return null;
    }

    public Driver parsePage(HtmlPage driverPage) {
        HtmlAnchor driverLink = driverPage.getFirstByXPath( "//a[starts-with(@href,'DriverProfile.asp?Id=')]" );

        Driver driver = new Driver();
        driver.setNumber( Integer.parseInt( driverLink.getAttribute( "href" ).substring( 21 ) ) );
        logger.info( "Found driver id = " + driver.getNumber() );

        HtmlForm form = driverPage.getFormByName( "formOffer" );
        @SuppressWarnings("unchecked")
        List<HtmlTableRow> trs = (List<HtmlTableRow>) form.getByXPath( "//tr" );

        for ( HtmlTableRow tr : trs ) {
            List<HtmlTableCell> cells = tr.getCells();
            try {
                if ( cells.size() == 2 ) {
                    if ( "Name:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setName( cells.get( 1 ).getTextContent().trim() );
                    } else if ( "Nationality:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String country = cells.get( 1 ).getTextContent().trim();
                        driver.setNationality( country.substring( country.indexOf( '(' )+1, country.indexOf( ')' ) ) );
                    } else if ( "Trophies:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setTrophies( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Number of GPs:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setGps( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Wins:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setWins( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Podiums:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setPodiums( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Points scored:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setPoints( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Pole positions:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setPoles( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Fastest laps:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        driver.setFastestLaps( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Salary:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.setSalary( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    } else if ( "Contract length:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.setContract( Integer.parseInt( val.substring( 0, val.indexOf( ' ' ) ) ) );
                    }
                } else if ( cells.size() == 3 ) {
                    // this is for drivers with a picture on cell 0
                    if ( "Name:".equals( cells.get( 1 ).getTextContent().trim() ) ) {
                        driver.setName( cells.get( 2 ).getTextContent().trim() );
                    } else if ( "Overall:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setOverall( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Concentration:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setConcentration( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Talent:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setTalent( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Aggressiveness:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setAggressiveness( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Experience:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setExperience( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Technical insight:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setTechInsight( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Stamina:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setStamina( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Charisma:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setCharisma( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Motivation:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setMotivation( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Reputation:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setReputation( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Weight(kg):".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setWeight( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    } else if ( "Age:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        driver.getAttributes().setAge( Integer.parseInt( val.substring( 0, val.length() - 1 ) ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Exception parsing " + tr.asText(), e );
            }
        }

        @SuppressWarnings("unchecked")
        List<HtmlAnchor> favoriteTracks = (List<HtmlAnchor>) form.getByXPath( "//a[starts-with(@href,'TrackDetails.asp?id=')]" );
        for ( HtmlAnchor ft : favoriteTracks ) {
            String link = ft.getHrefAttribute();
            Integer id = Integer.parseInt( link.substring( link.indexOf( '=' ) + 1 ) );
            Track track = db.getTrackById( id );
            if ( track != null && !driver.getFavoriteTracks().contains( track ) ) {
                driver.getFavoriteTracks().add( track );
            }
        }
        return driver;
    }
}