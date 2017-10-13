package org.gproman.ui;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RatingLabel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final ImageIcon[] icons = new ImageIcon[] { 
                                                              UIUtils.createImageIcon( "/icons/tyre_rate1.png" ),
                                                              UIUtils.createImageIcon( "/icons/tyre_rate2.png" ),
                                                              UIUtils.createImageIcon( "/icons/tyre_rate3.png" ),
                                                              UIUtils.createImageIcon( "/icons/tyre_rate4.png" ),
                                                              UIUtils.createImageIcon( "/icons/tyre_rate5.png" ),
    };
    
    private JLabel[] lbl = new JLabel[5];
    private int value = 3;
    
    
    public RatingLabel() {
        setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
        for( int i = 0; i < lbl.length; i++ ) {
            lbl[i] = new JLabel();
            lbl[i].setIcon( icons[i] );
            lbl[i].setVisible( i < value );
            add( lbl[i] );
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue( int value ) {
        if( value < 1 || value > 5) {
            throw new IllegalArgumentException( "Value must be between 1 and 5" );
        }
        this.value = value;
        for( int i = 0; i < lbl.length; i++ ) {
            lbl[i].setVisible( i < value );
        }
    }

}
