package org.gproman.model;

import java.sql.Timestamp;

public class ApplicationStatus extends PersistentEntity {

    private Integer schemaVersion;
    
    private Timestamp lastDownload;

    private Integer season;
    private Integer nextRace;

    public ApplicationStatus() {
    }

    public ApplicationStatus(Integer id,
                             Timestamp lastDownload,
                             Integer season,
                             Integer nextRace,
                             Integer schemaVersion ) {
        super( id );
        this.lastDownload = lastDownload;
        this.season = season;
        this.nextRace = nextRace;
        this.schemaVersion = schemaVersion != null ? schemaVersion : 1;
    }

    public Timestamp getLastDownload() {
        return lastDownload;
    }

    public ApplicationStatus setLastDownload(Timestamp lastDownload) {
        this.lastDownload = lastDownload;
        return this;
    }

    public Integer getCurrentSeason() {
        return season;
    }

    public ApplicationStatus setCurrentSeason(Integer currentSeason) {
        this.season = currentSeason;
        return this;
    }

    public Integer getNextRace() {
        return nextRace;
    }

    public ApplicationStatus setNextRace(Integer currentRace) {
        this.nextRace = currentRace;
        return this;
    }
    
    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public ApplicationStatus setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
        return this;
    }
    
    public String getNextRaceCode() {
        if( nextRace != null ) {
            if( nextRace <= 0 ) {
                return String.format( "S%02dR%02d", season+1, 1 );
            } else {
                return String.format( "S%02dR%02d", season, nextRace );
            }
        }
        return null;
    }
    
}
