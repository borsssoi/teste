package org.gproman.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.gproman.calc.FuelCalculator;
import org.gproman.model.race.Weather;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Track;
import org.junit.Test;

public class FuelCalculatorTest {

    @Test
    public void testPredictConsumption1() {
        checkConsumption( FuelConsumption.VERY_LOW, Weather.RAIN, 1, 1, .6150 );
        checkConsumption( FuelConsumption.LOW, Weather.RAIN, 2, 4, .6157 );
        checkConsumption( FuelConsumption.MEDIUM, Weather.RAIN, 3, 1, .6570 );
        checkConsumption( FuelConsumption.HIGH, Weather.RAIN, 7, 8, .5942 );
        checkConsumption( FuelConsumption.VERY_HIGH, Weather.RAIN, 5, 3, .6857 );
        checkConsumption( FuelConsumption.VERY_LOW, Weather.SUNNY, 6, 2, .6561 );
        checkConsumption( FuelConsumption.LOW, Weather.SUNNY, 3, 3, .7163 );
        checkConsumption( FuelConsumption.MEDIUM, Weather.SUNNY, 6, 4, .7070 );
        checkConsumption( FuelConsumption.HIGH, Weather.SUNNY, 3, 4, .7811 );
        checkConsumption( FuelConsumption.VERY_HIGH, Weather.SUNNY, 6, 6, .7614 );
    }

    private void checkConsumption(FuelConsumption fuel,
                                  Weather weather,
                                  int engine,
                                  int electronics,
                                  double expected) {
        Track track = mock( Track.class );
        when( track.getFuelConsumption() ).thenReturn( fuel );
        when( track.getFuelCoef() ).thenReturn( BigDecimal.ZERO );

        BigDecimal result = FuelCalculator.predictConsumption( weather, engine, electronics, track );
        assertEquals( expected, result.doubleValue(), 0.0001 );
    }

}
