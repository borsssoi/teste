package org.gproman.calc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.gproman.model.driver.Driver;
import org.gproman.model.race.Weather;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Track;

public class FuelCalculator {

    private static final BigDecimal COEF = new BigDecimal("1.05");
    
    @SuppressWarnings("serial")
    private static final Map<FuelConsumption,BigDecimal> BASE_DRY = new HashMap<FuelConsumption,BigDecimal>() {{
             put( FuelConsumption.VERY_LOW,  new BigDecimal("0.6450") );
             put( FuelConsumption.LOW,       get(FuelConsumption.VERY_LOW).multiply( COEF ) );
             put( FuelConsumption.MEDIUM,    get(FuelConsumption.LOW).multiply( COEF ) );
             put( FuelConsumption.HIGH,      get(FuelConsumption.MEDIUM).multiply( COEF ) );
             put( FuelConsumption.VERY_HIGH, get(FuelConsumption.HIGH).multiply( COEF ) );
    }};

    @SuppressWarnings("serial")
    private static final Map<FuelConsumption,BigDecimal> BASE_RAIN = new HashMap<FuelConsumption,BigDecimal>() {{
        put( FuelConsumption.VERY_LOW,  new BigDecimal("0.4400") );
        put( FuelConsumption.LOW,       get(FuelConsumption.VERY_LOW).multiply( COEF ) );
        put( FuelConsumption.MEDIUM,    get(FuelConsumption.LOW).multiply( COEF ) );
        put( FuelConsumption.HIGH,      get(FuelConsumption.MEDIUM).multiply( COEF ) );
        put( FuelConsumption.VERY_HIGH, get(FuelConsumption.HIGH).multiply( COEF ) );
    }};
    
    private static final BigDecimal CONST = new BigDecimal("-0.0096");
    
    private static final BigDecimal DEFAULT = new BigDecimal("0.855"); // default conversion factor for rain
    
    public static BigDecimal predictConsumption( Weather weather, int engine, int electronics, Track track ) {
        BigDecimal base = BASE_DRY.get( track.getFuelConsumption() );
        if( weather.equals( Weather.RAIN ) ) {
            if( track.getFuelCoef() == null || track.getFuelCoef().compareTo( BigDecimal.ZERO ) <= 0) {
                base = base.multiply( DEFAULT ); 
            } else {
                base = base.multiply( track.getFuelCoef() );
            }
        }
        BigDecimal parts = new BigDecimal( (((double)engine)*1.8)+(((double)electronics)*1.2)-15 );
        return base.multiply( BigDecimal.ONE.add( ( CONST.multiply( parts ) ) ) );
    }
    
    public static double predictConsumptionPerLap( Weather weather, int engine, int electronics, Track track  ) {
        return predictConsumption( weather, engine, electronics, track ).doubleValue() * track.getDistance() / track.getLaps();
    }
    
    private static final double bEle = 1.009;
    private static final double bEng = 1.014;
    private static final double bTeI = 1.00036;
    private static final double bExp = 1.00014;
    private static final double bCon = 1.00008;
    private static final double bHum = 1.00025;
    private static final double bAgr = 1.00018;
    
    public static double predictConsumption2( Weather weather, int engine, int electronics, double hum, Track track, Driver driver ) {
        int fcon = driver.getAttributes().getConcentration() - track.getFCon();
        int fagr = driver.getAttributes().getAggressiveness() - track.getFAgr();
        int fexp = driver.getAttributes().getExperience() - track.getFExp();
        int ftei = driver.getAttributes().getTechInsight() - track.getFTeI();
        int feng = engine - track.getFEng();
        int fele = electronics - track.getFEle();
        double fhum = hum - track.getFHum();
        double ffue = track.getFFue();
        
        double result = Math.ceil( 
                ( ffue +
                ( ffue - Math.pow(bEle, fele) * ffue ) +
                ( ffue - Math.pow(bEng, feng) * ffue ) +
                ( ffue - Math.pow(bTeI, ftei) * ffue ) +
                ( ffue - Math.pow(bExp, fexp) * ffue ) +
                ( ffue - Math.pow(bCon, fcon) * ffue ) +
                ( ffue - Math.pow(bHum, fhum) * ffue ) -
                ( ffue - Math.pow(bAgr, fagr) * ffue )
                ) * 1.01 );
        
        if( weather.equals( Weather.RAIN ) ) {
            if( track.getFuelCoef() == null || track.getFuelCoef().compareTo( BigDecimal.ZERO ) <= 0) {
                result *= DEFAULT.doubleValue(); 
            } else {
                result *= track.getFuelCoef().doubleValue();
            }
        }
        
        return result;
    }
    
    public static double predictConsumption3( Weather weather, int engine, int electronics, Track track, Driver driver ) {
        int fcon = driver.getAttributes().getConcentration() - track.getFCon();
        int fagr = driver.getAttributes().getAggressiveness() - track.getFAgr();
        int fexp = driver.getAttributes().getExperience() - track.getFExp();
        int ftei = driver.getAttributes().getTechInsight() - track.getFTeI();
        int feng = engine - track.getFEng();
        int fele = electronics - track.getFEle();
        double ffue = track.getFFue();
        
        double result = Math.ceil( 
                ( ffue +
                ( feng * -3 ) +
                ( fele * -2 ) +
                ( ((double)fagr / 25.0 ) *  1 ) +
                ( ((double)fcon / 50.0 ) * -1 ) +
                ( ((double)fexp / 30.0 ) * -1 ) +
                ( ((double)ftei / 12.5 ) * -1 ) 
                ) + 1 );
        
        if( weather.equals( Weather.RAIN ) ) {
            if( track.getFuelCoef() == null || track.getFuelCoef().compareTo( BigDecimal.ZERO ) <= 0) {
                result *= DEFAULT.doubleValue(); 
            } else {
                result *= track.getFuelCoef().doubleValue();
            }
        }
        
        return result;
    }
    
}
