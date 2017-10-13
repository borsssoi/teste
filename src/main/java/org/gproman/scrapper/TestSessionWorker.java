package org.gproman.scrapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.db.DataService;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Comment;
import org.gproman.model.race.TestPriority;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;
import org.gproman.util.CommentsTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class TestSessionWorker
        implements
        Callable<TestSession> {
    private static final Logger logger           = LoggerFactory.getLogger( TestSessionWorker.class );
    public static final String TEST_SESSION_URL_SUFFIX = "/gb/Testing.asp";

    private final HtmlPage      testSessionPage;
    private DataService         db;

    public TestSessionWorker(HtmlPage testSessionPage,
                             DataService db) {
        this.testSessionPage = testSessionPage;
        this.db = db;
    }

    @Override
    public TestSession call() {
        return parsePage( testSessionPage );
    }

    public TestSession parsePage(HtmlPage tsPage) {
        try {
            TestSession ts = new TestSession();
            HtmlAnchor tdLink = tsPage.getFirstByXPath( "//a[contains(@href,'TrackDetails.asp?id=')]" );
            String href = tdLink.getAttribute( "href" );

            int trackId = Integer.parseInt( href.substring( href.indexOf( '=' ) + 1 ) );
            Track track = db.getTrackById( trackId );
            if ( track != null ) {
                ts.setTrack( track );
            } else {
                logger.error( "Track not found for id = " + trackId );
            }
            parseForecast( tsPage, ts );
            parseCCP( tsPage, ts );
            parseCurrentCar( tsPage, ts );
            parseStints( tsPage, ts );

            logger.info( "TestSession retrieved = " + ts );
            return ts;
        } catch ( Exception e1 ) {
            logger.error( "Error retrieving Test Session page. Impossible to recover data.", e1 );
            return null;
        }
    }

    private void parseStints(HtmlPage tsPage,
                             TestSession ts) {
        HtmlTable stt = tsPage.getFirstByXPath( "//th[contains(text(),'Testing stints data')]/ancestor::table" );
        HtmlTableRow title = stt.getRow( 0 );

        Pattern fp = Pattern.compile( ".*laps done: (\\d+).*stints done: (\\d+).*", Pattern.MULTILINE | Pattern.DOTALL );
        Matcher fm = fp.matcher( title.asText() );
        if ( fm.matches() ) {
            ts.setLapsDone( Integer.parseInt( fm.group( 1 ) ) );
            ts.setStintsDone( Integer.parseInt( fm.group( 2 ) ) );
        } else {
            logger.error( "Unable to parse number of test stints: '" + title.asXml() + "'" );
        }

        if ( ts.getStintsDone() > 0 ) {
            Map<Integer, StintComments> comments = parseLapComments( tsPage );

            for ( int i = 3; i < stt.getRowCount(); i++ ) {
                HtmlTableRow row = stt.getRow( i );
                try {
                    int index = 0;
                    TestStint stint = new TestStint();
                    stint.setNumber( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    String laps = row.getCell( index++ ).asText().trim();
                    stint.setLapsDone( Integer.parseInt( laps.substring( 0, laps.indexOf( '/' ) ) ) );
                    stint.setLapsPlanned( Integer.parseInt( laps.substring( laps.indexOf( '/' ) + 1 ) ) );
                    stint.setBestTime( parseTime( row.getCell( index++ ).asText().trim() ) );
                    stint.setMeanTime( parseTime( row.getCell( index++ ).asText().trim() ) );

                    CarSettings settings = new CarSettings();
                    settings.setFrontWing( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setRearWing( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setEngine( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setBrakes( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setGear( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setSuspension( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    settings.setTyre( Tyre.determineTyre( row.getCell( index++ ).getTextContent().trim() ) );
                    stint.setSettings( settings );

                    stint.setFuelStart( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );
                    String tyres = row.getCell( index++ ).asText().trim();
                    stint.setTyresEnd( Integer.parseInt( tyres.substring( 0, tyres.indexOf( '%' ) ) ) );
                    stint.setFuelEnd( Integer.parseInt( row.getCell( index++ ).asText().trim() ) );

                    stint.setPriority( comments.get( stint.getNumber() ).priority );
                    stint.setComments( comments.get( stint.getNumber() ).comments.toString() );

                    ts.getStints().add( stint );
                } catch ( Exception e ) {
                    logger.error( "Error parsing test stint: " + row.asXml(), e );
                }
            }
        }
    }

    private int parseTime(String timeStr) throws ParseException {
        if ( timeStr.indexOf( ':' ) > 0 ) {
            // 'm:ss.SSS'
            String[] parts = timeStr.split( "[:\\.s]" );
            return (Integer.parseInt( parts[0] ) * 60000) + (Integer.parseInt( parts[1] ) * 1000) + Integer.parseInt( parts[2] );
        } else {
            // 'ss.SSS'
            String[] parts = timeStr.split( "[\\.s]" );
            return (Integer.parseInt( parts[0] ) * 1000) + Integer.parseInt( parts[1] );
        }
    }

    private void parseCurrentCar(HtmlPage tsPage,
                                 TestSession ts) {
        Car car = new Car();
        HtmlTable cart = tsPage.getFirstByXPath( "//th[contains(text(),'Setup related parts')]/ancestor::table" );
        for ( HtmlTableRow r : cart.getRows() ) {
            List<HtmlTableCell> cells = r.getCells();
            if ( cells.size() >= 5 ) {
                try {
                    if ( "Engine:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setEngine( parseCarPart( cells, 0 ) );
                    } else if ( "Front wing:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setFrontWing( parseCarPart( cells, 0 ) );
                    } else if ( "Rear wing:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setRearWing( parseCarPart( cells, 0 ) );
                    } else if ( "Gear:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setGearbox( parseCarPart( cells, 0 ) );
                    } else if ( "Brakes:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setBrakes( parseCarPart( cells, 0 ) );
                    } else if ( "Suspension:".equals( cells.get( 0 ).asText().trim() ) ) {
                        car.setSuspension( parseCarPart( cells, 0 ) );
                    }
                    if ( "Chassis:".equals( cells.get( 4 ).asText().trim() ) ) {
                        car.setChassis( parseCarPart( cells, 4 ) );
                    } else if ( "Underbody:".equals( cells.get( 4 ).asText().trim() ) ) {
                        car.setUnderbody( parseCarPart( cells, 4 ) );
                    } else if ( "Sidepods:".equals( cells.get( 4 ).asText().trim() ) ) {
                        car.setSidepods( parseCarPart( cells, 4 ) );
                    } else if ( "Cooling:".equals( cells.get( 4 ).asText().trim() ) ) {
                        car.setCooling( parseCarPart( cells, 4 ) );
                    } else if ( "Electronics:".equals( cells.get( 4 ).asText().trim() ) ) {
                        car.setElectronics( parseCarPart( cells, 4 ) );
                    }
                } catch ( Exception e ) {
                    logger.error( "Exception parsing car part" + r.asText(), e );
                }
            }
        }
        ts.setCurrentCar( car );
    }

    private CarPart parseCarPart(List<HtmlTableCell> cells,
                                 int startCell) {
        CarPart part = new CarPart();
        part.setName( cells.get( startCell ).asText().trim().replace( ":", "" ) );
        part.setLevel( Integer.valueOf( cells.get( startCell + 1 ).asText().trim() ) );
        String percent = cells.get( startCell + 2 ).asText().trim();
        part.setWear( Double.valueOf( percent.substring( 0, percent.indexOf( '%' ) ).trim() ) );
        return part;
    }

    private void parseCCP(HtmlPage tsPage,
                          TestSession ts) {
        HtmlTable ccpt = tsPage.getFirstByXPath( "//th[contains(text(),'Test points')]/ancestor::table" );
        ts.setTestPoints( parseCCPLine( ccpt.getRow( 1 ) ) );
        ts.setRdPoints( parseCCPLine( ccpt.getRow( 2 ) ) );
        ts.setEngPoints( parseCCPLine( ccpt.getRow( 3 ) ) );
        ts.setCcPoints( parseCCPLine( ccpt.getRow( 4 ) ) );
    }

    private PHA parseCCPLine(HtmlTableRow row) {
        PHA pha = new PHA();
        pha.setP( Double.parseDouble( row.getCell( 2 ).asText() ) );
        pha.setH( Double.parseDouble( row.getCell( 3 ).asText() ) );
        pha.setA( Double.parseDouble( row.getCell( 4 ).asText() ) );
        return pha;
    }

    private void parseForecast(HtmlPage page,
                               TestSession ts) {
        HtmlTableCell forecast = page.getFirstByXPath( "//td/text()[contains(.,'Humidity')]/ancestor::td" );
        if( forecast != null ) {
            Pattern fp = Pattern.compile( ".*Temp: (\\d+).*Humidity: (\\d+).*", Pattern.MULTILINE | Pattern.DOTALL );
            parseForcastCell( forecast, fp, ts );
        } else {
            logger.warn("Forecast cell not found for test track.");
        }
    }

    private void parseForcastCell(HtmlTableCell cell,
                                  Pattern fp,
                                  TestSession ts) {
        Matcher fm = fp.matcher( cell.asText() );
        if ( fm.matches() ) {
            HtmlImage weather = cell.getFirstByXPath( "./img" );
            ts.setWeather( Weather.determineWeather( weather.getAttribute( "title" ) ) );
            ts.setTemperature( Integer.parseInt( fm.group( 1 ) ) );
            ts.setHumidity( Integer.parseInt( fm.group( 2 ) ) );
        } else {
            logger.error( "Unable to parse weather forecast for: '" + cell.asXml() + "'" );
        }
    }

    private Map<Integer, StintComments> parseLapComments(HtmlPage practicePage) {
        Map<Integer, StintComments> ret = new HashMap<Integer, StintComments>();

        Pattern findComments = Pattern.compile( "function getLapComment.*(comments\\[1\\] =.*)return comments", Pattern.MULTILINE | Pattern.DOTALL );
        Matcher commentsMatcher = findComments.matcher( practicePage.asXml() );
        if ( commentsMatcher.find() ) {
            String[] comments = commentsMatcher.group( 1 ).split( ";" );
            Pattern commentFinder = Pattern.compile( "<br>(.+?)(?=<br>|$|\")" );
            Pattern priorityFinder = Pattern.compile( ".*\\(Stint research priority: (.*)\\).*" );
            for ( String c : comments ) {
                if ( !c.trim().isEmpty() ) {
                    String trimmed = c.trim();
                    Integer lap = Integer.parseInt( trimmed.substring( 9, trimmed.indexOf( ']' ) ) );
                    StintComments sc = new StintComments();
                    sc.comments = new ArrayList<Comment>();
                    ret.put( lap, sc );
                    Matcher pm = priorityFinder.matcher( c );
                    if ( pm.find() ) {
                        sc.priority = TestPriority.determinePriority( pm.group( 1 ).trim() );
                        if ( TestPriority.SETUP.equals( sc.priority ) ) {
                            Matcher cm = commentFinder.matcher( c );
                            while ( cm.find() ) {
                                String cleanComment = cm.group( 0 ).trim().replaceAll( "<.*?>", "" ).replaceAll( "\\s+", " " );
                                Comment normalizedComment = CommentsTranslator.getTranslation( cleanComment );
                                if ( normalizedComment != null ) {
                                    sc.comments.add( normalizedComment );
                                } else {
                                    if ( !cleanComment.startsWith( "I am satisfied" ) && !cleanComment.startsWith( "Estou satisfeito" ) && !cleanComment.startsWith( "(Stint research" ) ) {
                                        logger.error( "Error parsing test stint comment. Pre-defined comment not found for string '" + cleanComment + "'" );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            logger.error( "Unable to extract test stint comments." );
        }
        return ret;
    }

    private static class StintComments {
        public TestPriority  priority;
        public List<Comment> comments;
    }
}