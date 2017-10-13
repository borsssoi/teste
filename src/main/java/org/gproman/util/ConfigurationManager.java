package org.gproman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gproman.model.UserConfiguration;
import org.slf4j.Logger;

public class ConfigurationManager implements Cloneable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger( ConfigurationManager.class );
    private static final String CONFIGURATION_FILE = System.getProperty( "user.home" )+"/"+".gmt.conf";

    public static UserConfiguration loadConfiguration() {
        File file = new File( CONFIGURATION_FILE );
        UserConfiguration conf = new UserConfiguration();
        conf.initializeDefaultProperties();
        if( file.exists() ) {
            // load existing configuration file
            FileInputStream is = null;
            try {
                is = new FileInputStream( file );
                conf.load( is );
            } catch ( IOException e ) {
                logger.error( "Error loading configuration file...", e );
            } finally {
                if( is != null ) {
                    try {
                        is.close();
                    } catch ( IOException e ) {
                        // nothing to do
                    }
                }
            }
        } else {
            saveConfiguration( conf );
        }
        return conf;
    }

    public static void saveConfiguration(UserConfiguration conf) {
        File file = new File( CONFIGURATION_FILE );
        // save the default properties to a new file
        FileOutputStream out = null;
        try {
            out = new FileOutputStream( file );
            conf.store( out, "Configuração do GMT" );
        } catch ( IOException e ) {
            logger.error( "Error saving configuration file...", e );
        } finally {
            if( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    // nothing to do
                }
            }
        }
    }
}
