package org.gproman.util;

import static org.junit.Assert.assertEquals;

import org.gproman.calc.TyreDurabilityCalculator;
import org.gproman.model.race.Tyre;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.TyreWear;
import org.junit.Test;

public class TyreDurabilityCalculatorTest {

    @Test
    public void testRisk0() {
        assertEquals( 80, TyreDurabilityCalculator.predictDurability( 80, Tyre.XSOFT, 0 ), 0.5 );
        assertEquals( 106, TyreDurabilityCalculator.predictDurability( 80, Tyre.SOFT, 0 ), 0.5 );
        assertEquals( 138, TyreDurabilityCalculator.predictDurability( 80, Tyre.MEDIUM, 0 ), 0.5 );
        assertEquals( 182, TyreDurabilityCalculator.predictDurability( 80, Tyre.HARD, 0 ), 0.5 );
    }
    
    @Test
    public void testRisk30() {
        assertEquals( 75, TyreDurabilityCalculator.predictDurability( 80, Tyre.XSOFT, 30 ), 0.5 );
        assertEquals( 99, TyreDurabilityCalculator.predictDurability( 80, Tyre.SOFT, 30 ), 0.5 );
        assertEquals( 130, TyreDurabilityCalculator.predictDurability( 80, Tyre.MEDIUM, 30 ), 0.5 );
        assertEquals( 171, TyreDurabilityCalculator.predictDurability( 80, Tyre.HARD, 30 ), 0.5 );
    }
    
    @Test
    public void testNewCal() {
        assertEquals( 223, TyreDurabilityCalculator.predictDurability( 37, 93, TyreWear.MEDIUM, TyreSupplier.PIPIRELLI, Tyre.RAIN, 0 ), 0.5 );
        assertEquals( 172, TyreDurabilityCalculator.predictDurability( 20, 48, TyreWear.MEDIUM, TyreSupplier.PIPIRELLI, Tyre.MEDIUM, 0 ), 0.5 );
    }
    

}
