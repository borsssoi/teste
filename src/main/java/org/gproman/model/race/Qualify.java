package org.gproman.model.race;

import org.gproman.model.PersistentEntity;

public class Qualify extends PersistentEntity {
    
    private int number;
    
    private Lap lap = new Lap();
    private String riskDescr;

    public Qualify() {
        lap.setNumber( 1 );
    }

    public int getNumber() {
        return number;
    }
    
    public Qualify setNumber( int number ) {
        this.number = number;
        return this;
    }

    public Lap getLap() {
        return lap;
    }

    public Qualify setLap(Lap lap) {
        this.lap = lap;
        return this;
    }

    public String getRiskDescr() {
        return riskDescr;
    }
    
    public Qualify setRiskDescr( String riskDescr ) {
        this.riskDescr = riskDescr;
        return this;
    }

    public String toString() {
        return "Qualify [number=" + number + ", lap=" + lap + ", riskDescr=" + riskDescr + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lap == null) ? 0 : lap.hashCode());
        result = prime * result + number;
        result = prime * result + ((riskDescr == null) ? 0 : riskDescr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Qualify other = (Qualify) obj;
        if ( lap == null ) {
            if ( other.lap != null ) return false;
        } else if ( !lap.equals( other.lap ) ) return false;
        if ( number != other.number ) return false;
        if ( riskDescr == null ) {
            if ( other.riskDescr != null ) return false;
        } else if ( !riskDescr.equals( other.riskDescr ) ) return false;
        return true;
    }
}
