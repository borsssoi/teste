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

public class Q2WorkerTest {
    private Q2Worker worker;

    @Before
    public void setup() {
        worker = new Q2Worker( null );
    }

    @Test
    public void testParsePage() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Q2_1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Qualify q2 = worker.parsePage( practicePage );
        
        assertEquals( 2, q2.getNumber() );
        
        Lap lap = q2.getLap();
        assertEquals( 1, lap.getNumber().intValue() );
        assertEquals( 88245, lap.getTime().intValue() );
        assertNull( lap.getMistake() );
        assertNull( lap.getNetTime() );
        assertNull( lap.getPosition() );
        assertEquals( Weather.PARTIALLY_CLOUDY, lap.getWeather() );
        assertEquals( 11, lap.getTemperature().intValue() );
        assertEquals( 18, lap.getHumidity().intValue() );
        
        CarSettings settings = lap.getSettings();
        assertEquals( 509, settings.getFrontWing().intValue() );
        assertEquals( 909, settings.getRearWing().intValue() );
        assertEquals( 573, settings.getEngine().intValue() );
        assertEquals( 548, settings.getBrakes().intValue() );
        assertEquals( 455, settings.getGear().intValue() );
        assertEquals( 714, settings.getSuspension().intValue() );
        assertEquals( Tyre.XSOFT, settings.getTyre() );
        assertNull( lap.getComments() );
        assertEquals( "Push the car a lot", q2.getRiskDescr() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Q2_NotDone.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Qualify q1 = worker.parsePage( practicePage );
        assertNull( q1 );
    }
}
