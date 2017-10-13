package org.gproman.scrapper;

import java.util.List;
import java.util.concurrent.Callable;

import org.gproman.model.staff.TechDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class TechDirectorWorker
        implements
        Callable<TechDirector> {
    private static final Logger logger = LoggerFactory.getLogger( TechDirectorWorker.class );
    private final HtmlPage      tdPage;

    public TechDirectorWorker(HtmlPage driverPage) {
        this.tdPage = driverPage;
    }

    @Override
    public TechDirector call() {
        try {
            TechDirector td = parsePage( tdPage );
            logger.info( "Technical Director retrieved = " + td );
            return td;
        } catch ( Exception e1 ) {
            logger.info( "Error retrieving Technical Director page. Impossible to recover data.", e1 );
        }
        return null;
    }

    public TechDirector parsePage(HtmlPage tdPage) {
        HtmlAnchor tdLink = tdPage.getFirstByXPath( "//a[contains(@href,'TechDProfile.asp?ID=')]" );
        String href = tdLink.getAttribute( "href" );

        TechDirector td = new TechDirector();
        td.setNumber( Integer.parseInt( href.substring( href.indexOf( '=' )+1 ) ) );
        logger.info( "Found TD id = " + td.getNumber() );

        HtmlTable table = tdPage.getFirstByXPath( "//th[contains(text(),'Nationality:')]/ancestor::table" );
        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            try {
                if ( cells.size() == 2 ) {
                    if ( "Name:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        td.setName( cells.get( 1 ).getTextContent().trim() );
                    } else if ( "Nationality:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String country = cells.get( 1 ).getTextContent().trim();
                        td.setNationality( country.substring( country.indexOf( '(' ) + 1, country.indexOf( ')' ) ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Exception parsing " + tr.asText(), e );
            }
        }
        
        table = tdPage.getFirstByXPath( "//th[contains(text(),'Trophies:')]/ancestor::table" );
        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            try {
                if ( cells.size() == 2 ) {
                    if ( "Trophies:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        td.setTrophies( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Number of GPs:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        td.setGps( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    } else if ( "Wins:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        td.setWins( Integer.parseInt( cells.get( 1 ).getTextContent().trim() ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Exception parsing " + tr.asText(), e );
            }
        }
        
        table = tdPage.getFirstByXPath( "//th[contains(text(),'Overall:')]/ancestor::table" );
        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            try {
                if ( cells.size() == 3 ) {
                    if ( "Overall:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setOverall( Integer.parseInt( val ) );
                    } else if ( "Leadership:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setLeadership( Integer.parseInt( val ) );
                    } else if ( "R&D mechanics:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setRdMech( Integer.parseInt( val ) );
                    } else if ( "R&D electronics:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setRdElect( Integer.parseInt( val ) );
                    } else if ( "R&D aerodynamics:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setRdAero( Integer.parseInt( val ) );
                    } else if ( "Experience:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setExperience( Integer.parseInt( val ) );
                    } else if ( "Pit coordination:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setPitCoord( Integer.parseInt( val ) );
                    } else if ( "Motivation:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setMotivation( Integer.parseInt( val ) );
                    } else if ( "Age:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.getAttributes().setAge( Integer.parseInt( val ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Exception parsing " + tr.asText(), e );
            }
        }
        
        table = tdPage.getFirstByXPath( "//th[contains(text(),'Salary:')]/ancestor::table" );
        for ( HtmlTableRow tr : table.getRows() ) {
            List<HtmlTableCell> cells = tr.getCells();
            try {
                if ( cells.size() == 2 ) {
                    if ( "Salary:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setSalary( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    } else if ( "Contract length:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setContract( Integer.parseInt( val.substring( 0, val.indexOf( ' ' ) ) ) );
                    } else if ( "Points bonus:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setPointsBonus( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    } else if ( "Podium bonus:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setPodiumBonus( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    } else if ( "Win bonus:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setWinBonus( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    } else if ( "Trophy bonus:".equals( cells.get( 0 ).getTextContent().trim() ) ) {
                        String val = cells.get( 1 ).getTextContent().trim();
                        td.setTrophyBonus( Integer.parseInt( val.substring( 1 ).replaceAll( "\\.", "" ) ) );
                    }
                }
            } catch ( Exception e ) {
                logger.error( "Exception parsing " + tr.asText(), e );
            }
        }
        return td;
    }
}