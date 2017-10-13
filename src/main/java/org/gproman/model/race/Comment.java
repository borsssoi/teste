package org.gproman.model.race;

import java.awt.Color;

public class Comment {
    
    public static enum Satisfaction {
        DDD(-3, Color.RED ), DD(-2, Color.ORANGE), D(-1, Color.ORANGE), OK(0, Color.BLUE), I(1, Color.ORANGE ), II(2, Color.ORANGE), III(3, Color.RED);
        
        private final int factor;
        public final Color color;
        private Satisfaction( int factor, Color color ) {
            this.factor = factor;
            this.color = color;
        }
        
        public int getFactor() {
            return factor;
        }
        
        public String toString() {
            switch( factor ) {
                case -3: return "---";
                case -2: return "--";
                case -1: return "-";
                case 0: return "";
                case 1: return "+";
                case 2: return "++";
                case 3: return "+++";
            }
            return "";
        }
        
        public static Satisfaction fromString( String satisfaction ) {
            if( satisfaction.equals( "-" ) ) {
                return Satisfaction.D;
            } else if( satisfaction.equals( "--" ) ) {
                return Satisfaction.DD;
            } else if( satisfaction.equals( "---" ) ) {
                return Satisfaction.DDD;
            } else if( satisfaction.equals( "+" ) ) {
                return Satisfaction.I;
            } else if( satisfaction.equals( "++" ) ) {
                return Satisfaction.II;
            } else if( satisfaction.equals( "+++" ) ) {
                return Satisfaction.III;
            }
            return OK;
        }
    }
    
    public static enum Part {
        WNG, ENG, BRA, GEA, SUS;
        
        public static Part fromString( String str ) {
            if( WNG.toString().equalsIgnoreCase( str ) ) return WNG;
            else if( ENG.toString().equalsIgnoreCase( str ) ) return ENG;
            else if( BRA.toString().equalsIgnoreCase( str ) ) return BRA;
            else if( GEA.toString().equalsIgnoreCase( str ) ) return GEA;
            else if( SUS.toString().equalsIgnoreCase( str ) ) return SUS;
            return null;
        }
    }
    
    private Part part;
    private Satisfaction sat;
    
    public Comment(Part part,
                   Satisfaction sat) {
        this.part = part;
        this.sat = sat;
    }

    public Comment() {
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Satisfaction getSat() {
        return sat;
    }

    public void setSat(Satisfaction sat) {
        this.sat = sat;
    }

    @Override
    public String toString() {
        return part.toString() + sat.toString();
    }

    public static Comment valueOf(String sp) {
        return new Comment( Part.fromString( sp.substring( 0, 3 ) ), Satisfaction.fromString( sp.substring( 3 ) ));
    }
}
