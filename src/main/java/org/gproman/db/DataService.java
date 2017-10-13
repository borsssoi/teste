package org.gproman.db;

import java.util.List;

import org.gproman.model.ApplicationStatus;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.WearPlan;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.track.Track;
import org.gproman.model.track.WearCoefs;


public interface DataService {
    
    public static final int     CURRENT_SCHEMA_VERSION = 37;

    // General service lifecycle methods
    public int start();

    public void shutdown();
    
    public boolean isInitialized();
    
    // General reference data access
    public ApplicationStatus getApplicationStatus();
    
    public void store( ApplicationStatus status );
    
    public Track getTrackById(Integer trackId);
    
    public Track getTrackByName(String name);
    
    public List<Track> getAllTracks();

    public void store( Track track );
    
    public Manager getManager();

    public void store( Manager manager );
    
    public WearCoefs getWearCoefs();

    public void store( WearCoefs wp );
    
    public DriverWearWeight getDriverAttributesWearWeight();

    public void store( DriverWearWeight daww );
    
    // Driver related methods 
    public Driver getDriver( Integer id );

    public void store( Driver driver );
    
    // Car related methods
    public Car getCar( Integer id );

    public void store( Car car );
    
    public Race getNextRace();

    public Race getRace(Integer season,
                        Integer race);
    
    public void store( String managerName, Race race );

    public List<Integer> getSeasonsForSetup();
    
    public List<Integer> getSeasonsForTelemetry();
    
    public List<Race> getRacesForSetup(Integer season);
    
    public List<Race> getRacesForTelemetry(Integer season);

    public List<Integer> getSeasonsForTest();

    public List<Race> getRacesForTest(Integer season);

    public Season getSeason(String managerName, Integer season);
    
    public Season getCurrentSeason(String managerName);

    public void store(Season season);

    public void store(WearPlan plan);
    
    public void delete(WearPlan plan);
    
    public List<WearPlan> loadWearPlans();

    public boolean isFirstExecutionForSeason();
    
    public void store( TyreSupplierAttrs attrs );
    
    public TyreSupplierAttrs getTyreSupplier( Integer seasonNumber, String supplierName );
    
    public List<TyreSupplierAttrs> getTyreSuppliersForSeason( Integer seasonNumber );

    /**
     * Returns true if the database was modified since the time
     * it was started.
     * @return
     */
    public boolean wasModified();

    public List<Season> getSeasonsWithNullGroups();

}