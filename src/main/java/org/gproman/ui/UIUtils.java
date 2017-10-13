package org.gproman.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class UIUtils {
    final static Logger logger = LoggerFactory.getLogger( UIUtils.class );

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = UIUtils.class.getResource( path );
        if ( imgURL != null ) {
            return new ImageIcon( imgURL );
        } else {
            logger.error( "Couldn't find icon file: " + path );
            return null;
        }
    }

    public static void createColumnTitle(DefaultFormBuilder builder,
                                         JLabel lbl,
                                         int span,
                                         Color bg,
                                         Color fg, 
                                         Font font ) {
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setOpaque( true );
        lbl.setFont( font );
        if ( bg != null ) lbl.setBackground( bg );
        if ( fg != null ) lbl.setForeground( fg );
        builder.append( lbl, span );
    }

}
