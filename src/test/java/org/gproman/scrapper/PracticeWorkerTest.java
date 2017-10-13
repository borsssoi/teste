package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PracticeWorkerTest {
    private PracticeWorker worker;

    @Before
    public void setup() {
        worker = new PracticeWorker( null );
    }

    @Test
    public void testParsePage() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "PracticeQ1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Practice practice = worker.parsePage( practicePage );
        
        assertEquals( 8, practice.getLaps().size() );
        // test an arbitrary lap
        Lap lap = practice.getLaps().get( 3 );
        assertEquals( 4, lap.getNumber().intValue() );
        assertEquals( 80296, lap.getTime().intValue() );
        assertEquals( 137, lap.getMistake().intValue() );
        assertEquals( 80159, lap.getNetTime().intValue() );
        assertNull( lap.getPosition() );
        assertEquals( Weather.VERY_CLOUDY, lap.getWeather() );
        assertEquals( 33, lap.getTemperature().intValue() );
        assertEquals( 95, lap.getHumidity().intValue() );
        
        CarSettings settings = lap.getSettings();
        assertEquals( 721, settings.getFrontWing().intValue() );
        assertEquals( 721, settings.getRearWing().intValue() );
        assertEquals( 645, settings.getEngine().intValue() );
        assertEquals( 533, settings.getBrakes().intValue() );
        assertEquals( 464, settings.getGear().intValue() );
        assertEquals( 301, settings.getSuspension().intValue() );
        assertEquals( Tyre.HARD, settings.getTyre() );
        
        assertEquals( "[ENG+]", lap.getComments() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "PracticeQ1_NotDone.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Practice practice = worker.parsePage( practicePage );
        
        assertTrue( practice.getLaps().isEmpty() );
    }
}
