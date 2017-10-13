package org.gproman.ui.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ScaleGraph extends JPanel {
    private static final long serialVersionUID = -6167766281638056220L;

    private int value;
    private int minValue;
    private int maxValue;
    private int steps;
    private Color[] color;

    public ScaleGraph( int value, int minValue, int maxValue, int steps, Color start, Color end ) {
        //setBorder(BorderFactory.createLineBorder(Color.black)); 
        setMinimumSize( new Dimension( getMinimumSize().width, (int) ( getFontMetrics( getFont() ).getHeight() * 1.4 ) ) );
        setPreferredSize( getMinimumSize() );
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.steps = steps;
        this.color = new Color[steps];
        
        int dR = ( end.getRed() - start.getRed() ) / steps;
        int dG = ( end.getGreen() - start.getGreen() ) / steps;
        int dB = ( end.getBlue() - start.getBlue() ) / steps;
        for( int i = 0; i < color.length; i++ ) {
            color[i] = new Color( start.getRed() + (i * dR), start.getGreen() + (i * dG), start.getBlue() + (i * dB) );
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent( g );
        Dimension size = getSize();
        int margin = 2;
        int usable = ( size.width - 2 * margin ) / steps * steps; // this is to get a round number
        int step = usable / steps;
        int x = margin;
        int y = (int) (size.height * .9);
        
        int effectiveValue = Math.min( Math.max( value, minValue ), maxValue );
        
        int count = (int) ( ((((double)(effectiveValue - minValue)) / ((double)( maxValue - minValue ))) * 100 ) / ( 100 / steps ) );
        
        for( int i = 0; i < count; i++ ) {
            g.setColor( color[i] );
            g.fillRect( x + i*step, 2, step-1, y );
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        setToolTipText( String.valueOf( value ) );
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
