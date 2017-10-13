package org.gproman.model.race;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;
import org.gproman.model.car.Car;
import org.gproman.model.car.PHA;
import org.gproman.model.track.Track;

public class TestSession extends PersistentEntity {

    private Track           track;
    private Weather         weather;
    private Integer         temperature;
    private Integer         humidity;

    private Integer         lapsDone;
    private Integer         stintsDone;

    private PHA             testPoints;
    private PHA             rdPoints;
    private PHA             engPoints;
    private PHA             ccPoints;

    private Car             currentCar;

    private List<TestStint> stints = new ArrayList<TestStint>();

    public TestSession() {
        super();
    }

    public TestSession(Integer id) {
        super( id );
    }

    public TestSession(Track track,
                       Weather weather,
                       Integer temperature,
                       Integer humidity,
                       Integer lapsDone,
                       Integer stintsDone,
                       PHA testPoints,
                       PHA rdPoints,
                       PHA engPoints,
                       PHA ccPoints,
                       Car currentCar,
                       List<TestStint> stints) {
        super();
        this.track = track;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
        this.lapsDone = lapsDone;
        this.stintsDone = stintsDone;
        this.testPoints = testPoints;
        this.rdPoints = rdPoints;
        this.engPoints = engPoints;
        this.ccPoints = ccPoints;
        this.currentCar = currentCar;
        this.stints = stints;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getLapsDone() {
        return lapsDone;
    }

    public void setLapsDone(Integer lapsDone) {
        this.lapsDone = lapsDone;
    }

    public Integer getStintsDone() {
        return stintsDone;
    }

    public void setStintsDone(Integer stintsDone) {
        this.stintsDone = stintsDone;
    }

    public PHA getTestPoints() {
        return testPoints;
    }

    public void setTestPoints(PHA testPoints) {
        this.testPoints = testPoints;
    }

    public PHA getRdPoints() {
        return rdPoints;
    }

    public void setRdPoints(PHA rdPoints) {
        this.rdPoints = rdPoints;
    }

    public PHA getEngPoints() {
        return engPoints;
    }

    public void setEngPoints(PHA engPoints) {
        this.engPoints = engPoints;
    }

    public PHA getCcPoints() {
        return ccPoints;
    }

    public void setCcPoints(PHA ccPoints) {
        this.ccPoints = ccPoints;
    }

    public Car getCurrentCar() {
        return currentCar;
    }

    public void setCurrentCar(Car currentCar) {
        this.currentCar = currentCar;
    }

    public List<TestStint> getStints() {
        return stints;
    }

    public void setStints(List<TestStint> stints) {
        this.stints = stints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ccPoints == null) ? 0 : ccPoints.hashCode());
        result = prime * result + ((currentCar == null) ? 0 : currentCar.hashCode());
        result = prime * result + ((engPoints == null) ? 0 : engPoints.hashCode());
        result = prime * result + ((humidity == null) ? 0 : humidity.hashCode());
        result = prime * result + ((lapsDone == null) ? 0 : lapsDone.hashCode());
        result = prime * result + ((rdPoints == null) ? 0 : rdPoints.hashCode());
        result = prime * result + ((stints == null) ? 0 : stints.hashCode());
        result = prime * result + ((stintsDone == null) ? 0 : stintsDone.hashCode());
        result = prime * result + ((temperature == null) ? 0 : temperature.hashCode());
        result = prime * result + ((testPoints == null) ? 0 : testPoints.hashCode());
        result = prime * result + ((track == null) ? 0 : track.hashCode());
        result = prime * result + ((weather == null) ? 0 : weather.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TestSession other = (TestSession) obj;
        if ( ccPoints == null ) {
            if ( other.ccPoints != null ) return false;
        } else if ( !ccPoints.equals( other.ccPoints ) ) return false;
        if ( currentCar == null ) {
            if ( other.currentCar != null ) return false;
        } else if ( !currentCar.equals( other.currentCar ) ) return false;
        if ( engPoints == null ) {
            if ( other.engPoints != null ) return false;
        } else if ( !engPoints.equals( other.engPoints ) ) return false;
        if ( humidity == null ) {
            if ( other.humidity != null ) return false;
        } else if ( !humidity.equals( other.humidity ) ) return false;
        if ( lapsDone == null ) {
            if ( other.lapsDone != null ) return false;
        } else if ( !lapsDone.equals( other.lapsDone ) ) return false;
        if ( rdPoints == null ) {
            if ( other.rdPoints != null ) return false;
        } else if ( !rdPoints.equals( other.rdPoints ) ) return false;
        if ( stints == null ) {
            if ( other.stints != null ) return false;
        } else if ( !stints.equals( other.stints ) ) return false;
        if ( stintsDone == null ) {
            if ( other.stintsDone != null ) return false;
        } else if ( !stintsDone.equals( other.stintsDone ) ) return false;
        if ( temperature == null ) {
            if ( other.temperature != null ) return false;
        } else if ( !temperature.equals( other.temperature ) ) return false;
        if ( testPoints == null ) {
            if ( other.testPoints != null ) return false;
        } else if ( !testPoints.equals( other.testPoints ) ) return false;
        if ( track == null ) {
            if ( other.track != null ) return false;
        } else if ( !track.equals( other.track ) ) return false;
        if ( weather != other.weather ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestSession [track=" + (track != null ? track.getName() : "null") + ", weather=" + weather + ", temperature=" + temperature + ", humidity=" + humidity + ", lapsDone=" + lapsDone + ", stintsDone=" + stintsDone + ", testPoints=" + testPoints + ", rdPoints=" + rdPoints + ", engPoints=" + engPoints + ", ccPoints=" + ccPoints + ", currentCar=" + currentCar + ", stints=" + stints + "]";
    }

    public void merge(TestSession other) {
        setTrack( other.track );
        setWeather( other.weather );
        setTemperature( other.temperature );
        setHumidity( other.humidity );
        
        setLapsDone( other.lapsDone );
        setStintsDone( other.stintsDone );
        
        setTestPoints( other.testPoints );
        setRdPoints( other.rdPoints );
        setEngPoints( other.engPoints );
        setCcPoints( other.ccPoints );
        
        for( int i = 0; i < stints.size(); i++ ) {
            TestStint stint = stints.get( i );
            stint.merge( other.stints.get( i ) );
        }
        if( ! stints.isEmpty() ) {
            TestStint last = stints.get( stints.size()-1 );
            if( last.getCarFinish() == null ) {
                last.setCarFinish( currentCar );
            }
        }
        Car carStart = currentCar;
        for( int i = stints.size(); i < other.stints.size(); i++ ) {
            TestStint newStint = other.stints.get( i );
            newStint.setCarStart( carStart );
            carStart = null;
            stints.add( newStint );
        }
        if( ! stints.isEmpty() ) {
            TestStint last = stints.get( stints.size()-1 );
            if( last.getCarFinish() == null ) {
                last.setCarFinish( other.currentCar );
            }
        }
        setCurrentCar( other.currentCar );
    }

}
