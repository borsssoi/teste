package org.gproman.ui.table;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A renderer for the money fields
 */
public class MoneyRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 722465285565288031L;

    public MoneyRenderer() {
        setHorizontalAlignment( SwingConstants.RIGHT );
    }

    public void setValue(Object value) {
        setText( (value == null) ? "" : String.format( "$%,d", value ) );
    }
}