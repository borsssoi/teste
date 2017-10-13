package org.gproman.calc;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.gproman.model.driver.Driver;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Comment;
import org.gproman.model.race.Comment.Part;
import org.gproman.model.race.Comment.Satisfaction;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Tyre;
import org.gproman.model.track.Track;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PracticeHelperTest {

    @Test
    public void testPartSetup1() {
        for( int ia = 0; ia < 999; ia++ ) {
            for( int sz = 30; sz <= 127; sz++ ) {
                for( int init = (int) Math.max( 0, ia - 2.4*sz ); init < Math.min( 999, ia+2.4*sz ); init += (sz * 0.3) ) {
                    runTest( sz, ia, init, false );
                }
            }
        }
    }

    @Test @Ignore
    public void testPartSetup2() {
        runTest( 30, 130, 85, true );
        runTest( 30, 130, 58, true );
        runTest( 30, 1, 0, true );
        runTest( 31, 16, 0, true );
        runTest( 31, 130, 100, true );
        runTest( 36, 130, 113, true );
        runTest( 55, 130, 48, true );
    }
    
    @Test @Ignore("Requires fixing and use of mocks")
    public void testWingSplit() {
        Lap[] laps = new Lap[8];
        laps[0] = new Lap(1, 74501, 270, 74231, null, null, null, null, null, new CarSettings(1, 780, 780, 568, 500, 433, 838, Tyre.MEDIUM), "[]");
        laps[1] = new Lap(2, 74369, 160, 74209, null, null, null, null, null, new CarSettings(1, 736, 736, 524, 544, 477, 794, Tyre.MEDIUM), "[]");
        laps[2] = new Lap(3, 74228, 114, 74114, null, null, null, null, null, new CarSettings(1, 758, 758, 546, 522, 499, 816, Tyre.MEDIUM), "[]");
        laps[3] = new Lap(4, 74320,  97, 74223, null, null, null, null, null, new CarSettings(1, 747, 747, 535, 511, 488, 805, Tyre.MEDIUM), "[]");
        laps[4] = new Lap(5, 74168,   6, 74162, null, null, null, null, null, new CarSettings(1, 753, 753, 541, 517, 494, 811, Tyre.MEDIUM), "[]");
        laps[5] = new Lap(6, 73909,  15, 73894, null, null, null, null, null, new CarSettings(1, 800, 800, 582, 564, 535, 852, Tyre.MEDIUM), "[]");
        laps[6] = new Lap(7, 73866,  53, 73813, null, null, null, null, null, new CarSettings(1, 870, 730, 582, 564, 535, 852, Tyre.MEDIUM), "[]");
        laps[7] = new Lap(8, 74040, 135, 73905, null, null, null, null, null, new CarSettings(1, 940, 660, 582, 564, 535, 852, Tyre.MEDIUM), "[]");
        Practice practice = new Practice();
        practice.setLaps( Arrays.asList( laps ) );
        
        Track track = new Track();
        track.setWingSplit( 70 );
        PracticeHelper helper = new PracticeHelper();
        Driver driver = new Driver();
        driver.getAttributes().setExperience( 69 );
        driver.getAttributes().setTechInsight( 141 );
        
        helper.setDriver( driver );
        helper.setTrack( track );
        helper.setPractice( practice );
        helper.calculateWingSplit();

        Assert.assertEquals( 3, helper.getWingSplit().size() );
        Assert.assertEquals( 67, helper.getBestWingSplit());
    }

    @Test @Ignore("Requires fixing and use of mocks")
    public void testWingSplit2() {
        Lap[] laps = new Lap[7];
        laps[0] = new Lap(1, 74501, 270, 74231, null, null, null, null, null, new CarSettings(1, 780, 780, 568, 500, 433, 838, Tyre.MEDIUM), "[]");
        laps[1] = new Lap(2, 74369, 160, 74209, null, null, null, null, null, new CarSettings(1, 736, 736, 524, 544, 477, 794, Tyre.MEDIUM), "[]");
        laps[2] = new Lap(3, 74228, 114, 74114, null, null, null, null, null, new CarSettings(1, 758, 758, 546, 522, 499, 816, Tyre.MEDIUM), "[]");
        laps[3] = new Lap(4, 74320,  97, 74223, null, null, null, null, null, new CarSettings(1, 747, 747, 535, 511, 488, 805, Tyre.MEDIUM), "[]");
        laps[4] = new Lap(5, 74168,   6, 74162, null, null, null, null, null, new CarSettings(1, 753, 753, 541, 517, 494, 811, Tyre.MEDIUM), "[]");
        laps[5] = new Lap(6, 73909,  15, 73894, null, null, null, null, null, new CarSettings(1, 800, 800, 582, 564, 535, 852, Tyre.MEDIUM), "[]");
        laps[6] = new Lap(7, 73866,  53, 73813, null, null, null, null, null, new CarSettings(1, 870, 730, 582, 564, 535, 852, Tyre.MEDIUM), "[]");
        Practice practice = new Practice();
        practice.setLaps( Arrays.asList( laps ) );
        
        Track track = new Track();
        track.setWingSplit( 70 );
        PracticeHelper helper = new PracticeHelper();
        Driver driver = new Driver();
        driver.getAttributes().setExperience( 69 );
        driver.getAttributes().setTechInsight( 141 );
        
        helper.setDriver( driver );
        helper.setTrack( track );
        helper.setPractice( practice );
        helper.calculateWingSplit();

        Assert.assertEquals( 2, helper.getWingSplit().size() );
        Assert.assertEquals( 70, helper.getBestWingSplit());
    }

    private void runTest(int sz,
                         int ia,
                         int guess,
                         boolean verbose ) {
        int init = guess;
        PracticeHelper.PartSetup ps = new PracticeHelper.PartSetup( false, sz, sz );
        for ( int i = 0; i < 8; i++ ) {
            Comment comment = new Comment( Part.ENG, getSatisfaction( ia, sz, guess ) );
            ps.addHint( guess, comment );
            if( verbose ) System.out.println(String.format( "%d. %3d (%+3d [%3d, %3d]) -> %s", i, guess, ps.getError(), ps.getIARange()[0], ps.getIARange()[1], comment.toString() ) );
            if( ps.foundIdealAdjustment() ) {
                if( verbose ) System.out.println(String.format("SZ=%3d IA=%3d INIT=%3d iterations=%2d", sz, ps.getIdealAdjustment(), init, i+1 ));
                break;
            }
            guess = ps.getNextSetupValue();
        }
        assertTrue( String.format("SZ=%3d IA=%3d INIT=%3d calc= %3d iterations=8", sz, ia, init, ps.getIdealAdjustment() ), Math.abs( ia - ps.getIdealAdjustment()) <= 1 );
    }
    
    @Test
    public void testGetSatisfaction() {
        System.out.println(getSatisfaction( 130, 55, 48 ));
    }

    public Satisfaction getSatisfaction(int ia,
                                         int sz,
                                         int guess ) {
        if( guess <= Math.round( Math.floor( ia - ( 2 * sz + 0.5 * (sz-1) ) ) ) )
            return Satisfaction.III;
        else if( guess <= Math.round( Math.floor( ia - ( 1 * sz + 0.5 * (sz-1) )) ) )
            return Satisfaction.II;
        else if( guess <= Math.round( Math.floor( ia - ( 0.5 * (sz-1) ) ) ) )
            return Satisfaction.I;
        else if( guess <= Math.round( Math.floor( ia + ( 0.5 * (sz-1) ) ) ) )
            return Satisfaction.OK;
        else if( guess <= Math.round( Math.floor( ia + ( 1 * sz + 0.5 * (sz-1) ) ) ) )
            return Satisfaction.D;
        else if( guess <= Math.round( Math.floor( ia + ( 2 * sz + 0.5 * (sz-1) ) ) ) )
            return Satisfaction.DD;
        return Satisfaction.DDD;
    }

}
