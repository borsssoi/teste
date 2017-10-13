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

import org.gproman.db.DataService;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Race;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class DriverOACalcPanel extends UIPluginBase {

    private static final long serialVersionUID = 210232127277861273L;

    private JSpinner          weight           = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          motivation       = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          charisma         = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          stamina          = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          techInsight      = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          experience       = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          aggressiveness   = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          talent           = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );
    private JSpinner          concentration    = new JSpinner( new SpinnerNumberModel( 0, 0, 400, 1 ) );

    private JLabel            zs               = new JLabel();
    private JLabel            overall          = new JLabel();

    private JButton           loadDriver       = new JButton();

    private DriverAttributes  model            = new DriverAttributes();

    private Race              nextRace;

    public DriverOACalcPanel(GPROManFrame gproManFrame,
                             DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BorderLayout() );

        // Building the second column
        FormLayout layout = new FormLayout( "right:120dlu, 4dlu, 40dlu", "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        builder.appendSeparator( "Total e ZS: " );

        overall.setText( "0.0" );
        overall.setHorizontalAlignment( SwingConstants.RIGHT );
        JLabel lbl = builder.append( "Total: ", overall );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        lbl.setFont( bold );
        overall.setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 10) );
        overall.setOpaque( true );
        overall.setBackground( Color.blue );
        overall.setForeground( Color.white );
        overall.setFont( bold );
        builder.nextLine();

        zs.setText( "0.0" );
        zs.setHorizontalAlignment( SwingConstants.RIGHT );
        lbl = builder.append( "Zona de satisfação: ", zs );
        lbl.setFont( bold );
        zs.setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 10) );
        zs.setOpaque( true );
        zs.setBackground( Color.blue );
        zs.setForeground( Color.white );
        zs.setFont( bold );
        builder.nextLine();

        builder.appendSeparator( "Atributos: " );
        concentration.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setConcentration( ((Number) concentration.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Concentração: ", concentration );
        lbl.setFont( bold );
        builder.nextLine();

        talent.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setTalent( ((Number) talent.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Talento: ", talent );
        lbl.setFont( bold );
        builder.nextLine();

        aggressiveness.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setAggressiveness( ((Number) aggressiveness.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Agressividade: ", aggressiveness );
        lbl.setFont( bold );
        builder.nextLine();

        experience.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setExperience( ((Number) experience.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Experiência: ", experience );
        lbl.setFont( bold );
        builder.nextLine();

        techInsight.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setTechInsight( ((Number) techInsight.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Conhecimento Técnico: ", techInsight );
        lbl.setFont( bold );
        builder.nextLine();

        stamina.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setStamina( ((Number) stamina.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Resistência: ", stamina );
        lbl.setFont( bold );
        builder.nextLine();

        charisma.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setCharisma( ((Number) charisma.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Carisma: ", charisma );
        lbl.setFont( bold );
        builder.nextLine();

        motivation.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setMotivation( ((Number) motivation.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Motivação: ", motivation );
        lbl.setFont( bold );
        builder.nextLine();

        weight.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setWeight( ((Number) weight.getValue()).intValue() );
                updateFields();
            }
        } );
        lbl = builder.append( "Peso: ", weight );
        lbl.setFont( bold );
        builder.nextLine();

        loadDriver.setText( "Carregar Piloto Atual" );
        loadDriver.setIcon( UIUtils.createImageIcon( "/icons/helmet_16.png" ) );
        builder.append( loadDriver, 3 );
        loadDriver.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDriver();
            }
        } );

        JPanel panel = builder.getPanel();
        add( panel, BorderLayout.CENTER );
    }

    private void updateFields() {
        overall.setText( String.format( "%5.1f", model.getCalcOverall() ) );
        zs.setText( String.format( "%5.1f", model.getSatisfactionZone() ) );
    }

    private void updateSpinners() {
        concentration.setValue( model.getConcentration() );
        talent.setValue( model.getTalent() );
        aggressiveness.setValue( model.getAggressiveness() );
        experience.setValue( model.getExperience() );
        techInsight.setValue( model.getTechInsight() );
        stamina.setValue( model.getStamina() );
        charisma.setValue( model.getCharisma() );
        motivation.setValue( model.getMotivation() );
        weight.setValue( model.getWeight() );
    }

    private void loadDriver() {
        if ( nextRace != null && nextRace.getDriverStart() != null ) {
            Driver driver = nextRace.getDriverStart();
            DriverAttributes attr = driver.getAttributes();
            model.setConcentration( attr.getConcentration() );
            model.setTalent( attr.getTalent() );
            model.setAggressiveness( attr.getAggressiveness() );
            model.setExperience( attr.getExperience() );
            model.setTechInsight( attr.getTechInsight() );
            model.setStamina( attr.getStamina() );
            model.setCharisma( attr.getCharisma() );
            model.setMotivation( attr.getMotivation() );
            model.setWeight( attr.getWeight() );
            updateSpinners();
            updateFields();
        }
    }

    @Override
    public void update() {
        nextRace = db.getNextRace();
        loadDriver.setEnabled( nextRace != null );
        loadDriver();
        setDirty( false );
    }

    @Override
    public String getTitle() {
        return "OA do Piloto ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/helmet_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/helmet_16.png" );
    }

    @Override
    public String getDescription() {
        return "Calculadora do OA do Piloto";
    }

    @Override
    public Category getCategory() {
        return Category.CALC;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_O;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
