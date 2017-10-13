package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.CarPHACalculator;
import org.gproman.db.DataService;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.gproman.model.race.Race;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class PHACalcPanel extends UIPluginBase {

    private static final long serialVersionUID = 210232127277861273L;

    private JSpinner[]        parts            = new JSpinner[Car.PARTS_COUNT];

    private JLabel            power            = new JLabel();
    private JLabel            handl            = new JLabel();
    private JLabel            accel            = new JLabel();

    private JButton           loadCar          = new JButton();

    private CarPart[]         model            = new CarPart[Car.PARTS_COUNT];

    private Race              nextRace;

    public PHACalcPanel(GPROManFrame gproManFrame,
                        DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BorderLayout() );

        // Building the second column
        FormLayout layout = new FormLayout( "right:120dlu, 4dlu, 40dlu", "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Características do Carro: " );

        power.setText( "0.0" );
        power.setHorizontalAlignment( SwingConstants.RIGHT );
        JLabel lbl = builder.append( "Potência: ", power );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        lbl.setFont( bold );
        power.setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 10 ) );
        power.setOpaque( true );
        power.setBackground( Color.blue );
        power.setForeground( Color.white );
        power.setFont( bold );
        builder.nextLine();

        handl.setText( "0.0" );
        handl.setHorizontalAlignment( SwingConstants.RIGHT );
        lbl = builder.append( "Dirigibilidade: ", handl );
        lbl.setFont( bold );
        handl.setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 10 ) );
        handl.setOpaque( true );
        handl.setBackground( Color.blue );
        handl.setForeground( Color.white );
        handl.setFont( bold );
        builder.nextLine();

        accel.setText( "0.0" );
        accel.setHorizontalAlignment( SwingConstants.RIGHT );
        lbl = builder.append( "Aceleração: ", accel );
        lbl.setFont( bold );
        accel.setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 10 ) );
        accel.setOpaque( true );
        accel.setBackground( Color.blue );
        accel.setForeground( Color.white );
        accel.setFont( bold );
        builder.nextLine();

        builder.appendSeparator( "Peças: " );
        for ( int c = 0; c < parts.length; c++ ) {
            final int i = c;
            parts[i] = new JSpinner( new SpinnerNumberModel( 1, 1, 9, 1 ) );
            parts[i].addChangeListener( new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    model[i].setLevel( ((Number) parts[i].getValue()).intValue() );
                    updateFields();
                }
            } );
            lbl = builder.append( Car.PARTS_PTBR[i] + ": ", parts[i] );
            lbl.setFont( bold );
            builder.nextLine();
        }

        loadCar.setText( "Carregar Carro Atual" );
        loadCar.setIcon( UIUtils.createImageIcon( "/icons/Car_16.png" ) );
        builder.append( loadCar, 3 );
        loadCar.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCar();
            }
        } );

        JPanel panel = builder.getPanel();
        add( panel, BorderLayout.CENTER );
    }

    private void updateFields() {
        PHA pha = CarPHACalculator.calculateBasePHA( model );
        power.setText( String.format( "%5.1f", pha.getPd() ) );
        handl.setText( String.format( "%5.1f", pha.getHd() ) );
        accel.setText( String.format( "%5.1f", pha.getAd() ) );
    }

    private void updateSpinners() {
        for ( int i = 0; i < parts.length; i++ ) {
            parts[i].setValue( model[i].getLevel() );
        }
    }

    private void loadCar() {
        if ( nextRace != null && nextRace.getCarStart() != null ) {
            CarPart[] p = nextRace.getCarStart().getParts();
            for ( int i = 0; i < model.length; i++ ) {
                model[i] = p[i].clone();
            }
            updateSpinners();
            updateFields();
        }
    }

    @Override
    public void update() {
        nextRace = db.getNextRace();
        loadCar.setEnabled( nextRace != null && nextRace.getCarStart() != null );
        loadCar();
        setDirty( false );
    }

    @Override
    public String getTitle() {
        return "PHA do Carro ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/Car_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/Car_16.png" );
    }

    @Override
    public String getDescription() {
        return "Calculadora do PHA do Carro";
    }

    @Override
    public Category getCategory() {
        return Category.CALC;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_H;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
