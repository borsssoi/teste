package org.gproman.scrapper;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.db.DataService;
import org.gproman.model.season.OfficeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OfficeWorker
        implements
        Callable<OfficeData> {
    private static final Logger logger     = LoggerFactory.getLogger( OfficeWorker.class );
    private final Pattern       raceParser = Pattern.compile( ".*Season (\\d+),.*Race (\\d+):.*" );
    private final HtmlPage      office;
    private final DataService   db;

    public OfficeWorker(DataService db,
                        HtmlPage office) {
        this.db = db;
        this.office = office;
    }

    @Override
    public OfficeData call() {
        return parsePage( db, 
                          office );
    }

    public OfficeData parsePage( DataService db, 
                                 HtmlPage office ) {
        OfficeData data = new OfficeData();
        HtmlDivision racebar = office.getHtmlElementById( "racebar" );
        HtmlElement h1 = racebar.getHtmlElementsByTagName( "h1" ).get( 0 );
        if( h1.asText().contains( "End of Season" ) ) {
            data.setSeason( -1 );
            data.setNextRace( -1 );
        } else {
            DomNode r = h1.getChildNodes().get( 1 );
            Matcher m = raceParser.matcher( r.getTextContent() );
            if ( m.matches() ) {
                
                data.setSeason( Integer.parseInt( m.group( 1 ) ) );
                data.setNextRace( Integer.parseInt( m.group( 2 ) ) );

                logger.info( "Office data parsed = " + data );
            }
        }
        return data;
    }
}