package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.gproman.model.track.Downforce;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.GripLevel;
import org.gproman.model.track.Overtaking;
import org.gproman.model.track.SuspensionRigidity;
import org.gproman.model.track.Track;
import org.gproman.model.track.TyreWear;
import org.gproman.scrapper.TrackInfoWorker.PreviousRace;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TrackInfoWorkerTest {
    private WebClient   client;

    @Before
    public void setup() {
        client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException,
                                IOException {
        Track track = execScrapper( "TrackInfo1.html" );
        assertEquals( 52, track.getId().intValue() );
        assertEquals( "Slovakiaring", track.getName() );
        assertEquals( 313.9, track.getDistance(), 0.0001 );
        assertEquals( 5.923, track.getLapDistance(), 0.0001 );
        assertEquals( 53, track.getLaps() );
        assertEquals( 0, track.getAvgSpeed(), 0.0001 );
        assertEquals( 14, track.getCorners() );
        assertEquals( 17, track.getPower() );
        assertEquals( 10, track.getHandling() );
        assertEquals( 15, track.getAcceleration() );
        assertEquals( 19500, track.getTimeInOut() );
        assertEquals( Downforce.MEDIUM, track.getDownforce() );
        assertEquals( Overtaking.HARD, track.getOvertaking() );
        assertEquals( SuspensionRigidity.MEDIUM, track.getSuspension() );
        assertEquals( FuelConsumption.HIGH, track.getFuelConsumption() );
        assertEquals( TyreWear.HIGH, track.getTyreWear() );
        assertEquals( GripLevel.HIGH, track.getGripLevel() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
                                        IOException {
        Track track = execScrapper( "TrackInfo2.html" );
        assertEquals( 39, track.getId().intValue() );
        assertEquals( "Valencia", track.getName() );
        assertEquals( 310.1, track.getDistance(), 0.0001 );
        assertEquals( 5.44, track.getLapDistance(), 0.0001 );
        assertEquals( 57, track.getLaps() );
        assertEquals( 214.41, track.getAvgSpeed(), 0.0001 );
        assertEquals( 25, track.getCorners() );
        assertEquals( 11, track.getPower() );
        assertEquals( 12, track.getHandling() );
        assertEquals( 16, track.getAcceleration() );
        assertEquals( 14500, track.getTimeInOut() );
        assertEquals( Downforce.HIGH, track.getDownforce() );
        assertEquals( Overtaking.HARD, track.getOvertaking() );
        assertEquals( SuspensionRigidity.MEDIUM, track.getSuspension() );
        assertEquals( FuelConsumption.MEDIUM, track.getFuelConsumption() );
        assertEquals( TyreWear.MEDIUM, track.getTyreWear() );
        assertEquals( GripLevel.LOW, track.getGripLevel() );
    }

    @Test
    public void testParsePreviousRaces1() throws FailingHttpStatusCodeException,
                                        IOException {
        HtmlPage page = getPage( "TrackInfo3.html" );
        TrackInfoWorker worker = new TrackInfoWorker(page);
        List<PreviousRace> previous = worker.parserPreviousRaces(page);
        assertEquals( 12, previous.size() );
    }

    @Test
    public void testParsePreviousRaces2() throws FailingHttpStatusCodeException,
                                        IOException {
        HtmlPage page = getPage( "TrackInfo4.html" );
        TrackInfoWorker worker = new TrackInfoWorker(page);
        List<PreviousRace> previous = worker.parserPreviousRaces(page);
        assertEquals( 1, previous.size() );
    }

    private Track execScrapper(String fileName) throws IOException {
        HtmlPage page = getPage(fileName);
        TrackInfoWorker worker = new TrackInfoWorker( page );
        return worker.parsePage( page );
    }

    private HtmlPage getPage(String fileName) throws IOException {
        URL url = getClass().getResource( fileName );
        assertNotNull( url );

        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        return page;
    }

}
