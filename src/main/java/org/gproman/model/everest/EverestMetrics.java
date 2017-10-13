package org.gproman.model.everest;

import java.util.Map;

import org.gproman.model.season.TyreSupplier;

public class EverestMetrics {

    private Integer                    seasons;
    private Integer                    races;
    private Integer                    telemetries;
    private Integer                    stints;
    private Integer                    latestSeason;
    private Map<Integer, Integer>      telemetriesPerSeason;
    private Map<Integer, Integer>      telemetriesPerRaceCurrentSeason;
    private Map<WeatherType, Integer>  stintsPerWeather;
    private Map<TyreSupplier, Integer> telemetriesPerSupplier;

    public Map<Integer, Integer> getTelemetriesPerSeason() {
        return telemetriesPerSeason;
    }

    public void setTelemetriesPerSeason(Map<Integer, Integer> racesPerSeason) {
        this.telemetriesPerSeason = racesPerSeason;
    }

    public Map<Integer, Integer> getTelemetriesPerRaceCurrentSeason() {
        return telemetriesPerRaceCurrentSeason;
    }

    public void setTelemetriesPerRaceCurrentSeason(Map<Integer, Integer> telemetriesPerRaceCurrentSeason) {
        this.telemetriesPerRaceCurrentSeason = telemetriesPerRaceCurrentSeason;
    }

    public Integer getSeasons() {
        return seasons;
    }

    public void setSeasons(Integer seasons) {
        this.seasons = seasons;
    }

    public Integer getRaces() {
        return races;
    }

    public void setRaces(Integer races) {
        this.races = races;
    }

    public Integer getTelemetries() {
        return telemetries;
    }

    public void setTelemetries(Integer telemetries) {
        this.telemetries = telemetries;
    }

    public Integer getStints() {
        return stints;
    }

    public void setStints(Integer stints) {
        this.stints = stints;
    }

    public void setStintsPerWeather(Map<WeatherType, Integer> stintsPerWeather) {
        this.stintsPerWeather = stintsPerWeather;
    }

    public Map<WeatherType, Integer> getStintsPerWeather() {
        return stintsPerWeather;
    }

    public void setTelemetriesPerSupplier(Map<TyreSupplier, Integer> telemetriesPerSupplier) {
        this.telemetriesPerSupplier = telemetriesPerSupplier;
    }

    public Map<TyreSupplier, Integer> getTelemetriesPerSupplier() {
        return telemetriesPerSupplier;
    }

    public Integer getLatestSeason() {
        return latestSeason;
    }

    public void setLatestSeason(Integer latestSeason) {
        this.latestSeason = latestSeason;
    }

}
