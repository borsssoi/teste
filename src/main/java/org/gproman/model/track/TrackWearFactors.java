package org.gproman.model.track;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

import org.gproman.model.car.Car;

public class TrackWearFactors implements Serializable {
    private static final long serialVersionUID = 3932454262204179196L;
    
    private BigDecimal[] factors = new BigDecimal[Car.PARTS_COUNT];
    
    public TrackWearFactors() {}

    public BigDecimal[] getFactors() {
        return factors;
    }

    public void setFactors(BigDecimal[] factors) {
        this.factors = factors;
    }
    
    public BigDecimal getChassisWF() {
        return factors[Car.CHASSIS];
    }

    public void setChassisWF(BigDecimal chassis) {
        factors[Car.CHASSIS] = chassis;
    }

    public BigDecimal getEngineWF() {
        return factors[Car.ENGINE];
    }

    public void setEngineWF(BigDecimal engine) {
        factors[Car.ENGINE] = engine;
    }

    public BigDecimal getFrontWingWF() {
        return factors[Car.FRONT_WING];
    }

    public void setFrontWingWF(BigDecimal frontWing) {
        factors[Car.FRONT_WING] = frontWing;
    }

    public BigDecimal getRearWingWF() {
        return factors[Car.REAR_WING];
    }

    public void setRearWingWF(BigDecimal rearWing) {
        factors[Car.REAR_WING] = rearWing;
    }

    public BigDecimal getUnderbodyWF() {
        return factors[Car.UNDERBODY];
    }

    public void setUnderbodyWF(BigDecimal underbody) {
        factors[Car.UNDERBODY] = underbody;
    }

    public BigDecimal getSidepodsWF() {
        return factors[Car.SIDEPODS];
    }

    public void setSidepodsWF(BigDecimal sidepods) {
        factors[Car.SIDEPODS] = sidepods;
    }

    public BigDecimal getCoolingWF() {
        return factors[Car.COOLING];
    }

    public void setCoolingWF(BigDecimal cooling) {
        factors[Car.COOLING] = cooling;
    }

    public BigDecimal getGearboxWF() {
        return factors[Car.GEARBOX];
    }

    public void setGearboxWF(BigDecimal gearBox) {
        factors[Car.GEARBOX] = gearBox;
    }

    public BigDecimal getBrakesWF() {
        return factors[Car.BRAKES];
    }

    public void setBrakesWF(BigDecimal breakes) {
        factors[Car.BRAKES] = breakes;
    }

    public BigDecimal getSuspensionWF() {
        return factors[Car.SUSPENSION];
    }

    public void setSuspensionWF(BigDecimal suspension) {
        factors[Car.SUSPENSION] = suspension;
    }

    public BigDecimal getElectronicsWF() {
        return factors[Car.ELECTRONICS];
    }

    public void setElectronicsWF(BigDecimal electronics) {
        factors[Car.ELECTRONICS] = electronics;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( factors );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TrackWearFactors other = (TrackWearFactors) obj;
        if ( !Arrays.equals( factors, other.factors ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TrackWearFactors [ChassisWF=" + getChassisWF() + ", EngineWF=" + getEngineWF() + ", FrontWingWF=" + getFrontWingWF() + ", RearWingWF=" + getRearWingWF() + ", UnderbodyWF=" + getUnderbodyWF() + ", SidepodsWF=" + getSidepodsWF() + ", CoolingWF=" + getCoolingWF() + ", GearBoxWF=" + getGearboxWF() + ", BrakesWF=" + getBrakesWF() + ", SuspensionWF=" + getSuspensionWF() + ", ElectronicsWF=" + getElectronicsWF() + "]";
    }
    
    
}
