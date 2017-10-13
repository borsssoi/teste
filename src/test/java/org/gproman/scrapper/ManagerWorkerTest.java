package org.gproman.scrapper;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.gproman.model.Manager;
import org.gproman.model.SeasonHistory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ManagerWorkerTest {

    private WebClient client;

    @Before
    public void setup() {
        client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException,
            IOException {
        Manager manager = execScrapper("Office_newStyle_1.html");

        assertEquals( 30225, manager.getId().intValue() );
        assertEquals( "Edson Tirelli", manager.getName() );
        assertEquals( "Amateur - 8", manager.getGroup() );
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
                                        IOException {
        Manager manager = execScrapper("Office_newStyle_2.html");

        assertEquals( 30225, manager.getId().intValue() );
        assertEquals("Edson Tirelli", manager.getName() );
        assertEquals( "Master - 5", manager.getGroup() );
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException,
                                        IOException {
        Manager manager = execScrapper("Office_oldStyle_1.html");

        assertEquals( 30225, manager.getId().intValue() );
        assertEquals("Edson Tirelli", manager.getName() );
        assertEquals( "Amateur - 8", manager.getGroup() );
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException,
                                        IOException {
        Manager manager = execScrapper("Office_oldStyle_2.html");

        assertEquals( 30225, manager.getId().intValue() );
        assertEquals("Edson Tirelli", manager.getName() );
        assertEquals( "Master - 5", manager.getGroup() );
    }

    private Manager execScrapper(String fileName) throws IOException {
        URL url = getClass().getResource(fileName);
        assertNotNull( url );

        HtmlPage page = client.getPage(url);
        assertNotNull( page );

        ManagerWorker worker = new ManagerWorker(page);

        return worker.call();
    }

}
