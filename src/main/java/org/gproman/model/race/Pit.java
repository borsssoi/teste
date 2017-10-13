package org.gproman.model.race;

import org.gproman.model.PersistentEntity;


public class Pit extends PersistentEntity {
    
    private Integer number;
    private Integer lap;
    private String reason;
    private Integer tyres;
    private Integer fuel;
    private Integer refueledTo;
    private Integer time;
    
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public Integer getLap() {
        return lap;
    }
    public void setLap(Integer lap) {
        this.lap = lap;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Integer getTyres() {
        return tyres;
    }
    public void setTyres(Integer tyres) {
        this.tyres = tyres;
    }
    public Integer getFuel() {
        return fuel;
    }
    public void setFuel(Integer fuel) {
        this.fuel = fuel;
    }
    public Integer getRefueledTo() {
        return refueledTo;
    }
    public void setRefueledTo(Integer refueledTo) {
        this.refueledTo = refueledTo;
    }
    public Integer getTime() {
        return time;
    }
    public void setTime(Integer time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return "Pit [number=" + number + ", lap=" + lap + ", reason=" + reason + ", tyres=" + tyres + ", fuel=" + fuel + ", refueledTo=" + (refueledTo!=null?refueledTo:"No refill") + ", time=" + time + "]";
    }
}
