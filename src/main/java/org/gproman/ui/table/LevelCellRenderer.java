package org.gproman.ui.table;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

import org.gproman.calc.CarWearPlanner.StepAction;
import org.gproman.model.car.CarPartCost;
import org.gproman.ui.UIUtils;

public class LevelCellRenderer extends DefaultTableCellRenderer {
    private static final long     serialVersionUID = 5260458363166750021L;
    public static final ImageIcon KEEP             = UIUtils.createImageIcon( "/icons/Keep_16.png" );
    public static final ImageIcon REPLACE          = UIUtils.createImageIcon( "/icons/dollar_16.png" );
    public static final ImageIcon DOWNGRADE        = UIUtils.createImageIcon( "/icons/Downgrade_16.png" );

    public LevelCellRenderer() {
        //setOpaque( true );
        setHorizontalAlignment( CENTER );
        setVerticalAlignment( CENTER );
    }

    public void setValue(Object value) {
        if ( value == null ) {
            setIcon( null );
            setText( "" );
        } else if ( value instanceof Number ) {
            setText( String.format( "$%,d", value ) );
            setIcon( null );
        } else {
            StepAction action = (StepAction) value;
            //Set the icon and text.  If icon was null, say so.
            switch ( action.getAction() ) {
                case KEEP :
                    setIcon( KEEP );
                    setText( String.format( "%d (%1.0f%%)", action.getLevel(), action.getWear() ) );
                    break;
                case REPLACE :
                    setIcon( REPLACE );
                    setText( String.format( "%d ($%,d)", action.getLevel(), CarPartCost.getCost( action.getPartIndex(), action.getLevel() ) ) );
                    break;
                case DOWNGRADE :
                    setIcon( DOWNGRADE );
                    setText( String.format( "%d (%1.0f%%)", action.getLevel(), action.getWear() ) );
                    break;
            }
        }
    }
}