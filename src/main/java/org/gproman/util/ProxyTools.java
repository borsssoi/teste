package org.gproman.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyTools {
    
    private static final String GPRO_BR_URL = "http://gprobrasil.com/";
    
    private static final Logger logger = LoggerFactory.getLogger( ProxyTools.class );
    
    private static InetSocketAddress proxyAddr = null;
    private static boolean proxyChecked = false;

    public static InetSocketAddress detectProxySettings() {
        if( proxyChecked ) {
            return proxyAddr;
        }
        try {
            logger.info( "Detecting proxy settings..." );
            System.setProperty( "java.net.useSystemProxies", "true" );
            List<Proxy> l = ProxySelector.getDefault().select( new URI( GPRO_BR_URL ) );

            for ( Proxy proxy : l ) {
                logger.info( "proxy type : " + proxy.type() );
                InetSocketAddress addr = (InetSocketAddress) proxy.address();
                if( addr != null ) {
                    logger.info( "proxy detected: "+addr.getHostName()+":"+addr.getPort() );
                }
                proxyAddr = addr;
                proxyChecked = true;
                return addr;
            }
        } catch ( Exception e ) {
            logger.error("Error detecting proxy: ", e);
        }
        proxyChecked = true;
        return null;
    }
}
