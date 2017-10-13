/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gproman;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import org.gproman.calc.SetupCalculator;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;

/**
 *
 * @author renan
 */
public class NewTrackCoefCalculator {

    public static void main(String[] args) {
        BigDecimal tcd = new BigDecimal("0.805");
        TyreSupplier supplier = TyreSupplier.PIPIRELLI;
        int temp = 7;
        Weather weather = Weather.PARTIALLY_CLOUDY;

        Car car = new Car();
        car.setChassis(new CarPart("Cha", 6, 18 ));
        car.setEngine(new CarPart("Eng", 6, 88));
        car.setFrontWing(new CarPart("Fwg", 6, 58));
        car.setRearWing(new CarPart("Rwg", 6, 52));
        car.setUnderbody(new CarPart("Und", 7, 81));
        car.setSidepods(new CarPart("Sid", 6, 81));
        car.setCooling(new CarPart("Coo", 6, 87));
        car.setGearbox(new CarPart("Gea", 6, 81));
        car.setBrakes(new CarPart("Bra", 7, 25));
        car.setSuspension(new CarPart("Sus", 6, 84));
        car.setElectronics(new CarPart("Ele", 6, 82));

        DriverAttributes driver = new DriverAttributes(167, 248, 102, 193, 211, 131, 215, 148, 0, 0, 72, 33);

        int[] idealSetup = new int[] { 702, 927, 456, 677, 814 };
        int[] setupCoefs = new int[] { 500, 500, 500, 500, 500 };

        Driver d = new Driver();
        d.setAttributes(driver);
        
        Calc[] calc = new Calc[] {
                new Calc() {
                    @Override public int calculate(Track track, int temp, Weather weather, Car car,  Driver driver) {
                        return SetupCalculator.calculateWings( track, temp, weather, car, driver );
                    }
                    @Override public void setCoef(Track track, int coef) {
                        track.setSetupWings( coef );
                    }
                },
                new Calc() {
                    @Override public int calculate(Track track, int temp, Weather weather, Car car, Driver driver) {
                        return SetupCalculator.calculateEngine(track, temp, weather, car, driver);
                    }
                    @Override public void setCoef(Track track, int coef) {
                        track.setSetupEngine( coef );
                    }
                },
                new Calc() {
                    @Override public int calculate(Track track, int temp, Weather weather, Car car, Driver driver) {
                        return SetupCalculator.calculateBrakes(track, temp, weather, car, driver);
                    }
                    @Override public void setCoef(Track track, int coef) {
                        track.setSetupBrakes( coef );
                    }
                },
                new Calc() {
                    @Override public int calculate(Track track, int temp, Weather weather, Car car,Driver driver) {
                        return SetupCalculator.calculateGear(track, temp, weather, car, driver);
                    }
                    @Override public void setCoef(Track track, int coef) {
                        track.setSetupGear( coef );
                    }
                },
                new Calc() {
                    @Override public int calculate(Track track, int temp, Weather weather, Car car,Driver driver) {
                        return SetupCalculator.calculateSuspension(track, temp, weather, car, driver);
                    }
                    @Override public void setCoef(Track track, int coef) {
                        track.setSetupSuspension( coef );
                    }
                },
        };

        Track track = new Track();
        for( int i = 0; i < idealSetup.length; i++ ) {
            track.setSetupWings( setupCoefs[i] );
            int result = Integer.MIN_VALUE;
            int count = 0;
            while( result != idealSetup[i] && count++ < 1000) {
                setupCoefs[i] += (result < idealSetup[i] ? +1 : -1);
                calc[i].setCoef(track, setupCoefs[i] );
                result = calc[i].calculate(track, temp, weather, car, d);
                //System.out.printf("Calc[%d] ideal=%d coef=%d result=%d\n", i, idealSetup[i], setupCoefs[i], result);
            }
        }

        System.out.format("TCD coef = %s\n", calcTCDCoef(tcd, supplier, temp).toString());
        System.out.printf("Setup coefs = %s\n", Arrays.toString(setupCoefs));
    }

    public static BigDecimal calcTCDCoef(BigDecimal tcd, TyreSupplier supplier, int temp ) {
        return tcd.subtract( supplier.getCompoundDiff() ).divide( new BigDecimal(50 - temp), RoundingMode.HALF_UP);
    }

    public static interface Calc {
        public int calculate( Track track, int temp, Weather weather, Car car, Driver driver );
        public void setCoef( Track track, int coef );
    }


}