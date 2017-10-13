package org.gproman.model.everest;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.CarSettings;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;

import com.thoughtworks.xstream.XStream;

public class NormalizedRace extends PersistentEntity {

    public static enum RaceStatus {
        COMPLETED, CAR_PROBLEM, DROPPED_OUT, UNKNOWN, ERROR;
    }

    public static enum TDStatus {
        HIRED, NOT_HIRED, UNKNOWN;
    }

    private Integer               seasonNumber;
    private Integer               raceNumber;
    private String                manager;
    private String                group;
    private Track                 track;
    private RaceStatus            raceStatus = RaceStatus.UNKNOWN;

    private String                url;
    private String                tool;

    private TyreSupplier          supplier;
    private CarSettings           raceSettings;

    private Integer               riskOvertake;
    private Integer               riskDefend;
    private Integer               riskClear;
    private Integer               riskClearWet;
    private Integer               riskMalfunction;

    private Driver                driverStart;
    private Driver                driverFinish;

    private TDStatus              tdStatus   = TDStatus.UNKNOWN;
    private TechDirector          techDir;

    private Car                   carStart;
    private Car                   carFinish;
    private Car                   carWear;

    private Integer               startingFuel;
    private Integer               finishFuel;
    private Double                avgHum;
    private Double                avgTemp;
    private Double                fuelUsed;
    private Double                distance;

    private List<NormalizedStint> stints     = new ArrayList<NormalizedStint>();

    private List<NormalizedLap>   laps       = new ArrayList<NormalizedLap>();

    public NormalizedRace() {
        super();
    }

    public NormalizedRace(Integer id) {
        super(id);
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(Integer raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public RaceStatus getRaceStatus() {
        return raceStatus;
    }

    public void setRaceStatus(RaceStatus raceStatus) {
        this.raceStatus = raceStatus;
    }

    public TyreSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(TyreSupplier supplier) {
        this.supplier = supplier;
    }

    public CarSettings getRaceSettings() {
        return raceSettings;
    }

    public void setRaceSettings(CarSettings raceSettings) {
        this.raceSettings = raceSettings;
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

    public Integer getRiskClearWet() {
        return riskClearWet;
    }

    public void setRiskClearWet(Integer riskClearWet) {
        this.riskClearWet = riskClearWet;
    }

    public Integer getRiskMalfunction() {
        return riskMalfunction;
    }

    public void setRiskMalfunction(Integer riskMalfunction) {
        this.riskMalfunction = riskMalfunction;
    }

    public Driver getDriverStart() {
        return driverStart;
    }

    public void setDriverStart(Driver driverStart) {
        this.driverStart = driverStart;
    }

    public Driver getDriverFinish() {
        return driverFinish;
    }

    public void setDriverFinish(Driver driverFinish) {
        this.driverFinish = driverFinish;
    }

    public TDStatus getTdStatus() {
        return tdStatus;
    }

    public void setTdStatus(TDStatus tdStatus) {
        this.tdStatus = tdStatus;
    }

    public TechDirector getTechDirector() {
        return techDir;
    }

    public void setTechDirector(TechDirector tdStart) {
        this.techDir = tdStart;
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

    public Car getCarWear() {
        return carWear;
    }

    public void setCarWear(Car carWear) {
        this.carWear = carWear;
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

    public Double getAvgHum() {
        return avgHum;
    }

    public void setAvgHum(Double avgHum) {
        this.avgHum = avgHum;
    }

    public Double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(Double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public Double getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(Double fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<NormalizedStint> getStints() {
        return stints;
    }

    public void setStints(List<NormalizedStint> stints) {
        this.stints = stints;
    }

    public List<NormalizedLap> getLaps() {
        return laps;
    }

    public void setLaps(List<NormalizedLap> laps) {
        this.laps = laps;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    @Override
    public String toString() {
        XStream xs = getXStream();
        return xs.toXML( this );
    }

    public String getHeader() {
        return "NormalizedRace[ " + manager + ", season " + seasonNumber + ", race " + raceNumber + ", "+tool+", "+url+" ]";
    }
    
    private XStream getXStream() {
        XStream xstream = new XStream();
        xstream.alias("race", NormalizedRace.class);
        xstream.alias("carPart", CarPart.class);
        xstream.alias("track", Track.class);
        xstream.alias("stint", NormalizedStint.class);
        xstream.alias("lap", NormalizedLap.class);
        return xstream;
    }

    

}
