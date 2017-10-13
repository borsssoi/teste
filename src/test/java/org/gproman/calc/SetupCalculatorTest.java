package org.gproman.calc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SetupCalculatorTest {

    private Track track;
    private Car car;
    private Driver driver;

    @Before
    public void setup() {
        track = mock(Track.class);
        when(track.getSetupWings()).thenReturn(388);
        when(track.getSetupEngine()).thenReturn(633);
        when(track.getSetupBrakes()).thenReturn(518);
        when(track.getSetupGear()).thenReturn(690);
        when(track.getSetupSuspension()).thenReturn(362);

        car = new Car();
        car.setChassis(new CarPart("", 7, 32));
        car.setEngine(new CarPart("", 6, 65));
        car.setFrontWing(new CarPart("", 6, 33));
        car.setRearWing(new CarPart("", 6, 45));
        car.setUnderbody(new CarPart("", 6, 66));
        car.setSidepods(new CarPart("", 6, 8));
        car.setCooling(new CarPart("", 6, 24));
        car.setGearbox(new CarPart("", 6, 13));
        car.setBrakes(new CarPart("", 5, 90));
        car.setSuspension(new CarPart("", 5, 71));
        car.setElectronics(new CarPart("", 6, 80));
        
        driver = mock(Driver.class);
        DriverAttributes attr = mock(DriverAttributes.class);
        when(attr.getConcentration()).thenReturn(249);
        when(attr.getTalent()).thenReturn(198);
        when(attr.getAggressiveness()).thenReturn(10);
        when(attr.getExperience()).thenReturn(128);
        when(attr.getTechInsight()).thenReturn(90);
        when(attr.getWeight()).thenReturn(61);
        when(driver.getAttributes()).thenReturn(attr);
    }

    @Test
    public void testWings1() {
        Assert.assertEquals(469, SetupCalculator.calculateWings(track, 29, Weather.SUNNY, car, driver));
    }

    @Test
    public void testEngine1() {
        Assert.assertEquals(777, SetupCalculator.calculateEngine(track, 29, Weather.SUNNY, car, driver));
    }

    @Test
    public void testBrakes1() {
        Assert.assertEquals(578, SetupCalculator.calculateBrakes(track, 29, Weather.SUNNY, car, driver));
    }

    @Test
    public void testGearbox1() {
        Assert.assertEquals(509, SetupCalculator.calculateGear(track, 29, Weather.SUNNY, car, driver));
    }

    @Test
    public void testSuspension1() {
        Assert.assertEquals(403, SetupCalculator.calculateSuspension(track, 29, Weather.SUNNY, car, driver));
    }

    @Test
    public void testWings2() {
        Assert.assertEquals(560, SetupCalculator.calculateWings(track, 29, Weather.RAIN, car, driver));
    }

    @Test
    public void testEngine2() {
        Assert.assertEquals(676, SetupCalculator.calculateEngine(track, 29, Weather.RAIN, car, driver));
    }

    @Test
    public void testBrakes2() {
        Assert.assertEquals(626, SetupCalculator.calculateBrakes(track, 29, Weather.RAIN, car, driver));
    }

    @Test
    public void testGearbox2() {
        Assert.assertEquals(389, SetupCalculator.calculateGear(track, 29, Weather.RAIN, car, driver));
    }

    @Test
    public void testSuspension2() {
        Assert.assertEquals(300, SetupCalculator.calculateSuspension(track, 29, Weather.RAIN, car, driver));
    }

}
