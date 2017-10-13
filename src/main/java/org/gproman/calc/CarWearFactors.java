package org.gproman.calc;

import java.math.BigDecimal;

import org.gproman.model.car.CarPart;
import org.gproman.model.track.WearCoefs;

public class CarWearFactors {

    private final BigDecimal[]       factors;
    private final WearCoefs wpc;

    public CarWearFactors(CarPart[] parts,
                          WearCoefs wpc) {
        this.wpc = wpc;
        factors = new BigDecimal[parts.length];
        BigDecimal div = BigDecimal.valueOf( 10000 );
        for ( int i = 0; i < factors.length; i++ ) {
            BigDecimal partLevel = BigDecimal.valueOf( parts[i].getLevel() );
            factors[i] = calculateFactor( div, partLevel );
        }
    }

    private BigDecimal calculateFactor(BigDecimal div,
                                       BigDecimal partLevel) {
        BigDecimal result = BigDecimal.ZERO; 
        for ( int p = 0; p < wpc.getCoefs().length; p++ ) {
            result = result.add( wpc.getCoefs()[p].multiply( partLevel.pow( 8 - p ) ) );
        }
        return result.divide( div );
    }

    public BigDecimal calculateFactor( BigDecimal partLevel ) {
        return calculateFactor( BigDecimal.valueOf( 10000 ), partLevel );
    }

    public BigDecimal[] getFactors() {
        return factors;
    }

}
