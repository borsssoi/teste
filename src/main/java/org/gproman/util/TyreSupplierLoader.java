package org.gproman.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.gproman.model.season.TyreSupplier;
import org.gproman.model.season.TyreSupplierAttrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyreSupplierLoader {
    private static final Logger logger = LoggerFactory.getLogger(TyreSupplierLoader.class); 
    
    public static List<TyreSupplierAttrs> loadSuppliers( String filename, InputStream is ) {
        List<TyreSupplierAttrs> attrs = new ArrayList<TyreSupplierAttrs>();
        logger.info("Loading tyre supplier records from file "+filename);
        BufferedReader in = new BufferedReader( new InputStreamReader(is) );
        try {
            String line = in.readLine(); // skip title
            while( ( line = in.readLine() ) != null ) {
                String[] fields = line.trim().split(";");
                if( fields.length == 8 ) {
                    TyreSupplierAttrs s = new TyreSupplierAttrs();
                    int i = 0;
                    s.setSeasonNumber( Integer.valueOf( fields[i++].trim() ) );
                    s.setSupplier( TyreSupplier.determineTyre(fields[i++].trim()));
                    s.setDry(Integer.valueOf( fields[i++].trim() ) );
                    s.setWet(Integer.valueOf( fields[i++].trim() ) );
                    s.setPeak(Integer.valueOf( fields[i++].trim() ) );
                    s.setDurability(Integer.valueOf( fields[i++].trim() ) );
                    s.setWarmup(Integer.valueOf( fields[i++].trim() ) );
                    s.setCost(Integer.valueOf( fields[i++].trim().replaceAll("\\.", "" ) ) );
                    attrs.add(s);
                } else {
                    logger.error("Invalid record field count: "+line);
                }
            }
            logger.info("Successfuly loaded "+attrs.size()+" tyre supplier records.");
        } catch (IOException e) {
            logger.error("Error loading tyre supplier data from the file");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // nothing to do
            }
        }
        return attrs;
    }

}
