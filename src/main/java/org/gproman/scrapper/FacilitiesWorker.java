package org.gproman.scrapper;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.staff.Facilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeaderCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class FacilitiesWorker
        implements
        Callable<Facilities> {
    private static final Logger logger = LoggerFactory.getLogger( FacilitiesWorker.class );
    private final HtmlPage      facilitiesPage;
    private final Pattern salaryPattern = Pattern.compile("Staff salary: \\$([\\d\\.,]+).*Facilities maintenance: \\$([\\d\\.,]+)");
    private final Pattern mltPattern = Pattern.compile("Your maximum level of training is currently (\\d+)");

    public FacilitiesWorker(HtmlPage driverPage) {
        this.facilitiesPage = driverPage;
    }

    @Override
    public Facilities call() {
        try {
            Facilities facilities = parsePage( facilitiesPage );
            logger.info( "Facilities retrieved = " + facilities );
            return facilities;
        } catch ( Exception e1 ) {
            logger.info( "Error retrieving Facilities page. Impossible to recover data.", e1 );
        }
        return null;
    }

    public Facilities parsePage(HtmlPage facilitiesPage) {
        Facilities facilities = new Facilities();
        facilities.setOverall( extractVal(facilitiesPage, "//th[contains(text(),'Overall:')]/ancestor::tr[1]") );

        facilities.setExperience( extractVal(facilitiesPage, "//th[contains(text(),'Experience:')]/ancestor::tr[1]") );
        facilities.setMotivation( extractVal(facilitiesPage, "//th[contains(text(),'Motivation:')]/ancestor::tr[1]") );
        facilities.setTechnical( extractVal(facilitiesPage, "//th[contains(text(),'Technical skill:')]/ancestor::tr[1]") );
        facilities.setStress( extractVal(facilitiesPage, "//th[contains(text(),'Stress handling:')]/ancestor::tr[1]") );
        facilities.setConcentration( extractVal(facilitiesPage, "//th[contains(text(),'Concentration:')]/ancestor::tr[1]") );
        facilities.setEfficiency( extractVal(facilitiesPage, "//th[contains(text(),'Efficiency:')]/ancestor::tr[1]") );
        facilities.setWindtunnel( extractVal(facilitiesPage, "//th[contains(text(),'Windtunnel:')]/ancestor::tr[1]") );
        facilities.setPitstop( extractVal(facilitiesPage, "//th[contains(text(),'Pitstop training center:')]/ancestor::tr[1]") );
        facilities.setWorkshop( extractVal(facilitiesPage, "//th[contains(text(),'R&D workshop:')]/ancestor::tr[1]") );
        facilities.setDesign( extractVal(facilitiesPage, "//th[contains(text(),'R&D design center:')]/ancestor::tr[1]") );
        facilities.setEngineering( extractVal(facilitiesPage, "//th[contains(text(),'Engineering workshop:')]/ancestor::tr[1]") );
        facilities.setAlloy( extractVal(facilitiesPage, "//th[contains(text(),'Alloy and chemical lab:')]/ancestor::tr[1]") );
        facilities.setCommercial( extractVal(facilitiesPage, "//th[contains(text(),'Commercial:')]/ancestor::tr[1]") );
        
        parseSalaryAndMaintenance(facilitiesPage, facilities);
        parseMLT(facilitiesPage, facilities);
        
        return facilities;
    }

    private void parseSalaryAndMaintenance(HtmlPage facilitiesPage, Facilities facilities) {
        try {
            HtmlTableHeaderCell ss = facilitiesPage.getFirstByXPath("//th[contains(text(), 'Staff salary:')]");
            if( ss != null ) {
                String content = ss.getTextContent().trim();
                Matcher m = salaryPattern.matcher(content);
                if( m.matches() ) {
                    facilities.setSalary( Integer.valueOf(m.group(1).replaceAll("[\\.,]", "")));
                    facilities.setMaintenance( Integer.valueOf(m.group(2).replaceAll("[\\.,]", "")));
                } else {
                    logger.error("Error parsing salary and maintenance '"+content+"'");
                }
            } else {
                logger.error("Salary and maintenance cost not found");
            }
        } catch (Exception e) {
            logger.error("Error parsing salary and maintenance cost", e);
        }
    }

    private void parseMLT(HtmlPage facilitiesPage, Facilities facilities) {
        try {
            HtmlParagraph ss = facilitiesPage.getFirstByXPath("//p[contains(text(), 'Your maximum level of training')]");
            if( ss != null ) {
                String content = ss.getTextContent().trim();
                Matcher m = mltPattern.matcher(content);
                if( m.matches() ) {
                    facilities.setMlt( Integer.valueOf(m.group(1)));
                } else {
                    logger.error("Error parsing MLT '"+content+"'");
                }
            } else {
                logger.error("MLT not found");
            }
        } catch (Exception e) {
            logger.error("Error parsing MLT", e);
        }
    }

    private Integer extractVal(HtmlPage facilitiesPage, String xp) {
        Integer val = null;
        try {
            HtmlTableRow tr = facilitiesPage.getFirstByXPath( xp );
            if( tr != null ) {
                HtmlTableCell cell = tr.getCell(1);
                val = cell != null ? Integer.valueOf(cell.getTextContent().trim()) : 0;
            }
        } catch (Exception e) {
            logger.error("Error parsing value for xpath ["+xp+"]", e);
        }
        return val;
    }
}