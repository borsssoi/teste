package org.gproman.util;

import org.gproman.calc.CarPHACalculator;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.junit.Assert;
import org.junit.Test;

public class CarPHACalculatorTest {

    @Test
    public void test1() {
        int[] levels = new int[] { 1,1,1,1,1,1,1,1,1,1,1 }; 
        CarPart[] parts = createParts( levels );
        PHA base = CarPHACalculator.calculateBasePHA( parts );
        Assert.assertEquals( new PHA(13.38, 13.4, 13.3), base );
    }

    @Test
    public void test2() {
        int[] levels = new int[] { 3,3,4,4,3,2,3,3,3,4,2 }; 
        CarPart[] parts = createParts( levels );
        PHA base = CarPHACalculator.calculateBasePHA( parts );
        Assert.assertEquals( new PHA(38.9, 45.9, 42.1), base );
    }
    
    @Test
    public void test3() {
        int[] levels = new int[] { 3,3,4,4,3,2,3,3,3,4,2 }; 
        CarPart[] parts = createParts( levels );
        PHA base = CarPHACalculator.calculateBasePHA( parts );
        Assert.assertEquals( new PHA(1.06, 1.1, 0.9), new PHA( 40, 47, 43 ).getBonusPHA( base ) );
    }
    

    private CarPart[] createParts(int[] levels) {
        CarPart[] parts = new CarPart[Car.PARTS_COUNT];
        for( int i = 0; i < Car.PARTS_COUNT; i++ ) {
            parts[i] = new CarPart("", levels[i], 0);
        }
        return parts;
    }

}
