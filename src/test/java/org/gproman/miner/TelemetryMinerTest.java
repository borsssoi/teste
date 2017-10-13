package org.gproman.miner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gproman.db.DataService;
import org.gproman.miner.TelemetryMiner.TelemetryMinerWorker;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.track.Track;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


@Ignore("too heavy")
public class TelemetryMinerTest {

    private TelemetryMiner miner;
    private DataService db;

    @Before
    public void setup() {
        db = mock(DataService.class);
        Track track = new Track();
        track.setDistance(306);
        track.setLapDistance(3.825);
        when( db.getTrackByName( any(String.class) ) ).thenReturn( track );
        miner = new TelemetryMiner( db, null);
    }

    @Test
    public void testParsePageCountMultiple() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page1 = loadPage("GproBrTelemetryMultiple_p1.html");
        int pageCount = miner.parsePageCount(page1);
        assertEquals(4, pageCount);
    }

    @Test
    public void testParsePageCountSingle() throws FailingHttpStatusCodeException, IOException {
        HtmlPage page1 = loadPage("GproBrTelemetrySingle.html");
        int pageCount = miner.parsePageCount(page1);
        assertEquals(1, pageCount);
    }

    @Test
    public void testMiner1() throws Exception {
        List<HtmlPage> pages = new ArrayList<HtmlPage>();
        pages.add(loadPage("GproBrTelemetryMultiple_p1.html"));
        pages.add(loadPage("GproBrTelemetryMultiple_p2.html"));
        pages.add(loadPage("GproBrTelemetryMultiple_p3.html"));
        pages.add(loadPage("GproBrTelemetryMultiple_p4.html"));
        TelemetryMinerWorker worker = new TelemetryMinerWorker(db, pages, null, 0, 0, true);
        MiningResult result = worker.call();
    }

    @Test @Ignore("connects to the network")
    public void testMiner2() throws Exception {
        UserCredentials credentials = CredentialsManager.loadCredentials();
        UserConfiguration configuration = ConfigurationManager.loadConfiguration();
        miner = new TelemetryMiner(db, new GPROBrUtil(credentials, configuration));
        String url = "http://gprobrasil.com/topic/7651558";

        MiningResult result = miner.mine(url, null, 0, 0, true);
    }

    @Test
    public void testMiner3() throws Exception {
        List<HtmlPage> pages = new ArrayList<HtmlPage>();
        pages.add(loadPage("GproBrTelemetrySingle2.html"));
        TelemetryMinerWorker worker = new TelemetryMinerWorker(db, pages, null, 0, 0, true);
        MiningResult result = worker.call();
    }

    @Test
    public void testMiner4() throws Exception {
        List<HtmlPage> pages = new ArrayList<HtmlPage>();
        pages.add(loadPage("GproBrTelemetryMultiple2_p1.html"));
        TelemetryMinerWorker worker = new TelemetryMinerWorker(db, pages, null, 0, 0, true);
        MiningResult result = worker.call();
        System.out.println(result.toString());
    }

    private HtmlPage loadPage(String fileName) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        URL url = getClass().getResource(fileName);
        assertNotNull(url);

        HtmlPage page1 = client.getPage(url);
        assertNotNull(page1);
        return page1;
    }

}