package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.staff.Facilities;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class FacilitiesWorkerTest {
    private FacilitiesWorker worker;

    @Before
    public void setup() {
        worker = new FacilitiesWorker( null );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Facilities1.html" );
        assertNotNull( url );
        
        HtmlPage facilitiesPage = client.getPage( url );
        assertNotNull( facilitiesPage );
        
        Facilities f = worker.parsePage( facilitiesPage );

        assertEquals( 20, f.getOverall().intValue() ); 
        assertEquals( 18, f.getExperience().intValue() ); 
        assertEquals( 6, f.getMotivation().intValue() ); 
        assertEquals( 23, f.getTechnical().intValue() ); 
        assertEquals( 20, f.getStress().intValue() ); 
        assertEquals( 19, f.getConcentration().intValue() ); 
        assertEquals( 20, f.getEfficiency().intValue() ); 
        assertEquals( 14, f.getWindtunnel().intValue() ); 
        assertEquals( 34, f.getPitstop().intValue() ); 
        assertEquals( 26, f.getWorkshop().intValue() ); 
        assertEquals( 9, f.getDesign().intValue() ); 
        assertEquals( 14, f.getEngineering().intValue() ); 
        assertEquals( 15, f.getAlloy().intValue() ); 
        assertEquals( 51, f.getCommercial().intValue() ); 
        assertEquals( 694000, f.getSalary().intValue() ); 
        assertEquals( 815000, f.getMaintenance().intValue() ); 
        assertEquals( 23, f.getMlt().intValue() ); 
    }

}