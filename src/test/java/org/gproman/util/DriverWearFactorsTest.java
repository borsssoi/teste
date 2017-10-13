package org.gproman.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.driver.DriverWearFactors;
import org.gproman.model.driver.DriverWearWeight;
import org.junit.Before;
import org.junit.Test;

public class DriverWearFactorsTest {
    
    private DriverWearFactors dwf;

    @Before
    public void setup() {
        Driver driver = new Driver();
        DriverAttributes attrs = driver.getAttributes();
        attrs.setConcentration( 205 );
        attrs.setTalent( 110 );
        attrs.setAggressiveness( 0 );
        attrs.setExperience( 34 );
        attrs.setStamina( 58 );
        
        DriverWearWeight daw = new DriverWearWeight();
        daw.setConcentration( new BigDecimal("0.0008") );
        daw.setTalent( new BigDecimal("0.0005") );
        daw.setExperience( new BigDecimal("0.0005") );
        daw.setAggressiveness( BigDecimal.ZERO );
        daw.setStamina( BigDecimal.ZERO );
        
        dwf = new DriverWearFactors( driver.getAttributes(), daw );
    }

    @Test
    public void testConcentration() {
        assertEquals( new BigDecimal("1.164"), dwf.getConcentration() );
    }

    @Test
    public void testTalent() {
        assertEquals( new BigDecimal("1.055"), dwf.getTalent() );
    }
    @Test
    public void testAggressiveness() {
        assertEquals( new BigDecimal("1.000"), dwf.getAggressiveness() );
    }
    @Test
    public void testExperience() {
        assertEquals( new BigDecimal("1.017"), dwf.getExperience() );
    }
    @Test
    public void testStamina() {
        assertEquals( new BigDecimal("1.000"), dwf.getStamina() );
    }
}
