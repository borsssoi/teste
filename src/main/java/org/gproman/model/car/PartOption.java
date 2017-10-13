package org.gproman.model.car;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gproman.model.PersistentEntity;

public class PartOption extends PersistentEntity implements Serializable {
    private static final long serialVersionUID = -2584540934254889991L;
    
    public static enum Action {
        BUY_NEW, DOWNGRADE;
    }
    
    private Action action;
    private int toLevel;
    private double newWear;
    private int cost;
    
    public PartOption() {
    }
    
    public PartOption(Action action,
                      int toLevel,
                      double newWear,
                      int cost) {
        super();
        this.action = action;
        this.toLevel = toLevel;
        this.newWear = newWear;
        this.cost = cost;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }
    public int getToLevel() {
        return toLevel;
    }
    public void setToLevel(int toLevel) {
        this.toLevel = toLevel;
    }
    public double getNewWear() {
        return newWear;
    }
    public void setNewWear(double newWear) {
        this.newWear = newWear;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    @Override
    public synchronized String toString() {
        if( action.equals( Action.BUY_NEW ) ) {
            return String.format( "nível %d ($%,d)", toLevel, cost );
        } else {
            return String.format( "nível %d (Desg.: %2.0f%%)", toLevel, newWear );
        }
    }
    
    
    private static Pattern replace = Pattern.compile( ".*Replace with level (\\d+) \\(\\$([\\d\\.]+)\\).*" );
    private static Pattern downgrade = Pattern.compile( ".*Downgrade to level (\\d+) \\(Wear: (\\d+)%\\).*" );
    public static synchronized PartOption parse( String text ) {
        if( text.startsWith( "----" ) || text.equals( "Don't replace" ) ) {
            return null;
        }
        Matcher m = replace.matcher( text );
        if( m.matches() ) {
            return new PartOption(Action.BUY_NEW,
                                  Integer.parseInt( m.group( 1 ) ),
                                  0,
                                  Integer.parseInt( m.group( 2 ).replaceAll( "\\.", "" ) ) );
        }
        m = downgrade.matcher( text );
        if( m.matches() ) {
            return new PartOption(Action.DOWNGRADE,
                                  Integer.parseInt( m.group( 1 ) ),
                                  Double.parseDouble( m.group( 2 ) ),
                                  0 );
        }
        return null;
    }

    
}
