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


public class GMTTelemetryParserTest {

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
        String content = readFile("GproTextTelemetry_2.2.1.txt");
        GMTTelemetryParser parser = new GMTTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GMT 2.2.1.GA", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    @Test
    public void testTextParsing2() {
        String content = readFile("GproTextTelemetry_2.2.3.txt");
        GMTTelemetryParser parser = new GMTTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GMT 2.2.3.GA", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    @Test
    public void testTextParsing3() {
        String content = readFile("GproTextTelemetry_2.2.2.txt");
        GMTTelemetryParser parser = new GMTTelemetryParser(db);
        ParsingResult result = new ParsingResult();
        
        parser.parseText(result, "Foo", content, "GMT 2.2.2.GA", "http://foo.bar");
        
//        System.out.println(result.getErrors());
//        System.out.println(result.getRace().toString());
    }

    private String readFile(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new InputStreamReader( GMTTelemetryParserTest.class.getResourceAsStream(fileName) ) );
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
