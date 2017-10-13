package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.track.Track;
import org.gproman.ui.comp.ScaleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class SeasonTracksPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger( SeasonTracksPanel.class );

    private static final Color  DARK_GREEN       = new Color( 100, 210, 0 );

    private static final int    RACE_COUNT       = 17;

    private JLabel[]            raceName         = new JLabel[RACE_COUNT];
    private JLabel[]            profile          = new JLabel[RACE_COUNT];
    private ScaleGraph[]        power            = new ScaleGraph[RACE_COUNT];
    private ScaleGraph[]        handling         = new ScaleGraph[RACE_COUNT];
    private ScaleGraph[]        acceleration     = new ScaleGraph[RACE_COUNT];
    private ScaleGraph[]        overtaking       = new ScaleGraph[RACE_COUNT];
    private ScaleGraph[]        fuel             = new ScaleGraph[RACE_COUNT];
    private ScaleGraph[]        tyreWear         = new ScaleGraph[RACE_COUNT];
    private JPanel[]            rows             = new JPanel[RACE_COUNT];

    private JLabel              seasonLbl;

    public SeasonTracksPanel(GPROManFrame frame,
                             DataService db) {
        super( frame, db );
        setLayout( new BorderLayout() );

        String columns = "20dlu, 4dlu, 80dlu, 4dlu, 10dlu, 4dlu, 40dlu,4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu ";
        FormLayout layout = new FormLayout( columns, "" );

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        JComponent separator = builder.appendSeparator( "Temporada" );
        seasonLbl = (JLabel) separator.getComponent( 0 );

        JLabel lbl = builder.append( "" );
        Font bold = lbl.getFont().deriveFont( Font.BOLD );
        lbl = builder.append( "" );
        lbl = builder.append( "" );
        addTitle( builder, "Potência", "/icons/engine_32.png" );
        addTitle( builder, "Dirigibilidade", "/icons/handling_32.png" );
        addTitle( builder, "Aceleração", "/icons/acceleration_32.png" );
        addTitle( builder, "Ultrapassagem", "/icons/overtake_32.png" );
        addTitle( builder, "Consumo de Combustível", "/icons/fuel_32.png" );
        addTitle( builder, "Consumo de Pneus", "/icons/tyres_32.png" );
        lbl.setFont( bold );

        builder.nextLine();

        for ( int i = 0; i < RACE_COUNT; i++ ) {
            DefaultFormBuilder rowBuilder = new DefaultFormBuilder( new FormLayout( columns, "" ) );
            rowBuilder.border( Borders.EMPTY );
            
            raceName[i] = new JLabel();
            profile[i] = new JLabel();
            profile[i].setFont( bold );
            power[i] = new ScaleGraph( 10, 0, 20, 10, DARK_GREEN, Color.RED );
            handling[i] = new ScaleGraph( 10, 0, 20, 10, DARK_GREEN, Color.RED );
            acceleration[i] = new ScaleGraph( 10, 0, 20, 10, DARK_GREEN, Color.RED );
            overtaking[i] = new ScaleGraph( 3, 0, 5, 5, DARK_GREEN, Color.RED );
            fuel[i] = new ScaleGraph( 3, 0, 5, 5, DARK_GREEN, Color.RED );
            tyreWear[i] = new ScaleGraph( 3, 0, 5, 5, DARK_GREEN, Color.RED );
            lbl = rowBuilder.append( String.format( "%02d.", (i + 1) ), raceName[i], profile[i], power[i], handling[i] );
            lbl.setFont( bold );
            rowBuilder.append( acceleration[i], overtaking[i], fuel[i] );
            rowBuilder.append( tyreWear[i] );
            rowBuilder.nextLine();
            
            rows[i] = rowBuilder.build();
            rows[i].setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
            builder.append(rows[i], 17);
            builder.nextLine();
        }

        add( builder.build(), BorderLayout.CENTER );
    }

    private void addTitle(DefaultFormBuilder builder,
                          String tooltip,
                          String iconName) {
        JLabel lbl;
        lbl = new JLabel( UIUtils.createImageIcon( iconName ) );
        lbl.setToolTipText( tooltip );
        lbl.setHorizontalAlignment( SwingConstants.LEFT );
        builder.append( lbl );
    }

    public void updateSeason(Season season) {
        this.seasonLbl.setText( "Temporada " + season.getNumber() + " " );
        Driver driver = null;
        Race nextRace = db.getNextRace();
        if ( nextRace != null ) {
            driver = nextRace.getDriverStart();
        }
        for ( Race race : season.getRaces() ) {
            int i = race.getNumber() - 1;
            if ( i >= 0 ) {
                if ( driver != null && race.getTrack() != null && driver.getFavoriteTracks().contains( race.getTrack() ) ) {
                    raceName[i].setForeground( Color.BLUE );
                    raceName[i].setText( race.getTrack().getName() + "*" );
                    raceName[i].setToolTipText( "Pista favorita do piloto " + driver.getName() );
                } else {
                    raceName[i].setForeground( Color.BLACK );
                    raceName[i].setText( race.getTrack() != null ? race.getTrack().getName() : "Não disponível" );
                    raceName[i].setToolTipText( null );
                }
                if( nextRace != null && race.getNumber() == nextRace.getNumber() ) {
                    rows[i].setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK) );
                } else {
                    rows[i].setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0) );
                }
                Track track = race.getTrack();
                if ( track != null ) {
                    profile[i].setText( determineProfile( track ) );
                    power[i].setValue( track.getPower() );
                    handling[i].setValue( track.getHandling() );
                    acceleration[i].setValue( track.getAcceleration() );
                    overtaking[i].setValue( track.getOvertaking().ordinal() + 1 );
                    fuel[i].setValue( track.getFuelConsumption().ordinal() + 1 );
                    tyreWear[i].setValue( track.getTyreWear().ordinal() + 1 );

                    overtaking[i].setToolTipText( track.getOvertaking().portuguese );
                    fuel[i].setToolTipText( track.getFuelConsumption().portuguese );
                    tyreWear[i].setToolTipText( track.getTyreWear().portuguese );

                    setProfileBorder( track, power[i], handling[i], acceleration[i] );
                }
            }
        }
    }

    private void setProfileBorder(Track track,
                                  ScaleGraph ps,
                                  ScaleGraph hs,
                                  ScaleGraph as) {
        int p = track.getPower();
        int h = track.getHandling();
        int a = track.getAcceleration();
        if ( p > h && p > a ) {
            ps.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        } else if ( h > p && h > a ) {
            hs.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        } else if ( a > p && a > h ) {
            as.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        }
    }

    private String determineProfile(Track track) {
        int p = track.getPower();
        int h = track.getHandling();
        int a = track.getAcceleration();
        if ( p > h && p > a ) {
            return "P";
        } else if ( h > p && h > a ) {
            return "H";
        } else if ( a > p && a > h ) {
            return "A";
        }
        return "B";
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
        return "Pistas ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/track_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/track_16.png" );
    }

    @Override
    public String getDescription() {
        return "Informações sobre as pistas da temporada";
    }

    @Override
    public Category getCategory() {
        return Category.SEASON;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_S;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
