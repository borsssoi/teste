package org.gproman.model.staff;

import java.io.Serializable;

public class TDAttributes implements Serializable {
    private static final long serialVersionUID = 3155541126064450480L;
    
    private int overall;
    private int leadership;
    private int rdMech;
    private int rdElect;
    private int rdAero;
    private int experience;
    private int pitCoord;
    private int motivation;
    private int age;

    public TDAttributes() {
    }

    public TDAttributes(int overall,
                        int leadership,
                        int rdMech,
                        int rdElect,
                        int rdAero,
                        int experience,
                        int pitCoord,
                        int motivation,
                        int age) {
        super();
        this.overall = overall;
        this.leadership = leadership;
        this.rdMech = rdMech;
        this.rdElect = rdElect;
        this.rdAero = rdAero;
        this.experience = experience;
        this.pitCoord = pitCoord;
        this.motivation = motivation;
        this.age = age;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public int getLeadership() {
        return leadership;
    }

    public void setLeadership(int leadership) {
        this.leadership = leadership;
    }

    public int getRdMech() {
        return rdMech;
    }

    public void setRdMech(int rdMech) {
        this.rdMech = rdMech;
    }

    public int getRdElect() {
        return rdElect;
    }

    public void setRdElect(int rdElect) {
        this.rdElect = rdElect;
    }

    public int getRdAero() {
        return rdAero;
    }

    public void setRdAero(int rdAero) {
        this.rdAero = rdAero;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getPitCoord() {
        return pitCoord;
    }

    public void setPitCoord(int pitCoord) {
        this.pitCoord = pitCoord;
    }

    public int getMotivation() {
        return motivation;
    }

    public void setMotivation(int motivation) {
        this.motivation = motivation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + experience;
        result = prime * result + leadership;
        result = prime * result + motivation;
        result = prime * result + overall;
        result = prime * result + pitCoord;
        result = prime * result + rdAero;
        result = prime * result + rdElect;
        result = prime * result + rdMech;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TDAttributes other = (TDAttributes) obj;
        if ( age != other.age ) return false;
        if ( experience != other.experience ) return false;
        if ( leadership != other.leadership ) return false;
        if ( motivation != other.motivation ) return false;
        if ( overall != other.overall ) return false;
        if ( pitCoord != other.pitCoord ) return false;
        if ( rdAero != other.rdAero ) return false;
        if ( rdElect != other.rdElect ) return false;
        if ( rdMech != other.rdMech ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TDAttributes [overall=" + overall + ", leadership=" + leadership + ", rdMech=" + rdMech + ", rdElect=" + rdElect + ", rdAero=" + rdAero + ", experience=" + experience + ", pitCoord=" + pitCoord + ", motivation=" + motivation + ", age=" + age + "]";
    }

}
