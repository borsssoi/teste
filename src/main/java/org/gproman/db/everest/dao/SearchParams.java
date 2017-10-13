package org.gproman.db.everest.dao;

import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Tyre;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Track;
import org.gproman.model.track.TyreWear;

public class SearchParams {

    private Integer         minRisk;
    private Integer         maxRisk;
    private Integer         minTemp;
    private Integer         maxTemp;
    private Integer         minHum;
    private Integer         maxHum;

    private Integer         minExp;
    private Integer         maxExp;
    private Integer         minTI;
    private Integer         maxTI;
    private Integer         minAggr;
    private Integer         maxAggr;

    private Integer         minLaps;
    private Integer         maxLaps;
    private Integer         minSusp;
    private Integer         maxSusp;
    private Integer         minDurab;
    private Integer         maxDurab;

    private Track           track;
    private FuelConsumption consump;
    private TyreWear        wear;
    private Tyre            tyre;
    private WeatherType     weather;
    private TyreSupplier    supplier;
    
    private Integer         limit;

    public Integer getMinRisk() {
        return minRisk;
    }

    public SearchParams setMinRisk(Integer minRisk) {
        this.minRisk = minRisk;
        return this;
    }

    public Integer getMaxRisk() {
        return maxRisk;
    }

    public SearchParams setMaxRisk(Integer maxRisk) {
        this.maxRisk = maxRisk;
        return this;
    }

    public Integer getMinTemp() {
        return minTemp;
    }

    public SearchParams setMinTemp(Integer minTemp) {
        this.minTemp = minTemp;
        return this;
    }

    public Integer getMaxTemp() {
        return maxTemp;
    }

    public SearchParams setMaxTemp(Integer maxTemp) {
        this.maxTemp = maxTemp;
        return this;
    }

    public Integer getMinHum() {
        return minHum;
    }

    public SearchParams setMinHum(Integer minHum) {
        this.minHum = minHum;
        return this;
    }

    public Integer getMaxHum() {
        return maxHum;
    }

    public SearchParams setMaxHum(Integer maxHum) {
        this.maxHum = maxHum;
        return this;
    }

    public Integer getMinExp() {
        return minExp;
    }

    public SearchParams setMinExp(Integer minExp) {
        this.minExp = minExp;
        return this;
    }

    public Integer getMaxExp() {
        return maxExp;
    }

    public SearchParams setMaxExp(Integer maxExp) {
        this.maxExp = maxExp;
        return this;
    }

    public Integer getMinTI() {
        return minTI;
    }

    public SearchParams setMinTI(Integer minTI) {
        this.minTI = minTI;
        return this;
    }

    public Integer getMaxTI() {
        return maxTI;
    }

    public SearchParams setMaxTI(Integer maxTI) {
        this.maxTI = maxTI;
        return this;
    }

    public Integer getMinAggr() {
        return minAggr;
    }

    public SearchParams setMinAggr(Integer minAggr) {
        this.minAggr = minAggr;
        return this;
    }

    public Integer getMaxAggr() {
        return maxAggr;
    }

    public SearchParams setMaxAggr(Integer maxAggr) {
        this.maxAggr = maxAggr;
        return this;
    }

    public Track getTrack() {
        return track;
    }

    public SearchParams setTrack(Track track) {
        this.track = track;
        return this;
    }

    public FuelConsumption getConsump() {
        return consump;
    }

    public SearchParams setConsump(FuelConsumption consump) {
        this.consump = consump;
        return this;
    }

    public TyreWear getWear() {
        return wear;
    }

    public SearchParams setWear(TyreWear wear) {
        this.wear = wear;
        return this;
    }

    public Tyre getTyre() {
        return tyre;
    }

    public SearchParams setTyre(Tyre tyre) {
        this.tyre = tyre;
        return this;
    }

    public WeatherType getWeather() {
        return weather;
    }

    public SearchParams setWeather(WeatherType weather) {
        this.weather = weather;
        return this;
    }

    public TyreSupplier getSupplier() {
        return supplier;
    }

    public SearchParams setSupplier(TyreSupplier supplier) {
        this.supplier = supplier;
        return this;
    }

    public Integer getMinLaps() {
        return minLaps;
    }

    public SearchParams setMinLaps(Integer minLaps) {
        this.minLaps = minLaps;
        return this;
    }

    public Integer getMaxLaps() {
        return maxLaps;
    }

    public SearchParams setMaxLaps(Integer maxLaps) {
        this.maxLaps = maxLaps;
        return this;
    }

    public Integer getMinSusp() {
        return minSusp;
    }

    public SearchParams setMinSusp(Integer minSusp) {
        this.minSusp = minSusp;
        return this;
    }

    public Integer getMaxSusp() {
        return maxSusp;
    }

    public SearchParams setMaxSusp(Integer maxSusp) {
        this.maxSusp = maxSusp;
        return this;
    }

    public Integer getMinDurab() {
        return minDurab;
    }

    public SearchParams setMinDurab(Integer minDurab) {
        this.minDurab = minDurab;
        return this;
    }

    public Integer getMaxDurab() {
        return maxDurab;
    }

    public SearchParams setMaxDurab(Integer maxDurab) {
        this.maxDurab = maxDurab;
        return this;
    }
    
    public Integer getLimit() {
        return limit;
    }

    public SearchParams setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public String toString() {
        return "SearchParams [minRisk=" + minRisk + ", maxRisk=" + maxRisk + ", minTemp=" + minTemp + ", maxTemp=" + maxTemp + ", minHum=" + minHum + ", maxHum=" + maxHum + ", minExp=" + minExp + ", maxExp=" + maxExp + ", minTI=" + minTI + ", maxTI=" + maxTI + ", minAggr=" + minAggr + ", maxAggr=" + maxAggr + ", minLaps=" + minLaps + ", maxLaps=" + maxLaps + ", minSusp=" + minSusp + ", maxSusp=" + maxSusp + ", minDurab=" + minDurab + ", maxDurab=" + maxDurab + ", track=" + track + ", consump=" + consump + ", wear=" + wear + ", tyre=" + tyre + ", weather=" + weather + ", supplier=" + supplier + ", limit=" + limit + "]";
    }
}