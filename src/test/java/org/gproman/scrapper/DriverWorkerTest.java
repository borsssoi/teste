package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.gproman.db.DataService;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.track.Track;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DriverWorkerTest {
    private DriverWorker worker;
    private DataService ds;

    @Before
    public void setup() {
        ds = mock( DataService.class );
        worker = new DriverWorker( ds, null );
        
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException, IOException {
        Track paulRicard = mock( Track.class );
        when( paulRicard.getName() ).thenReturn( "Paul Ricard" );
        
        Track monza = mock( Track.class );
        when( monza.getName() ).thenReturn( "Monza" );
        
        when( ds.getTrackById( 25 ) ).thenReturn( paulRicard );
        when( ds.getTrackById( 11 ) ).thenReturn( monza );
        
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Driver1.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Driver driver = worker.parsePage( practicePage );

        assertEquals( 3923, driver.getNumber().intValue() );
        assertEquals( "Andy Dumfries", driver.getName() );
        assertEquals( "United Kingdom", driver.getNationality());
        assertEquals( 0, driver.getTrophies());
        assertEquals( 73, driver.getGps() );
        assertEquals( 11, driver.getWins() );
        assertEquals( 29, driver.getPodiums() );
        assertEquals( 280, driver.getPoints() );
        assertEquals( 5, driver.getPoles() );
        assertEquals( 7, driver.getFastestLaps() );
        assertEquals( 1075611, driver.getSalary() );
        assertEquals( 8, driver.getContract() );
        assertEquals( paulRicard.getName(), driver.getFavoriteTracks().get( 0 ).getName() );
        assertEquals( monza.getName(), driver.getFavoriteTracks().get( 1 ).getName() );
        
        DriverAttributes att = driver.getAttributes();
        
        assertEquals( 125, att.getOverall() ); 
        assertEquals( 203, att.getConcentration() );
        assertEquals( 152, att.getTalent() );
        assertEquals( 11, att.getAggressiveness() );
        assertEquals( 84, att.getExperience() );
        assertEquals( 131, att.getTechInsight() );
        assertEquals( 128, att.getStamina() );
        assertEquals( 121, att.getCharisma() );
        assertEquals( 44, att.getMotivation() );
        assertEquals( 34, att.getReputation() );
        assertEquals( 54, att.getWeight() );
        assertEquals( 22, att.getAge() );        
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException, IOException {
        Track zandvoort = mock( Track.class );
        when( zandvoort.getName() ).thenReturn( "Zandvoort" );
        
        Track ahvenisto = mock( Track.class );
        when( ahvenisto.getName() ).thenReturn( "Ahvenisto" );
        
        Track nurburgring = mock( Track.class );
        when( nurburgring.getName() ).thenReturn( "Nurburgring" );
        
        when( ds.getTrackById( 27 ) ).thenReturn( zandvoort );
        when( ds.getTrackById( 47 ) ).thenReturn( ahvenisto );
        when( ds.getTrackById( 21 ) ).thenReturn( nurburgring );

        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( "Driver2.html" );
        assertNotNull( url );
        
        HtmlPage practicePage = client.getPage( url );
        assertNotNull( practicePage );
        
        Driver driver = worker.parsePage( practicePage );
        
        assertEquals( 1695, driver.getNumber().intValue() );
        assertEquals( "Oscar Comas", driver.getName() );
        assertEquals( "France", driver.getNationality());
        assertEquals( 0, driver.getTrophies());
        assertEquals( 40, driver.getGps() );
        assertEquals( 4, driver.getWins() );
        assertEquals( 14, driver.getPodiums() );
        assertEquals( 141, driver.getPoints() );
        assertEquals( 3, driver.getPoles() );
        assertEquals( 2, driver.getFastestLaps() );
        assertEquals( 1098848, driver.getSalary() );
        assertEquals( 16, driver.getContract() );
        assertEquals( zandvoort.getName(), driver.getFavoriteTracks().get( 0 ).getName() );
        assertEquals( ahvenisto.getName(), driver.getFavoriteTracks().get( 1 ).getName() );
        assertEquals( nurburgring.getName(), driver.getFavoriteTracks().get( 2 ).getName() );
        
        DriverAttributes att = driver.getAttributes();
        
        assertEquals( 108, att.getOverall() ); 
        assertEquals( 213, att.getConcentration() );
        assertEquals( 110, att.getTalent() );
        assertEquals( 0, att.getAggressiveness() );
        assertEquals( 55, att.getExperience() );
        assertEquals( 128, att.getTechInsight() );
        assertEquals( 98, att.getStamina() );
        assertEquals( 50, att.getCharisma() );
        assertEquals( 126, att.getMotivation() );
        assertEquals( 0, att.getReputation() );
        assertEquals( 58, att.getWeight() );
        assertEquals( 22, att.getAge() );        
    }
}