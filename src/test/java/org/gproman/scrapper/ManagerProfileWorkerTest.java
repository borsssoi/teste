package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.Manager;
import org.gproman.model.SeasonHistory;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ManagerProfileWorkerTest {

    private WebClient client;

    @Before
    public void setup() {
        client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
    }

    @Test
    public void testParsePage1() throws FailingHttpStatusCodeException,
            IOException {
        Manager manager = execScrapper("Manager1.html");
        assertEquals("Edson Tirelli", manager.getName());
        
        System.out.println(manager);
        for( SeasonHistory season : manager.getSeasonHistory() ) {
            System.out.println(season);
        }
    }

    private Manager execScrapper(String fileName) throws IOException {
        URL url = getClass().getResource(fileName);
        assertNotNull(url);

        HtmlPage page = client.getPage(url);
        assertNotNull(page);

        ManagerProfileWorker worker = new ManagerProfileWorker(null);

        return worker.parsePage(page);
    }

}
