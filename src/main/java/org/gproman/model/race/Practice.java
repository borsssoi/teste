package org.gproman.model.race;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;


public class Practice extends PersistentEntity {
    
    private Integer id;
    
    private List<Lap> laps;

    public Practice() {
        laps = new ArrayList<Lap>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Lap> getLaps() {
        return laps;
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
    }

    public Lap getBestNetTimeLap() {
        Lap best = laps.isEmpty() ? null : laps.get( 0 );
        for( Lap lap : laps ) {
            if( lap.getNetTime() < best.getNetTime() ) {
                best = lap;
            }
        }
        return best;
    }
    
    public List<List<Lap>> getWingSplitLapGroups() {
        List<List<Lap>> lapGroups = new ArrayList<List<Lap>>();
        for( Lap lap : laps ) {
            boolean found = false;
            CarSettings ls = lap.getSettings();
            for( List<Lap> ll : lapGroups ) {
                CarSettings c = ll.get( 0 ).getSettings();
                if( ls.getEngine().intValue() == c.getEngine().intValue() && ls.getBrakes().intValue() == c.getBrakes().intValue() && 
                    ls.getGear().intValue() == c.getGear().intValue() && ls.getSuspension().intValue() == c.getSuspension().intValue() &&
                    ls.getTyre().equals( c.getTyre() ) ) {
                    ll.add( lap );
                    found = true;
                    break;
                }
            }
            if( !found ) {
                List<Lap> ng = new ArrayList<Lap>();
                ng.add( lap );
                lapGroups.add( ng );
            }
        }
        return lapGroups;
    }
    
    @Override
    public String toString() {
        return "Practice [id=" + id + ", laps=" + laps + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((laps == null) ? 0 : laps.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Practice other = (Practice) obj;
        if ( laps == null ) {
            if ( other.laps != null ) return false;
        } else if ( !laps.equals( other.laps ) ) return false;
        return true;
    }

}
