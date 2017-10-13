package org.gproman.model.race;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;

public class RaceReport {

    private Integer season;
    private Integer race;
    private Practice practice;
    private Qualify qualify1 = new Qualify().setNumber( 1 );
    private Qualify qualify2 = new Qualify().setNumber( 2 );
    private CarSettings raceSettings = new CarSettings();
    private StartingRisk riskStarting;
    private Integer riskOvertake;
    private Integer riskDefend;
    private Integer riskClear;
    private Integer riskClearWet;
    private Integer riskMalfunction;
    
    private String energiaInicial;
    private String energiaFinal;
    
    private Driver driver;
    private Car carStart;
    private Car carFinish;
    private List<Lap> laps = new ArrayList<Lap>();
    
    private Integer startingFuel;
    private Integer finishFuel;
    private Integer finishTyre;
    private List<Pit> pits = new ArrayList<Pit>();

    public Integer getSeason() {
        return season;
    }
    public void setSeason(Integer season) {
        this.season = season;
    }
    public Integer getRace() {
        return this.race;
    }
    public void setRace(Integer race) {
        this.race = race;
    }

    
    public Practice getPractice() {
        return practice;
    }
    public void setPractice(Practice practice) {
        this.practice = practice;
    }
    public Qualify getQualify1() {
        return qualify1;
    }
    public void setQualify1(Qualify qualify1) {
        this.qualify1 = qualify1;
    }
    public Qualify getQualify2() {
        return qualify2;
    }
    public void setQualify2(Qualify qualify2) {
        this.qualify2 = qualify2;
    }
    public CarSettings getRaceSettings() {
        return raceSettings;
    }
    public void setRaceSettings(CarSettings raceSettings) {
        this.raceSettings = raceSettings;
    }
    public StartingRisk getRiskStarting() {
        return riskStarting;
    }
    public void setRiskStarting(StartingRisk riskStarting) {
        this.riskStarting = riskStarting;
    }
    public Integer getRiskOvertake() {
        return riskOvertake;
    }
    public void setRiskOvertake(Integer riskOvertake) {
        this.riskOvertake = riskOvertake;
    }
    public Integer getRiskDefend() {
        return riskDefend;
    }
    public void setRiskDefend(Integer riskDefend) {
        this.riskDefend = riskDefend;
    }
    public Integer getRiskClear() {
        return riskClear;
    }
    public void setRiskClear(Integer riskClear) {
        this.riskClear = riskClear;
    }
    public Integer getRiskMalfunction() {
        return riskMalfunction;
    }
    public void setRiskMalfunction(Integer riskMalfunction) {
        this.riskMalfunction = riskMalfunction;
    }
    
    public void setEnergiaInicial(String energiaInicial) {
        this.energiaInicial = energiaInicial;
    }
    public void setEnergiaFinal(String energiaFinal) {
        this.energiaFinal = energiaFinal;
    }
    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
    public Car getCarStart() {
        return carStart;
    }
    public void setCarStart(Car carStart) {
        this.carStart = carStart;
    }
    public Car getCarFinish() {
        return carFinish;
    }
    public void setCarFinish(Car carFinish) {
        this.carFinish = carFinish;
    }
    public List<Lap> getLaps() {
        return laps;
    }
    public void setLaps(List<Lap> laps) {
        this.laps = laps;
    }
    public Integer getStartingFuel() {
        return startingFuel;
    }
    public void setStartingFuel(Integer startingFuel) {
        this.startingFuel = startingFuel;
    }
    public Integer getFinishFuel() {
        return finishFuel;
    }
    public void setFinishFuel(Integer finishFuel) {
        this.finishFuel = finishFuel;
    }
    public Integer getFinishTyre() {
        return finishTyre;
    }
    public void setFinishTyre(Integer finishTyre) {
        this.finishTyre = finishTyre;
    }
    public List<Pit> getPits() {
        return pits;
    }
    public void setPits(List<Pit> pits) {
        this.pits = pits;
    }

    public Integer getRiskClearWet() {
        return riskClearWet;
    }

    public void setRiskClearWet(Integer riskClearWet) {
        this.riskClearWet = riskClearWet;
    }
    
    public String getEnergiaInicial() {
        return this.energiaInicial;
    }
    public String getEnergiaFinal() {
        return this.energiaFinal;
    }
}
