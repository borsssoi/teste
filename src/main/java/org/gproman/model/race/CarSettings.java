package org.gproman.model.race;

import org.gproman.model.PersistentEntity;

public class CarSettings extends PersistentEntity
        implements
        Cloneable {

    private Integer frontWing;
    private Integer rearWing;
    private Integer engine;
    private Integer brakes;
    private Integer gear;
    private Integer suspension;

    private Tyre    tyre;
    
    public CarSettings() {
    }

    public CarSettings(Integer id) {
        super( id );
    }

    public CarSettings( Integer id,
                        Integer frontWing,
                        Integer rearWing,
                        Integer engine,
                        Integer brakes,
                        Integer gear,
                        Integer suspension,
                        Tyre tyre) {
        super( id );
        this.frontWing = frontWing;
        this.rearWing = rearWing;
        this.engine = engine;
        this.brakes = brakes;
        this.gear = gear;
        this.suspension = suspension;
        this.tyre = tyre;
    }
    
    @Override
    public CarSettings clone() {
        return new CarSettings( getId(), frontWing, rearWing, engine, brakes, gear, suspension, tyre );
    }

    public Integer getFrontWing() {
        return frontWing;
    }

    public void setFrontWing(Integer frontWing) {
        this.frontWing = frontWing;
    }

    public Integer getRearWing() {
        return rearWing;
    }

    public void setRearWing(Integer rearWing) {
        this.rearWing = rearWing;
    }

    public Integer getEngine() {
        return engine;
    }

    public void setEngine(Integer engine) {
        this.engine = engine;
    }

    public Integer getBrakes() {
        return brakes;
    }

    public void setBrakes(Integer brakes) {
        this.brakes = brakes;
    }

    public Integer getGear() {
        return gear;
    }

    public void setGear(Integer gear) {
        this.gear = gear;
    }

    public Integer getSuspension() {
        return suspension;
    }

    public void setSuspension(Integer suspension) {
        this.suspension = suspension;
    }

    public Tyre getTyre() {
        return tyre;
    }

    public void setTyre(Tyre tyre) {
        this.tyre = tyre;
    }

    @Override
    public String toString() {
        return "CarSettings [frontWing=" + frontWing + ", rearWing=" + rearWing + ", engine=" + engine + ", brakes=" + brakes + ", gear=" + gear + ", suspension=" + suspension + ", tyre=" + tyre + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brakes == null) ? 0 : brakes.hashCode());
        result = prime * result + ((engine == null) ? 0 : engine.hashCode());
        result = prime * result + ((frontWing == null) ? 0 : frontWing.hashCode());
        result = prime * result + ((gear == null) ? 0 : gear.hashCode());
        result = prime * result + ((rearWing == null) ? 0 : rearWing.hashCode());
        result = prime * result + ((suspension == null) ? 0 : suspension.hashCode());
        result = prime * result + ((tyre == null) ? 0 : tyre.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CarSettings other = (CarSettings) obj;
        if ( brakes == null ) {
            if ( other.brakes != null ) return false;
        } else if ( !brakes.equals( other.brakes ) ) return false;
        if ( engine == null ) {
            if ( other.engine != null ) return false;
        } else if ( !engine.equals( other.engine ) ) return false;
        if ( frontWing == null ) {
            if ( other.frontWing != null ) return false;
        } else if ( !frontWing.equals( other.frontWing ) ) return false;
        if ( gear == null ) {
            if ( other.gear != null ) return false;
        } else if ( !gear.equals( other.gear ) ) return false;
        if ( rearWing == null ) {
            if ( other.rearWing != null ) return false;
        } else if ( !rearWing.equals( other.rearWing ) ) return false;
        if ( suspension == null ) {
            if ( other.suspension != null ) return false;
        } else if ( !suspension.equals( other.suspension ) ) return false;
        if ( tyre != other.tyre ) return false;
        return true;
    }
    
    public boolean isSameSetting( CarSettings other ) {
        if ( brakes == null ) {
            if ( other.brakes != null ) return false;
        } else if ( !brakes.equals( other.brakes ) ) return false;
        if ( engine == null ) {
            if ( other.engine != null ) return false;
        } else if ( !engine.equals( other.engine ) ) return false;
        if ( frontWing == null ) {
            if ( other.frontWing != null ) return false;
        } else if ( !frontWing.equals( other.frontWing ) ) return false;
        if ( gear == null ) {
            if ( other.gear != null ) return false;
        } else if ( !gear.equals( other.gear ) ) return false;
        if ( rearWing == null ) {
            if ( other.rearWing != null ) return false;
        } else if ( !rearWing.equals( other.rearWing ) ) return false;
        if ( suspension == null ) {
            if ( other.suspension != null ) return false;
        } else if ( !suspension.equals( other.suspension ) ) return false;
        return true;
    }
}
