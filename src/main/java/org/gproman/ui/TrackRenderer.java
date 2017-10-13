package org.gproman.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.gproman.model.track.Track;

class TrackRenderer extends JLabel implements ListCellRenderer {

    public TrackRenderer() {
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    /*
    * This method finds the image and text corresponding
    * to the selected value and returns the label, set up
    * to display the text and image.
    */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (value instanceof Track) {
            setText(((Track) value).getName());
        } else {
            setText("Todas");
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}