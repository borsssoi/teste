package org.gproman.scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;

import org.gproman.model.season.TyreSupplier;
import org.gproman.model.season.TyreSupplierAttrs;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TyreSupplierWorkerTest {

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
        TyreSupplierWorkerResult supplier = execScrapper("TyreSupplier_Pipirelli.html");
        assertEquals(TyreSupplier.PIPIRELLI, supplier.signed);
    }

    @Test
    public void testParsePage3() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSupplier_Pipirelli2.html");
        assertEquals(TyreSupplier.PIPIRELLI, supplier.signed);
    }

    @Test
    public void testParsePage2() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSupplier_Yokomama.html");
        assertEquals(TyreSupplier.YOKOMAMA, supplier.signed);
    }

    @Test
    public void testParsePage4() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSuppliers_NoneSelected.html");
        assertNull(supplier.signed);
    }

    @Test
    public void testParsePage5() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSupplier_dunnolop.html");
        assertEquals(TyreSupplier.DUNNOLOP, supplier.signed);
    }

    @Test
    public void testParsePage6() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSuppliers_S39_NoneSelected.html");
        for (TyreSupplierAttrs attrs : supplier.suppliers) {
            if (attrs.getSupplier().equals(TyreSupplier.PIPIRELLI)) {
                assertEquals(3, attrs.getDry().intValue());
                assertEquals(2, attrs.getWet().intValue());
                assertEquals(34, attrs.getPeak().intValue());
                assertEquals(1, attrs.getDurability().intValue());
                assertEquals(7, attrs.getWarmup().intValue());
                assertEquals(250000, attrs.getCost().intValue());
            } else if (attrs.getSupplier().equals(TyreSupplier.CONTIMENTAL)) {
                assertEquals(7, attrs.getDry().intValue());
                assertEquals(1, attrs.getWet().intValue());
                assertEquals(38, attrs.getPeak().intValue());
                assertEquals(8, attrs.getDurability().intValue());
                assertEquals(8, attrs.getWarmup().intValue());
                assertEquals(2500000, attrs.getCost().intValue());
            }
        }
        assertNull(supplier.signed);
    }

    @Test
    public void testParsePage7() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSuppliers_S39_FakeContimental.html");
        assertEquals(TyreSupplier.CONTIMENTAL, supplier.signed);
    }

    @Test
    public void testParsePage8() throws FailingHttpStatusCodeException,
            IOException {
        TyreSupplierWorkerResult supplier = execScrapper("TyreSuppliers_S39_Pipirelli.html");
        assertEquals(TyreSupplier.PIPIRELLI, supplier.signed);
    }

    private TyreSupplierWorkerResult execScrapper(String fileName) throws IOException {
        URL url = getClass().getResource(fileName);
        assertNotNull(url);

        HtmlPage page = client.getPage(url);
        assertNotNull(page);

        TyreSupplierWorker worker = new TyreSupplierWorker(null);

        return worker.parsePage(page);
    }

}
