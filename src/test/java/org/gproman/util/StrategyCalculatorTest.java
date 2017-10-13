package org.gproman.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.gproman.calc.StrategyCalculator;
import org.gproman.calc.StrategyCalculator.TopStrategy;
import org.gproman.model.race.Race;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Track;
import org.junit.Before;
import org.junit.Test;

public class StrategyCalculatorTest {

    private StrategyCalculator calc;
    
    

    @Before
    public void setup() {
        Race race = mock( Race.class );
        Track track = mock( Track.class );
        when( race.getTrack() ).thenReturn( track );
        when( track.getLaps() ).thenReturn( 55 );
        when( track.getDistance() ).thenReturn( 305.5 );
        when( track.getLapDistance() ).thenReturn( 5.555 );
        when( track.getFuelConsumption() ).thenReturn( FuelConsumption.LOW );
        when( track.getFuelCoef() ).thenReturn( new BigDecimal( "0.855" ) );
        when( track.getTimeInOut() ).thenReturn( 18500 );
        when( track.getCompoundCoef() ).thenReturn( new BigDecimal("0.0219090909") );
        
        calc = new StrategyCalculator( race, 6, 4, TyreSupplier.PIPIRELLI );
    }
    
    @Test
    public void testCalculator() {
        calc.setAveragePitTime( 25000 )
            .setBaseDurability( 80 )
            .setRisk( 0 )
            .setAverageTemperature( 27 )
            .setLapTime( 90000 )
            .setBaseCompoundDiff(400);
        for( TopStrategy top : calc.getTopStrategies() ) {
            System.out.println(top);
            
        }
//        for( StrategyCandidate cand : calc.getCandidates() ) {
//            System.out.println(String.format( "CANDIDATE = %d - fuel=%7.3fs pits=%7.3fs total=%7.3fs",
//                                              cand.getPits(),
//                                              cand.getFuelTimeLoss(),
//                                              cand.getPitTimeLoss(),
//                                              cand.getTotalTimeLoss() ) );
//            for( Stint stint : cand.getStints() ) {
//                System.out.println(stint);
//            }
//        }
    }
}
