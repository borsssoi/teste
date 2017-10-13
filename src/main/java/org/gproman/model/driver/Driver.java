package org.gproman.model.driver;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;
import org.gproman.model.track.Track;

public class Driver extends PersistentEntity implements Serializable {
    private static final long serialVersionUID = 6669111277797376470L;
    
    private Integer          number;
    private Timestamp        datetime;
    private String           name;
    private String           nationality;
    private int              trophies;
    private int              gps;
    private int              wins;
    private int              podiums;
    private int              points;
    private int              poles;
    private int              fastestLaps;
    private int              salary;
    private int              contract;

    private List<Track>     favoriteTracks;
    private DriverAttributes attributes;

    public Driver() {
        this( null, null );
    }

    public Driver(Integer number,
                  String name) {
        super();
        this.number = number;
        this.name = name;
        this.attributes = new DriverAttributes();
        this.favoriteTracks = new ArrayList<Track>(0);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public Timestamp getDatetime() {
        return this.datetime;
    }
    
    public void setDatetime( Timestamp datetime ) {
        this.datetime = datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public int getGps() {
        return gps;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getPodiums() {
        return podiums;
    }

    public void setPodiums(int podiums) {
        this.podiums = podiums;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoles() {
        return poles;
    }

    public void setPoles(int poles) {
        this.poles = poles;
    }

    public int getFastestLaps() {
        return fastestLaps;
    }

    public void setFastestLaps(int fastestLaps) {
        this.fastestLaps = fastestLaps;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getContract() {
        return contract;
    }

    public void setContract(int contract) {
        this.contract = contract;
    }

    public List<Track> getFavoriteTracks() {
        return favoriteTracks;
    }
    
    public List<String> getFavoriteTracksNames() {
        List<String> names = new ArrayList<String>();
        for( Track track : favoriteTracks ) {
            names.add( track.getName() );
        }
        return names;
    }

    public void setFavoriteTracks(List<Track> favoriteTracks) {
        this.favoriteTracks = favoriteTracks;
    }

    public DriverAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(DriverAttributes attributes) {
        this.attributes = attributes;
    }
    
    public int getSatisfactionZone() {
        return (int) Math.round( this.attributes.getSatisfactionZone() );
    }

    @Override
    public String toString() {
        return "Driver [number=" + number + ", name=" + name + ", nationality=" + nationality + ", trophies=" + trophies + ", gps=" + gps + ", wins=" + wins + ", podiums=" + podiums + ", points=" + points + ", poles=" + poles + ", fastestLaps=" + fastestLaps + ", salary=" + salary + ", contract=" + contract + ", favoriteTracks=" + getFavoriteTracksNames() + ", attributes=" + attributes + "]";
    }

}
