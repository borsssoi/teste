package org.gproman.ui.table;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A renderer for percent based numbers
 */
public class PercentRenderer extends DefaultTableCellRenderer {
    private static final long  serialVersionUID = -905893778806980008L;
    private static final Color DARK_RED         = new Color( 207, 0, 0 );
    private static final Color DARK_ORANGE      = new Color( 207, 128, 128 );

    public PercentRenderer() {
        setHorizontalAlignment( SwingConstants.RIGHT );
    }

    public void setValue(Object value) {
        if ( value != null ) {
            Number val = (Number) value;
            setText( String.format( "%3.0f%%", val ) );
            if ( val.intValue() >= 100 ) {
                setForeground( DARK_RED );
            } else if ( val.intValue() >= 90 ) {
                setForeground( DARK_ORANGE );
            } else {
                setForeground( Color.black );
            }
        } else {
            setText( "" );
            setForeground( Color.black );
        }
    }
}