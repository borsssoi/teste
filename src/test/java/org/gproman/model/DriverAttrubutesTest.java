package org.gproman.model;

import static org.junit.Assert.assertEquals;

import org.gproman.model.driver.DriverAttributes;
import org.junit.Test;

public class DriverAttrubutesTest {

    @Test
    public void testGetCalcOverall() {
        DriverAttributes attrs = new DriverAttributes( 104, 
                                                       205, 
                                                       110, 
                                                       0, 
                                                       33, 
                                                       106, 
                                                       56, 
                                                       44, 
                                                       242, 
                                                       19, 
                                                       69, 
                                                       21 );
        assertEquals( attrs.getOverall(), attrs.getCalcOverall(), 0.9999999 );
    }

}
