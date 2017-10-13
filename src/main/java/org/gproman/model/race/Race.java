package org.gproman.model.race;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gproman.model.PersistentEntity;
import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.staff.Facilities;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;

public class Race extends PersistentEntity {

    private int          number;
    private Integer      seasonNumber;
    private Timestamp    date;
    private Track        track;

    private RaceStatus   status   = new RaceStatus();

    private Practice     practice;
    private Qualify      qualify1;
    private Qualify      qualify2;
    private CarSettings  raceSettings;
    private StartingRisk riskStarting;
    private Integer      riskOvertake;
    private Integer      riskDefend;
    private Integer      riskClear;
    private Integer      riskClearWet;
    private Integer      riskMalfunction;
    private String       energiaInicial;
    private String       energiaFinal;
    private Tyre         atStart;
    private Tyre         whenWet;
    private Tyre         whenDry;
    private Integer      pitWet;
    private Integer      pitDry;
    private Driver       driverStart;
    private Driver       driverFinish;
    private TechDirector tdStart;
    private TechDirector tdFinish;
    private Car          carStart;
    private Car          carFinish;
    private List<Lap>    laps     = new ArrayList<Lap>();

    private Integer      startingFuel;
    private Integer      finishFuel;
    private Integer      finishTyre;
    private String       fuelStrategy;
    private List<Pit>    pits     = new ArrayList<Pit>();

    private Forecast[]   forecast = new Forecast[6];

    private TestSession  testSession;
    private Facilities   facilities;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int race) {
        this.number = race;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public RaceStatus getStatus() {
        return status;
    }

    public void setStatus(RaceStatus status) {
        this.status = status;
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
    
    public String getEnergiaInicial() {
        return energiaInicial;
    }

    public void setEnergiaInicial(String energiaInicial) {
        this.energiaInicial = energiaInicial;
    }
    
    public String getEnergiaFinal() {
        return energiaFinal;
    }

    public void setEnergiaFinal(String energiaFinal) {
        this.energiaFinal = energiaFinal;
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

    public Driver getDriverStart() {
        return driverStart;
    }

    public void setDriverStart(Driver driver) {
        this.driverStart = driver;
    }

    public Driver getDriverFinish() {
        return driverFinish;
    }

    public void setDriverFinish(Driver driver) {
        this.driverFinish = driver;
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

    public Facilities getFacilities() {
        return facilities;
    }

    public void setFacilities(Facilities facilities) {
        this.facilities = facilities;
    }

    public void populateFromReport(RaceReport report) {
        setSeasonNumber(report.getSeason());
        if (report.getPractice() != null) {
            setPractice(report.getPractice());
            getStatus().setPractice(true);
        }
        if (report.getQualify1() != null) {
            if (getQualify1() != null && getQualify1().getRiskDescr() != null) {
                // risk does not show up on race report
                report.getQualify1().setRiskDescr(getQualify1().getRiskDescr());
            }
            setQualify1(report.getQualify1());
            getStatus().setQualify1(true);
        }
        if (report.getQualify2() != null) {
            if (getQualify2() != null && getQualify2().getRiskDescr() != null) {
                // risk does not show up on race report
                report.getQualify2().setRiskDescr(getQualify2().getRiskDescr());
            }
            setQualify2(report.getQualify2());
            getStatus().setQualify2(true);
        }
        if (report.getRaceSettings() != null) {
            setRaceSettings(report.getRaceSettings());
            getStatus().setSetup(true);
        }
        if (report.getRiskStarting() != null) {
            setRiskStarting(report.getRiskStarting());
        }
        if (report.getRiskOvertake() != null) {
            setRiskOvertake(report.getRiskOvertake());
        }
        if (report.getEnergiaInicial()!= null) {
            setEnergiaInicial(report.getEnergiaInicial());
        }
        if (report.getEnergiaFinal()!= null) {
            setEnergiaFinal(report.getEnergiaFinal());
        }
        if (report.getRiskDefend() != null) {
            setRiskDefend(report.getRiskDefend());
        }
        if (report.getRiskClear() != null) {
            setRiskClear(report.getRiskClear());
        }
        if (report.getRiskClearWet() != null) {
            setRiskClearWet(report.getRiskClearWet());
        }
        if (report.getRiskMalfunction() != null) {
            setRiskMalfunction(report.getRiskMalfunction());
        }
        if (report.getDriver() != null) {
            setDriverFinish(report.getDriver());
        }
        if (report.getCarStart() != null) {
            setCarStart(report.getCarStart());
        }
        if (report.getCarFinish() != null) {
            setCarFinish(report.getCarFinish());
        }
        setLaps(report.getLaps());
        setStartingFuel(report.getStartingFuel());
        setFinishFuel(report.getFinishFuel());
        setFinishTyre(report.getFinishTyre());
        setPits(report.getPits());
        getStatus().setTelemetry(true);
    }

    public Double getFuelConsumption() {
        if (getStartingFuel() == null || getFinishFuel() == null) {
            return null;
        }
        double ret = safeGet(getStartingFuel());
        for (Pit pit : pits) {
            if (pit.getRefueledTo() != null) {
                ret -= (safeGet(pit.getFuel()) * 1.8); // fuel left
                ret += safeGet(pit.getRefueledTo());
            }
        }
        ret -= safeGet(getFinishFuel());
        return ret;
    }

    public double safeGet(Number number) {
        return number == null ? 0 : number.doubleValue();
    }

    public Double getFuelEfficiency() {
        Double fuel = getFuelConsumption();
        return (getTrack() == null || fuel == null) ? null : getTrack().getDistance() / fuel;
    }

    public List<Stint> getStints() {
        List<Stint> ret = new ArrayList<Stint>();
        int number = 1;
        int startingLap = 1;
        for (Pit pit : pits) {
            Stint st = populateStint(number,
                    startingLap,
                    pit.getLap(),
                    pit.getReason(),
                    pit.getTyres(),
                    pit.getFuel(),
                    pit.getRefueledTo(),
                    pit.getTime());

            ret.add(st);
            number++;
            startingLap = pit.getLap() + 1;
        }
        Lap lastLap = null;
        for (int i = startingLap; i < laps.size(); i++) {
            lastLap = laps.get(i);
            if (lastLap == null || lastLap.getTime() == null || lastLap.getTime() <= 0) {
                break;
            }
        }
        if (!laps.isEmpty() && (laps.get(1).getEvents() == null || !laps.get(1).getEvents().equalsIgnoreCase("Start Accident"))
                && getFinishFuel() != null && getFinishTyre() != null && lastLap == laps.get(laps.size() - 1)) {
            Stint st = populateStint(number,
                    startingLap,
                    lastLap.getNumber(),
                    "Race end",
                    getFinishTyre(),
                    getFinishFuel() != null ? (int) (getFinishFuel() / 1.8) : null, // has to be percentual of the full tank
                    null,
                    null);
            ret.add(st);
        }
        return ret;
    }

    private Stint populateStint(int number,
            int startingLap,
            int lap,
            String reason,
            Integer tyresLeft,
            Integer fuelLeft,
            Integer refueledTo,
            Integer time) {
        Stint st = new Stint();
        st.setNumber(number);
        st.setInitialLap(startingLap);
        st.setFinalLap(lap);
        st.setPitReason(reason);
        st.setTyre(laps.get(startingLap).getSettings().getTyre());
        st.setTyreLeft(tyresLeft);

        double used = st.getLapsCount() * getTrack().getLapDistance();
        double total = (100 * used) / (100 - st.getTyreLeft());
        double noBad = total * .82;
        st.setTyreUsed(used);
        st.setTyreNoBad(noBad);
        st.setTyreDurability(total);

        double avgTemp = 0;
        double avgHum = 0;
        for (int j = st.getInitialLap(); j <= st.getFinalLap(); j++) {
            Lap l = laps.get(j);
            if (l.getTemperature() == null || l.getHumidity() == null) {
                avgTemp = 0;
                avgHum = 0;
                break;
            }
            avgTemp += l.getTemperature();
            avgHum += l.getHumidity();
        }
        avgTemp /= st.getLapsCount();
        avgHum /= st.getLapsCount();
        st.setAvgTemp(avgTemp);
        st.setAvgHum(avgHum);

        st.setFuelLeft(fuelLeft);
        st.setRefueledTo(refueledTo);
        st.setPitTime(time);
        return st;
    }

    public static class Stint {

        private int     number;
        private int     initialLap;
        private int     finalLap;
        private String  pitReason;
        private Tyre    tyre;
        private int     tyreLeft;
        private double  tyreUsed;
        private double  tyreNoBad;
        private double  tyreDurability;
        private double  avgTemp;
        private double  avgHum;
        private int     fuelLeft;
        private Integer refueledTo;    // can be null
        private Integer pitTime;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getInitialLap() {
            return initialLap;
        }

        public void setInitialLap(int initialLap) {
            this.initialLap = initialLap;
        }

        public int getFinalLap() {
            return finalLap;
        }

        public void setFinalLap(int finalLap) {
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

        public int getTyreLeft() {
            return tyreLeft;
        }

        public void setTyreLeft(int tyreLeft) {
            this.tyreLeft = tyreLeft;
        }

        public double getTyreUsed() {
            return tyreUsed;
        }

        public void setTyreUsed(double tyreUsed) {
            this.tyreUsed = tyreUsed;
        }

        public double getTyreNoBad() {
            return tyreNoBad;
        }

        public void setTyreNoBad(double tyreNoBad) {
            this.tyreNoBad = tyreNoBad;
        }

        public double getTyreDurability() {
            return tyreDurability;
        }

        public void setTyreDurability(double tyreDurability) {
            this.tyreDurability = tyreDurability;
        }

        public double getAvgTemp() {
            return avgTemp;
        }

        public void setAvgTemp(double avgTemp) {
            this.avgTemp = avgTemp;
        }

        public double getAvgHum() {
            return avgHum;
        }

        public void setAvgHum(double avtHum) {
            this.avgHum = avtHum;
        }

        public int getFuelLeft() {
            return fuelLeft;
        }

        public void setFuelLeft(int fuelLeft) {
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

        public int getLapsCount() {
            return finalLap - initialLap + 1;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        result = prime * result + ((seasonNumber == null) ? 0 : seasonNumber.hashCode());
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
        Race other = (Race) obj;
        if (number != other.number)
            return false;
        if (seasonNumber == null) {
            if (other.seasonNumber != null)
                return false;
        } else if (!seasonNumber.equals(other.seasonNumber))
            return false;
        return true;
    }

    public String getFuelStrategy() {
        return fuelStrategy;
    }

    public void setFuelStrategy(String fuelStrategy) {
        this.fuelStrategy = fuelStrategy;
    }

    public Forecast[] getForecast() {
        return forecast;
    }

    public void setForecast(Forecast[] forecast) {
        this.forecast = forecast;
    }

    public double getAverageTemperature() {
        double avg = 0;
        // calculates the average temperature of the first 3 quarters, ignoring the last one by default
        for (int i = 2; i < 5; i++) {
            if (forecast[i] != null) {
                avg += (forecast[i].getTempMax() + forecast[i].getTempMin()) / 2;
            }
        }
        return avg / 3;
    }

    public TechDirector getTDStart() {
        return tdStart;
    }

    public void setTDStart(TechDirector tdStart) {
        this.tdStart = tdStart;
    }

    public TechDirector getTDFinish() {
        return tdFinish;
    }

    public void setTDFinish(TechDirector tdFinish) {
        this.tdFinish = tdFinish;
    }

    public Tyre getTyreAtStart() {
        return atStart;
    }

    public void setTyreAtStart(Tyre atStart) {
        this.atStart = atStart;
    }

    public Tyre getTyreWhenWet() {
        return whenWet;
    }

    public void setTyreWhenWet(Tyre whenWet) {
        this.whenWet = whenWet;
    }

    public Tyre getTyreWhenDry() {
        return whenDry;
    }

    public void setTyreWhenDry(Tyre whenDry) {
        this.whenDry = whenDry;
    }

    public Integer getWaitPitWet() {
        return pitWet;
    }

    public void setWaitPitWet(Integer pitWet) {
        this.pitWet = pitWet;
    }

    public Integer getWaitPitDry() {
        return pitDry;
    }

    public void setWaitPitDry(Integer pitDry) {
        this.pitDry = pitDry;
    }

    public TestSession getTestSession() {
        return testSession;
    }

    public void setTestSession(TestSession testSession) {
        this.testSession = testSession;
    }
    
    public Integer getRiskClearWet() {
        return riskClearWet;
    }

    public void setRiskClearWet(Integer riskClearWet) {
        this.riskClearWet = riskClearWet;
    }

    @Override
    public String toString() {
        return "Race [number=" + number + ", seasonNumber=" + seasonNumber + ", date=" + date + ", track=" + track + ", status=" + status + ", practice=" + practice + ", qualify1=" + qualify1 + ", qualify2=" + qualify2 + ", raceSettings=" + raceSettings + ", riskStarting=" + riskStarting + ", riskOvertake=" + riskOvertake + ", riskDefend=" + riskDefend + ", riskClear=" + riskClear + ", riskClearWet=" + riskClearWet + ", riskMalfunction=" + riskMalfunction + ", atStart=" + atStart + ", whenWet=" + whenWet + ", whenDry=" + whenDry + ", pitWet=" + pitWet + ", pitDry=" + pitDry + ", driverStart=" + driverStart + ", driverFinish=" + driverFinish + ", tdStart=" + tdStart + ", tdFinish=" + tdFinish + ", carStart=" + carStart + ", carFinish=" + carFinish + ", laps=" + laps + ", startingFuel=" + startingFuel + ", finishFuel=" + finishFuel + ", finishTyre=" + finishTyre + ", fuelStrategy=" + fuelStrategy + ", pits=" + pits + ", forecast=" + Arrays.toString(forecast) + ", testSession=" + testSession + "]";
    }


}
