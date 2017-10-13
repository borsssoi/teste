package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Race;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.season.Season;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class SeasonDataPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger( SeasonDataPanel.class );

    private static final int    RACE_COUNT       = 17;

    private JLabel[]            raceName         = new JLabel[RACE_COUNT];
    private JLabel[]            practice         = new JLabel[RACE_COUNT];
    private JLabel[]            qualify1         = new JLabel[RACE_COUNT];
    private JLabel[]            qualify2         = new JLabel[RACE_COUNT];
    private JLabel[]            setup            = new JLabel[RACE_COUNT];
    private JLabel[]            telemetry        = new JLabel[RACE_COUNT];

    private final ImageIcon     unavailable      = UIUtils.createImageIcon( "/icons/cancel_16.png" ); ;
    private final ImageIcon     available        = UIUtils.createImageIcon( "/icons/check_16.png" );

    private JLabel seasonLbl;

    public SeasonDataPanel(GPROManFrame frame,
                       DataService db) {
        super( frame, db );
        setLayout( new BorderLayout() );

        FormLayout layout = new FormLayout( "20dlu, 4dlu, 100dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 50dlu ", // 1st major column
        "" );

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        JComponent separator = builder.appendSeparator( "Temporada" );
        seasonLbl = (JLabel) separator.getComponent( 0 );

        JLabel lbl = builder.append( "" );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        int height = (int) ( lbl.getFontMetrics( lbl.getFont() ).getHeight() * 1.4 );
        
        lbl = builder.append( "" );
        addTitle( builder, bold, height, "Treino" );
        addTitle( builder, bold, height, "Q1" );
        addTitle( builder, bold, height, "Q2" );
        addTitle( builder, bold, height, "Setup" );
        addTitle( builder, bold, height, "Telemetria" );
        builder.nextLine();
        
        for ( int i = 0; i < RACE_COUNT; i++ ) {
            raceName[i] = new JLabel();
            raceName[i].setMinimumSize( new Dimension( raceName[i].getMinimumSize().width, height ) );
            raceName[i].setPreferredSize( raceName[i].getMinimumSize() );
            practice[i] = new JLabel();
            practice[i].setMinimumSize( new Dimension( practice[i].getMinimumSize().width, height ) );
            practice[i].setPreferredSize( practice[i].getMinimumSize() );
            qualify1[i] = new JLabel();
            qualify1[i].setMinimumSize( new Dimension( qualify1[i].getMinimumSize().width, height ) );
            qualify1[i].setPreferredSize( qualify1[i].getMinimumSize() );
            qualify2[i] = new JLabel();
            qualify2[i].setMinimumSize( new Dimension( qualify2[i].getMinimumSize().width, height ) );
            qualify2[i].setPreferredSize( qualify2[i].getMinimumSize() );
            setup[i] = new JLabel();
            setup[i].setMinimumSize( new Dimension( setup[i].getMinimumSize().width, height ) );
            setup[i].setPreferredSize( setup[i].getMinimumSize() );
            telemetry[i] = new JLabel();
            telemetry[i].setMinimumSize( new Dimension( telemetry[i].getMinimumSize().width, height ) );
            telemetry[i].setPreferredSize( telemetry[i].getMinimumSize() );
            lbl = builder.append( String.format( "%02d.", (i + 1) ), raceName[i], practice[i], qualify1[i] );
            lbl.setFont( bold );
            builder.append( qualify2[i], setup[i], telemetry[i] );
            builder.nextLine();
        }

        add( builder.build(), BorderLayout.CENTER );
    }

    private void addTitle(DefaultFormBuilder builder,
                          Font bold,
                          int height,
                          String string) {
        JLabel lbl;
        lbl = builder.append( string );
        lbl.setFont( bold );
        lbl.setMinimumSize( new Dimension( lbl.getMinimumSize().width, height ) );
        lbl.setPreferredSize( lbl.getMinimumSize() );
    }

    public void updateSeason(Season season) {
        this.seasonLbl.setText( "Temporada "+season.getNumber()+" " );
        Driver driver = null;
        if( db.getNextRace() != null ) {
            driver = db.getNextRace().getDriverStart();
        }
        for ( Race race : season.getRaces() ) {
            int i = race.getNumber() - 1;
            if ( i >= 0 ) {
                if( driver != null && race.getTrack() != null && driver.getFavoriteTracks().contains( race.getTrack() ) ) {
                    raceName[i].setForeground( Color.BLUE );
                    raceName[i].setText( race.getTrack().getName()+"*" );
                    raceName[i].setToolTipText( "Pista favorita do piloto "+driver.getName() );
                } else {
                    raceName[i].setForeground( Color.BLACK );
                    raceName[i].setText( race.getTrack() != null ? race.getTrack().getName() : "Não disponível" );
                    raceName[i].setToolTipText( null );
                }
                practice[i].setIcon( race.getStatus().isPractice() ? available : unavailable );
                qualify1[i].setIcon( race.getStatus().isQualify1() ? available : unavailable );
                qualify2[i].setIcon( race.getStatus().isQualify2() ? available : unavailable );
                setup[i].setIcon( race.getStatus().isSetup() ? available : unavailable );
                telemetry[i].setIcon( race.getStatus().isTelemetry() ? available : unavailable );
            }
        }
        checkStartingTires();
    }

    private void checkStartingTires() {
        Race nextRace = db.getNextRace();
        if( nextRace != null && nextRace.getStatus().isSetup() && !nextRace.getStatus().isTelemetry() ) {
            if( nextRace.getTyreAtStart().equals(Tyre.RAIN) && ! nextRace.getForecast()[1].getWeather().equals(Weather.RAIN) ) {
                JOptionPane.showMessageDialog(gproManFrame.getFrame(), 
                                              "<html><center><font size='4'>Você está largando com pneus de chuva, mas a largada<br/>"
                                              + "será com pista seca. Verifique se esta é mesmo sua<br/>"
                                              + "estratégia para esta corrida.</font></center></html>",
                                              "Aviso: Pneu da largada",
                                              JOptionPane.WARNING_MESSAGE,
                                              UIUtils.createImageIcon("/icons/carrain.jpg"));
            } else if( !nextRace.getTyreAtStart().equals( Tyre.RAIN ) && nextRace.getForecast()[1].getWeather().equals(Weather.RAIN) ) {
                JOptionPane.showMessageDialog(gproManFrame.getFrame(), 
                        "<html><center><font size='4'>Você está largando com pneus lisos, mas a largada<br/>"
                        + "será com pista molhada. Verifique se esta é mesmo sua<br/>"
                        + "estratégia para esta corrida.</font></center></html>",
                        "Aviso: Pneu da largada",
                        JOptionPane.WARNING_MESSAGE,
                        UIUtils.createImageIcon("/icons/carrain.jpg"));
            }
        }
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            Manager manager = db.getManager();
            if( manager != null ) {
                Season season = db.getCurrentSeason(manager.getName());
                if ( season != null ) {
                    logger.info( "Updating screen with season data for season '" + season.getNumber() + "'" );
                    updateSeason( season );
                }
            }
            setDirty( false );
        }
    }

    @Override
    public String getTitle() {
        return "Dados ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/data_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/data_16.png" );
    }

    @Override
    public String getDescription() {
        return "Dados baixados";
    }

    @Override
    public Category getCategory() {
        return Category.SEASON;
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
