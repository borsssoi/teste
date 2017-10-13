package org.gproman.report;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.gproman.model.Manager;
import org.gproman.model.race.Race;
import org.gproman.model.race.RaceReport;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;
import org.gproman.report.TelemetryReportGenerator.BBReportGenerator;
import org.gproman.report.TelemetryReportGenerator.TextReportGenerator;
import org.gproman.scrapper.PastSetupWorker;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TelemetryReportGeneratorTest {

    private Race race;
    private Manager manager;
    private Season season;
    
    @Before
    public void setup() throws IOException {
        RaceReport report = execScrapper( "../scrapper/PastSetups13.html" );
        race = new Race();
        race.populateFromReport( report );
        race.setSeasonNumber( 34 );
        race.setNumber( 17 );
        Track track = new Track();
        track.setName( "Poznan (Poland)" );
        track.setDistance( 306.2 );
        track.setLapDistance( 4.083 );
        race.setTrack( track );
        
        manager = new Manager();
        manager.setGroup( "Amateur - 8" );

        season = new Season( 34,
                             Collections.<Race> emptyList(),
                             TyreSupplier.PIPIRELLI,
                             "Amateur - 22",
                             "Edson Tirelli");
    }
    
    private RaceReport execScrapper(String fileName) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled( false );
        client.getOptions().setCssEnabled( false );
        URL url = getClass().getResource( fileName );
        assertNotNull( url );

        HtmlPage page = client.getPage( url );
        assertNotNull( page );

        PastSetupWorker worker = new PastSetupWorker( page );

        return worker.parsePage( page );
    }
    

    @Test @Ignore("Not really a test")
    public void testGenerateBBReport() {
        BBReportGenerator generator  = new BBReportGenerator();
        String report = generator.generate( manager, 
                                            season, 
                                            race,
                                            null );
        System.out.println(report);
        
    }

    @Test @Ignore("Not really a test")
    public void testGenerateTextReport() {
        TextReportGenerator generator  = new TextReportGenerator();
        String report = generator.generate( manager, 
                                            season, 
                                            race,
                                            null );
        System.out.println(report);
        
    }

}
