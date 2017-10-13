package org.gproman.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;

public class SetupCalculator {

    public static int calculateWings( Track track,
                                        double temperature,
                                        Weather weather,
                                        Car car,
                                        Driver driver) {
        BigDecimal temp = new BigDecimal( temperature );
        BigDecimal trackConst = new BigDecimal( track.getSetupWings() );
        BigDecimal wconst = Weather.RAIN == weather ? PartConsts.WINGS.tempWet : PartConsts.WINGS.tempDry;
        BigDecimal rconst = Weather.RAIN == weather ? PartConsts.WINGS.rainOffset : BigDecimal.ZERO;
        BigDecimal xconst = new BigDecimal("765.0");

        return Math.max(0, Math.min(999,
                // (FW_CONST - Y_CONST1 + TEMP * WEATHER_FWING + RAIN_CONST1)
                (int) Math.round(trackConst.add(temp.multiply(wconst)).add(rconst)
                        // * (X_CONST1-TALENT)/X_CONST1
                        .multiply(xconst.subtract(new BigDecimal(driver.getAttributes().getTalent())).divide(xconst, 6, RoundingMode.HALF_UP))
                        // -10 * CHASSI + 15 * FWING + 15 * RWING - 8 * UNDER +
                        .add(PartConsts.WINGS.chaLvl.multiply(new BigDecimal(car.getChassis().getLevel())))
                        .add(PartConsts.WINGS.wngLvl.multiply(new BigDecimal(car.getFrontWing().getLevel())))
                        .add(PartConsts.WINGS.wngLvl.multiply(new BigDecimal(car.getRearWing().getLevel())))
                        .add(PartConsts.WINGS.undLvl.multiply(new BigDecimal(car.getUnderbody().getLevel())))
                        // 25 * WCHASSI - 28 * WFWING - 28 * WFWING + 15 * UNDER
                        .add(PartConsts.WINGS.chaWear.multiply(new BigDecimal(car.getChassis().getWear()/100.0)))
                        .add(PartConsts.WINGS.wngWear.multiply(new BigDecimal(car.getFrontWing().getWear()/100.0)))
                        .add(PartConsts.WINGS.wngWear.multiply(new BigDecimal(car.getRearWing().getWear()/100.0)))
                        .add(PartConsts.WINGS.undWear.multiply(new BigDecimal(car.getUnderbody().getWear()/100.0))).doubleValue()
                        )));
    }

    public static int calculateEngine(Track track,
                                        double temperature,
                                        Weather weather,
                                        Car car,
                                        Driver driver) {
        BigDecimal temp = new BigDecimal( temperature );
        BigDecimal trackConst = new BigDecimal( track.getSetupEngine() );
        BigDecimal wconst = Weather.RAIN == weather ? PartConsts.ENGINE.tempWet : PartConsts.ENGINE.tempDry;
        BigDecimal rconst = Weather.RAIN == weather ? PartConsts.ENGINE.rainOffset : BigDecimal.ZERO;
        BigDecimal xconst = new BigDecimal("-605.0");
        BigDecimal yconst = new BigDecimal("-30.0");

        return Math.max(0, Math.min(999,
                // (ENG_CONST - Y_CONST3 + TEMP * WEATHER_ENG + RAIN_CONST3) 
                (int) Math.round(trackConst.subtract(yconst).add(temp.multiply(wconst)).add(rconst)
                        // * (X_CONST3-EXP)/X_CONST3
                        .multiply(xconst.subtract(new BigDecimal(driver.getAttributes().getExperience())).divide(xconst, 6, RoundingMode.HALF_UP))
                        // + Y_CONST3
                        .add(yconst)
                        // 0.3 * AGGR +
                        .add(PartConsts.ENGINE.agg.multiply(new BigDecimal(driver.getAttributes().getAggressiveness())))
                        // 16 * ENG + 5 * COOLING + 3 * ELEC
                        .add(PartConsts.ENGINE.engLvl.multiply(new BigDecimal(car.getEngine().getLevel())))
                        .add(PartConsts.ENGINE.cooLvl.multiply(new BigDecimal(car.getCooling().getLevel())))
                        .add(PartConsts.ENGINE.eleLvl.multiply(new BigDecimal(car.getElectronics().getLevel())))
                        // -50 * WENG -7 * WCOOLING - 5 * WELEC
                        .add(PartConsts.ENGINE.engWear.multiply(new BigDecimal(car.getEngine().getWear()/100.0)))
                        .add(PartConsts.ENGINE.cooWear.multiply(new BigDecimal(car.getCooling().getWear()/100.0)))
                        .add(PartConsts.ENGINE.eleWear.multiply(new BigDecimal(car.getElectronics().getWear()/100.0))).doubleValue()
                        )));
    }

    public static int calculateBrakes(Track track,
                                        double temperature,
                                        Weather weather,
                                        Car car,
                                        Driver driver) {
        BigDecimal temp = new BigDecimal( temperature );
        BigDecimal trackConst = new BigDecimal( track.getSetupBrakes() );
        BigDecimal wconst = Weather.RAIN == weather ? PartConsts.BRAKES.tempWet : PartConsts.BRAKES.tempDry;
        BigDecimal rconst = Weather.RAIN == weather ? PartConsts.BRAKES.rainOffset : BigDecimal.ZERO;

        return Math.max(0, Math.min(999,
                // BRA_CONST + TEMP * WEATHER_BRA + RAIN_CONST4 +
                (int) Math.round(trackConst.add(temp.multiply(wconst)).add(rconst)
                        // -0.5 * TALENT +
                        .add(PartConsts.BRAKES.tal.multiply(new BigDecimal(driver.getAttributes().getTalent())))
                        // 6 * CHASSI - 29 * BRAKES + 6 * ELEC
                        .add(PartConsts.BRAKES.chaLvl.multiply(new BigDecimal(car.getChassis().getLevel())))
                        .add(PartConsts.BRAKES.braLvl.multiply(new BigDecimal(car.getBrakes().getLevel())))
                        .add(PartConsts.BRAKES.eleLvl.multiply(new BigDecimal(car.getElectronics().getLevel())))
                        // -14 * WCHASSI + 71 * WBRAKES - 9 * WELEC
                        .add(PartConsts.BRAKES.chaWear.multiply(new BigDecimal(car.getChassis().getWear()/100.0)))
                        .add(PartConsts.BRAKES.braWear.multiply(new BigDecimal(car.getBrakes().getWear()/100.0)))
                        .add(PartConsts.BRAKES.eleWear.multiply(new BigDecimal(car.getElectronics().getWear()/100.0))).doubleValue()
                        )));
    }

    public static int calculateGear(Track track,
                                      double temperature,
                                      Weather weather,
                                      Car car,
                                      Driver driver) {
        BigDecimal temp = new BigDecimal( temperature );
        BigDecimal trackConst = new BigDecimal( track.getSetupGear() );
        BigDecimal wconst = Weather.RAIN == weather ? PartConsts.GEAR.tempWet : PartConsts.GEAR.tempDry;
        BigDecimal rconst = Weather.RAIN == weather ? PartConsts.GEAR.rainOffset : BigDecimal.ZERO;

        return Math.max(0, Math.min(999,
                // GEA_CONST + TEMP * WEATHER_BRA + RAIN_CONST5 +
                (int) Math.round(trackConst.add(temp.multiply(wconst)).add(rconst)
                        // 0.5 * CONCENTRATION +
                        .add(PartConsts.GEAR.con.multiply(new BigDecimal(driver.getAttributes().getConcentration())))
                        // -41 * GEAR + 9 * ELEC +
                        .add(PartConsts.GEAR.geaLvl.multiply(new BigDecimal(car.getGearbox().getLevel())))
                        .add(PartConsts.GEAR.eleLvl.multiply(new BigDecimal(car.getElectronics().getLevel())))
                        // 108 * WGEAR - 14 * WELEC
                        .add(PartConsts.GEAR.geaWear.multiply(new BigDecimal(car.getGearbox().getWear()/100.0)))
                        .add(PartConsts.GEAR.eleWear.multiply(new BigDecimal(car.getElectronics().getWear()/100.0))).doubleValue()
                        )));
    }

    public static int calculateSuspension(Track track,
                                            double temperature,
                                            Weather weather,
                                            Car car,
                                            Driver driver) {
        BigDecimal temp = new BigDecimal( temperature );
        BigDecimal trackConst = new BigDecimal( track.getSetupSuspension() );
        BigDecimal wconst = Weather.RAIN == weather ? PartConsts.SUSPENSION.tempWet : PartConsts.SUSPENSION.tempDry;
        BigDecimal rconst = Weather.RAIN == weather ? PartConsts.SUSPENSION.rainOffset : BigDecimal.ZERO;
        BigDecimal tconst = Weather.RAIN == weather ? PartConsts.SUSPENSION.tei : BigDecimal.ZERO;

        return Math.max(0, Math.min(999,
                // SUSP_CONST + TEMP * WEATHER_SUSP + RAIN_CONST6 +
                (int) Math.round(trackConst.add(temp.multiply(wconst)).add(rconst)
                        // 0.75 * EXP + 2 * WEIGHT + IF(RAIN, 0.11 * TECH_INSIGHT)
                        .add(PartConsts.SUSPENSION.exp.multiply(new BigDecimal(driver.getAttributes().getExperience())))
                        .add(PartConsts.SUSPENSION.wei.multiply(new BigDecimal(driver.getAttributes().getWeight())))
                        .add(tconst.multiply(new BigDecimal(driver.getAttributes().getTechInsight())))
                        // -14 * CHASSI - 12 * UNDER + 6 * SIDEP + 31 * SUSP
                        .add(PartConsts.SUSPENSION.chaLvl.multiply(new BigDecimal(car.getChassis().getLevel())))
                        .add(PartConsts.SUSPENSION.undLvl.multiply(new BigDecimal(car.getUnderbody().getLevel())))
                        .add(PartConsts.SUSPENSION.sidLvl.multiply(new BigDecimal(car.getSidepods().getLevel())))
                        .add(PartConsts.SUSPENSION.susLvl.multiply(new BigDecimal(car.getSuspension().getLevel())))
                        // +36 * WCHASSI  + 22 * WUNDER - 11 * WSIDEP - 69 * WSUSP
                        .add(PartConsts.SUSPENSION.chaWear.multiply(new BigDecimal(car.getChassis().getWear()/100.0)))
                        .add(PartConsts.SUSPENSION.undWear.multiply(new BigDecimal(car.getUnderbody().getWear()/100.0)))
                        .add(PartConsts.SUSPENSION.sidWear.multiply(new BigDecimal(car.getSidepods().getWear()/100.0)))
                        .add(PartConsts.SUSPENSION.susWear.multiply(new BigDecimal(car.getSuspension().getWear()/100.0))).doubleValue()
                        )));
    }

}
