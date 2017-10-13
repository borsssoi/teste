package org.gproman.miner.parsers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.gproman.db.DataService;
import org.gproman.miner.ParsingResult;
import org.gproman.model.track.Track;
import org.junit.Before;
import org.junit.Test;


public class GoBRTelemetryParserTest {

    private DataService db;

    @Before
    public void setup() {
        db = mock(DataService.class);
        Track track = new Track();
        track.setDistance(306);
        track.setLapDistance(3.825);
        when( db.getTrackByName( any(String.class) ) ).thenReturn( track );
    }
    
    @Test
    public void testTextParsing1() {
        String content = readFile("gobr_1.txt");
        GoBRTelemetryParser parser = new GoBRTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GoBR", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    @Test
    public void testTextParsing2() {
        String content = readFile("gobr_2.txt");
        GoBRTelemetryParser parser = new GoBRTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GoBR", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    @Test
    public void testTextParsing3() {
        String content = readFile("gobr_3.txt");
        GoBRTelemetryParser parser = new GoBRTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GoBR", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    @Test
    public void testTextParsing4() {
        String content = readFile("gobr_4.txt");
        GoBRTelemetryParser parser = new GoBRTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GoBR", "http://foo.bar");
        
        System.out.println(result.getErrors());
        System.out.println(result.getRace().toString());
    }

    private String readFile(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new InputStreamReader( GoBRTelemetryParserTest.class.getResourceAsStream(fileName) ) );
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while( line != null ) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }
            String content = builder.toString();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Error reading input file: "+fileName);
            return null;
        } finally {
            if( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
