package org.gproman.model.track;

import java.math.BigDecimal;

import org.gproman.model.PersistentEntity;

public class Track extends PersistentEntity {

    private String             name;
    private double             distance;
    private int                laps;
    private double             lapDistance;
    private double             avgSpeed;
    private int                corners;
    private int                timeInOut;
    private int                power;
    private int                handling;
    private int                acceleration;
    private Downforce          downforce;
    private Overtaking         overtaking;
    private SuspensionRigidity suspension;
    private FuelConsumption    fuelConsumption;
    private TyreWear           tyreWear;
    private GripLevel          gripLevel;
    private BigDecimal         fuelCoef;
    private BigDecimal         compoundCoef;

    private TrackWearFactors   wearFactors;

    private Integer            setupWings;
    private Integer            setupEngine;
    private Integer            setupBrakes;
    private Integer            setupGear;
    private Integer            setupSuspension;
    private Integer            wingSplit;
    private boolean            wingNormal;

    private Integer            fCon;
    private Integer            fAgr;
    private Integer            fExp;
    private Integer            fTeI;
    private Integer            fEng;
    private Integer            fEle;
    private Double             fHum;
    private Double             fFue;

    public Track() {
        wearFactors = new TrackWearFactors();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public double getLapDistance() {
        return lapDistance;
    }

    public void setLapDistance(double lapDistance) {
        this.lapDistance = lapDistance;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getCorners() {
        return corners;
    }

    public void setCorners(int corners) {
        this.corners = corners;
    }

    public int getTimeInOut() {
        return timeInOut;
    }

    public void setTimeInOut(int timeInOut) {
        this.timeInOut = timeInOut;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHandling() {
        return handling;
    }

    public void setHandling(int handling) {
        this.handling = handling;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public Downforce getDownforce() {
        return downforce;
    }

    public void setDownforce(Downforce downforce) {
        this.downforce = downforce;
    }

    public Overtaking getOvertaking() {
        return overtaking;
    }

    public void setOvertaking(Overtaking overtaking) {
        this.overtaking = overtaking;
    }

    public SuspensionRigidity getSuspension() {
        return suspension;
    }

    public void setSuspension(SuspensionRigidity suspension) {
        this.suspension = suspension;
    }

    public FuelConsumption getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(FuelConsumption fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public TyreWear getTyreWear() {
        return tyreWear;
    }

    public void setTyreWear(TyreWear tyreWear) {
        this.tyreWear = tyreWear;
    }

    public GripLevel getGripLevel() {
        return gripLevel;
    }

    public void setGripLevel(GripLevel gripLevel) {
        this.gripLevel = gripLevel;
    }

    public TrackWearFactors getWearFactors() {
        return wearFactors;
    }

    public void setWearFactors(TrackWearFactors wearFactors) {
        this.wearFactors = wearFactors;
    }

    public BigDecimal getFuelCoef() {
        return fuelCoef;
    }

    public void setFuelCoef(BigDecimal fuelCoef) {
        this.fuelCoef = fuelCoef;
    }

    public BigDecimal getCompoundCoef() {
        return compoundCoef;
    }

    public void setCompoundCoef(BigDecimal compoundCoef) {
        this.compoundCoef = compoundCoef;
    }

    public Integer getSetupWings() {
        return setupWings;
    }

    public void setSetupWings(Integer setupWings) {
        this.setupWings = setupWings;
    }

    public Integer getSetupEngine() {
        return setupEngine;
    }

    public void setSetupEngine(Integer setupEngine) {
        this.setupEngine = setupEngine;
    }

    public Integer getSetupBrakes() {
        return setupBrakes;
    }

    public void setSetupBrakes(Integer setupBrakes) {
        this.setupBrakes = setupBrakes;
    }

    public Integer getSetupGear() {
        return setupGear;
    }

    public void setSetupGear(Integer setupGear) {
        this.setupGear = setupGear;
    }

    public Integer getSetupSuspension() {
        return setupSuspension;
    }

    public void setSetupSuspension(Integer setupSuspension) {
        this.setupSuspension = setupSuspension;
    }

    public Integer getWingSplit() {
        return wingSplit;
    }

    public void setWingSplit(Integer wingSplit) {
        this.wingSplit = wingSplit;
    }

    public boolean isWingNormal() {
        return wingNormal;
    }

    public void setWingNormal(boolean wingNormal) {
        this.wingNormal = wingNormal;
    }

    public Integer getFCon() {
        return fCon;
    }

    public void setFCon(Integer fCon) {
        this.fCon = fCon;
    }

    public Integer getFAgr() {
        return fAgr;
    }

    public void setFAgr(Integer fAgr) {
        this.fAgr = fAgr;
    }

    public Integer getFExp() {
        return fExp;
    }

    public void setFExp(Integer fExp) {
        this.fExp = fExp;
    }

    public Integer getFTeI() {
        return fTeI;
    }

    public void setFTeI(Integer fTeI) {
        this.fTeI = fTeI;
    }

    public Integer getFEng() {
        return fEng;
    }

    public void setFEng(Integer fEng) {
        this.fEng = fEng;
    }

    public Integer getFEle() {
        return fEle;
    }

    public void setFEle(Integer fEle) {
        this.fEle = fEle;
    }

    public Double getFHum() {
        return fHum;
    }

    public void setFHum(Double fHum) {
        this.fHum = fHum;
    }

    public Double getFFue() {
        return fFue;
    }

    public void setFFue(Double fFue) {
        this.fFue = fFue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + acceleration;
        long temp;
        temp = Double.doubleToLongBits(avgSpeed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((compoundCoef == null) ? 0 : compoundCoef.hashCode());
        result = prime * result + corners;
        temp = Double.doubleToLongBits(distance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((downforce == null) ? 0 : downforce.hashCode());
        result = prime * result + ((fAgr == null) ? 0 : fAgr.hashCode());
        result = prime * result + ((fCon == null) ? 0 : fCon.hashCode());
        result = prime * result + ((fEle == null) ? 0 : fEle.hashCode());
        result = prime * result + ((fEng == null) ? 0 : fEng.hashCode());
        result = prime * result + ((fExp == null) ? 0 : fExp.hashCode());
        result = prime * result + ((fFue == null) ? 0 : fFue.hashCode());
        result = prime * result + ((fHum == null) ? 0 : fHum.hashCode());
        result = prime * result + ((fTeI == null) ? 0 : fTeI.hashCode());
        result = prime * result + ((fuelCoef == null) ? 0 : fuelCoef.hashCode());
        result = prime * result + ((fuelConsumption == null) ? 0 : fuelConsumption.hashCode());
        result = prime * result + ((gripLevel == null) ? 0 : gripLevel.hashCode());
        result = prime * result + handling;
        temp = Double.doubleToLongBits(lapDistance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + laps;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((overtaking == null) ? 0 : overtaking.hashCode());
        result = prime * result + power;
        result = prime * result + ((setupBrakes == null) ? 0 : setupBrakes.hashCode());
        result = prime * result + ((setupEngine == null) ? 0 : setupEngine.hashCode());
        result = prime * result + ((setupGear == null) ? 0 : setupGear.hashCode());
        result = prime * result + ((setupSuspension == null) ? 0 : setupSuspension.hashCode());
        result = prime * result + ((setupWings == null) ? 0 : setupWings.hashCode());
        result = prime * result + ((suspension == null) ? 0 : suspension.hashCode());
        result = prime * result + timeInOut;
        result = prime * result + ((tyreWear == null) ? 0 : tyreWear.hashCode());
        result = prime * result + ((wearFactors == null) ? 0 : wearFactors.hashCode());
        result = prime * result + (wingNormal ? 1231 : 1237);
        result = prime * result + ((wingSplit == null) ? 0 : wingSplit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Track other = (Track) obj;
        if (acceleration != other.acceleration)
            return false;
        if (Double.doubleToLongBits(avgSpeed) != Double.doubleToLongBits(other.avgSpeed))
            return false;
        if (compoundCoef == null) {
            if (other.compoundCoef != null)
                return false;
        } else if (!compoundCoef.equals(other.compoundCoef))
            return false;
        if (corners != other.corners)
            return false;
        if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
            return false;
        if (downforce != other.downforce)
            return false;
        if (fAgr == null) {
            if (other.fAgr != null)
                return false;
        } else if (!fAgr.equals(other.fAgr))
            return false;
        if (fCon == null) {
            if (other.fCon != null)
                return false;
        } else if (!fCon.equals(other.fCon))
            return false;
        if (fEle == null) {
            if (other.fEle != null)
                return false;
        } else if (!fEle.equals(other.fEle))
            return false;
        if (fEng == null) {
            if (other.fEng != null)
                return false;
        } else if (!fEng.equals(other.fEng))
            return false;
        if (fExp == null) {
            if (other.fExp != null)
                return false;
        } else if (!fExp.equals(other.fExp))
            return false;
        if (fFue == null) {
            if (other.fFue != null)
                return false;
        } else if (!fFue.equals(other.fFue))
            return false;
        if (fHum == null) {
            if (other.fHum != null)
                return false;
        } else if (!fHum.equals(other.fHum))
            return false;
        if (fTeI == null) {
            if (other.fTeI != null)
                return false;
        } else if (!fTeI.equals(other.fTeI))
            return false;
        if (fuelCoef == null) {
            if (other.fuelCoef != null)
                return false;
        } else if (!fuelCoef.equals(other.fuelCoef))
            return false;
        if (fuelConsumption != other.fuelConsumption)
            return false;
        if (gripLevel != other.gripLevel)
            return false;
        if (handling != other.handling)
            return false;
        if (Double.doubleToLongBits(lapDistance) != Double.doubleToLongBits(other.lapDistance))
            return false;
        if (laps != other.laps)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (overtaking != other.overtaking)
            return false;
        if (power != other.power)
            return false;
        if (setupBrakes == null) {
            if (other.setupBrakes != null)
                return false;
        } else if (!setupBrakes.equals(other.setupBrakes))
            return false;
        if (setupEngine == null) {
            if (other.setupEngine != null)
                return false;
        } else if (!setupEngine.equals(other.setupEngine))
            return false;
        if (setupGear == null) {
            if (other.setupGear != null)
                return false;
        } else if (!setupGear.equals(other.setupGear))
            return false;
        if (setupSuspension == null) {
            if (other.setupSuspension != null)
                return false;
        } else if (!setupSuspension.equals(other.setupSuspension))
            return false;
        if (setupWings == null) {
            if (other.setupWings != null)
                return false;
        } else if (!setupWings.equals(other.setupWings))
            return false;
        if (suspension != other.suspension)
            return false;
        if (timeInOut != other.timeInOut)
            return false;
        if (tyreWear != other.tyreWear)
            return false;
        if (wearFactors == null) {
            if (other.wearFactors != null)
                return false;
        } else if (!wearFactors.equals(other.wearFactors))
            return false;
        if (wingNormal != other.wingNormal)
            return false;
        if (wingSplit == null) {
            if (other.wingSplit != null)
                return false;
        } else if (!wingSplit.equals(other.wingSplit))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Track [name=" + name + ", distance=" + distance + ", laps=" + laps + ", lapDistance=" + lapDistance + ", avgSpeed=" + avgSpeed + ", corners=" + corners + ", timeInOut=" + timeInOut + ", power=" + power + ", handling=" + handling + ", acceleration=" + acceleration + ", downforce=" + downforce + ", overtaking=" + overtaking + ", suspension=" + suspension + ", fuelConsumption=" + fuelConsumption + ", tyreWear=" + tyreWear + ", gripLevel=" + gripLevel + ", fuelCoef=" + fuelCoef + ", compoundCoef=" + compoundCoef + ", wearFactors=" + wearFactors + ", setupWings=" + setupWings + ", setupEngine=" + setupEngine + ", setupBrakes=" + setupBrakes + ", setupGear=" + setupGear + ", setupSuspension=" + setupSuspension + ", wingSplit=" + wingSplit + ", wingNormal=" + wingNormal + ", fCon=" + fCon + ", fAgr=" + fAgr + ", fExp=" + fExp + ", fTeI=" + fTeI + ", fEng=" + fEng + ", fEle=" + fEle + ", fHum=" + fHum + ", fFue=" + fFue + "]";
    }

}
