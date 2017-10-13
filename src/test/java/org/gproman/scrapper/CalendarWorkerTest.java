package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.URL;

import javax.swing.ProgressMonitor;

import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CalendarWorkerTest {
    private DataService    db;
    private CalendarWorker worker;

    @Before
    public void setup() {
        db = mock( DataService.class );
        Manager manager = mock( Manager.class );
        HtmlPage office = mock( HtmlPage.class );
        ProgressMonitor pm = mock( ProgressMonitor.class );
        worker = new CalendarWorker( db,
                                     manager,
                                     office,
                                     34,
                                     pm );
    }

    @Test
    public void testParsePage() throws FailingHttpStatusCodeException,
                               IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        URL url = getClass().getResource( "Calendar1.html" );
        assertNotNull( url );

        HtmlPage calPage = client.getPage( url );
        assertNotNull( calPage );

        Season season = new Season();
        worker.parsePage( db,
                          season,
                          calPage );

        assertEquals( 17, season.getRaces().size() );
        int c = 1;
        for ( Race race : season.getRaces() ) {
            assertEquals( c++, race.getNumber() );
        }
        //System.out.println(season);
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
                                IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        URL url = getClass().getResource( "Calendar2.html" );
        assertNotNull( url );

        HtmlPage calPage = client.getPage( url );
        assertNotNull( calPage );

        Season season = new Season();
        worker.parsePage( db,
                          season,
                          calPage );

        assertEquals( 17, season.getRaces().size() );
        int c = 1;
        for ( Race race : season.getRaces() ) {
            assertEquals( c++, race.getNumber() );
        }
        //System.out.println(season);
    }
}
