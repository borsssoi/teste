package org.gproman.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.gproman.calc.CarWearPlanner;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.track.Track;
import org.gproman.model.track.TrackWearFactors;
import org.gproman.model.track.WearCoefs;
import org.junit.Before;
import org.junit.Test;

public class CarWearPlannerTest {

    private CarWearPlanner planner;

    @Before
    public void setup() {
        DriverWearWeight dww = new DriverWearWeight( new BigDecimal( "0.0008" ),
                                                     new BigDecimal( "0.0005" ),
                                                     new BigDecimal( "0.0000" ),
                                                     new BigDecimal( "0.0005" ),
                                                     new BigDecimal( "0.0000" ) );
        BigDecimal[] coefs = new BigDecimal[]{
                new BigDecimal( "-0.0121527777775693" ),
                new BigDecimal( "0.459325396817217" ),
                new BigDecimal( "-7.260416666532" ),
                new BigDecimal( "62.3194444432347" ),
                new BigDecimal( "-314.692708326878" ),
                new BigDecimal( "942.090277756945" ),
                new BigDecimal( "-1580.53472218276" ),
                new BigDecimal( "1227.63095234109" ),
                new BigDecimal( "-99.9999999836494" )
        };
        WearCoefs wpc = new WearCoefs( coefs );
        
        Track t1 = new Track();
        t1.setName( "A1-Ring" );
        t1.setLaps(100);
        TrackWearFactors twf = new TrackWearFactors();
        twf.setFactors( new BigDecimal[] { 
                                          new BigDecimal( "20.8641920589025" ),
                                          new BigDecimal( "38.150327915676" ),
                                          new BigDecimal( "14.4443094135096" ),
                                          new BigDecimal( "15.1790442612346" ),
                                          new BigDecimal( "14.9404069230366" ),
                                          new BigDecimal( "17.6065554350895" ),
                                          new BigDecimal( "13.0903009802181" ),
                                          new BigDecimal( "26.7287958743127" ),
                                          new BigDecimal( "31.2772728671537" ),
                                          new BigDecimal( "19.9695669529134" ),
                                          new BigDecimal( "15.1702329155677" )
        } );
        t1.setWearFactors( twf );
        
        planner = new CarWearPlanner( dww,
                                      wpc,
                                      t1 );
        Race r1 = mock( Race.class );
        when( r1.getTrack() ).thenReturn( t1 );
        when( r1.getNumber() ).thenReturn( 8 );
        
        Track t2 = new Track();
        t2.setName( "Imola" );
        t2.setLaps(100);
        
        twf = new TrackWearFactors();
        twf.setFactors( new BigDecimal[] { 
                                          new BigDecimal( "16.628831433023" ),
                                          new BigDecimal( "30.0373247696124" ),
                                          new BigDecimal( "29.1309428530115" ),
                                          new BigDecimal( "31.642229242135" ),
                                          new BigDecimal( "24.0614802326622" ),
                                          new BigDecimal( "12.5323432507355" ),
                                          new BigDecimal( "14.8557156356478" ),
                                          new BigDecimal( "28.6583394176481" ),
                                          new BigDecimal( "35.0856180683054" ),
                                          new BigDecimal( "28.6020721215406" ),
                                          new BigDecimal( "13.7499676833161" )
        } );
        t2.setWearFactors( twf );
        Race r2 = mock( Race.class );
        when( r2.getTrack() ).thenReturn( t2 );
        when( r2.getNumber() ).thenReturn( 9 );
        
        Track t3 = new Track();
        t3.setName( "Zolder" );
        t3.setLaps(100);
        twf = new TrackWearFactors();
        twf.setFactors( new BigDecimal[] { 
                                          new BigDecimal( "14.1656257912679" ),
                                          new BigDecimal( "15.5723410408358" ),
                                          new BigDecimal( "19.8489167541084" ),
                                          new BigDecimal( "18.0010064457508" ),
                                          new BigDecimal( "16.3239836864816" ),
                                          new BigDecimal( "18.1147895834424" ),
                                          new BigDecimal( "16.9041773336488" ),
                                          new BigDecimal( "31.2885680222233" ),
                                          new BigDecimal( "22.5834278639544" ),
                                          new BigDecimal( "24.9895761033261" ),
                                          new BigDecimal( "15.0304958743239" )
        } );
        t3.setWearFactors( twf );
        Race r3 = mock( Race.class );
        when( r3.getTrack() ).thenReturn( t3 );
        when( r3.getNumber() ).thenReturn( 10 );
        
        
        
        planner.addRace( r1 );
        planner.addRace( r2 );
        planner.addRace( r3 );
        
        Driver driver = new Driver();
        driver.setName( "Oscar Comas" );
        DriverAttributes attrs = new DriverAttributes(107, 220, 110, 0, 56, 129, 91, 50, 121, 0, 67, 23);
        driver.setAttributes( attrs );
        
        planner.setDriver( driver );
        
        when (r1.getDriverStart()).thenReturn(driver);
    }

    @Test
    public void testProjectWear() {
        Car start = new Car();
        CarPart[] parts = new CarPart[] {
                                         new CarPart( "Chassis", 5, 0 ),
                                         new CarPart( "Engine", 5, 0 ),
                                         new CarPart( "Front Wing", 5, 0 ),
                                         new CarPart( "Rear Wing", 5, 0 ),
                                         new CarPart( "Underbody", 5, 0 ),
                                         new CarPart( "Sidepods", 5, 0 ),
                                         new CarPart( "Cooling", 5, 0 ),
                                         new CarPart( "Gearbox", 5, 0 ),
                                         new CarPart( "Brakes", 5, 0 ),
                                         new CarPart( "Suspension", 5, 0 ),
                                         new CarPart( "Electronics", 5, 0 ),
        };
        start.setParts( parts );
        planner.setCar( 0, start );
        List<Car> cars = planner.projectWear( 0 );
        assertEquals( 3, cars.size() );
        checkWear( cars.get( 0 ), 17, 30, 12, 12, 12, 14, 11, 21, 25, 16, 12 );
        checkWear( cars.get( 1 ), 31, 54, 35, 37, 31, 24, 23, 44, 53, 39, 23 );
        checkWear( cars.get( 2 ), 43, 67, 51, 52, 44, 39, 37, 69, 71, 59, 35 );
        
        planner.setRisk( 0, new BigDecimal("40") );
        cars = planner.projectWear( 0 );
        assertEquals( 3, cars.size() );
        checkWear( cars.get( 0 ), 20, 36, 14, 15, 14, 17, 13, 25, 29, 19, 15 );
        checkWear( cars.get( 1 ), 34, 60, 37, 40, 33, 27, 25, 48, 57, 42, 26 );
        checkWear( cars.get( 2 ), 46, 73, 53, 55, 46, 42, 39, 73, 75, 62, 38 );
        
        
        planner.setRisk( 2, new BigDecimal("80") );
        cars = planner.projectWear( 2 );
        assertEquals( 1, cars.size() );
//        for( Car car : cars ) {
//            System.out.println(car);
//        }
        checkWear( cars.get( 0 ), 50, 77, 59, 60, 51, 47, 44, 82, 82, 69, 43 );
        
    }

    private void checkWear(Car car,
                           double... wear) {
        for( int i = 0; i < wear.length; i++ ) {
            assertEquals( wear[i], car.getParts()[i].getWear(), 0.01 );
        }
    }

}
