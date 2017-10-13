package org.gproman.model.driver;

import java.math.BigDecimal;

import org.gproman.model.PersistentEntity;

public class DriverWearWeight extends PersistentEntity {

    private BigDecimal concentration;
    private BigDecimal talent;
    private BigDecimal aggressiveness;
    private BigDecimal experience;
    private BigDecimal stamina;

    public DriverWearWeight() {
        this.concentration = BigDecimal.ZERO;
        this.talent = BigDecimal.ZERO;
        this.aggressiveness = BigDecimal.ZERO;
        this.experience = BigDecimal.ZERO;
        this.stamina = BigDecimal.ZERO;
    }

    public DriverWearWeight(BigDecimal concentration,
                            BigDecimal talent,
                            BigDecimal aggressiveness,
                            BigDecimal experience,
                            BigDecimal stamina) {
        this.concentration = concentration;
        this.talent = talent;
        this.aggressiveness = aggressiveness;
        this.experience = experience;
        this.stamina = stamina;
    }

    public BigDecimal getConcentration() {
        return concentration;
    }

    public void setConcentration(BigDecimal concentration) {
        this.concentration = concentration;
    }

    public BigDecimal getTalent() {
        return talent;
    }

    public void setTalent(BigDecimal talent) {
        this.talent = talent;
    }

    public BigDecimal getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(BigDecimal aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    public BigDecimal getExperience() {
        return experience;
    }

    public void setExperience(BigDecimal experience) {
        this.experience = experience;
    }

    public BigDecimal getStamina() {
        return stamina;
    }

    public void setStamina(BigDecimal stamina) {
        this.stamina = stamina;
    }

    @Override
    public String toString() {
        return "DriverAttributesWearWeight [concentration=" + concentration + ", talent=" + talent + ", aggressiveness=" + aggressiveness + ", experience=" + experience + ", stamina=" + stamina + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((aggressiveness == null) ? 0 : aggressiveness.hashCode());
        result = prime * result + ((concentration == null) ? 0 : concentration.hashCode());
        result = prime * result + ((experience == null) ? 0 : experience.hashCode());
        result = prime * result + ((stamina == null) ? 0 : stamina.hashCode());
        result = prime * result + ((talent == null) ? 0 : talent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DriverWearWeight other = (DriverWearWeight) obj;
        if ( aggressiveness == null ) {
            if ( other.aggressiveness != null ) return false;
        } else if ( !aggressiveness.equals( other.aggressiveness ) ) return false;
        if ( concentration == null ) {
            if ( other.concentration != null ) return false;
        } else if ( !concentration.equals( other.concentration ) ) return false;
        if ( experience == null ) {
            if ( other.experience != null ) return false;
        } else if ( !experience.equals( other.experience ) ) return false;
        if ( stamina == null ) {
            if ( other.stamina != null ) return false;
        } else if ( !stamina.equals( other.stamina ) ) return false;
        if ( talent == null ) {
            if ( other.talent != null ) return false;
        } else if ( !talent.equals( other.talent ) ) return false;
        return true;
    }
    
    

}
