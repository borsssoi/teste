package org.gproman.model.driver;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DriverWearFactors {
    
    private BigDecimal concentration;
    private BigDecimal talent;
    private BigDecimal aggressiveness;
    private BigDecimal experience;
    private BigDecimal stamina;
    
    public DriverWearFactors( DriverAttributes attr, DriverWearWeight ww ) {
        this.concentration = new BigDecimal( attr.getConcentration() ).multiply( ww.getConcentration() ).add( BigDecimal.ONE ).setScale( 3, RoundingMode.HALF_UP );
        this.talent = new BigDecimal( attr.getTalent() ).multiply( ww.getTalent() ).add( BigDecimal.ONE ).setScale( 3, RoundingMode.HALF_UP );
        this.aggressiveness = new BigDecimal( attr.getAggressiveness() ).multiply( ww.getAggressiveness() ).add( BigDecimal.ONE ).setScale( 3, RoundingMode.HALF_UP );
        this.experience = new BigDecimal( attr.getExperience() ).multiply( ww.getExperience() ).add( BigDecimal.ONE ).setScale( 3, RoundingMode.HALF_UP );
        this.stamina = new BigDecimal( attr.getStamina() ).multiply( ww.getStamina() ).add( BigDecimal.ONE ).setScale( 3, RoundingMode.HALF_UP );
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
        return "DriverWearFactors [concentration=" + concentration + ", talent=" + talent + ", aggressiveness=" + aggressiveness + ", experience=" + experience + ", stamina=" + stamina + "]";
    }

}
