package org.gproman.model.everest;

import org.gproman.model.PersistentEntity;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;

public class NormalizedLap extends PersistentEntity {

    private Integer number;
    private Integer time;
    private Integer position;
    private Tyre    tyre;
    private Weather weather;
    private Integer temperature;
    private Integer humidity;
    private String  events;

    public NormalizedLap() {
        super();
    }

    public NormalizedLap(Integer id) {
        super(id);
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Tyre getTyre() {
        return tyre;
    }

    public void setTyre(Tyre tyre) {
        this.tyre = tyre;
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
        return "NormalizedLap[number=" + number + ", time=" + time + ", position=" + position + ", tyre=" + tyre + ", weather=" + weather + ", temperature=" + temperature + ", humidity=" + humidity + ", events=" + events + "]";
    }
}
