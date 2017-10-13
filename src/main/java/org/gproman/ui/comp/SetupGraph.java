package org.gproman.ui.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.gproman.calc.PracticeHelper.PartSetup;
import org.gproman.model.race.Comment;

public class SetupGraph extends JPanel {
    private static final long serialVersionUID = -6167766281638056220L;
    private static final Color DARK_GREEN = new Color( 0, 128, 0 );
    private static final Color DARK_RED = new Color( 210, 0, 0 );

    private PartSetup part = null;

    public SetupGraph() {
        //setBorder(BorderFactory.createLineBorder(Color.black)); 
        setMinimumSize( new Dimension( getMinimumSize().width, 24 ) );
        setPreferredSize( getMinimumSize() );
    }
    
    public void updatePart( PartSetup part ) {
        this.part = part;
        setToolTipText( String.format( "Ajuste ideal: %d (± %d). Para afinar o ajuste use: %d.", part.getIdealAdjustment(), part.getError(), part.getNextSetupValue() ) );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent( g );
        Dimension size = getSize();
        int margin = 5;
        int usable = ( size.width - 2 * margin ) / 20 * 20; // this is to get a round number
        int step = usable / 20;
        int x = margin;
        int y = (int) (size.height * .8);
        
        for( int i = 0; i <= 20; i++ ) {
            int h = i%2==0 ? 10 : 5;
            if( i == 0 || i == 10 || i == 20 ) {
                h = 15;
                g.setColor( Color.BLACK );
            } else {
                g.setColor( Color.GRAY );
            }
            g.fillRect( x + i*step, y-h, 1, h+2 );
        }
        g.setColor( Color.BLACK );
        g.fillRect( x, y, 20*step, 2 );
        
        if( part != null ) {
            if( ! part.getHints().isEmpty() ) {
                int sz = (int) (((double) part.getSz() / 1000.0) * usable);
                int px = x + (int) (((double)(part.getIARange()[0]+part.getOffset()) / 1000.0) * usable);
                int pw = x + (int) (((double)(part.getIARange()[1]+part.getOffset()) / 1000.0) * usable) - px;
                g.setColor( Color.ORANGE );
                int tx = Math.max( px-2*sz, x );
                int tz = Math.max( 0, px-sz-tx );
                g.fillRect( tx, y-10, tz, 8 );
                g.setColor( Color.YELLOW );
                
                tx = Math.max( px-sz, x );
                tz = Math.max( 0, px-tx );
                g.fillRect( tx, y-10, tz, 8 );
                g.setColor( DARK_GREEN );
                g.fillRect( px, y-10, pw, 8 );
                g.setColor( Color.YELLOW );
                
                tx = Math.min( px+pw, x+usable );
                tz = Math.min( px+pw+sz, x+usable ) - tx;
                g.fillRect( tx, y-10, tz, 8 );
                g.setColor( Color.ORANGE );
                tx = Math.min( px+pw+sz, x+usable );
                tz = Math.min( px+pw+sz*2, x+usable ) - tx;
                g.fillRect( tx, y-10, tz, 8 );
                
                g.setColor( Color.BLACK );
                for( Entry<Integer, Comment> e : part.getHints().entrySet() ) {
                    int cx = x + (int) ((e.getKey().doubleValue() / 1000.0) * usable);
                    g.fillOval( cx, y-7, 3, 3 );
                }
                g.setColor( Color.BLACK );
            }
        }
        
    }

}
