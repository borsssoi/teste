package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.gproman.db.DataService;
import org.gproman.model.car.PHA;
import org.gproman.model.race.TestPriority;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestSessionWorkerTest {
    private TestSessionWorker worker;
    private DataService db;

    @Before
    public void setup() {
        db = mock( DataService.class);
        Track track = new Track();
        track.setId( 23 );
        track.setName( "Brands Hatch" );
        when( db.getTrackById( 23 ) ).thenReturn( track );
        
        worker = new TestSessionWorker( null, db );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Test_not_done_1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        TestSession ts = worker.parsePage( practicePage );
        
        assertEquals( Weather.CLOUDY, ts.getWeather() );
        assertEquals( 41, ts.getTemperature().intValue() );
        assertEquals( 89, ts.getHumidity().intValue() );
        
        assertEquals( new PHA(0, 0, 0), ts.getTestPoints() );
        assertEquals( new PHA(18.6, 27.5, 16.1), ts.getRdPoints() );
        assertEquals( new PHA(0, 0, 0), ts.getEngPoints() );
        assertEquals( new PHA(7, 12, 8), ts.getCcPoints() );
        
        assertEquals( 0, ts.getLapsDone().intValue() );
        assertEquals( 0, ts.getStintsDone().intValue() );

    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Test_done_1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        TestSession ts = worker.parsePage( practicePage );

        assertEquals( Weather.CLOUDY, ts.getWeather() );
        assertEquals( 25, ts.getTemperature().intValue() );
        assertEquals( 50, ts.getHumidity().intValue() );
        
        assertEquals( new PHA(7.5, 58.4, 7.5), ts.getTestPoints() );
        assertEquals( new PHA(0, 0, 0), ts.getRdPoints() );
        assertEquals( new PHA(0, 0, 0), ts.getEngPoints() );
        assertEquals( new PHA(4, 5, 7), ts.getCcPoints() );
        
        assertEquals( 100, ts.getLapsDone().intValue() );
        assertEquals( 5, ts.getStintsDone().intValue() );
        
        assertEquals( 5, ts.getStints().size() );
        
        TestStint stint = ts.getStints().get( 0 );
        assertEquals( 1, stint.getNumber().intValue() );
        assertEquals( 5, stint.getLapsDone().intValue() );
        assertEquals( 5, stint.getLapsPlanned().intValue() );
        assertEquals( 76235, stint.getBestTime().intValue() );
        assertEquals( 76710, stint.getMeanTime().intValue() );
        assertEquals( 20, stint.getFuelStart().intValue() );
        assertEquals( 5, stint.getFuelEnd().intValue() );
        assertEquals( 76, stint.getTyresEnd().intValue() );
        assertEquals( TestPriority.SETUP, stint.getPriority() );
        assertEquals( "[WNG+, ENG+, SUS+]", stint.getComments() );

        stint = ts.getStints().get( 2 );
        assertEquals( 3, stint.getNumber().intValue() );
        assertEquals( 30, stint.getLapsDone().intValue() );
        assertEquals( 30, stint.getLapsPlanned().intValue() );
        assertEquals( 77779, stint.getBestTime().intValue() );
        assertEquals( 78004, stint.getMeanTime().intValue() );
        assertEquals( 100, stint.getFuelStart().intValue() );
        assertEquals( 12, stint.getFuelEnd().intValue() );
        assertEquals( 29, stint.getTyresEnd().intValue() );
        assertEquals( TestPriority.CORNERING, stint.getPriority() );
        assertEquals( "[]", stint.getComments() );
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Test_10_stints.html" );
        assertNotNull( url );

        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );

        TestSession ts = worker.parsePage( practicePage );

        assertEquals( Weather.PARTIALLY_CLOUDY, ts.getWeather() );
        assertEquals( 21, ts.getTemperature().intValue() );
        assertEquals( 12, ts.getHumidity().intValue() );

        assertEquals( new PHA(8, 8, 63.8), ts.getTestPoints() );
        assertEquals( new PHA(7.7, 61.7, 7.7), ts.getRdPoints() );
        assertEquals( new PHA(50.1, 6.3, 6.3), ts.getEngPoints() );
        assertEquals( new PHA(14, 14, 17), ts.getCcPoints() );

        assertEquals( 100, ts.getLapsDone().intValue() );
        assertEquals( 10, ts.getStintsDone().intValue() );

        assertEquals( 10, ts.getStints().size() );

        TestStint stint = ts.getStints().get( 0 );
        assertEquals( 1, stint.getNumber().intValue() );
        assertEquals( 5, stint.getLapsDone().intValue() );
        assertEquals( 5, stint.getLapsPlanned().intValue() );
        assertEquals( 110278, stint.getBestTime().intValue() );
        assertEquals( 110898, stint.getMeanTime().intValue() );
        assertEquals( 30, stint.getFuelStart().intValue() );
        assertEquals( 8, stint.getFuelEnd().intValue() );
        assertEquals( 64, stint.getTyresEnd().intValue() );
        assertEquals( TestPriority.HAIRPINS, stint.getPriority() );
        assertEquals( "[]", stint.getComments() );

        stint = ts.getStints().get( 9 );
        assertEquals( 10, stint.getNumber().intValue() );
        assertEquals( 9, stint.getLapsDone().intValue() );
        assertEquals( 9, stint.getLapsPlanned().intValue() );
        assertEquals( 110255, stint.getBestTime().intValue() );
        assertEquals( 110651, stint.getMeanTime().intValue() );
        assertEquals( 45, stint.getFuelStart().intValue() );
        assertEquals( 7, stint.getFuelEnd().intValue() );
        assertEquals( 37, stint.getTyresEnd().intValue() );
        assertEquals( TestPriority.HAIRPINS, stint.getPriority() );
        assertEquals( "[]", stint.getComments() );
    }

}