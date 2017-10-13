package org.gproman.calc;

import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;

public class ConversionCalculator {

    public static int convert(Track track,
                                Car car,
                                Driver driver, 
                                Weather oWeather,
                                Weather nWeather,
                                int oTemp,
                                int nTemp,
                                PartConsts part) {
        int from = 0;
        int to = 0;
        switch( part ) {
            case WINGS:
                from = SetupCalculator.calculateWings(track, oTemp, oWeather, car, driver);
                to = SetupCalculator.calculateWings(track, nTemp, nWeather, car, driver);
                break;
            case ENGINE:
                from = SetupCalculator.calculateEngine(track, oTemp, oWeather, car, driver);
                to = SetupCalculator.calculateEngine(track, nTemp, nWeather, car, driver);
                break;
            case BRAKES:
                from = SetupCalculator.calculateBrakes(track, oTemp, oWeather, car, driver);
                to = SetupCalculator.calculateBrakes(track, nTemp, nWeather, car, driver);
                break;
            case GEAR:
                from = SetupCalculator.calculateGear(track, oTemp, oWeather, car, driver);
                to = SetupCalculator.calculateGear(track, nTemp, nWeather, car, driver);
                break;
            case SUSPENSION:
                from = SetupCalculator.calculateSuspension(track, oTemp, oWeather, car, driver);
                to = SetupCalculator.calculateSuspension(track, nTemp, nWeather, car, driver);
                break;
        }
        return to - from;
    }

    /**
     * Converts all 6 parts from a given forecast to both dry and wet custom temp
     * 
     * @param q1
     * @param customTemp
     * @return
     */
    public static int[][] convert(Track track,
                                    Car car,
                                    Driver driver,
                                    Forecast q1,
                                    int customTemp) {
        int[][] conv = new int[6][];
        for ( int i = 0; i < conv.length; i++ ) {
            conv[i] = new int[2];
            conv[i][0] = ConversionCalculator.convert( track,
                                                       car,
                                                       driver,
                                                       q1.getWeather(),
                                                       Weather.SUNNY,
                                                       q1.getTempMin(),
                                                       customTemp,
                                                       PartConsts.getByIndex( i ) );
            conv[i][1] = ConversionCalculator.convert( track,
                                                       car,
                                                       driver,
                                                       q1.getWeather(),
                                                       Weather.RAIN,
                                                       q1.getTempMin(),
                                                       customTemp,
                                                       PartConsts.getByIndex( i ) );
        }
        return conv;
    }

    /**
     * Returns a matrix where lines are each of the parts in order and columns are 
     * the conversions
     * 
     * @param forecast
     * @param temp
     * @return
     */
    public static int[][] convertAll(Track track,
                                        Car car,
                                        Driver driver,
                                        Forecast[] forecast,
                                        int customTemp) {
        int[][] conv = new int[6][];
        // for each part
        for ( int i = 0; i < conv.length; i++ ) {
            conv[i] = new int[11];
            // Q2 conversion
            conv[i][0] = convert( track, car, driver,
                                  forecast[0].getWeather(), // Q1 
                                  forecast[1].getWeather(), // to Q2 
                                  forecast[0].getTempMin(),
                                  forecast[1].getTempMin(),
                                  PartConsts.getByIndex( i ) );
            double sum = 0;
            // 6 race conversions
            for ( int j = 0; j < 3; j++ ) {
                int temp = (forecast[j + 2].getTempMin() + forecast[j + 2].getTempMax()) / 2;
                sum += temp;
                conv[i][j * 2 + 1] = convert( track, car, driver,
                                              forecast[0].getWeather(), // Q1 
                                              Weather.SUNNY, // to dry race quarter
                                              forecast[0].getTempMin(),
                                              temp,
                                              PartConsts.getByIndex( i ) );
                conv[i][j * 2 + 2] = convert( track, car, driver,
                                              forecast[0].getWeather(), // Q1 
                                              Weather.RAIN, // to wet race quarter 
                                              forecast[0].getTempMin(),
                                              temp,
                                              PartConsts.getByIndex( i ) );
            }
            int avg = (int) Math.round( sum / 3.0 );
            // race average
            conv[i][7] = convert( track, car, driver,
                                  forecast[0].getWeather(), // Q1 
                                  Weather.SUNNY, // to dry race avg 
                                  forecast[0].getTempMin(),
                                  avg,
                                  PartConsts.getByIndex( i ) );
            conv[i][8] = convert( track, car, driver,
                                  forecast[0].getWeather(), // Q1 
                                  Weather.RAIN, // to wet race avg 
                                  forecast[0].getTempMin(),
                                  avg,
                                  PartConsts.getByIndex( i ) );
            // custom temperature
            conv[i][9] = convert( track, car, driver,
                                  forecast[0].getWeather(), // Q1 
                                  Weather.SUNNY, // to dry race avg 
                                  forecast[0].getTempMin(),
                                  customTemp,
                                  PartConsts.getByIndex( i ) );
            conv[i][10] = convert( track, car, driver,
                                   forecast[0].getWeather(), // Q1 
                                   Weather.RAIN, // to wet race avg 
                                   forecast[0].getTempMin(),
                                   customTemp,
                                   PartConsts.getByIndex( i ) );
        }
        return conv;
    }

}
