package org.gproman.model.race;

import org.gproman.model.PersistentEntity;
import org.gproman.model.car.Car;

public class TestStint extends PersistentEntity {
    
    private Integer     number;
    
    private Integer     lapsDone;
    private Integer     lapsPlanned;
    
    private Integer     bestTime;
    private Integer     meanTime;
    
    private CarSettings settings;
    
    private Integer     fuelStart;
    private Integer     fuelEnd;
    
    private Integer     tyresEnd;
    
    private TestPriority priority;     

    private String      comments;
    
    private Car         carStart;
    private Car         carFinish;

    public TestStint() {
        super();
    }

    public TestStint(Integer id) {
        super( id );
    }

    public TestStint(Integer number,
                      Integer lapsDone,
                      Integer lapsPlanned,
                      Integer bestTime,
                      Integer meanTime,
                      CarSettings settings,
                      Integer fuelStart,
                      Integer fuelEnd,
                      Integer tyresEnd,
                      TestPriority priority,
                      String comments,
                      Car carStart,
                      Car carFinish) {
        super();
        this.number = number;
        this.lapsDone = lapsDone;
        this.lapsPlanned = lapsPlanned;
        this.bestTime = bestTime;
        this.meanTime = meanTime;
        this.settings = settings;
        this.fuelStart = fuelStart;
        this.fuelEnd = fuelEnd;
        this.tyresEnd = tyresEnd;
        this.priority = priority;
        this.comments = comments;
        this.carStart = carStart;
        this.carFinish = carFinish;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getLapsDone() {
        return lapsDone;
    }

    public void setLapsDone(Integer lapsDone) {
        this.lapsDone = lapsDone;
    }

    public Integer getLapsPlanned() {
        return lapsPlanned;
    }

    public void setLapsPlanned(Integer lapsPlanned) {
        this.lapsPlanned = lapsPlanned;
    }

    public Integer getBestTime() {
        return bestTime;
    }

    public void setBestTime(Integer bestTime) {
        this.bestTime = bestTime;
    }

    public Integer getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(Integer meanTime) {
        this.meanTime = meanTime;
    }

    public CarSettings getSettings() {
        return settings;
    }

    public void setSettings(CarSettings settings) {
        this.settings = settings;
    }

    public Integer getFuelStart() {
        return fuelStart;
    }

    public void setFuelStart(Integer fuelStart) {
        this.fuelStart = fuelStart;
    }

    public Integer getFuelEnd() {
        return fuelEnd;
    }

    public void setFuelEnd(Integer fuelEnd) {
        this.fuelEnd = fuelEnd;
    }

    public Integer getTyresEnd() {
        return tyresEnd;
    }

    public void setTyresEnd(Integer tyresEnd) {
        this.tyresEnd = tyresEnd;
    }

    public TestPriority getPriority() {
        return priority;
    }

    public void setPriority(TestPriority priority) {
        this.priority = priority;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bestTime == null) ? 0 : bestTime.hashCode());
        result = prime * result + ((carFinish == null) ? 0 : carFinish.hashCode());
        result = prime * result + ((carStart == null) ? 0 : carStart.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((fuelEnd == null) ? 0 : fuelEnd.hashCode());
        result = prime * result + ((fuelStart == null) ? 0 : fuelStart.hashCode());
        result = prime * result + ((lapsDone == null) ? 0 : lapsDone.hashCode());
        result = prime * result + ((lapsPlanned == null) ? 0 : lapsPlanned.hashCode());
        result = prime * result + ((meanTime == null) ? 0 : meanTime.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((settings == null) ? 0 : settings.hashCode());
        result = prime * result + ((tyresEnd == null) ? 0 : tyresEnd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TestStint other = (TestStint) obj;
        if ( bestTime == null ) {
            if ( other.bestTime != null ) return false;
        } else if ( !bestTime.equals( other.bestTime ) ) return false;
        if ( carFinish == null ) {
            if ( other.carFinish != null ) return false;
        } else if ( !carFinish.equals( other.carFinish ) ) return false;
        if ( carStart == null ) {
            if ( other.carStart != null ) return false;
        } else if ( !carStart.equals( other.carStart ) ) return false;
        if ( comments == null ) {
            if ( other.comments != null ) return false;
        } else if ( !comments.equals( other.comments ) ) return false;
        if ( fuelEnd == null ) {
            if ( other.fuelEnd != null ) return false;
        } else if ( !fuelEnd.equals( other.fuelEnd ) ) return false;
        if ( fuelStart == null ) {
            if ( other.fuelStart != null ) return false;
        } else if ( !fuelStart.equals( other.fuelStart ) ) return false;
        if ( lapsDone == null ) {
            if ( other.lapsDone != null ) return false;
        } else if ( !lapsDone.equals( other.lapsDone ) ) return false;
        if ( lapsPlanned == null ) {
            if ( other.lapsPlanned != null ) return false;
        } else if ( !lapsPlanned.equals( other.lapsPlanned ) ) return false;
        if ( meanTime == null ) {
            if ( other.meanTime != null ) return false;
        } else if ( !meanTime.equals( other.meanTime ) ) return false;
        if ( number == null ) {
            if ( other.number != null ) return false;
        } else if ( !number.equals( other.number ) ) return false;
        if ( priority != other.priority ) return false;
        if ( settings == null ) {
            if ( other.settings != null ) return false;
        } else if ( !settings.equals( other.settings ) ) return false;
        if ( tyresEnd == null ) {
            if ( other.tyresEnd != null ) return false;
        } else if ( !tyresEnd.equals( other.tyresEnd ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestStint [number=" + number + ", lapsDone=" + lapsDone + ", lapsPlanned=" + lapsPlanned + ", bestTime=" + bestTime + ", meanTime=" + meanTime + ", settings=" + settings + ", fuelStart=" + fuelStart + ", fuelEnd=" + fuelEnd + ", tyresEnd=" + tyresEnd + ", priority=" + priority + ", comments=" + comments + ", carStart=" + carStart + ", carFinish=" + carFinish + "]";
    }

    public void merge(TestStint other) {
        this.number = other.number;
        this.lapsDone = other.lapsDone;
        this.lapsPlanned = other.lapsPlanned;
        this.bestTime = other.bestTime;
        this.meanTime = other.meanTime;
        this.settings = other.settings;
        this.fuelStart = other.fuelStart;
        this.fuelEnd = other.fuelEnd;
        this.tyresEnd = other.tyresEnd;
        this.priority = other.priority;
        this.comments = other.comments;
        this.carStart = other.carStart != null ? other.carStart : this.carStart;
        this.carFinish = other.carFinish != null ? other.carFinish : this.carFinish;
    }

}
