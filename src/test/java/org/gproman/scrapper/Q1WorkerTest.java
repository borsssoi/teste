package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Qualify;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Q1WorkerTest {
    private Q1Worker worker;

    @Before
    public void setup() {
        worker = new Q1Worker( null );
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
        
        Qualify q1 = worker.parsePage( practicePage );
        
        assertEquals( 1, q1.getNumber() );
        
        Lap lap = q1.getLap();
        assertEquals( 1, lap.getNumber().intValue() );
        assertEquals( 79756, lap.getTime().intValue() );
        assertNull( lap.getMistake() );
        assertNull( lap.getNetTime() );
        assertNull( lap.getPosition() );
        assertEquals( Weather.VERY_CLOUDY, lap.getWeather() );
        assertEquals( 33, lap.getTemperature().intValue() );
        assertEquals( 95, lap.getHumidity().intValue() );
        
        CarSettings settings = lap.getSettings();
        assertEquals( 662, settings.getFrontWing().intValue() );
        assertEquals( 866, settings.getRearWing().intValue() );
        assertEquals( 700, settings.getEngine().intValue() );
        assertEquals( 576, settings.getBrakes().intValue() );
        assertEquals( 506, settings.getGear().intValue() );
        assertEquals( 343, settings.getSuspension().intValue() );
        assertEquals( Tyre.HARD, settings.getTyre() );
        assertNull( lap.getComments() );
        assertEquals( "Push the car a lot", q1.getRiskDescr() );
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
        
        Qualify q1 = worker.parsePage( practicePage );
        assertNull( q1 );
    }
}
