package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.car.PHA;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.RaceReport;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PastSetupWorkerTest {
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
        RaceReport report = execScrapper( "PastSetups1.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 4, report.getRace().intValue() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups2.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 2, report.getRace().intValue() );
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups3.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 1, report.getRace().intValue() );
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups4.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 4, report.getRace().intValue() );
    }

    @Test
    public void testParsePage5() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups5.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 4, report.getRace().intValue() );
        
        assertEquals( 120, report.getStartingFuel().intValue() );
        assertEquals( 10, report.getFinishFuel().intValue() );
        assertEquals( 37, report.getFinishTyre().intValue() );
    }

    @Test
    public void testParsePage9() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups9.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 15, report.getRace().intValue() );
        
        assertEquals( 48, report.getStartingFuel().intValue() );
        assertEquals( 31, report.getFinishFuel().intValue() );
        assertEquals( 76, report.getFinishTyre().intValue() );
    }

    @Test
    public void testParsePage10() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups10.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 17, report.getRace().intValue() );
        
        assertEquals( 105, report.getStartingFuel().intValue() );
        assertEquals( 11, report.getFinishFuel().intValue() );
        assertEquals( 26, report.getFinishTyre().intValue() );
    }

    @Test
    public void testParsePage11() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups11.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 17, report.getRace().intValue() );
        
        assertEquals( 83, report.getStartingFuel().intValue() );
        assertNull( report.getFinishFuel() );
        assertNull( report.getFinishTyre() );
    }

    @Test
    public void testParsePage14Wet() throws FailingHttpStatusCodeException,
            IOException {
        RaceReport report = execScrapper( "PastSetups14.html" );
        assertEquals( 43, report.getSeason().intValue() );
        assertEquals( 2, report.getRace().intValue() );

        assertEquals( 10, report.getRiskOvertake().intValue() );
        assertEquals( 20, report.getRiskDefend().intValue() );
        assertEquals( 30, report.getRiskClear().intValue() );
        assertEquals( 40, report.getRiskClearWet().intValue() );
        assertEquals( 50, report.getRiskMalfunction().intValue() );
    }

    @Test
    public void testParsePageAbandon() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups_abandon.html" );
        assertEquals( 34, report.getSeason().intValue() );
        assertEquals( 16, report.getRace().intValue() );
        assertEquals( 82, report.getStartingFuel().intValue() );
        assertNull( report.getFinishFuel() );
        assertNull( report.getFinishTyre() );
    }

    @Test
    public void testParsePage14() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups14_changeDriverAttrs.html" );
        assertEquals( 39, report.getSeason().intValue() );
        assertEquals( 2, report.getRace().intValue() );

        Driver d = report.getDriver();
        DriverAttributes a = d.getAttributes();
        assertEquals( 125, a.getOverall() );
        assertEquals( 243, a.getConcentration() );
        assertEquals( 187, a.getTalent() );
        assertEquals( 7, a.getAggressiveness() );
        assertEquals( 94, a.getExperience() );
        assertEquals( 81, a.getTechInsight() );
        assertEquals( 87, a.getStamina() );
        assertEquals( 148, a.getCharisma() );
        assertEquals( 4, a.getMotivation() );
        assertEquals( 0, a.getReputation() );
        assertEquals( 64, a.getWeight() );
        assertEquals( 28, a.getAge() );
    }

    @Test
    public void testParsePage15() throws FailingHttpStatusCodeException,
                                IOException {
        RaceReport report = execScrapper( "PastSetups15_changedS39R05.html" );
        assertEquals( 39, report.getSeason().intValue() );
        assertEquals( 5, report.getRace().intValue() );
        
        PHA pha = report.getCarStart().getPHA();
        assertEquals( 91, pha.getP() );
        assertEquals( 92, pha.getH() );
        assertEquals( 92, pha.getA() );
    }

    @Test
    public void testParsePage16() throws FailingHttpStatusCodeException,
                                         IOException {
        RaceReport report = execScrapper( "PastSetups15.html" );
        assertEquals( 46, report.getSeason().intValue() );
        assertEquals( 1, report.getRace().intValue() );

        PHA pha = report.getCarStart().getPHA();
        assertEquals( 107, pha.getP() );
        assertEquals( 104, pha.getH() );
        assertEquals( 103, pha.getA() );
    }

    private RaceReport execScrapper(String fileName) throws IOException {
        URL url = getClass().getResource( fileName );
        assertNotNull( url );

        HtmlPage page = client.getPage( url );
        assertNotNull( page );

        PastSetupWorker worker = new PastSetupWorker( page );

        return worker.parsePage( page );

    }

}
