package org.gproman.ui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.gproman.calc.CarWearPlanner.StepAction;
import org.gproman.ui.WearPlanningPanel;

/**
 * A combo box editor for the table
 */
public class LevelComboBoxEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 2317480242060071461L;
    private JComboBox         comboBox;
    private FontMetrics       metrics;

    public LevelComboBoxEditor(JComboBox comboBox) {
        super( comboBox );
        this.comboBox = comboBox;
        this.metrics = comboBox.getFontMetrics( comboBox.getFont() );
        this.comboBox.addPopupMenuListener( getPopupMenuListener() );
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        JComboBox cb = (JComboBox) super.getTableCellEditorComponent( table, value, isSelected, row, column );
        cb.removeAllItems();
        WearPlanTableModel model = (WearPlanTableModel) table.getModel();
        // needs to add FIXED_COLUMNS to the column index due to the fixed columns at the start
        List<StepAction> actions = model.getActionsFor( row, column + WearPlanningPanel.FIXED_COLUMNS );
        for ( StepAction action : actions ) {
            cb.addItem( action );
        }
        if( value != null ) {
            cb.setSelectedItem( value ); 
        }
        return cb;
    }

    /**
     * @param box is the ComboBox that is about to show its own popup menu
     * @param metrics is used to calculate the width of your combo box's items
     */
    public void adjustPopupWidth() {
        if ( comboBox.getItemCount() == 0 ) {
            return;
        }
        Object comp = comboBox.getUI().getAccessibleChild( comboBox, 0 );
        if ( !(comp instanceof JPopupMenu) ) {
            return;
        }

        //Find which option is the most wide, to set this width as pop up menu's preferred!
        int maxWidth = metrics.stringWidth( "XX 9 ($99,999,999)" );
        JPopupMenu popup = (JPopupMenu) comp;
        JScrollPane scrollPane = (JScrollPane) popup.getComponent( 0 );
        Dimension size = scrollPane.getPreferredSize();
        // +20, as the vertical scroll bar occupy space too.
        size.width = maxWidth + 20;
        scrollPane.setPreferredSize( size );
        scrollPane.setMaximumSize( size );
    }

    private PopupMenuListener getPopupMenuListener() {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // We will have a much wider drop down list.
                adjustPopupWidth();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
    }

}