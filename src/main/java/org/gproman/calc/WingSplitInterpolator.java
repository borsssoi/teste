package org.gproman.calc;

public class WingSplitInterpolator {

    /**
     * Interpolates the given wing splits to calculate the best wingsplit
     * 
     * @param ws an array with 3 wing split values
     * @param to an array with the corresponding 3 time values
     * 
     * @return the best wing split based on the interpolation
     */
    public static int interpolate(int[] ws,
                                  int[] to) {
        int da = (to[0] * ws[1]) + (ws[0] * to[2]) + (to[1] * ws[2]) - (to[2] * ws[1]) - (ws[2] * to[0]) - (to[1] * ws[0]);
        int db = (ws[0] * ws[0] * to[1]) + (to[0] * ws[2] * ws[2]) + (ws[1] * ws[1] * to[2]) - (ws[2] * ws[2] * to[1]) - (to[2] * ws[0] * ws[0]) - (ws[1] * ws[1] * to[0]);
        int result = 0;
        if ( da != 0 ) {
            result = (-1 * db) / (2 * da);
        }
        return result;
    }

}
