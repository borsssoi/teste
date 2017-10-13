package org.gproman.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearFactors;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.track.Track;
import org.gproman.model.track.TrackWearFactors;
import org.gproman.model.track.WearCoefs;

public class CarWearCalculator {

    private DriverWearWeight  dww;
    private WearCoefs         wpc;
    
    private Track             track;
    private Car               car;
    private DriverWearFactors df;
    private CarWearFactors    cf;

    public CarWearCalculator(DriverWearWeight dww,
                             WearCoefs wpc,
                             Track track) {
        this( dww, wpc, null, track, null );
    }

    public CarWearCalculator(DriverWearWeight dww,
                             WearCoefs wpc,
                             Driver driver,
                             Track track,
                             Car car) {
        this.dww = dww;
        this.wpc = wpc;
        updateDriver( driver );
        updateTrack( track );
        updateCar( car );
    }


    public void updateDriver(Driver driver) {
        if( driver != null) {
            this.df = new DriverWearFactors( driver.getAttributes(), dww );
        }
    }

    public void updateTrack(Track track) {
        this.track = track;
    }

    public void updateCar(Car car) {
        if( car != null ) {
            this.car = car;
            this.cf = new CarWearFactors( car.getParts(), wpc );
        }
    }

    public CarPart[] predictWear(BigDecimal risk) {
        CarPart[] result = new CarPart[car.getParts().length];
        for ( int i = 0; i < result.length; i++ ) {
            result[i] = predictWear( risk, i );
        }
        return result;
    }

    public CarPart[] predictWearForTestLaps(BigDecimal risk, int laps) {
        CarPart[] result = new CarPart[car.getParts().length];
        for ( int i = 0; i < result.length; i++ ) {
            result[i] = predictWearForTestLapsOnPart( risk, laps, i ); 
        }
        return result;
    }

    public CarPart predictWearForTestLapsOnPart(BigDecimal risk,
                                                int laps,
                                                int partIndex) {
        CarPart result = predictWear( risk, partIndex );
        result.setWear( Math.ceil( laps * result.getWear() / this.track.getLaps() / 2.5 ) );
        return result;
    }

    public CarPart predictWear(BigDecimal risk,
                               int partIndex ) {
        CarPart result = new CarPart();
        result.setName( car.getParts()[partIndex].getName() );
        result.setLevel( car.getParts()[partIndex].getLevel() );
        result.setWear( calculateWear( risk, cf.getFactors()[partIndex], partIndex ) );
        return result;
    }

    private double calculateWear(BigDecimal risk,
                                 BigDecimal partWearFactor,
                                 int i) {
        TrackWearFactors tf = track.getWearFactors();
        if( tf.getFactors()[i] == null ) {
            return 0;
        } else {
            return tf.getFactors()[i].multiply( df.getAggressiveness() )
                    .multiply( BigDecimal.ONE.add( risk.multiply( partWearFactor ) ) )
                    .divide( df.getConcentration().multiply( df.getTalent() ).multiply( df.getExperience() ).multiply( df.getStamina() ), 0, RoundingMode.UP )
                    .doubleValue();
        }
    }

    public CarPart predictWearForLevel(int partIndex,
                                       BigDecimal risk,
                                       int newLevel) {
        CarPart part = car.getParts()[partIndex];
        CarPart result = new CarPart( part.getName(), newLevel, 0 );
        result.setWear( calculateWear( risk, cf.calculateFactor( BigDecimal.valueOf( newLevel ) ), partIndex ) );
        return result;
    }


}
