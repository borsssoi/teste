package org.gproman.db;

import java.util.List;

import org.gproman.db.everest.dao.SearchParams;
import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.EverestStatus;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.track.Track;

public interface EverestService {
    
    public static final int     CURRENT_SCHEMA_VERSION = 6;

    // General service lifecycle methods
    public int start();

    public void shutdown();
    
    public boolean isInitialized();
    
    public boolean wasModified();

    // General reference data access
    public EverestStatus getEverestStatus();
    
    public EverestMetrics getEverestMetrics();
    
    public void store( EverestStatus status );
    
    public ForumTopic getForumTopic( TopicType type, Integer season, Integer race );
    
    public void store( ForumTopic topic );
    
    public void deleteAllForumTopic();
    
    // Track methods
    public Track getTrackById(Integer trackId);
    
    public Track getTrackByName(String name);

    public void store( Track track );
    
    public List<Track> getAllTracks();

    // Race methods
    public void store( NormalizedRace race );
    
    public List<NormalizedRace> getRaces( Integer season, Integer race );
    
    public List<NormalizedRace> getRaces( SearchParams param );

    // Tyre Supplier
    public void store( TyreSupplierAttrs attrs );
    
    public TyreSupplierAttrs getTyreSupplier( Integer seasonNumber, String supplierName );
    
    public List<TyreSupplierAttrs> getTyreSuppliersForSeason( Integer seasonNumber );

}