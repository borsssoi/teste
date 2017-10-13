package org.gproman.model.car;

import org.gproman.model.PersistentEntity;

public class WearPlan extends PersistentEntity {

    private String name;
    private Integer season;
    private Integer race;
    private String plan;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getSeason() {
        return season;
    }
    public void setSeason(Integer season) {
        this.season = season;
    }
    public Integer getRace() {
        return race;
    }
    public void setRace(Integer race) {
        this.race = race;
    }
    public String getPlan() {
        return plan;
    }
    public void setPlan(String plan) {
        this.plan = plan;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((plan == null) ? 0 : plan.hashCode());
        result = prime * result + ((race == null) ? 0 : race.hashCode());
        result = prime * result + ((season == null) ? 0 : season.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        WearPlan other = (WearPlan) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( plan == null ) {
            if ( other.plan != null ) return false;
        } else if ( !plan.equals( other.plan ) ) return false;
        if ( race == null ) {
            if ( other.race != null ) return false;
        } else if ( !race.equals( other.race ) ) return false;
        if ( season == null ) {
            if ( other.season != null ) return false;
        } else if ( !season.equals( other.season ) ) return false;
        return true;
    }
    @Override
    public String toString() {
        return "WearPlan [name=" + name + ", season=" + season + ", race=" + race + "]";
    }

}
