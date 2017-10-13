package org.gproman.update;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.gproman.GproManager;
import org.gproman.Version;

public class UpdateInfo {
    
    public static final String PREFIX = "gmt.ver.";
    public static final String LATEST = "gmt.latest";
    public static final String URL_SUFIX = ".url";
    public static final String MD5_SUFIX = ".md5";
    
    private Version latest;
    private String url;
    private String md5;
    
    public UpdateInfo(Properties prop) {
        this.latest = new Version( prop.getProperty( LATEST ) );
        this.url = prop.getProperty( PREFIX+this.latest+URL_SUFIX );
        this.md5 = prop.getProperty( PREFIX+this.latest+MD5_SUFIX );
    }
    
    public UpdateInfo(Version latest,
                      String url,
                      String md5) {
        this.latest = latest;
        this.url = url;
        this.md5 = md5;
    }

    public Version getLatestVersion() {
        return latest;
    }
    public void setLatest(Version latest) {
        this.latest = latest;
    }
    public String getLatestUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getLatestMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public Properties toProperties() {
        Properties prop = new Properties();
        prop.setProperty( LATEST, latest.toString() );
        prop.setProperty( PREFIX+this.latest+URL_SUFIX, url );
        prop.setProperty( PREFIX+this.latest+MD5_SUFIX, md5 );
        return prop;
    }

    @Override
    public String toString() {
        return "UpdateInfo [latest=" + latest + ", url=" + url + ", md5=" + md5 + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((latest == null) ? 0 : latest.hashCode());
        result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        UpdateInfo other = (UpdateInfo) obj;
        if ( latest == null ) {
            if ( other.latest != null ) return false;
        } else if ( !latest.equals( other.latest ) ) return false;
        if ( md5 == null ) {
            if ( other.md5 != null ) return false;
        } else if ( !md5.equals( other.md5 ) ) return false;
        if ( url == null ) {
            if ( other.url != null ) return false;
        } else if ( !url.equals( other.url ) ) return false;
        return true;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // this is a quick hack to create the info file
        // TODO: develop a proper script to do it during maven build
        UpdateInfo info = new UpdateInfo( GproManager.getVersion(), 
                                          "http://", 
                                          "AAA" );
        Properties prop = info.toProperties();
        FileOutputStream fos = new FileOutputStream( "gmt.update.properties" );
        prop.store( fos, "Update information for the GPRO Manager's Toolbox application" );
        fos.close();
    }
    
}
