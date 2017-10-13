package org.gproman.calc;

import static org.gproman.calc.DriverPlanner.Range.ZERO;
import static org.gproman.calc.DriverPlanner.Range.range;

import org.gproman.calc.DriverPlanner.DriverAttributes;

public enum DriverEvent {
    RACE(new DriverAttributes( ZERO,    // oa
                                ZERO,    // con
                                ZERO,    // tal
                                range(-2,+2),    // agr
                                range(1),    // exp
                                range(1),    // ti
                                ZERO,    // sta
                                range(0.33),    // cha
                                ZERO,    // mot
                                ZERO,    // wei
                                0 )),    // age
    TEST(new DriverAttributes( ZERO,    // oa
                                ZERO,    // con
                                ZERO,    // tal
                                ZERO,    // agr
                                range(0,1),    // exp
                                range(0,1),    // ti
                                range(1,6),    // sta
                                ZERO,    // cha
                                ZERO,    // mot
                                ZERO,    // wei
                                0 )),    // age
    RESET(new DriverAttributes( ZERO,    // oa
                                range(5, 10),    // con
                                ZERO,    // tal
                                ZERO,    // agr
                                ZERO,    // exp
                                ZERO,    // ti
                                range(-7),    // sta
                                ZERO,    // cha
                                ZERO,    // mot
                                range(4,9),    // wei
                                1 )),    // age
    FITNESS(new DriverAttributes( ZERO,         // oa
                               ZERO,            // con
                               ZERO,            // tal
                               ZERO,            // agr
                               ZERO,            // exp
                               ZERO,            // ti
                               range( 2 ),      // sta
                               ZERO,            // cha
                               range( -7, -5 ), // mot
                               range( -1 ),     // wei
                               0 )),            // age
    YOGA(new DriverAttributes( ZERO,            // oa
                               range( 5, 6 ),   // con
                               ZERO,            // tal
                               range( -2 ),     // agr
                               ZERO,            // exp
                               ZERO,            // ti
                               range( -2 ),     // sta
                               ZERO,            // cha
                               range( 5, 8 ),   // mot
                               ZERO,            // wei
                               0 )),            // age
    PR(new DriverAttributes( ZERO,       // oa
                                range(-3),    // con
                                ZERO,    // tal
                                ZERO,    // agr
                                ZERO,    // exp
                                ZERO,    // ti
                                ZERO,    // sta
                                range(7),    // cha
                                ZERO,    // mot
                                ZERO,    // wei
                                0 )),    // age
    NINJA(new DriverAttributes( ZERO,    // oa
                                range(1),    // con
                                ZERO,    // tal
                                range(4,7),    // agr
                                ZERO,    // exp
                                ZERO,    // ti
                                ZERO,    // sta
                                ZERO,    // cha
                                ZERO,    // mot
                                ZERO,    // wei
                                0 )),    // age
    TECH(new DriverAttributes( ZERO,    // oa
                                range(-3),    // con
                                ZERO,    // tal
                                ZERO,    // agr
                                ZERO,    // exp
                                range(5),    // ti
                                ZERO,    // sta
                                ZERO,    // cha
                                range(15),    // mot
                                ZERO,    // wei
                                0 )),    // age
    PSYCHOLOGY(new DriverAttributes( ZERO,    // oa
                                ZERO,    // con
                                ZERO,    // tal
                                ZERO,    // agr
                                ZERO,    // exp
                                ZERO,    // ti
                                ZERO,    // sta
                                ZERO,    // cha
                                range(20),    // mot
                                ZERO,    // wei
                                0 ));    // age

    public final DriverAttributes offset;

    private DriverEvent(DriverAttributes offset) {
        this.offset = offset;
    }
}