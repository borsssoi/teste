package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SetupWorkerTest {
    private SetupWorker worker;

    @Before
    public void setup() {
        worker = new SetupWorker( null );
    }

    @Test
    public void testParsePage() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Setup1.html" );
        assertNotNull( url );
        
        HtmlPage setupPage = client.getPage( url );
        assertNotNull( setupPage );
        
        SetupWorker.SetupData setup = worker.parsePage( setupPage );
        
        assertEquals( 509, setup.fwing.intValue() );
        assertEquals( 909, setup.rwing.intValue() );
        assertEquals( 573, setup.engine.intValue() );
        assertEquals( 548, setup.brakes.intValue() );
        assertEquals( 455, setup.gear.intValue() );
        assertEquals( 714, setup.suspension.intValue() );
        
        assertEquals( 58, setup.startingFuel.intValue() );
        assertEquals( "58/58/58/0/0", setup.fuelStrategy );
        
        assertEquals( 40, setup.overtake.intValue() );
        assertEquals( 0, setup.defend.intValue() );
        assertEquals( 40, setup.clear.intValue() );
        assertEquals( 0, setup.malfunc.intValue() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Setup_NotDone.html" );
        assertNotNull( url );
        
        HtmlPage setupPage = client.getPage( url );
        assertNotNull( setupPage );
        
        SetupWorker.SetupData setup = worker.parsePage( setupPage );
        assertNotNull( setup );
    }
    
    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Setup2.html" );
        assertNotNull( url );
        
        HtmlPage setupPage = client.getPage( url );
        assertNotNull( setupPage );
        
        SetupWorker.SetupData setup = worker.parsePage( setupPage );
        
        assertEquals( 999, setup.fwing.intValue() );
        assertEquals( 919, setup.rwing.intValue() );
        assertEquals( 587, setup.engine.intValue() );
        assertEquals( 606, setup.brakes.intValue() );
        assertEquals( 707, setup.gear.intValue() );
        assertEquals( 387, setup.suspension.intValue() );
        
        assertEquals( 85, setup.startingFuel.intValue() );
        assertEquals( "90/0/0/0/0", setup.fuelStrategy );
        
        assertEquals( 20, setup.overtake.intValue() );
        assertEquals( 0, setup.defend.intValue() );
        assertEquals( 80, setup.clear.intValue() );
        assertEquals( 0, setup.malfunc.intValue() );
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Setup_added_wet_CT.html" );
        assertNotNull( url );
        
        HtmlPage setupPage = client.getPage( url );
        assertNotNull( setupPage );
        
        SetupWorker.SetupData setup = worker.parsePage( setupPage );
        
        assertEquals( 0, setup.fwing.intValue() );
        assertEquals( 0, setup.rwing.intValue() );
        assertEquals( 0, setup.engine.intValue() );
        assertEquals( 0, setup.brakes.intValue() );
        assertEquals( 0, setup.gear.intValue() );
        assertEquals( 0, setup.suspension.intValue() );
        
        assertEquals( 20, setup.startingFuel.intValue() );
        assertEquals( "70/10/0/0/0", setup.fuelStrategy );
        
        assertEquals( 0, setup.overtake.intValue() );
        assertEquals( 0, setup.defend.intValue() );
        assertEquals( 100, setup.clear.intValue() );
        assertEquals( 100, setup.clearWet.intValue() );
        assertEquals( 100, setup.malfunc.intValue() );
    }
    
}
