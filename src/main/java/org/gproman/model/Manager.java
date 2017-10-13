package org.gproman.model;

import java.util.ArrayList;
import java.util.List;


public class Manager extends PersistentEntity {

    private String name;
    private String login;
    private String group;
    
    private Integer points;
    private Integer position;
    
    private Integer money;
    
    private List<SeasonHistory> seasonHistory;

    public Manager() {
        this.seasonHistory = new ArrayList<SeasonHistory>();
    }

    public Manager(String name,
                   String login,
                   String group,
                   int points,
                   int position,
                   int money) {
        super();
        this.name = name;
        this.login = login;
        this.group = group;
        this.points = points;
        this.position = position;
        this.money = money;
        this.seasonHistory = new ArrayList<SeasonHistory>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
    
    public List<SeasonHistory> getSeasonHistory() {
        return seasonHistory;
    }
    
    public void setSeasonHistory(List<SeasonHistory> seasonHistory) {
        this.seasonHistory = seasonHistory;
    }

    @Override
    public String toString() {
        return "Manager [name=" + name + ", login=" + login + ", group=" + group + ", points=" + points + ", position=" + position + ", money=" + money + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((money == null) ? 0 : money.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((points == null) ? 0 : points.hashCode());
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Manager other = (Manager) obj;
        if ( group == null ) {
            if ( other.group != null ) return false;
        } else if ( !group.equals( other.group ) ) return false;
        if ( login == null ) {
            if ( other.login != null ) return false;
        } else if ( !login.equals( other.login ) ) return false;
        if ( money == null ) {
            if ( other.money != null ) return false;
        } else if ( !money.equals( other.money ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( points == null ) {
            if ( other.points != null ) return false;
        } else if ( !points.equals( other.points ) ) return false;
        if ( position == null ) {
            if ( other.position != null ) return false;
        } else if ( !position.equals( other.position ) ) return false;
        return true;
    }

}
