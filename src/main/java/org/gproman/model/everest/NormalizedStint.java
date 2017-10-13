package org.gproman.model.everest;

import org.gproman.model.PersistentEntity;
import org.gproman.model.race.Tyre;

public class NormalizedStint extends PersistentEntity {
    private Integer number;
    private Integer initialLap;
    private Integer finalLap;
    private String  pitReason;
    private Tyre    tyre;
    private Integer tyreLeft;
    private Double  tyreUsed;
    private Double  tyreNoBad;
    private Double  tyreDurability;
    private Double  avgTemp;
    private Double  avgHum;
    private Double  fuelStart;
    private Double  fuelLeft;
    private Integer refueledTo;
    private Integer pitTime;
    private WeatherType weatherType = WeatherType.UNKNOWN;

    public NormalizedStint() {
        super();
    }

    public NormalizedStint(Integer id) {
        super(id);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getInitialLap() {
        return initialLap;
    }

    public void setInitialLap(Integer initialLap) {
        this.initialLap = initialLap;
    }

    public Integer getFinalLap() {
        return finalLap;
    }

    public void setFinalLap(Integer finalLap) {
        this.finalLap = finalLap;
    }

    public String getPitReason() {
        return pitReason;
    }

    public void setPitReason(String pitReason) {
        this.pitReason = pitReason;
    }

    public Tyre getTyre() {
        return tyre;
    }

    public void setTyre(Tyre tyre) {
        this.tyre = tyre;
    }

    public Integer getTyreLeft() {
        return tyreLeft;
    }

    public void setTyreLeft(Integer tyreLeft) {
        this.tyreLeft = tyreLeft;
    }

    public Double getTyreUsed() {
        return tyreUsed;
    }

    public void setTyreUsed(Double tyreUsed) {
        this.tyreUsed = tyreUsed;
    }

    public Double getTyreNoBad() {
        return tyreNoBad;
    }

    public void setTyreNoBad(Double tyreNoBad) {
        this.tyreNoBad = tyreNoBad;
    }

    public Double getTyreDurability() {
        return tyreDurability;
    }

    public void setTyreDurability(Double tyreDurability) {
        this.tyreDurability = tyreDurability;
    }

    public Double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(Double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public Double getAvgHum() {
        return avgHum;
    }

    public void setAvgHum(Double avgHum) {
        this.avgHum = avgHum;
    }

    public Double getFuelStart() {
        return fuelStart;
    }

    public void setFuelStart(Double fuelStart) {
        this.fuelStart = fuelStart;
    }

    public Double getFuelLeft() {
        return fuelLeft;
    }

    public void setFuelLeft(Double fuelLeft) {
        this.fuelLeft = fuelLeft;
    }

    public Integer getRefueledTo() {
        return refueledTo;
    }

    public void setRefueledTo(Integer refueledTo) {
        this.refueledTo = refueledTo;
    }

    public Integer getPitTime() {
        return pitTime;
    }

    public void setPitTime(Integer pitTime) {
        this.pitTime = pitTime;
    }
    
    public WeatherType getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType;
    }

    @Override
    public String toString() {
        return "NormalizedStint [number=" + number + ", initialLap=" + initialLap + ", finalLap=" + finalLap + ", pitReason=" + pitReason + ", tyre=" + tyre + ", tyreLeft=" + tyreLeft + ", tyreUsed=" + tyreUsed + ", tyreNoBad=" + tyreNoBad + ", tyreDurability=" + tyreDurability + ", avgTemp=" + avgTemp + ", avgHum=" + avgHum + ", fuelStart=" + fuelStart + ", fuelLeft=" + fuelLeft + ", refueledTo=" + refueledTo + ", pitTime=" + pitTime + ", weatherType=" + weatherType + "]";
    }

    public Integer getLaps() {
        return initialLap != null && finalLap != null ? finalLap - initialLap + 1: 0;
    }

}
