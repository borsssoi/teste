package org.gproman.model.car;


public class PHA implements Cloneable {
    private double p;
    private double h;
    private double a;
    
    public PHA() {
    }

    public PHA(double p,
               double h,
               double a) {
        this.p = p;
        this.h = h;
        this.a = a;
    }

    @Override
    public String toString() {
        return String.format( "PHA [p=%3.2f, h=%3.2f, a=%3.2f]", p, h, a );
    }

    public int getP() {
        return (int) Math.round( p );
    }

    public void setP(double p) {
        this.p = p;
    }

    public int getH() {
        return (int) Math.round( h );
    }

    public void setH(double h) {
        this.h = h;
    }

    public int getA() {
        return (int) Math.round( a );
    }

    public void setA(double a) {
        this.a = a;
    }
    
    public double getPd() {
        return this.p;
    }

    public double getHd() {
        return this.h;
    }

    public double getAd() {
        return this.a;
    }

    public PHA getBonusPHA(PHA base) {
        return new PHA( p - base.p, h - base.h, a - base.a );
    }
    
    public PHA add( PHA base ) {
        return new PHA( p + base.p, h + base.h, a + base.a );
    }
    
    @Override
    public PHA clone() {
        return new PHA( p, h, a );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits( a );
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( h );
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits( p );
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        PHA other = (PHA) obj;
        if ( a - other.a > 0.001 ) return false;
        if ( h - other.h > 0.001 ) return false;
        if ( p - other.p > 0.001 ) return false;
        return true;
    }

}