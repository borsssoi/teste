package org.gproman.util;

public class CSVHelper {
    private static final String DELIMITER   = ",";

    private static final String EOL = System.getProperty("line.separator");
    
    private StringBuilder builder;
    
    public CSVHelper() {
    }
    
    public CSVHelper newLine() {
        this.builder = new StringBuilder();
        return this;
    }
    
    public String endLine() {
        this.builder.append(EOL);
        String line = builder.toString();
        builder = null;
        return line;
    }
    
    public CSVHelper printHeader(String[] headers) {
        fs(headers[0], false);
        for( int i = 1; i < headers.length; i++ ) {
            fs( headers[i] );
        }
        return this;
    }

    public CSVHelper fi(Integer field) {
        fi( field, true );
        return this;
    }
    
    public CSVHelper fi(Integer field, boolean delim) {
        if( delim ) delim();
        if( field != null ) {
            builder.append(field);
        }
        return this;
    }

    public CSVHelper fd(Double field) {
        fd( field, true );
        return this;
    }    
    
    public CSVHelper fd(Double field, boolean delim) {
        if(delim) delim();
        if( field != null ) {
            builder.append(String.format("%5.3f", field));
        }
        return this;
    }

    public CSVHelper fs(String field) {
        fs( field, true );
        return this;
    }    
    
    public CSVHelper fs(String field, boolean delim) {
        if(delim) delim();
        if( field != null ) {
            builder.append("\"" + field + "\"");
        }
        return this;
    }

    public CSVHelper delim() {
        builder.append(DELIMITER);
        return this;
    }

}
