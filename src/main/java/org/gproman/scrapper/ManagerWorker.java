package org.gproman.scrapper;

import java.util.concurrent.Callable;

import org.gproman.model.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ManagerWorker
        implements
        Callable<Manager> {
    private static final Logger logger    = LoggerFactory.getLogger( ManagerWorker.class );
    final HtmlPage office;

    public ManagerWorker(HtmlPage office) {
        this.office = office;
    }

    @Override
    public Manager call() {
        HtmlAnchor managerLink = office.getFirstByXPath( "//a[starts-with(@href,'ManagerProfile.asp?')]" );
        Manager manager = new Manager();
        String link = managerLink.getHrefAttribute();
        manager.setId( new Integer( link.substring( link.indexOf( '=' )+1 ) ) );
        manager.setName( managerLink.getTextContent().trim() );
        
        HtmlAnchor group = office.getFirstByXPath( "//a[starts-with(@href,'Standings.asp?Group=')]" );
        manager.setGroup( group.getTextContent().trim() );
        
        logger.info( "Logged in as manager = " + manager );
        return manager;
    }
}