package org.gproman.ui.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.border.Border;

import org.gproman.calc.CarWearPlanner.StepAction;
import org.gproman.model.car.CarPartCost;

import sun.swing.DefaultLookup;

/**
 * A combo box renderer for the combo box editor
 */
public class LevelComboBoxRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = -5799069863086293253L;

    public LevelComboBoxRenderer() {
        setHorizontalAlignment( CENTER );
        setVerticalAlignment( CENTER );
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        setComponentOrientation( list.getComponentOrientation() );

        Color bg = null;
        Color fg = null;

        JList.DropLocation dropLocation = list.getDropLocation();
        if ( dropLocation != null
             && !dropLocation.isInsert()
             && dropLocation.getIndex() == index ) {

            bg = DefaultLookup.getColor( this, ui, "List.dropCellBackground" );
            fg = DefaultLookup.getColor( this, ui, "List.dropCellForeground" );

            isSelected = true;
        }

        if ( isSelected ) {
            setBackground( bg == null ? list.getSelectionBackground() : bg );
            setForeground( fg == null ? list.getSelectionForeground() : fg );
        }
        else {
            setBackground( list.getBackground() );
            setForeground( list.getForeground() );
        }

        StepAction action = (StepAction) value;
        if ( action != null ) {
            //Set the icon and text.  If icon was null, say so.
            switch ( action.getAction() ) {
                case KEEP :
                    setIcon( LevelCellRenderer.KEEP );
                    setText( String.format( "%d (%1.0f%%)", action.getLevel(), action.getWear() ) );
                    break;
                case REPLACE :
                    setIcon( LevelCellRenderer.REPLACE );
                    setText( String.format( "%d ($%,d)", action.getLevel(), CarPartCost.getCost( action.getPartIndex(), action.getLevel() ) ) );
                    break;
                case DOWNGRADE :
                    setIcon( LevelCellRenderer.DOWNGRADE );
                    setText( String.format( "%d (%1.0f%%)", action.getLevel(), action.getWear() ) );
                    break;
            }
        } else {
            setIcon( null );
            setText( "" );
        }
        setEnabled( list.isEnabled() );
        setFont( list.getFont() );

        Border border = null;
        if ( cellHasFocus ) {
            if ( isSelected ) {
                border = DefaultLookup.getBorder( this, ui, "List.focusSelectedCellHighlightBorder" );
            }
            if ( border == null ) {
                border = DefaultLookup.getBorder( this, ui, "List.focusCellHighlightBorder" );
            }
        } else {
            border = getNoFocusBorder();
        }
        setBorder( border );
        return this;
    }

    private Border getNoFocusBorder() {
        Border border = DefaultLookup.getBorder( this, ui, "List.cellNoFocusBorder" );
        if ( System.getSecurityManager() != null ) {
            if ( border != null ) return border;
            return noFocusBorder;
        } else {
            return noFocusBorder;
        }
    }
}