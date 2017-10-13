package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TechDirectorWorkerTest {
    private TechDirectorWorker worker;

    @Before
    public void setup() {
        worker = new TechDirectorWorker( null );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "TechnicalDirector1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        TechDirector td = worker.parsePage( practicePage );

        assertEquals( 240, td.getNumber().intValue() );
        assertEquals( "Eddie Jordan", td.getName() );
        assertEquals( "Ireland", td.getNationality());
        assertEquals( 0, td.getTrophies());
        assertEquals( 447, td.getGps() );
        assertEquals( 28, td.getWins() );
        assertEquals( 264119, td.getSalary() );
        assertEquals( 3, td.getContract() );
        assertEquals( 10000, td.getPointsBonus() );
        assertEquals( 30000, td.getPodiumBonus() );
        assertEquals( 50000, td.getWinBonus() );
        assertEquals( 0, td.getTrophyBonus() );
        
        TDAttributes att = td.getAttributes();
        
        assertEquals( 117, att.getOverall() ); 
        assertEquals( 203, att.getLeadership() );
        assertEquals( 23, att.getRdMech() );
        assertEquals( 26, att.getRdElect() );
        assertEquals( 28, att.getRdAero() );
        assertEquals( 289, att.getExperience() );
        assertEquals( 126, att.getPitCoord() );
        assertEquals( 116, att.getMotivation() );
        assertEquals( 68, att.getAge() );        
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "TechnicalDirector2.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        TechDirector td = worker.parsePage( practicePage );
        System.out.println(td);

        assertEquals( 111, td.getNumber().intValue() );
        assertEquals( "Max Mosley", td.getName() );
        assertEquals( "United Kingdom", td.getNationality());
        assertEquals( 0, td.getTrophies());
        assertEquals( 402, td.getGps() );
        assertEquals( 10, td.getWins() );
        assertEquals( 533000, td.getSalary() );
        assertEquals( 8, td.getContract() );
        assertEquals( 0, td.getPointsBonus() );
        assertEquals( 0, td.getPodiumBonus() );
        assertEquals( 500000, td.getWinBonus() );
        assertEquals( 18500000, td.getTrophyBonus() );
        
        TDAttributes att = td.getAttributes();
        
        assertEquals( 108, att.getOverall() ); 
        assertEquals( 184, att.getLeadership() );
        assertEquals( 30, att.getRdMech() );
        assertEquals( 42, att.getRdElect() );
        assertEquals( 98, att.getRdAero() );
        assertEquals( 226, att.getExperience() );
        assertEquals( 172, att.getPitCoord() );
        assertEquals( 0, att.getMotivation() );
        assertEquals( 61, att.getAge() );        
    }
}