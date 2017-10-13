package org.gproman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.gproman.ui.UIPlugin;
import org.gproman.ui.UIPlugin.UIPluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class PluginContextManager {
    private static final Logger logger = LoggerFactory.getLogger(PluginContextManager.class);
    
    public static final String PLUGIN_CTX_FOLDER = ".gmt.plugins";
	public static final String DEFAULT_ENCODING = "UTF-8";

    public static boolean saveContext(UIPlugin p, UIPluginContext context) {
        try {
            File folder = new File(PLUGIN_CTX_FOLDER);
            if( ! folder.exists() ) {
                folder.mkdirs();
            }
            if( folder.canWrite() ) {
                String ctxfile = getContextFileName(p, folder);
                XStream xs = getXStream();
                String xml = xs.toXML(context);
                FileOutputStream fos = new FileOutputStream(ctxfile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, DEFAULT_ENCODING);
                
        		try {
        			osw.write(xml);
        		} finally {
        			osw.close();
        			fos.close();
        		}
                
                return true;
            } else {
                logger.error("Unable to access cache folder to save plugin context for plugin "+p.getClass().getCanonicalName());
            }
        } catch (IOException e) {
            logger.error("Unable to save context file for plugin "+p.getClass().getCanonicalName(), e);
        }
        return false;
    }

    private static String getContextFileName(UIPlugin p, File folder) throws IOException {
        return folder.getCanonicalPath() + "/" + p.getClass().getCanonicalName() + ".context.xml";
    }
    
    public static UIPluginContext loadContext(UIPlugin p) {
        try {
            File folder = new File(PLUGIN_CTX_FOLDER);
            if( folder.exists() && folder.canRead() ) {
                String ctxfile = getContextFileName(p, folder);
                File file = new File(ctxfile);
                if( file.exists() && file.canRead() ) {
                    XStream xs = getXStream();
                    FileInputStream fis = null;
                    InputStreamReader isr = null;
                    UIPluginContext ctx = null;
                    try {
                        fis = new FileInputStream(file);
                        isr = new InputStreamReader(fis, DEFAULT_ENCODING);
                        ctx = (UIPluginContext) xs.fromXML(isr );
                    } finally {
                    	if( isr != null ) {
                            isr.close();
                        }
                        if( fis != null ) {
                            fis.close();
                        }
                    }
                    return ctx;
                }
            } else {
                logger.error("Unable to access cache folder to load plugin context for plugin "+p.getClass().getCanonicalName());
            }
        } catch (IOException e) {
            logger.error("Unable to load context file for plugin "+p.getClass().getCanonicalName(), e);
        }
        return null;
    }
    
    private static XStream getXStream() {
        XStream xstream = new XStream();
        return xstream;
    }
    

}
