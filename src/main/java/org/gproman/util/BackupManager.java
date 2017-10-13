package org.gproman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;

public class BackupManager {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger( BackupManager.class );

    public static boolean backup(File inputDir,
                                 File outputDir,
                                 String sufix, 
                                 int keep) {
        logger.info( "Creating backup for directory: " + inputDir.getName() );
        List<File> files = getAllFiles( inputDir, new ArrayList<File>() );
        writeZipFile( inputDir, 
                      files, 
                      sufix, 
                      outputDir );
        removeOldFiles( outputDir, keep ); 
        
        return true;
    }

    private static void removeOldFiles(File outputDir,
                                       int keep) {
        File[] existing = outputDir.listFiles( new FilenameFilter() {
            @Override
            public boolean accept(File dir,
                                  String name) {
                return name != null && name.startsWith( "gmt.data." ) && name.endsWith( ".zip" );
            }
            
        });
        if( existing.length > keep ) {
            Arrays.sort( existing, new Comparator<File>() {
                @Override
                public int compare(File o1,
                                   File o2) {
                    try {
                        return o1.getCanonicalPath().compareTo( o2.getCanonicalPath() );
                    } catch ( IOException e ) {
                        return 0;
                    }
                }
            });
            for( int i = 0; i < (existing.length-keep); i++ ) {
                existing[i].delete();
            }
        }
    }

    private static List<File> getAllFiles(File dir,
                                          List<File> fileList) {
        File[] files = dir.listFiles();
        for ( File file : files ) {
            fileList.add( file );
            if ( file.isDirectory() ) {
                getAllFiles( file, fileList );
            }
        }
        return fileList;
    }

    private static void writeZipFile(File directoryToZip,
                                     List<File> fileList,
                                     String sufix,
                                     File outputDir ) {
        try {
            String fileName = outputDir.getCanonicalPath() + "/gmt.data."+ sufix + ".zip";
            logger.info( "Creating backup file: "+fileName );
            FileOutputStream fos = new FileOutputStream( fileName );
            ZipOutputStream zos = new ZipOutputStream( fos );

            String prefix = directoryToZip.getCanonicalPath(); 
            if( prefix.lastIndexOf( "/" ) > 0 ) {
                prefix = prefix.substring( 0, prefix.lastIndexOf( "/" ) );
            }
            for ( File file : fileList ) {
                if ( !file.isDirectory() ) { // we only zip files, not directories
                    addToZip( prefix, file, zos );
                }
            }

            zos.close();
            fos.close();
        } catch ( Exception e ) {
            logger.error( "Error creating backup file", e );
        }
    }

    private static void addToZip(String prefix,
                                 File file,
                                 ZipOutputStream zos) throws FileNotFoundException,
                                                     IOException {

        FileInputStream fis = new FileInputStream( file );
        String zipFilePath = file.getCanonicalPath().substring( prefix.length() + 1,
                                                                file.getCanonicalPath().length() );
        logger.debug( "Writing '" + zipFilePath + "' to zip file" );
        ZipEntry zipEntry = new ZipEntry( zipFilePath );
        zos.putNextEntry( zipEntry );

        byte[] bytes = new byte[1024];
        int length;
        while ( (length = fis.read( bytes )) >= 0 ) {
            zos.write( bytes, 0, length );
        }

        zos.closeEntry();
        fis.close();
    }

}
