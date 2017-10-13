package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.race.RaceReport;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RaceAnalysisWorkerTest {
    private WebClient   client;

    @Before
    public void setup() {
        client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "RaceAnalysis1.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 4, report.getRace().intValue() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "RaceAnalysis2.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 10, report.getRace().intValue() );
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "RaceAnalysis3_norefill.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 13, report.getRace().intValue() );
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "RaceAnalysis4.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 16, report.getRace().intValue() );
        assertEquals( 82, report.getStartingFuel().intValue() );
        assertNull( report.getFinishFuel() );
        assertNull( report.getFinishTyre() );
    }

    private RaceReport execScrapper(String fileName) throws IOException {
        URL url = getClass().getResource( fileName );
        assertNotNull( url );

        HtmlPage page = client.getPage( url );
        assertNotNull( page );

        RaceAnalysisWorker worker = new RaceAnalysisWorker( page );

        return worker.parsePage( page );

    }

}
