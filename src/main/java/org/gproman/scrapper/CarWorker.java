package org.gproman.scrapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PartOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class CarWorker
        implements
        Callable<Car> {
    private static final Logger logger    = LoggerFactory.getLogger( CarWorker.class );
    private static final String URL_SUFFIX = "/gb/UpdateCar.asp";
    
    private final WebConnection webClient;

    public CarWorker(WebConnection webClient) {
        this.webClient = webClient;
    }

    @Override
    public Car call() {
        logger.info( "Fetching car data" );
        try {
            HtmlPage carPage = webClient.getPage( this.webClient.getConf().getGproUrl() + URL_SUFFIX );

            Car car = new Car();

            logger.info( "Fetching car PHA" );
            HtmlTable phaTable = (HtmlTable) carPage.getByXPath( "//th[text()='Current car character']/../../.." ).get( 0 );
            HtmlTableRow row = (HtmlTableRow) phaTable.getRow( 2 );

            car.setPower( Integer.valueOf( row.getCell( 0 ).getTextContent() ) );
            car.setHandling( Integer.valueOf( row.getCell( 1 ).getTextContent() ) );
            car.setAcceleration( Integer.valueOf( row.getCell( 2 ).getTextContent() ) );

            logger.info( "Fetching car parts" );
            HtmlForm updateCarForm = carPage.getFormByName( "UpdateCar" );
            // Jeronimo 14/06/2016 - GMT 3.7.6 - Inicio
            //HtmlTable table = updateCarForm.getElementById( "Table1" );
            HtmlTable table = (HtmlTable) carPage.getByXPath("//th[text()='Car part']/../../.." ).get( 0 );
            // Jeronimo 14/06/2016 - GMT 3.7.6 - Fim
            
            @SuppressWarnings("unchecked")
            List<HtmlTableRow> rows = (List<HtmlTableRow>) table.getByXPath( "//tr" );
            for ( HtmlTableRow r : rows ) {
                List<HtmlTableCell> cells = r.getCells();
                // Jeronimo 14/06/2016 - GMT 3.7.6 - Inicio
                //if ( cells.size() == 4 ) {
                if ( cells.size() == 7 ) {
                // Jeronimo 14/06/2016 - GMT 3.7.6 - Fim
                    try {
                        if ( "Chassis:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setChassis( parseCarPart( cells ) );
                        } else if ( "Engine:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setEngine( parseCarPart( cells ) );
                        } else if ( "Front wing:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setFrontWing( parseCarPart( cells ) );
                        } else if ( "Rear wing:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setRearWing( parseCarPart( cells ) );
                        } else if ( "Underbody:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setUnderbody( parseCarPart( cells ) );
                        } else if ( "Sidepods:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setSidepods( parseCarPart( cells ) );
                        } else if ( "Cooling:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setCooling( parseCarPart( cells ) );
                        } else if ( "Gearbox:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setGearbox( parseCarPart( cells ) );
                        } else if ( "Brakes:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setBrakes( parseCarPart( cells ) );
                        } else if ( "Suspension:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setSuspension( parseCarPart( cells ) );
                        } else if ( "Electronics:".equals( cells.get( 0 ).getTextContent() ) ) {
                            car.setElectronics( parseCarPart( cells ) );
                        }
                    } catch ( Exception e ) {
                        logger.error( "Exception parsing " + r.asText(), e );
                    }

                }
            }
            logger.info( "Car attributes loaded = " + car );
            return car;
        } catch ( IOException e1 ) {
            logger.info( "Error retrieving car page. Impossible to recover data.", e1 );
        }
        return null;
    }

    private CarPart parseCarPart(List<HtmlTableCell> cells) {
        CarPart part = new CarPart();
        part.setName( cells.get( 0 ).getTextContent() );
        part.setLevel( Integer.valueOf( cells.get( 1 ).getTextContent() ) );

        HtmlTableCell cell = cells.get( 2 );
        boolean isEnabled = cell.getFirstByXPath( ".//select[@disabled]" ) == null;
        if( isEnabled ) {
            @SuppressWarnings("unchecked")
            List<HtmlOption> options = (List<HtmlOption>) cell.getByXPath( ".//option" );

            for ( HtmlOption op : options ) {
                PartOption po = PartOption.parse( op.getText() );
                if ( po != null ) {
                    part.addOption( po );
                }
            }
        }

        String percent = cells.get( 3 ).getTextContent();
        part.setWear( Double.valueOf( percent.substring( 0, percent.indexOf( '%' ) ).trim() ) );
        logger.debug( "Parsed : " + part );
        return part;
    }
}