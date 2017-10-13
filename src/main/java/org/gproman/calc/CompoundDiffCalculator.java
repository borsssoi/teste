package org.gproman.calc;

import java.math.BigDecimal;

import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;

public class CompoundDiffCalculator {
    
    public static BigDecimal predictDiff( Track track, double temp, TyreSupplier supplier ) {
        return track.getCompoundCoef().multiply( new BigDecimal( 50 - temp ) ).add( supplier.getCompoundDiff() );
    }

}
