package org.gproman.scrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.Set;

import org.gproman.GproManager;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserConfiguration.ProxyType;
import org.gproman.util.ProxyTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

public class WebConnection {
    private static final Logger   logger    = LoggerFactory.getLogger( WebConnection.class );

    private WebClient webClient;
    private UserConfiguration conf;
    
    private String proxyHost;
    private int proxyPort;
    
    private static final BrowserVersion browser = new BrowserVersion("GMT", GproManager.getVersionString(), "GMT/"+GproManager.getVersionString(), 1) {
        private static final long serialVersionUID = -1087085863163112186L;
        public boolean hasFeature(BrowserVersionFeatures property) {
            return BrowserVersion.CHROME.hasFeature(property);
        }            
    };
    
    public WebConnection( UserConfiguration conf ) {
        this.conf = conf;
        if( ProxyType.NO_PROXY.equals( conf.getProxyType() ) ) {
            logger.info("Creating web connection with no proxy.");
            this.webClient = new WebClient( browser );
        } else if( ProxyType.AUTO.equals(conf.getProxyType()) ) {
            logger.info("Creating web connection with auto detected proxy.");
            InetSocketAddress proxy = ProxyTools.detectProxySettings();
            if ( proxy != null ) {
                proxyHost = proxy.getHostName();
                proxyPort = proxy.getPort();
                this.webClient = new WebClient( browser, proxyHost, proxyPort );
            } else {
                this.webClient = new WebClient( browser );
            }
        } else {
            logger.info("Creating web connection with manually configured proxy = "+conf.getProxyAddr()+":"+conf.getProxyPort());
            this.webClient = new WebClient( browser, conf.getProxyAddr(), conf.getProxyPort() );
            String proxyUser = conf.getProxyUser();
            if( proxyUser != null && proxyUser.length() > 0 ) {
                final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
                credentialsProvider.addCredentials(proxyUser, conf.getProxyPassword());        
            }
        }
        setOptions( webClient );
    }
    
    /**
     * Constructor using for cloning to avoid running proxy detection
     * @param conf
     * @param host
     * @param port
     */
    private WebConnection( UserConfiguration conf, String host, int port ) {
        logger.info("Cloning connection with proxy = "+conf.getProxyType() );
        this.conf = conf;
        if( ProxyType.NO_PROXY.equals( conf.getProxyType() ) ) {
            logger.info("Cloning web connection with no proxy.");
            this.webClient = new WebClient( browser );
        } else if( ProxyType.AUTO.equals(conf.getProxyType()) ) {
            logger.info("Cloning web connection with auto detected proxy = "+host+":"+port);
            proxyHost = host;
            proxyPort = port;
            if ( host != null ) {
                this.webClient = new WebClient( browser, proxyHost, proxyPort );
            } else {
                this.webClient = new WebClient( browser );
            }
        } else {
            logger.info("Cloning web connection with manually configured proxy = "+conf.getProxyAddr()+":"+conf.getProxyPort());
            this.webClient = new WebClient( browser, conf.getProxyAddr(), conf.getProxyPort() );
            final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
            credentialsProvider.addCredentials(conf.getProxyUser(), conf.getProxyPassword());        
        }
        setOptions( webClient );
    }
    
    private final void setOptions( WebClient webClient ) {
        WebClientOptions options = webClient.getOptions();
        options.setJavaScriptEnabled( false );
        options.setCssEnabled( false );
        options.setDoNotTrackEnabled( true );
        options.setPopupBlockerEnabled( true );
        webClient.setRefreshHandler( new ThreadedRefreshHandler() );
        webClient.setWebConnection( new WebConnectionFilter( webClient.getWebConnection() ) );
    }

    public synchronized HtmlPage getPage(String urlStr) throws FailingHttpStatusCodeException,
                                                       MalformedURLException,
                                                       IOException {
        HtmlPage page = this.webClient.getPage( urlStr );
        return page;
    }

    public synchronized Set<Cookie> getCookies() {
        return webClient.getCookieManager().getCookies();
    }

    public synchronized void setCookies(Set<Cookie> cookies) {
        if ( cookies != null ) {
            for ( Cookie c : cookies ) {
                webClient.getCookieManager().addCookie( c );
            }
        }
    }

    public synchronized WebConnection clone() {
        WebConnection clone = new WebConnection( conf, proxyHost, proxyPort );
        clone.setCookies( getCookies() );
        return clone;
    }
    
    public static class WebConnectionFilter extends FalsifyingWebConnection {

        public WebConnectionFilter(com.gargoylesoftware.htmlunit.WebConnection webConnection) throws IllegalArgumentException {
            super( webConnection );
        }
        
        @Override
        public WebResponse getResponse(WebRequest request) throws IOException {
            if( request.getUrl().toString().contains( "www.facebook.com" ) ||
                request.getUrl().toString().contains( "delivery.zetabbs.com" ) ||
                request.getUrl().toString().contains( "delivery.heavyhearted.com" ) ||
                request.getUrl().toString().contains( "ads.pubmatic.com" ) ) {
                logger.info("Faking page: "+request.getUrl() );
                return createWebResponse( request, "", "text/plain" );
            } else {
                logger.info("Fetching page: "+request.getUrl() );
                return super.getResponse( request );
            }
        }
        
    }
    
    public void closeAllWindows() {
        this.webClient.closeAllWindows();
    }
    
    public UserConfiguration getConf() {
        return conf;
    }
    
}
