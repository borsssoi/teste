package org.gproman.scrapper;

import java.util.concurrent.Callable;

import org.gproman.model.Manager;
import org.gproman.model.SeasonHistory;
import org.gproman.model.season.TyreSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;

public class ManagerProfileWorker
        implements
        Callable<Manager> {
    private static final Logger logger    = LoggerFactory.getLogger( ManagerProfileWorker.class );
    final HtmlPage managerPage;

    public ManagerProfileWorker(HtmlPage managerPage) {
        this.managerPage = managerPage;
    }

    @Override
    public Manager call() {
        try {
            return parsePage(managerPage);
        } catch (Exception e) {
            logger.error("Error downloading tyre supplier page.", e);
        }
        return null;
    }

    public Manager parsePage(HtmlPage page) {
        Manager manager = new Manager();
        try { 
            HtmlTitle title = page.getFirstByXPath("//title");
            if( title != null ) {
                String name = title.getTextContent();
                name = name.substring(0, name.indexOf('-') ).trim();
                manager.setName( name );
            } else {
                logger.error("Page title not found in manager's profile. Unable to parse manager's name.");
            }
        } catch( Exception e ) {
            logger.error("Error parsing manager's name on page title: '"+page.getTitleText()+"'", e);
        }
        
        HtmlTable table = page.getFirstByXPath( "//div[@id='dvManHistory']/table" );
        if( table != null ) {
            boolean skipTitle = true;
            for( HtmlTableRow row : table.getRows() ) {
                if( skipTitle ) {
                    skipTitle = false;
                    continue;
                }
                try {
                    SeasonHistory history = new SeasonHistory();
                    history.setSeasonNumber( Integer.parseInt( row.getCell(0).getTextContent().trim() ) );
                    history.setGroupName( row.getCell(1).getTextContent().trim() );
                    String pos = row.getCell(2).getTextContent().trim();
                    history.setPosition( "-".equals(pos) ? null : Integer.parseInt( pos ) );
                    history.setWins( Integer.parseInt( row.getCell(3).getTextContent().trim() ) );
                    history.setPodiums( Integer.parseInt( row.getCell(4).getTextContent().trim() ) );
                    history.setPoles( Integer.parseInt( row.getCell(5).getTextContent().trim() ) );
                    history.setFastestLaps( Integer.parseInt( row.getCell(6).getTextContent().trim() ) );
                    history.setPoints( Integer.parseInt( row.getCell(7).getTextContent().trim() ) );
                    history.setRaces( Integer.parseInt( row.getCell(8).getTextContent().trim() ) );
                    HtmlImage img = row.getCell(10).getFirstByXPath("./img");
                    history.setTyres( img != null ? TyreSupplier.determineTyre( img.getAttribute("title") ) : null );
                    String money = row.getCell(11).getTextContent().trim();
                    history.setMoney( Integer.parseInt( money.substring( money.indexOf('$')+1 ).replaceAll("\\.", "") ) );
                    history.setStatus( row.getCell(12).getTextContent().trim() );
                    
                    manager.getSeasonHistory().add(history);
                } catch( Exception e ) {
                    logger.error("Error parsing season history: "+row.asText(), e);
                }
            }
            
        } else {
            logger.error("Error parsing manager history. Table not found.");
        }
        return manager;
    }
    
    
}