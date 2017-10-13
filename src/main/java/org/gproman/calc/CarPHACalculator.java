package org.gproman.calc;

import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;

public class CarPHACalculator {
    
    public static final double[] pf = new double[] { 0.8, 5.78, .25, .25, .20, .30, 1.20, 3.20, 0, 0, 1.4 };
    public static final double[] hf = new double[] { 1.8, 0.6, 2.4, 2.4, 1.2, 0.7, 0, 0.7, 2, 1.6, 0 };
    public static final double[] af = new double[] { 1.4, 2.1, 1.2, 1.2, 0.5, 0, .2, 4.1, 0, 1.2, 1.4 };
    
    public static PHA calculateBasePHA( CarPart[] parts ) {
        double p = 0, h = 0, a = 0;
        for( int i = 0; i < parts.length; i++ ) {
            p += pf[i] * parts[i].getLevel();
            h += hf[i] * parts[i].getLevel();
            a += af[i] * parts[i].getLevel();
        }
        PHA pha = new PHA( p, h, a );
        return pha;
    }

}
