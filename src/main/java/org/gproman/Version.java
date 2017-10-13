package org.gproman;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
        implements
        Comparable<Version> {
    private final int    major;
    private final int    minor;
    private final int    rev;
    private final String qualifier;

    public Version(int major,
                   int minor,
                   int rev,
                   String qualifier) {
        this.major = major;
        this.minor = minor;
        this.rev = rev;
        this.qualifier = qualifier;
    }

    public Version(String version) {
        Matcher matcher = Pattern.compile( "(\\d+)\\.(\\d+)\\.(\\d+).(.*)" ).matcher( version );
        if ( matcher.matches() ) {
            this.major = Integer.parseInt( matcher.group( 1 ) );
            this.minor = Integer.parseInt( matcher.group( 2 ) );
            this.rev = Integer.parseInt( matcher.group( 3 ) );
            this.qualifier = matcher.group( 4 );
        } else {
            throw new IllegalArgumentException( "Error parsing version number: "+version );
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRev() {
        return rev;
    }

    public String getQualifier() {
        return qualifier;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + rev + "." + qualifier;
    }

    @Override
    public int compareTo(Version o) {
        if ( major - o.major != 0 ) {
            return major - o.major;
        } else if ( minor - o.minor != 0 ) {
            return minor - o.minor;
        } else if ( rev - o.rev != 0 ) {
            return rev - o.rev;
        } else if ( qualifier != null && o.qualifier != null ) {
            return qualifier.compareTo( o.qualifier );
        } else if ( qualifier != null && o.qualifier == null ) {
            return 1;
        } else if ( qualifier == null && o.qualifier != null ) {
            return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
        result = prime * result + rev;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Version other = (Version) obj;
        if ( major != other.major ) return false;
        if ( minor != other.minor ) return false;
        if ( qualifier == null ) {
            if ( other.qualifier != null ) return false;
        } else if ( !qualifier.equals( other.qualifier ) ) return false;
        if ( rev != other.rev ) return false;
        return true;
    }

}
