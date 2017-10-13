package org.gproman.scrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.gproman.model.track.Downforce;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.GripLevel;
import org.gproman.model.track.Overtaking;
import org.gproman.model.track.SuspensionRigidity;
import org.gproman.model.track.Track;
import org.gproman.model.track.TyreWear;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class TrackInfoWorker
        implements
        Callable<Track> {
    private static final Logger    logger      = LoggerFactory.getLogger( TrackInfoWorker.class );
    private final HtmlPage         page;

    public TrackInfoWorker( HtmlPage page ) {
        this.page = page;
    }

    @Override
    public Track call() {
        return parsePage( page );
    }

    public Track parsePage(HtmlPage page) {
        logger.info( "Parsing track info page." );
        Track track = new Track();
        HtmlAnchor anchor = page.getAnchorByText( "Deutsch" );
        String link = anchor.getHrefAttribute();
        track.setId( Integer.parseInt( link.substring( link.indexOf( '=' )+1 ) ) );
        
        String trackName = page.getTitleText();
        trackName = trackName.substring( trackName.indexOf( '-' )+1, trackName.lastIndexOf( '-' )-1 ).trim();
        track.setName( trackName );
        
        HtmlTable table = page.getFirstByXPath( "//td[contains(text(),'Location:')]/ancestor::table[1]" );

        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            
            extractInfo( track, cells.get( 0 ), cells.get( 1 ) );
            extractInfo( track, cells.get( 2 ), cells.get( 3 ) );
        }
        logger.info( "Track info successfully parsed: "+track );
        return track;
    }

    private void extractInfo(Track track,
                             HtmlTableCell cellK,
                             HtmlTableCell cellV) {
        if( "Location:".equals( cellK.getTextContent().trim() ) ) {
            //track.setName( track.getName() + " ("+cellV.getTextContent().trim()+")" );
        } else if ( "Power:".equals( cellK.getTextContent().trim() ) ){
            track.setPower( Integer.parseInt( cellV.getAttribute( "title" ) ) );
        } else if ( "Handling:".equals( cellK.getTextContent().trim() ) ){
            track.setHandling( Integer.parseInt( cellV.getAttribute( "title" ) ) );
        } else if ( "Acceleration:".equals( cellK.getTextContent().trim() ) ){
            track.setAcceleration( Integer.parseInt( cellV.getAttribute( "title" ) ) );
        } else if ( "Race distance:".equals( cellK.getTextContent().trim() ) ){
            String dist = cellV.getTextContent().trim();
            track.setDistance( Double.parseDouble( dist.replaceAll("[^0-9\\.]", "") ) );
        } else if ( "Laps:".equals( cellK.getTextContent().trim() ) ){
            track.setLaps( Integer.parseInt( cellV.getTextContent().trim() ) );
        } else if ( "Downforce:".equals( cellK.getTextContent().trim() ) ){
            track.setDownforce( Downforce.fromString( cellV.getTextContent().trim() ) );
        } else if ( "Lap distance:".equals( cellK.getTextContent().trim() ) ){
            String dist = cellV.getTextContent().trim();
            track.setLapDistance( Double.parseDouble( dist.replaceAll("[^0-9\\.]", "") ) );
        } else if ( "Overtaking:".equals( cellK.getTextContent().trim() ) ){
            track.setOvertaking( Overtaking.fromString( cellV.getTextContent().trim() ) );
        } else if ( "Average speed:".equals( cellK.getTextContent().trim() ) ){
            String speed = cellV.getTextContent().trim();
            if( ! "?".equals( speed ) ) {
                track.setAvgSpeed( Double.parseDouble( speed.replaceAll("[^0-9\\.]", "") ) );
            }
        } else if ( "Suspension rigidity:".equals( cellK.getTextContent().trim() ) ){
            track.setSuspension( SuspensionRigidity.fromString( cellV.getTextContent().trim() ) );
        } else if ( "Fuel consumption:".equals( cellK.getTextContent().trim() ) ){
            track.setFuelConsumption( FuelConsumption.fromString( cellV.getTextContent().trim() ) );
        } else if ( "Tyre wear:".equals( cellK.getTextContent().trim() ) ){
            track.setTyreWear( TyreWear.fromString( cellV.getTextContent().trim() ) );
        } else if ( "Number of corners:".equals( cellK.getTextContent().trim() ) ){
            track.setCorners( Integer.parseInt( cellV.getTextContent().trim() ) );
        } else if ( "Time in/out of pits:".equals( cellK.getTextContent().trim() ) ){
            String time = cellV.getTextContent().trim();
            track.setTimeInOut( (int) (Double.parseDouble( time.substring( 0, time.indexOf( 's' ) ) )*1000) );
        } else if ( "Grip level:".equals( cellK.getTextContent().trim() ) ){
            track.setGripLevel( GripLevel.fromString( cellV.getTextContent().trim() ) );
        }
    }

    /**
     * This is a helper method called by other classes
     * @param page
     * @return
     */
    public List<PreviousRace> parserPreviousRaces( HtmlPage page ) {
        logger.info("Parsing previous races for track "+page.getUrl().toString());
        List<PreviousRace> previous = new ArrayList<PreviousRace>();
        HtmlTable table = page.getFirstByXPath( "//th[contains(text(),'Winner')]/ancestor::table[1]" );
        if( table != null ) {
            boolean first = true;
            for ( HtmlTableRow tr : table.getRows() ) {
                if( first ) {
                    first = false;
                    continue;
                }
                try {
                    List<HtmlTableCell> cells = tr.getCells();
                    PreviousRace p = new PreviousRace();
                    String ss = cells.get(0).getTextContent().trim();
                    String sr = cells.get(1).getTextContent().trim();
                    if( ss != null && sr != null && !ss.equals("-") && !sr.equals("-") ) {
                        p.season = Integer.parseInt( ss );
                        p.race = Integer.parseInt( sr );
                        previous.add(p);
                    }
                } catch (Exception e) {
                    logger.error("Unable to parse previous race: "+tr.asText(), e);
                }
            }
        }
        logger.info("Found "+previous.size()+" previous races.");
        return previous;
    }
    
    public static class PreviousRace {
        public int season;
        public int race;
        @Override
        public String toString() {
            return "PreviousRace[" + season + ", " + race + "]";
        }
    }
}