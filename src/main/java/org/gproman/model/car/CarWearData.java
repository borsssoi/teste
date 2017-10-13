package org.gproman.model.car;

import java.util.List;

import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.track.Track;
import org.gproman.model.track.WearCoefs;

public class CarWearData {

    private List<Track> tracks;
    private DriverWearWeight driverAttributesWearWeight;
    private WearCoefs wearPolinomialCoef;
    
    public CarWearData() {
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setDriverattributsWearWeight(DriverWearWeight dww) {
        this.driverAttributesWearWeight = dww; 
    }

    public DriverWearWeight getDriverAttributesWearWeight() {
        return driverAttributesWearWeight;
    }

    public void setDriverAttributesWearWeight(DriverWearWeight driverAttributesWearWeight) {
        this.driverAttributesWearWeight = driverAttributesWearWeight;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setWearPolinomialCoef(WearCoefs wearPolinomialCoefs) {
        this.wearPolinomialCoef = wearPolinomialCoefs;
    }

    public WearCoefs getWearPolinomialCoef() {
        return wearPolinomialCoef;
    }

    @Override
    public String toString() {
        return "CarWearSpreadsheet [tracks=" + tracks + ", driverAttributesWearWeight=" + driverAttributesWearWeight + ", wearPolinomialCoef=" + wearPolinomialCoef + "]";
    }
    
}
