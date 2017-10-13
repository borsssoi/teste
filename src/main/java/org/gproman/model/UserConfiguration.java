package org.gproman.model;

import java.util.Properties;

public class UserConfiguration extends Properties {
    private static final long serialVersionUID = -7009150325024375957L;
    
    public static final String PROP_DATABASE_DIR = "gmt.app.database";
    public static final String PROP_CHECK_UPDATES = "gmt.app.checkUpdates";
    private static final String PROP_BACKUP_DIR = "gmt.app.backup.dir";
    private static final String PROP_BACKUP_KEEP = "gmt.app.backup.keep";
    public static final String PROP_EVEREST_DIR = "gmt.app.everest.dir";
    public static final String PROP_GPRO_URL = "gmt.app.gpro.url";

    // proxy configurations
    private static final String PROP_PROXY_TYPE = "gmt.app.proxy.type";
    private static final String PROP_PROXY_ADDR = "gmt.app.proxy.addr";
    private static final String PROP_PROXY_PORT = "gmt.app.proxy.port";
    private static final String PROP_PROXY_USER = "gmt.app.proxy.user";
    private static final String PROP_PROXY_PWD  = "gmt.app.proxy.pwd";
    
    public UserConfiguration() {
    }
    
    @Override
    public synchronized UserConfiguration clone() {
        UserConfiguration clone = new UserConfiguration();
        clone.putAll( this );
        return clone;
    }

    public synchronized void initializeDefaultProperties() {
        setDatabaseDir( "data" );
        setEverestDir( "everest" );
        setCheckUpdates( true );
        setBackupDir( "." );
        setBackupKeep( 17 );
        setProxyType(ProxyType.NO_PROXY);
        setGproUrl("http://gpro.net");
    }
    
    public synchronized String getDatabaseDir() {
        return getProperty( PROP_DATABASE_DIR );
    }
    
    public synchronized void setDatabaseDir( String value ) {
        setProperty( PROP_DATABASE_DIR, value );
    }
    
    public synchronized String getEverestDir() {
        return getProperty( PROP_EVEREST_DIR );
    }
    
    public synchronized void setEverestDir( String value ) {
        setProperty( PROP_EVEREST_DIR, value );
    }
    
    public synchronized String getGproUrl() {
        return getProperty( PROP_GPRO_URL );
    }
    
    public synchronized void setGproUrl( String value ) {
        setProperty( PROP_GPRO_URL, value );
    }
    
    public synchronized boolean isCheckUpdates() {
        return Boolean.valueOf( getProperty( PROP_CHECK_UPDATES ) );
    }
    
    public synchronized void setCheckUpdates( boolean checkUpdates ) {
        setProperty( PROP_CHECK_UPDATES, String.valueOf( checkUpdates ) );
    }

    public synchronized String getBackupDir() {
        return getProperty( PROP_BACKUP_DIR );
    }
    
    public synchronized void setBackupDir( String value ) {
        setProperty( PROP_BACKUP_DIR, value );
    }
    
    public synchronized int getBackupKeep() {
        return Integer.parseInt( getProperty( PROP_BACKUP_KEEP ) );
    }
    
    public synchronized void setBackupKeep( int value ) {
        setProperty( PROP_BACKUP_KEEP, String.valueOf(value) );
    }
    
    public synchronized ProxyType getProxyType() {
        return ProxyType.determineType( getProperty( PROP_PROXY_TYPE ) );
    }
    
    public synchronized void setProxyType( ProxyType proxyType ) {
        setProperty( PROP_PROXY_TYPE, proxyType.toString() );
    }
    
    public synchronized String getProxyAddr() {
        return getProperty( PROP_PROXY_ADDR );
    }
    
    public synchronized void setProxyAddr( String value ) {
        setProperty( PROP_PROXY_ADDR, value );
    }
    
    public synchronized int getProxyPort() {
        return Integer.parseInt( getProperty( PROP_PROXY_PORT, "1080" ) );
    }
    
    public synchronized void setProxyPort( int value ) {
        setProperty( PROP_PROXY_PORT, String.valueOf( value ) );
    }
    
    public synchronized String getProxyUser() {
        return getProperty( PROP_PROXY_USER );
    }
    
    public synchronized void setProxyUser( String value ) {
        setProperty( PROP_PROXY_USER, value );
    }
    
    public synchronized String getProxyPassword() {
        return getProperty( PROP_PROXY_PWD );
    }
    
    public synchronized void setProxyPassword( String value ) {
        setProperty( PROP_PROXY_PWD, value );
    }
    
    public static enum ProxyType {
        NO_PROXY, AUTO, MANUAL;
        
        public static ProxyType determineType( String type ) {
            if( type != null ) {
                if( AUTO.toString().equalsIgnoreCase(type) ) {
                    return AUTO;
                } else if( MANUAL.toString().equalsIgnoreCase(type) ) {
                    return MANUAL;
                }
            }
            return NO_PROXY;
        }
    }
    
}
