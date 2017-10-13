package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.URL;

import org.gproman.db.DataService;
import org.gproman.model.season.OfficeData;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OfficeDataWorkerTest {
    private DataService db;
    private WebClient   client;

    @Before
    public void setup() {
        db = mock( DataService.class );
        client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
    }

    @Test
    public void testParseOfficeNewStyle() throws FailingHttpStatusCodeException,
                                         IOException {
        OfficeData data = executeTest( "Office_newStyle_1.html" );
        assertEquals( 34, data.getSeason() );
        assertEquals( 5, data.getNextRace() );
    }

    @Test
    public void testParseOfficeOldStyle() throws FailingHttpStatusCodeException,
                                         IOException {
        OfficeData data = executeTest( "Office_oldStyle_1.html" );
        assertEquals( 34, data.getSeason() );
        assertEquals( 5, data.getNextRace() );
    }

    @Test
    public void testParseNewStyleSeasonReset() throws FailingHttpStatusCodeException,
                                              IOException {
        OfficeData data = executeTest( "Office_newStyle_SeasonReset.html" );
        assertEquals( -1, data.getSeason() );
        assertEquals( -1, data.getNextRace() );
    }

    private OfficeData executeTest(String fileName) throws FailingHttpStatusCodeException, IOException {
        URL url = getClass().getResource( fileName );
        assertNotNull( url );

        HtmlPage officePage = client.getPage( url );
        assertNotNull( officePage );

        OfficeWorker worker = new OfficeWorker( db,
                                                officePage );

        OfficeData data = worker.parsePage( db,
                                            officePage );

        return data;
    }

}
