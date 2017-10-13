package org.gproman.scrapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.track.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class CalendarWorker
        implements
        Callable<Season> {
    private static final Logger    logger      = LoggerFactory.getLogger( CalendarWorker.class );
    private final SimpleDateFormat DATE_PARSER = new SimpleDateFormat( "MMM d yyyy", Locale.US );
    private final HtmlPage         office;
    private final Manager          manager;
    private final DataService      db;
    private Integer                seasonNumber;
    private ProgressMonitor        monitor;

    public CalendarWorker(DataService db,
                          Manager manager,
                          HtmlPage office,
                          Integer number,
                          ProgressMonitor monitor) {
        this.db = db;
        this.manager = manager;
        this.office = office;
        this.seasonNumber = number;
        this.monitor = monitor;
    }

    @Override
    public Season call() {
        HtmlAnchor calLink = office.getAnchorByHref( "Calendar.asp?Group=" + manager.getGroup() );

        Season season = new Season();
        season.setNumber( seasonNumber );

        try {
            HtmlPage calPage = calLink.click();
            parsePage( db,
                       season,
                       calPage );
        } catch ( IOException e1 ) {
            logger.info( "Error retrieving driver page. Impossible to recover data.", e1 );
        }
        return season;
    }

    public void parsePage(DataService db,
                          Season season,
                          HtmlPage calPage) {
        logger.info( "Parsing season calendar." );
        HtmlTable table = calPage.getFirstByXPath( "//table" );

        List<Track> tracks = new ArrayList<Track>();
        int progress = 20;

        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            if ( "#".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                // skip header row
                continue;
            }
            Race race = new Race();
            race.setNumber( Integer.parseInt( cells.get( 0 ).getTextContent().trim().substring( 0, 2 ) ) );

            HtmlAnchor trackAnchor = (HtmlAnchor) cells.get( 2 ).getHtmlElementsByTagName( "a" ).get( 0 );
            try {
                // download new track
                if( ! updateProgress( progress, "Carregando pista: " + trackAnchor.getTextContent().trim() ) ) {
                    cancelDownload();
                    return;
                }
                logger.info( "Trying to load track: " + trackAnchor.getTextContent().trim() );
                String link = trackAnchor.getHrefAttribute();
                Integer trackId = new Integer( link.substring( link.indexOf( '=' )+1 ) );
                tracks.add( db.getTrackById( trackId ) );
            } catch ( Exception e ) {
                logger.info( "Error downloading track: " + trackAnchor.getTextContent().trim(), e );
            }

            try {
                String dtString = cells.get( 3 ).getTextContent().trim();
                if ( "Today".equalsIgnoreCase( dtString ) ) {
                    Calendar date = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
                    date.set( Calendar.HOUR, 19 );
                    date.set( Calendar.MINUTE, 0 );
                    date.set( Calendar.SECOND, 0 );
                    date.set( Calendar.MILLISECOND, 0 );
                    race.setDate( new Timestamp( date.getTime().getTime() ) );
                } else if ( "Yesterday".equalsIgnoreCase( dtString ) ) {
                    Calendar date = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
                    date.set( Calendar.HOUR, 19 );
                    date.set( Calendar.MINUTE, 0 );
                    date.set( Calendar.SECOND, 0 );
                    date.set( Calendar.MILLISECOND, 0 );
                    date.add( Calendar.DAY_OF_MONTH, -1 );
                    race.setDate( new Timestamp( date.getTime().getTime() ) );
                } else {
                    Calendar date = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
                    date.setTime( DATE_PARSER.parse( dtString.replaceAll( "(?:st|nd|rd|th),", "" ) ) );
                    date.set( Calendar.HOUR, 19 );
                    date.set( Calendar.MINUTE, 0 );
                    date.set( Calendar.SECOND, 0 );
                    date.set( Calendar.MILLISECOND, 0 );
                    race.setDate( new Timestamp( date.getTime().getTime() ) );
                }
            } catch ( ParseException e ) {
                logger.error( "Error parsing date: " + cells.get( 3 ).getTextContent().trim(), e );
            }
            logger.debug( "Race parsed: " + race );
            season.getRaces().add( race );
            race.setSeasonNumber( season.getNumber() );
            progress += 2;
        }
        for ( int i = 0; i < tracks.size(); i++ ) {
            try {
                Track track = tracks.get( i );
                season.getRaces().get( i ).setTrack( track );
            } catch ( Exception e ) {
                logger.info( "Error downloading track.", e );
            }
        }
        logger.info( "Season calendar parsed" );
    }

    private boolean updateProgress(int perc,
                                   String note) {
        if ( this.monitor.isCanceled() ) {
            return false;
        }
        this.monitor.setNote( note );
        this.monitor.setProgress( perc );
        return true;
    }

    private void cancelDownload() {
        JOptionPane.showMessageDialog( null, "Download cancelado...", "Download cancelado", JOptionPane.INFORMATION_MESSAGE );
        monitor.close();
    }

}