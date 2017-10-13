package org.gproman.model.staff;

import java.io.Serializable;
import java.sql.Timestamp;

import org.gproman.model.PersistentEntity;

public class TechDirector extends PersistentEntity implements Serializable {
    private static final long serialVersionUID = 6669111277797376470L;
    
    private Integer          number;
    private Timestamp        datetime;
    private String           name;
    private String           nationality;
    private int              trophies;
    private int              gps;
    private int              wins;
    
    private int              salary;
    private int              contract;
    private int              pointsBonus;
    private int              podiumBonus;
    private int              winBonus;
    private int              trophyBonus;

    private TDAttributes     attributes;

    public TechDirector() {
        this( null, null );
    }

    public TechDirector(Integer number,
                        String name) {
        super();
        this.number = number;
        this.name = name;
        this.attributes = new TDAttributes();
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

    public TDAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(TDAttributes attributes) {
        this.attributes = attributes;
    }

    public int getPointsBonus() {
        return pointsBonus;
    }

    public void setPointsBonus(int pointsBonus) {
        this.pointsBonus = pointsBonus;
    }

    public int getPodiumBonus() {
        return podiumBonus;
    }

    public void setPodiumBonus(int podiumBonus) {
        this.podiumBonus = podiumBonus;
    }

    public int getWinBonus() {
        return winBonus;
    }

    public void setWinBonus(int winBonus) {
        this.winBonus = winBonus;
    }

    public int getTrophyBonus() {
        return trophyBonus;
    }

    public void setTrophyBonus(int trophyBonus) {
        this.trophyBonus = trophyBonus;
    }
    
    public int getWingsSZ() {
        return (int) -Math.round((0.0425979*attributes.getExperience())+(0.390817*attributes.getRdAero())-2);
    }

    public int getEngineSZ() {
        return (int) -Math.round((0.042665*attributes.getExperience())+(0.218959*attributes.getRdElect())+(0.148625*attributes.getRdMech())-2);
    }

    public int getBrakesSZ() {
        return (int) -Math.round((0.06499*attributes.getExperience())+(0.009791*attributes.getRdAero())+(0.08916*attributes.getRdElect())+(0.24292*attributes.getRdMech())-2);
    }

    public int getGearboxSZ() {
        return (int) -Math.round((0.062533*attributes.getExperience())+(0.291894*attributes.getRdElect())+(0.065017*attributes.getRdMech())-2);
    }

    public int getSuspensionSZ() {
        return (int) -Math.round((0.073354*attributes.getExperience())+(0.092522*attributes.getRdAero())+(0.259221*attributes.getRdMech())-2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + contract;
        result = prime * result + ((datetime == null) ? 0 : datetime.hashCode());
        result = prime * result + gps;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + podiumBonus;
        result = prime * result + pointsBonus;
        result = prime * result + salary;
        result = prime * result + trophies;
        result = prime * result + trophyBonus;
        result = prime * result + winBonus;
        result = prime * result + wins;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TechDirector other = (TechDirector) obj;
        if ( attributes == null ) {
            if ( other.attributes != null ) return false;
        } else if ( !attributes.equals( other.attributes ) ) return false;
        if ( contract != other.contract ) return false;
        if ( datetime == null ) {
            if ( other.datetime != null ) return false;
        } else if ( !datetime.equals( other.datetime ) ) return false;
        if ( gps != other.gps ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( nationality == null ) {
            if ( other.nationality != null ) return false;
        } else if ( !nationality.equals( other.nationality ) ) return false;
        if ( number == null ) {
            if ( other.number != null ) return false;
        } else if ( !number.equals( other.number ) ) return false;
        if ( podiumBonus != other.podiumBonus ) return false;
        if ( pointsBonus != other.pointsBonus ) return false;
        if ( salary != other.salary ) return false;
        if ( trophies != other.trophies ) return false;
        if ( trophyBonus != other.trophyBonus ) return false;
        if ( winBonus != other.winBonus ) return false;
        if ( wins != other.wins ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TechDirector [number=" + number + ", datetime=" + datetime + ", name=" + name + ", nationality=" + nationality + ", trophies=" + trophies + ", gps=" + gps + ", wins=" + wins + ", salary=" + salary + ", contract=" + contract + ", pointsBonus=" + pointsBonus + ", podiumBonus=" + podiumBonus + ", winBonus=" + winBonus + ", trophyBonus=" + trophyBonus + ", attributes=" + attributes + "]";
    }

}
