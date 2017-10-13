package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.race.Forecast;
import org.gproman.model.race.Weather;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ForecastWorkerTest {
    private ForecastWorker worker;

    @Before
    public void setup() {
        worker = new ForecastWorker( null );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "PracticeQ1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Forecast[] forecast = worker.parsePage( practicePage );
        Forecast[] expected = new Forecast[] {
            new Forecast( Weather.VERY_CLOUDY, 33, 33, 95, 95, 0, 0 ),                                     
            new Forecast( Weather.VERY_CLOUDY, 38, 38, 90, 90, 0, 0 ),                                     
            new Forecast( null, 36, 40, 89, 97, 0, 0 ),                                     
            new Forecast( null, 38, 42, 90, 98, 50, 60 ),                                     
            new Forecast( null, 37, 41, 90, 99, 60, 70 ),                                     
            new Forecast( null, 37, 41, 92, 98, 65, 75 ),                                     
        };
        verifyForecast( forecast, expected );
    }

    private void verifyForecast(Forecast[] forecast,
                                Forecast[] expected) {
        for( int i = 0; i < expected.length; i++ ) {
            assertEquals( expected[i], forecast[i] );
        }
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "PracticeQ1_2.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Forecast[] forecast = worker.parsePage( practicePage );
        Forecast[] expected = new Forecast[] {
            new Forecast( Weather.CLOUDY, 26, 26, 31, 31, 0, 0 ),                                     
            new Forecast( Weather.PARTIALLY_CLOUDY, 26, 26, 19, 19, 0, 0 ),                                     
            new Forecast( null, 25, 30, 15, 20, 0, 0 ),                                     
            new Forecast( null, 25, 30, 15, 20, 0, 0 ),                                     
            new Forecast( null, 23, 28, 15, 25, 0, 0 ),                                     
            new Forecast( null, 24, 32, 15, 25, 0, 5 ),                                     
        };
        verifyForecast( forecast, expected );
    }
}
