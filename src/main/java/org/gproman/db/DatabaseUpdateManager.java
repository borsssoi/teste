package org.gproman.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.gproman.GproManager;
import org.gproman.model.car.CarWearData;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.track.Track;
import org.gproman.util.CarWearSpreadsheetLoader;
import org.gproman.util.TyreSupplierLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUpdateManager {
    
    private static Logger logger = LoggerFactory.getLogger( DatabaseUpdateManager.class );
    
    private static final String REF_DATA_SOURCE = "/refdata.bin";
    private static final String SUPPLIER_DATA_SOURCE = "/tyresuppliers.csv";
    
    private static final int LAST_REFDATA_UPDATE = 37;
    private static final int LAST_EVEREST_UPDATE = 6;

    public static void initDatabase( GproManager gmt, DataService db, int previousSchemaVersion ) {
        if ( previousSchemaVersion < DataService.CURRENT_SCHEMA_VERSION ) {
            logger.info( "Populating database." );
            logger.info( "Loading reference data..." );
            CarWearSpreadsheetLoader loader = new CarWearSpreadsheetLoader();
            CarWearData spreadsheet = null;
            try {
                spreadsheet = loader.deserializeFromXML( DatabaseUpdateManager.class.getResourceAsStream( REF_DATA_SOURCE ) );
            } catch ( Exception e ) {
                logger.info( "Error initializing reference data. Impossible to continue.", e );
                gmt.terminateApplication();
                System.exit( 0 );
            }

            if( previousSchemaVersion < 1 ) {
                logger.info( "Populating reference data for schema version 1..." );
                db.store( spreadsheet.getWearPolinomialCoef() );
                db.store( spreadsheet.getDriverAttributesWearWeight() );
            }
            if( previousSchemaVersion < LAST_REFDATA_UPDATE ) {
                logger.info( "Populating track reference data for schema version "+LAST_REFDATA_UPDATE+"..." );
                for ( Track track : spreadsheet.getTracks() ) {
                    logger.debug( "Saving track " + track );
                    db.store( track );
                }
            }
            if( previousSchemaVersion < 28 ) {
                // fix possible duplicate race records
                fixRaceDuplicates( db );
            }

            logger.info( "Database initialization succeeded." );
        } else {
            logger.info( "Database file already initialized." );
        }
        
    }
    
    public static void initEverest( GproManager gmt, EverestService everest, int previousSchemaVersion ) {
        if ( previousSchemaVersion < EverestService.CURRENT_SCHEMA_VERSION ) {
            logger.info( "Populating everest." );
            
            if( previousSchemaVersion < LAST_EVEREST_UPDATE ) {
                logger.info( "Loading reference data..." );
                CarWearSpreadsheetLoader loader = new CarWearSpreadsheetLoader();
                CarWearData spreadsheet = null;
                try {
                    spreadsheet = loader.deserializeFromXML( DatabaseUpdateManager.class.getResourceAsStream( REF_DATA_SOURCE ) );
                } catch ( Exception e ) {
                    logger.info( "Error initializing reference data. Impossible to continue.", e );
                    gmt.terminateApplication();
                    System.exit( 0 );
                }

                logger.info( "Populating track reference data for schema version "+LAST_REFDATA_UPDATE+"..." );
                for ( Track track : spreadsheet.getTracks() ) {
                    logger.debug( "Saving track " + track );
                    everest.store( track );
                }
            }
            
            if( previousSchemaVersion < 2 ) {
                logger.info( "Populating tyre supplier attributes for schema version 2..." );
                List<TyreSupplierAttrs> list = TyreSupplierLoader.loadSuppliers(SUPPLIER_DATA_SOURCE, DatabaseUpdateManager.class.getResourceAsStream( SUPPLIER_DATA_SOURCE ) );
                for( TyreSupplierAttrs s : list ) {
                    everest.store(s);
                }
            }

            logger.info( "Everest initialization succeeded." );
        } else {
            logger.info( "Everest file already initialized." );
        }
        
    }

    private static void fixRaceDuplicates(DataService db) {
        if( db instanceof JDBCDataService ) {
            try {
                Connection conn = ((JDBCDataService)db).getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "select season_id, number, count(*) c from race "
                        + "group by season_id, number "
                        + "having c > 1 "
                        + "order by season_id, number" );

                ResultSet rs = ps.executeQuery();
                while( rs.next() ) {
                    int seasonId = rs.getInt( "SEASON_ID" );
                    int raceNumber = rs.getInt( "NUMBER" );
                    int count = rs.getInt( "C" );

                    PreparedStatement dups = conn.prepareStatement( "select * from race "
                                                                    + " where season_id=? and number=? "
                                                                    + " order by race_date" );
                    dups.setInt( 1, seasonId );
                    dups.setInt( 2, raceNumber );

                    ResultSet dupsrs = dups.executeQuery();
                    int raceId = -1;
                    int otherRaceId;
                    int finishFuel;
                    int finishTyre;
                    int carFinishId;
                    int driverFinishId;
                    if( dupsrs.next() ) {
                        // original record
                        raceId = dupsrs.getInt( "ID" );
                    }
                    while( dupsrs.next() ) {
                        otherRaceId = dupsrs.getInt( "ID" );
                        finishFuel = dupsrs.getInt( "FINISH_FUEL" );
                        finishTyre = dupsrs.getInt( "FINISH_TYRE" );
                        carFinishId = dupsrs.getInt( "CAR_FINISH_ID" );
                        driverFinishId = dupsrs.getInt( "DRIVER_FINISH_ID" );

                        PreparedStatement up1 = conn.prepareStatement("update race "
                                                                      + "   set finish_fuel = ?, finish_tyre = ?, car_finish_id = ?, driver_finish_id = ? "
                                                                      + " where id = ?");
                        up1.setInt( 1, finishFuel );
                        up1.setInt( 2, finishTyre );
                        up1.setInt( 3, carFinishId );
                        up1.setInt( 4, driverFinishId );
                        up1.setInt( 5, raceId );
                        up1.executeUpdate();

                        PreparedStatement up2 = conn.prepareStatement( "update pit "
                                                                       + "   set race_id = ? "
                                                                       + " where race_id = ?" );

                        up2.setInt( 1, raceId );
                        up2.setInt( 2, otherRaceId );
                        up2.executeUpdate();

                        PreparedStatement up3 = conn.prepareStatement( "update race_lap "
                                                                       + "   set race_id = ? "
                                                                       + " where race_id = ?" );

                        up3.setInt( 1, raceId );
                        up3.setInt( 2, otherRaceId );
                        up3.executeUpdate();

                        PreparedStatement del = conn.prepareStatement( "delete race where id = ?" );
                        del.setInt( 1, otherRaceId );
                        del.executeUpdate();
                    }
                    conn.commit();
                }
            } catch ( SQLException e ) {
                logger.error( "Error fixing duplicate race records pre-schema 41" );
            }
        }
    }

}
