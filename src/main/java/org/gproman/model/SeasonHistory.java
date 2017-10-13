package org.gproman.model;

import org.gproman.model.season.TyreSupplier;

public class SeasonHistory extends PersistentEntity {

    private Integer      seasonNumber;
    private String       groupName;
    private Integer      position;
    private Integer      wins;
    private Integer      podiums;
    private Integer      poles;
    private Integer      fastestLaps;
    private Integer      points;
    private Integer      races;
    private TyreSupplier tyres;
    private Integer      money;
    private String       status;

    public SeasonHistory() {
        super();
    }

    public SeasonHistory(Integer id) {
        super(id);
    }

    public SeasonHistory(Integer seasonNumber, String groupName, Integer position, Integer wins, Integer podiums, Integer poles, Integer fastestLaps, Integer points, Integer races, TyreSupplier tyres, Integer money, String status) {
        super();
        this.seasonNumber = seasonNumber;
        this.groupName = groupName;
        this.position = position;
        this.wins = wins;
        this.podiums = podiums;
        this.poles = poles;
        this.fastestLaps = fastestLaps;
        this.points = points;
        this.races = races;
        this.tyres = tyres;
        this.money = money;
        this.status = status;
    }

    
    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    
    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    
    public String getGroupName() {
        return groupName;
    }

    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    
    public Integer getPosition() {
        return position;
    }

    
    public void setPosition(Integer position) {
        this.position = position;
    }

    
    public Integer getWins() {
        return wins;
    }

    
    public void setWins(Integer wins) {
        this.wins = wins;
    }

    
    public Integer getPodiums() {
        return podiums;
    }

    
    public void setPodiums(Integer podiums) {
        this.podiums = podiums;
    }

    
    public Integer getPoles() {
        return poles;
    }

    
    public void setPoles(Integer poles) {
        this.poles = poles;
    }

    
    public Integer getFastestLaps() {
        return fastestLaps;
    }

    
    public void setFastestLaps(Integer fastestLaps) {
        this.fastestLaps = fastestLaps;
    }

    
    public Integer getPoints() {
        return points;
    }

    
    public void setPoints(Integer points) {
        this.points = points;
    }

    
    public Integer getRaces() {
        return races;
    }

    
    public void setRaces(Integer races) {
        this.races = races;
    }

    
    public TyreSupplier getTyres() {
        return tyres;
    }

    
    public void setTyres(TyreSupplier tyres) {
        this.tyres = tyres;
    }

    
    public Integer getMoney() {
        return money;
    }

    
    public void setMoney(Integer money) {
        this.money = money;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fastestLaps == null) ? 0 : fastestLaps.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((money == null) ? 0 : money.hashCode());
        result = prime * result + ((podiums == null) ? 0 : podiums.hashCode());
        result = prime * result + ((points == null) ? 0 : points.hashCode());
        result = prime * result + ((poles == null) ? 0 : poles.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((races == null) ? 0 : races.hashCode());
        result = prime * result + ((seasonNumber == null) ? 0 : seasonNumber.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((tyres == null) ? 0 : tyres.hashCode());
        result = prime * result + ((wins == null) ? 0 : wins.hashCode());
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
        SeasonHistory other = (SeasonHistory) obj;
        if (fastestLaps == null) {
            if (other.fastestLaps != null)
                return false;
        } else if (!fastestLaps.equals(other.fastestLaps))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        if (money == null) {
            if (other.money != null)
                return false;
        } else if (!money.equals(other.money))
            return false;
        if (podiums == null) {
            if (other.podiums != null)
                return false;
        } else if (!podiums.equals(other.podiums))
            return false;
        if (points == null) {
            if (other.points != null)
                return false;
        } else if (!points.equals(other.points))
            return false;
        if (poles == null) {
            if (other.poles != null)
                return false;
        } else if (!poles.equals(other.poles))
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (races == null) {
            if (other.races != null)
                return false;
        } else if (!races.equals(other.races))
            return false;
        if (seasonNumber == null) {
            if (other.seasonNumber != null)
                return false;
        } else if (!seasonNumber.equals(other.seasonNumber))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (tyres != other.tyres)
            return false;
        if (wins == null) {
            if (other.wins != null)
                return false;
        } else if (!wins.equals(other.wins))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SeasonHistory [seasonNumber=" + seasonNumber + ", groupName=" + groupName + ", position=" + position + ", wins=" + wins + ", podiums=" + podiums + ", poles=" + poles + ", fastestLaps=" + fastestLaps + ", points=" + points + ", races=" + races + ", tyres=" + tyres + ", money=" + money + ", status=" + status + "]";
    }

}
