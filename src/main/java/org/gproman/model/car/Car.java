package org.gproman.model.car;

import java.util.Arrays;

import org.gproman.calc.CarPHACalculator;
import org.gproman.model.PersistentEntity;

public class Car extends PersistentEntity implements Cloneable {

    public static final int PARTS_COUNT = 11;
    
    public static final int CHASSIS = 0;
    public static final int ENGINE = 1;
    public static final int FRONT_WING = 2;
    public static final int REAR_WING = 3;
    public static final int UNDERBODY = 4;
    public static final int SIDEPODS = 5;
    public static final int COOLING = 6;
    public static final int GEARBOX = 7;
    public static final int BRAKES = 8;
    public static final int SUSPENSION = 9;
    public static final int ELECTRONICS = 10;
    
    public static final String[] PARTS = new String[]{"Chassis", "Engine", "Front Wing", "Rear Wing", "Underbody",
                                                       "Sidepods", "Cooling", "Gearbox", "Brakes", "Suspension", "Electronics"};

    public static final String[] PARTS_PTBR = new String[]{"Chassi", "Motor", "Asa Dianteira", "Asa Traseira", "Assoalho",
                                                      "Laterais", "Radiador", "Câmbio", "Freios", "Suspensão", "Eletrônicos"};

    public static final String[] MNEM_PTBR = new String[]{"Cha", "Mot", "AsD", "AsT", "Ass", "Lat", "Rad", "Câm", "Fre", "Sus", "Ele"};
    
    public static final String[] MNEM_ENUS = new String[]{"Cha", "Eng", "FWg", "RWg", "Und", "Sid", "Coo", "Gea", "Bra", "Sus", "Ele"};
    
    private PHA pha = new PHA();
    private CarPart[] parts = new CarPart[PARTS_COUNT];

    public int getPower() {
        return pha.getP();
    }

    public void setPower(int power) {
        this.pha.setP( power );
    }

    public int getHandling() {
        return pha.getH();
    }

    public void setHandling(int handling) {
        this.pha.setH( handling );
    }

    public int getAcceleration() {
        return pha.getA();
    }

    public void setAcceleration(int acceleration) {
        this.pha.setA( acceleration );
    }

    public PHA getPHA() {
        return this.pha;
    }
    
    public PHA getBonusPHA() {
        return pha.getBonusPHA( CarPHACalculator.calculateBasePHA( parts ) );
    }
    
    public void setPHA( PHA pha ) {
        this.pha = pha;
    }

    public CarPart getChassis() {
        return parts[CHASSIS];
    }

    public void setChassis(CarPart chassis) {
        parts[CHASSIS] = chassis;
    }

    public CarPart getEngine() {
        return parts[ENGINE];
    }

    public void setEngine(CarPart engine) {
        parts[ENGINE] = engine;
    }

    public CarPart getFrontWing() {
        return parts[FRONT_WING];
    }

    public void setFrontWing(CarPart frontWing) {
        parts[FRONT_WING] = frontWing;
    }

    public CarPart getRearWing() {
        return parts[REAR_WING];
    }

    public void setRearWing(CarPart rearWing) {
        parts[REAR_WING] = rearWing;
    }

    public CarPart getUnderbody() {
        return parts[UNDERBODY];
    }

    public void setUnderbody(CarPart underbody) {
        parts[UNDERBODY] = underbody;
    }

    public CarPart getSidepods() {
        return parts[SIDEPODS];
    }

    public void setSidepods(CarPart sidepods) {
        parts[SIDEPODS] = sidepods;
    }

    public CarPart getCooling() {
        return parts[COOLING];
    }

    public void setCooling(CarPart cooling) {
        parts[COOLING] = cooling;
    }

    public CarPart getGearbox() {
        return parts[GEARBOX];
    }

    public void setGearbox(CarPart gearBox) {
        parts[GEARBOX] = gearBox;
    }

    public CarPart getBrakes() {
        return parts[BRAKES];
    }

    public void setBrakes(CarPart breakes) {
        parts[BRAKES] = breakes;
    }

    public CarPart getSuspension() {
        return parts[SUSPENSION];
    }

    public void setSuspension(CarPart suspension) {
        parts[SUSPENSION] = suspension;
    }

    public CarPart getElectronics() {
        return parts[ELECTRONICS];
    }

    public void setElectronics(CarPart electronics) {
        parts[ELECTRONICS] = electronics;
    }
    
    public CarPart[] getParts() {
        return parts;
    }
    
    public void setPart( int part, CarPart value ) {
        parts[part] = value;
    }

    @Override
    public String toString() {
        return "Car [power=" + getPower() + ", handling=" + getHandling() + ", acceleration=" + getAcceleration() + 
                ", chassi=" + getChassis() + ", engine=" + getEngine() + ", frontWing=" + getFrontWing() + 
                ", rearWing=" + getRearWing() + ", underbody=" + getUnderbody() + ", sidepods=" + getSidepods() + 
                ", cooling=" + getCooling() + ", gearBox=" + getGearbox() + ", brakes=" + getBrakes() + 
                ", suspension=" + getSuspension() + ", electronics=" + getElectronics() + "]";
    }
    
    @Override
    public Car clone() {
        Car clone = new Car();
        clone.pha = this.pha.clone();
        for( int i = 0; i < clone.parts.length; i++ ) {
            clone.parts[i] = parts[i] != null ? parts[i].clone() : null;
        }
        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( parts );
        result = prime * result + ((pha == null) ? 0 : pha.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Car other = (Car) obj;
        if ( !Arrays.equals( parts, other.parts ) ) return false;
        if ( pha == null ) {
            if ( other.pha != null ) return false;
        } else if ( !pha.equals( other.pha ) ) return false;
        return true;
    }

    public void setParts(CarPart[] newParts) {
        for( int i = 0; i < parts.length; i++ ) {
            parts[i] = newParts[i];
        }
    }

    public boolean isUpdateDone() {
        // TODO get this information from GPRO... for now, using just a simple check on part options 
        return parts[0].getOptions().isEmpty();
    }
    
    public boolean isCarPartDataAvailable() {
        for( int i = 0; i < parts.length; i++ ) {
            if( parts[i] == null ) {
                return false;
            }
        }
        return true;
    }

}
