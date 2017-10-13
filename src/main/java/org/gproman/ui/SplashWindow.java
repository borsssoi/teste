package org.gproman.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import org.gproman.GproManager;

public class SplashWindow extends JWindow {

    private static final long serialVersionUID = 1L;
    private JProgressBar      pbar;
    private JLabel            status;

    public SplashWindow(JFrame owner,
                        ImageIcon splashImage) {
        super( owner );
        JPanel pane = new JPanel();
        pane.setLayout( new BoxLayout(pane, BoxLayout.PAGE_AXIS) );
        pane.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
        
        JLabel lbl = new JLabel( "GMT - GPRO Manager's Toolbox" );
        Font italic = lbl.getFont().deriveFont( Font.ITALIC );
        Font bold14 = lbl.getFont().deriveFont( Font.BOLD, 14 );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setFont( bold14 );
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add( lbl );
        
        lbl = new JLabel( GproManager.getVersionString() );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setFont( italic );
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add( lbl );
        
        JLabel image = new JLabel( splashImage );
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add( image );
        
        this.pbar = new JProgressBar(0, 100);
        this.pbar.setAlignmentX( Component.CENTER_ALIGNMENT );
        pane.add( pbar );
        
        this.status = new JLabel("Carregando...");
        status.setSize( 280, status.getHeight() );
        this.status.setAlignmentX( Component.CENTER_ALIGNMENT );
        pane.add( status );
        
        getContentPane().add( pane );
        
        int imgWidth = 330;
        int imgHeight = 260;
        setSize( imgWidth, imgHeight );
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (screenDim.width - imgWidth) / 2,
                     (screenDim.height - imgHeight) / 2 );
    }

    public static SplashWindow splash(JFrame parent, ImageIcon splashImage) {
        SplashWindow w = new SplashWindow( parent, splashImage );
        w.toFront();
        w.setVisible( true );
        return w;
    }
    
    public void update( int progress, String message ) {
        this.pbar.setValue( progress );
        this.status.setText( message );
    }
}