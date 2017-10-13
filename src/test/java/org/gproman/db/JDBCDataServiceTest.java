package org.gproman.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.gproman.db.model.dao.TechDirectorDAO;
import org.gproman.db.model.dao.TestSessionDAO;
import org.gproman.db.model.orm.TechDirectorDAOImpl;
import org.gproman.db.model.orm.TestSessionDAOImpl;
import org.gproman.model.ApplicationStatus;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.race.RaceReport;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.season.Season;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;
import org.gproman.scrapper.PastSetupWorker;
import org.gproman.scrapper.TechDirectorWorker;
import org.gproman.scrapper.TestSessionWorker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class JDBCDataServiceTest {

    private static final String           DB_TEST_DOMAIN = "jdbc:h2:mem:";
    private JDBCDataService                db;

    @Before
    public void cleanDB() throws Exception {
        db = new JDBCDataService( DB_TEST_DOMAIN, "sa", "" );
        db.start();
    }

    @After
    public void teardown() {
        db.shutdown();
    }
    
    @Test
    public void testStoreSeason() {
        ApplicationStatus status = db.getApplicationStatus();
        status.setCurrentSeason( 34 );
        status.setNextRace( 1 );
        db.store( status );
        
        Manager manager = new Manager("Edson Tirelli", "foobar", "Amateur - 22", 0, 30, 1000000);
        db.store(manager);
        
        Track track = new Track();
        track.setId( 10 );
        track.setName( "Interlagos" );

        db.store( track );

        Season season = new Season();
        season.setNumber( 34 );
        season.setManagerName("Edson Tirelli");
        Calendar cal = Calendar.getInstance();
        for ( int i = 0; i < 17; i++ ) {
            cal.add( Calendar.DAY_OF_MONTH, 4 );
            Race race = new Race();
            race.setSeasonNumber( season.getNumber() );
            race.setDate( new Timestamp( cal.getTime().getTime() ) );
            race.setNumber( i + 1 );
            race.setTrack( track );
            season.getRaces().add( race );
        }

        db.store( season );

        Season cs = db.getCurrentSeason(manager.getName());
        assertEquals( season.getNumber(), cs.getNumber() );
        assertEquals( season.getRaces().size(), cs.getRaces().size() );
        assertEquals( season.getRaces().get( 0 ).getTrack().getId(), cs.getRaces().get( 0 ).getTrack().getId() );
        assertEquals( season.getRaces().get( 0 ).getTrack().getName(), cs.getRaces().get( 0 ).getTrack().getName() );

    }

    @Test
    public void testStoreRace() throws FailingHttpStatusCodeException, IOException {
        URL url = getClass().getResource( "../scrapper/PastSetups1.html" );
        assertNotNull( url );
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        PastSetupWorker worker = new PastSetupWorker( page );
        RaceReport report = worker.parsePage( page );
        
        Race race = new Race();
        race.setNumber( report.getRace() );
        race.populateFromReport( report );
        
        Season season = new Season();
        season.setNumber( race.getSeasonNumber() );
        season.setManagerName("Edson Tirelli");
        db.store( season );

        db.store( "Edson Tirelli", race );
        
        Race nrace = db.getRace( season.getNumber(), race.getNumber() );
        
        assertEquals( race.getPractice(), nrace.getPractice() );
        assertEquals( race.getQualify1(), nrace.getQualify1() );
        assertEquals( race.getQualify2(), nrace.getQualify2() );
        assertEquals( race.getLaps(), nrace.getLaps() );
        assertEquals( race, nrace );
    }
    
    @Test
    public void testStoreDWW() {
        DriverWearWeight dww = new DriverWearWeight();
        dww.setAggressiveness( new BigDecimal( "1.50000" ) );
        dww.setConcentration( new BigDecimal( "2.50000" ) );
        dww.setExperience( new BigDecimal( "3.50000" ) );
        dww.setStamina( new BigDecimal( "4.50000" ) );
        dww.setTalent( new BigDecimal( "5.50000" ) );
        
        db.store( dww );
        
        DriverWearWeight ndww = db.getDriverAttributesWearWeight();
        
        assertEquals( dww, ndww );
        
    }

    @Test
    public void testStoreDWW2() {
        DriverWearWeight dww = new DriverWearWeight();
        dww.setAggressiveness( new BigDecimal( "0.00000" ) );
        dww.setConcentration( new BigDecimal( "0.00080" ) );
        dww.setExperience( new BigDecimal( "0.00050" ) );
        dww.setStamina( new BigDecimal( "0.00000" ) );
        dww.setTalent( new BigDecimal( "0.00050" ) );
        
        db.store( dww );
        
        DriverWearWeight ndww = db.getDriverAttributesWearWeight();
        assertEquals( dww, ndww );
    }
    
    @Test
    public void testStoreTechDir() throws FailingHttpStatusCodeException, IOException, SQLException {
        URL url = getClass().getResource( "../scrapper/TechnicalDirector1.html" );
        assertNotNull( url );
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        TechDirectorWorker worker = new TechDirectorWorker( page );
        TechDirector td = worker.call();
        
        Connection conn = db.getConnection();
        TechDirectorDAO tddao = TechDirectorDAOImpl.INSTANCE;
        
        tddao.create( td, conn );
        conn.commit();
        TechDirector td2 = tddao.load( td.getId(), conn );
        assertEquals( td, td2 );

        td.setGps( 10 );
        td.setPointsBonus( 1234 );
        
        tddao.update( td, conn );
        conn.commit();
        td2 = tddao.load( td.getId(), conn );
        assertEquals( td, td2 );
        
        tddao.delete( td.getId(), conn );
        td2 = tddao.load( td.getId(), conn );
        assertNull( td2 );
    }
    
    @Test
    public void testStoreTestSession() throws FailingHttpStatusCodeException, IOException, SQLException {
        URL url = getClass().getResource( "../scrapper/Test_done_1.html" );
        assertNotNull( url );
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        TestSessionWorker worker = new TestSessionWorker( page, db );
        TestSession ts = worker.call();
        
        Connection conn = db.getConnection();
        TestSessionDAO tddao = TestSessionDAOImpl.INSTANCE;
        
        tddao.create( ts, conn );
        conn.commit();
        TestSession ts2 = tddao.load( ts.getId(), conn );
        assertEquals( ts, ts2 );

        ts.setLapsDone( 120 );
        ts.setStintsDone( 10 );
        
        tddao.update( ts, conn );
        conn.commit();
        ts2 = tddao.load( ts.getId(), conn );
        assertEquals( ts, ts2 );
        
        tddao.delete( ts.getId(), conn );
        ts2 = tddao.load( ts.getId(), conn );
        assertNull( ts2 );
    }
    
    @Test
    public void testStoreTestStintByStint() throws FailingHttpStatusCodeException, IOException, SQLException {
        HtmlPage page = loadPage("../scrapper/Testing_S36R07_1.html");
        TestSessionWorker worker = new TestSessionWorker( page, db );
        TestSession ts_1 = worker.call();
        
        Connection conn = db.getConnection();
        TestSessionDAO tddao = TestSessionDAOImpl.INSTANCE;
        
        // CREATE TEST SESSION
        tddao.create( ts_1, conn );
        conn.commit();
        TestSession saved = tddao.load( ts_1.getId(), conn );
        assertEquals( ts_1, saved );
        
        Car car_1 = ts_1.getCurrentCar();
        assertEquals( 0, ts_1.getStints().size() );

        // Update after first stint
        page = loadPage("../scrapper/Testing_S36R07_2.html");
        worker = new TestSessionWorker( page, db );
        TestSession ts_2 = worker.call();
        
        ts_1.merge( ts_2 );
        tddao.update( ts_1, conn );
        conn.commit();
        saved = tddao.load( ts_1.getId(), conn );
        assertEquals( ts_1, saved );
        
        assertEquals( 1, ts_1.getStints().size() );
        TestStint stint_1 = ts_1.getStints().get( 0 ); 
        Car car_2 = ts_1.getCurrentCar();
        assertNotSame( car_1, car_2 );
        assertEquals( car_1, stint_1.getCarStart() );
        assertEquals( car_2, stint_1.getCarFinish() );
        
        // Update after second stint
        page = loadPage("../scrapper/Testing_S36R07_3.html");
        worker = new TestSessionWorker( page, db );
        TestSession ts_3 = worker.call();
        
        ts_1.merge( ts_3 );
        tddao.update( ts_1, conn );
        conn.commit();
        saved = tddao.load( ts_1.getId(), conn );
        assertEquals( ts_1, saved );
        
        assertEquals( 2, ts_1.getStints().size() );
        stint_1 = ts_1.getStints().get( 0 ); 
        TestStint stint_2 = ts_1.getStints().get( 1 ); 
        Car car_3 = ts_1.getCurrentCar();
        assertNotSame( car_1, car_2 );
        assertNotSame( car_2, car_3 );
        assertEquals( car_1, stint_1.getCarStart() );
        assertEquals( car_2, stint_1.getCarFinish() );
        assertEquals( car_2, stint_2.getCarStart() );
        assertEquals( car_3, stint_2.getCarFinish() );
        
        // Update after third stint
        page = loadPage("../scrapper/Testing_S36R07_4.html");
        worker = new TestSessionWorker( page, db );
        TestSession ts_4 = worker.call();
        
        ts_1.merge( ts_4 );
        tddao.update( ts_1, conn );
        conn.commit();
        saved = tddao.load( ts_1.getId(), conn );
        assertEquals( ts_1, saved );
        
        assertEquals( 3, ts_1.getStints().size() );
        stint_1 = ts_1.getStints().get( 0 ); 
        stint_2 = ts_1.getStints().get( 1 ); 
        TestStint stint_3 = ts_1.getStints().get( 2 ); 
        Car car_4 = ts_1.getCurrentCar();
        assertNotSame( car_1, car_2 );
        assertNotSame( car_2, car_3 );
        assertNotSame( car_3, car_4 );
        assertEquals( car_1, stint_1.getCarStart() );
        assertEquals( car_2, stint_1.getCarFinish() );
        assertEquals( car_2, stint_2.getCarStart() );
        assertEquals( car_3, stint_2.getCarFinish() );
        assertEquals( car_3, stint_3.getCarStart() );
        assertEquals( car_4, stint_3.getCarFinish() );
        
        // Delete it
        tddao.delete( ts_1.getId(), conn );
        saved = tddao.load( ts_1.getId(), conn );
        assertNull( saved );
    }

    private HtmlPage loadPage( String file ) throws IOException {
        URL url = getClass().getResource( file );
        assertNotNull( url );
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        return page;
    }
    
    @Test @Ignore( "this is not a test. just a quick hack to load td data into the db for testing purposes.")
    public void testStoreTechDir2() throws FailingHttpStatusCodeException, IOException, SQLException {
        URL url = getClass().getResource( "../scrapper/TechnicalDirector1.html" );
        assertNotNull( url );
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        HtmlPage page = client.getPage( url );
        assertNotNull( page );
        TechDirectorWorker worker = new TechDirectorWorker( page );
        TechDirector td = worker.call();
        
        char[] usr = new char[]{'g', 'p', 'r', 'o', 'm', 'a', 'n'};
        char[] pwd = new char[]{'f', 'e', '1', '3', ' ', 'g', 'm', 't', '1', '3'};
        db = new JDBCDataService( "jdbc:h2:file:data/gprodata;CIPHER=AES", new String(usr), new String(pwd) );
        db.start();
        TechDirectorDAO tddao = TechDirectorDAOImpl.INSTANCE;
        
        Race race = db.getRace( 35, 13 );
        race.setTDStart( td );
        
        db.store( "Edson Tirelli", race );
        
        TechDirector td2 = tddao.load( td.getId(), db.getConnection() );
        assertEquals( td, td2 );
        
        db.shutdown();
    }
}
