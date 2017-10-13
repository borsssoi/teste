package org.gproman.model.race;

import java.util.HashMap;
import java.util.Map;

import org.gproman.model.PersistentEntity;
import org.gproman.model.race.Comment.Part;

public class Lap extends PersistentEntity {

    private Integer     number;
    private Integer     time;
    private Integer     mistake;
    private Integer     netTime;
    private Integer     position;
    private Weather     weather;
    private Integer     temperature;
    private Integer     humidity;
    private String      events;

    private CarSettings settings;

    private String      comments;

    public Lap() {
        this.settings = new CarSettings();
    }

    public Lap(Integer number,
               Integer time,
               Integer mistake,
               Integer netTime,
               Integer position,
               Weather weather,
               Integer temperature,
               Integer humidity,
               String events,
               CarSettings settings,
               String comments) {
        super();
        this.number = number;
        this.time = time;
        this.mistake = mistake;
        this.netTime = netTime;
        this.position = position;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
        this.events = events;
        this.settings = settings;
        this.comments = comments;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getMistake() {
        return mistake;
    }

    public void setMistake(Integer mistake) {
        this.mistake = mistake;
    }

    public Integer getNetTime() {
        return netTime;
    }

    public void setNetTime(Integer netTime) {
        this.netTime = netTime;
    }

    public CarSettings getSettings() {
        return this.settings;
    }

    public void setSettings(CarSettings settings) {
        this.settings = settings;
    }

    public String getComments() {
        return comments;
    }

    public Map<Part, Comment> getParsedComments() {
        Map<Part, Comment> res = new HashMap<Part, Comment>();
        String[] split = comments.substring( 1, comments.length() - 1 ).split( "," );
        for ( String sp : split ) {
            if ( sp != null && !sp.isEmpty() ) {
                Comment comment = Comment.valueOf( sp.trim() );
                res.put( comment.getPart(), comment );
            }
        }
        return res;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Lap [number=" + number + ", time=" + time + ", mistake=" + mistake + ", netTime=" + netTime + ", position=" + position + ", weather=" + weather + ", temperature=" + temperature + ", humidity=" + humidity + ", events=" + events + ", settings=" + settings + ", comments=" + comments + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + ((humidity == null) ? 0 : humidity.hashCode());
        result = prime * result + ((mistake == null) ? 0 : mistake.hashCode());
        result = prime * result + ((netTime == null) ? 0 : netTime.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((settings == null) ? 0 : settings.hashCode());
        result = prime * result + ((temperature == null) ? 0 : temperature.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((weather == null) ? 0 : weather.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Lap other = (Lap) obj;
        if ( comments == null ) {
            if ( other.comments != null ) return false;
        } else if ( !comments.equals( other.comments ) ) return false;
        if ( events == null ) {
            if ( other.events != null ) return false;
        } else if ( !events.equals( other.events ) ) return false;
        if ( humidity == null ) {
            if ( other.humidity != null ) return false;
        } else if ( !humidity.equals( other.humidity ) ) return false;
        if ( mistake == null ) {
            if ( other.mistake != null ) return false;
        } else if ( !mistake.equals( other.mistake ) ) return false;
        if ( netTime == null ) {
            if ( other.netTime != null ) return false;
        } else if ( !netTime.equals( other.netTime ) ) return false;
        if ( number == null ) {
            if ( other.number != null ) return false;
        } else if ( !number.equals( other.number ) ) return false;
        if ( position == null ) {
            if ( other.position != null ) return false;
        } else if ( !position.equals( other.position ) ) return false;
        if ( settings == null ) {
            if ( other.settings != null ) return false;
        } else if ( !settings.equals( other.settings ) ) return false;
        if ( temperature == null ) {
            if ( other.temperature != null ) return false;
        } else if ( !temperature.equals( other.temperature ) ) return false;
        if ( time == null ) {
            if ( other.time != null ) return false;
        } else if ( !time.equals( other.time ) ) return false;
        if ( weather != other.weather ) return false;
        return true;
    }

}
