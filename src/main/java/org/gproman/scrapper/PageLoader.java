package org.gproman.scrapper;

import java.net.URL;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PageLoader
        implements
        Callable<HtmlPage> {

    private final static Logger logger = LoggerFactory.getLogger( PageLoader.class );

    private final WebConnection wc;
    private final String    url;

    public PageLoader(WebConnection wc,
                      String url) {
        this.wc = wc;
        this.url = url;
    }

    public PageLoader(WebConnection wc,
                      URL url) {
        this.wc = wc;
        this.url = url.toString();
    }

    @Override
    public HtmlPage call() throws Exception {
        try {
            HtmlPage page = wc.getPage( url );
            return page;
        } catch( Exception e ) {
            logger.error("Error loading page: "+url, e );
            throw e;
        }
    }

}
