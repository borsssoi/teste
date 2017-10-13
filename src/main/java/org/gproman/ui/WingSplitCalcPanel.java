package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.WingSplitInterpolator;
import org.gproman.db.DataService;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class WingSplitCalcPanel extends UIPluginBase {

    private static final long serialVersionUID = 210232127277861273L;

    private JSpinner[]        wings            = new JSpinner[3];
    private JSpinner[]        temps            = new JSpinner[3];
    private JLabel            idealWS          = new JLabel();

    private Date start;


    public WingSplitCalcPanel(GPROManFrame gproManFrame,
                        DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BorderLayout() );

        // Building the second column
        FormLayout layout = new FormLayout( "80dlu, 4dlu, 80dlu", "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );

        idealWS.setText( "0" );
        idealWS.setHorizontalAlignment( SwingConstants.RIGHT );
        Font bold = idealWS.getFont().deriveFont( Font.BOLD );
        
        JLabel dif = new JLabel("Diferença");
        JLabel time = new JLabel("Tempo");
        builder.append( dif, time );
        dif.setFont( bold );
        time.setFont( bold );
        builder.nextLine();
        
        Date value = new GregorianCalendar( 1970, Calendar.JANUARY, 1, 0, 1, 30 ).getTime();
        start = new GregorianCalendar( 1970, Calendar.JANUARY, 1, 0, 0, 0 ).getTime();
        Date end = new GregorianCalendar( 1970, Calendar.JANUARY, 1, 0, 3, 0 ).getTime();

        for( int i = 0; i < wings.length; i++ ) {
            wings[i] = new JSpinner( new SpinnerNumberModel( 0, -999, 999, 1 ) );
            wings[i].addChangeListener( new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    updateFields();
                }
            } );
            
            // Java Spinner Date Model has a bug... to work around it, have to be instantiated like this
            temps[i] = new JSpinner( new SpinnerDateModel( value, start, end, Calendar.SECOND ) );
            JSpinner.DateEditor editor = new JSpinner.DateEditor( temps[i], "m:ss.SSS" );
            temps[i].setEditor( editor );
            JFormattedTextField tf = editor.getTextField();
            tf.setHorizontalAlignment( SwingConstants.RIGHT );
            tf.setEditable( true );
            temps[i].addChangeListener( new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    updateFields();
                }
            } );
            
            builder.append( wings[i], temps[i] );
        }
        
        builder.appendSeparator();
        idealWS.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 10 ) );
        idealWS.setBackground( Color.BLUE );
        idealWS.setForeground( Color.WHITE );
        idealWS.setFont( bold );
        idealWS.setOpaque( true );
        JLabel lbl = builder.append( "Diferença ideal: ", idealWS );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );

        JPanel panel = builder.getPanel();
        add( panel, BorderLayout.CENTER );
    }

    private void updateFields() {
        int[] ws = new int[3];
        int[] to = new int[3];
        for( int i = 0; i < wings.length; i++ ) {
            ws[i] = ((Number)wings[i].getValue()).intValue();
            to[i] = (int) (((SpinnerDateModel) temps[i].getModel()).getDate().getTime() - start.getTime());
        }
        int bws = WingSplitInterpolator.interpolate( ws, to );
        idealWS.setText( String.valueOf( bws ) );
    }

    @Override
    public void update() {
        setDirty( false );
    }

    @Override
    public String getTitle() {
        return "Diferença de asas ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/wings_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/wings_16.png" );
    }

    @Override
    public String getDescription() {
        return "Calculadora de diferença de asas";
    }

    @Override
    public Category getCategory() {
        return Category.CALC;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_D;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
