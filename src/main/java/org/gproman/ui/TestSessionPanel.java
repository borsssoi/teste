package org.gproman.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.CarWearCalculator;
import org.gproman.calc.FuelCalculator;
import org.gproman.calc.PracticeHelper;
import org.gproman.db.DataService;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Race;
import org.gproman.model.race.TestPriority;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.track.Track;
import org.gproman.model.track.WearCoefs;
import org.gproman.scrapper.GPROUtil;
import org.gproman.scrapper.TestSessionWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class TestSessionPanel extends UIPluginBase {

    private static final String TEST_SESSION_MASK = "Sessão de Teste ( voltas: %d/100 sints: %d/10 )";

    private static final Logger logger           = LoggerFactory.getLogger( TestSessionPanel.class );

    private static final long   serialVersionUID = 210232127277861273L;
    private static final Color  DARK_GREEN       = new Color( 0, 128, 0 );
    private static final Color  DARK_RED         = new Color( 210, 0, 0 );
    private static final Color  DARK_ORANGE      = new Color( 210, 100, 100 );

    private JLabel              track;
    private JLabel              weather          = new JLabel();
    private JLabel              temp             = new JLabel();
    private JLabel              humidity         = new JLabel();
    private JLabel[]            adj              = new JLabel[6];

    private JSpinner            laps             = new JSpinner( new SpinnerNumberModel( 5, 5, 50, 1 ) );
    private JSpinner            durability       = new JSpinner( new SpinnerNumberModel( 150, 40, 500, 1 ) );
    private JComboBox           priority         = new JComboBox( TestPriority.values() );
    private JLabel              fuel             = new JLabel();
    private JLabel              tyre             = new JLabel();
    private JLabel[]            partLvl          = new JLabel[11];
    private JLabel[]            partWearC        = new JLabel[11];
    private JLabel[]            partWearE        = new JLabel[11];
    private JLabel[]            partWearF        = new JLabel[11];

    private JLabel              lapsLabel;
    private JLabel[]            tlaps            = new JLabel[10];
    private JLabel[]            tbest            = new JLabel[10];
    private JLabel[]            tmean            = new JLabel[10];
    private JLabel[]            tfwg             = new JLabel[10];
    private JLabel[]            trwg             = new JLabel[10];
    private JLabel[]            teng             = new JLabel[10];
    private JLabel[]            tbra             = new JLabel[10];
    private JLabel[]            tgea             = new JLabel[10];
    private JLabel[]            tsus             = new JLabel[10];
    private JLabel[]            ttyre            = new JLabel[10];
    private JLabel[]            tfuel            = new JLabel[10];
    private JLabel[]            ttyreEnd         = new JLabel[10];
    private JLabel[]            tfuelEnd         = new JLabel[10];
    private JLabel[]            tprio            = new JLabel[10];

    private JButton             refresh          = new JButton();

    private Race                nextRace;
    private PracticeHelper      helper;
    private CarWearCalculator   wearCalc;

    private Font                bold;
    private Font                plain;

    public TestSessionPanel(GPROManFrame gproManFrame,
                            DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        add( buildInfoPanel() );
        add( buildSimulationPanel() );
        add( buildLapsPanel() );
    }

    private JPanel buildInfoPanel() {
        FormLayout layout = new FormLayout( "42dlu, 4dlu, 42dlu, 4dlu, 42dlu, 10dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
                                            "" );

        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        //        builder.appendSeparator( "Sessão de teste: " );
        //        builder.nextLine();

        track = new JLabel( "Pista" );
        plain = track.getFont();
        bold = plain.deriveFont( Font.BOLD );

        UIUtils.createColumnTitle( builder, track, 5, Color.BLACK, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Ajustes Sugeridos" ), 11, Color.BLACK, Color.WHITE, bold );
        builder.nextLine();

        JLabel lbl = null;
        String[] titles = new String[]{"Clima", "Temp", "Humidade", "Asa D.", "Asa T.", "Motor", "Freio", "Câmbio", "Susp"};
        for ( int i = 0; i < titles.length; i++ ) {
            lbl = builder.append( titles[i] );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.CENTER );
        }
        builder.nextLine();

        weather.setPreferredSize( new Dimension( 32, 32 ) );
        addLabel( builder, weather, SwingConstants.CENTER );
        addLabel( builder, temp, SwingConstants.CENTER );
        addLabel( builder, humidity, SwingConstants.CENTER );

        for ( int i = 0; i < adj.length; i++ ) {
            adj[i] = new JLabel();
            adj[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( adj[i] );
        }

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getMaximumSize().width, panel.getPreferredSize().height ) );
        return panel;
    }

    private JPanel buildSimulationPanel() {
        FormLayout layout = new FormLayout( "30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, " +
                                            "4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 30dlu",
                                            "" );

        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        JLabel lbl = new JLabel( "Planejamento do Stint" );
        UIUtils.createColumnTitle( builder, lbl, 23, Color.BLACK, Color.WHITE, bold );
        builder.nextLine();

        lbl = new JLabel( "Voltas:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( lbl, 3 );
        builder.append( laps );
        laps.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateStint();
            }
        } );
        lbl = new JLabel( "Durabilidade Pneu (km):" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( lbl, 5 );
        durability.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTyreWear();
            }
        } );
        builder.append( durability );
        lbl = new JLabel( "Prioridade:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( lbl, 3 );
        priority.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStint();
            }
        } );
        builder.append( priority, 5 );
        priority.setRenderer( new PriorityComboBoxRenderer( laps.getPreferredSize() ) );
        builder.nextLine();

        builder.appendSeparator();
        builder.nextLine();

        builder.append( "" );
        for ( int i = 0; i < Car.MNEM_PTBR.length; i++ ) {
            UIUtils.createColumnTitle( builder, new JLabel( Car.MNEM_PTBR[i] ), 1, null, null, bold );
        }
        builder.nextLine();

        lbl = builder.append( "Nível:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < partLvl.length; i++ ) {
            partLvl[i] = new JLabel();
            partLvl[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( partLvl[i] );
        }
        builder.nextLine();

        lbl = builder.append( "Atual:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < partWearC.length; i++ ) {
            partWearC[i] = new JLabel();
            partWearC[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( partWearC[i] );
        }
        builder.nextLine();

        lbl = builder.append( "Desg:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < partWearE.length; i++ ) {
            partWearE[i] = new JLabel();
            partWearE[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( partWearE[i] );
        }
        builder.nextLine();

        lbl = builder.append( "Final:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < partWearF.length; i++ ) {
            partWearF[i] = new JLabel();
            partWearF[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( partWearF[i] );
        }
        builder.nextLine();

        lbl = new JLabel( "Combustível:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( lbl, 3 );
        fuel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.BLACK ), BorderFactory.createEmptyBorder( 0, 4, 0, 4 ) ) );
        fuel.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( fuel );
        lbl = new JLabel( "Condição final do pneu:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( lbl, 5 );
        tyre.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.BLACK ), BorderFactory.createEmptyBorder( 0, 4, 0, 4 ) ) );
        tyre.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.append( tyre );

        refresh.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchPracticeLaps();
            }
        } );
        refresh.setText( "Atualizar" );
        refresh.setIcon( UIUtils.createImageIcon( "/icons/FetchData_16.png" ) );
        refresh.setEnabled( false );
        builder.add( refresh, CC.xyw( 19, builder.getRow(), 5 ) );

        builder.nextLine();
        lbl = new JLabel("IMPORTANTE: clique no botão atualizar antes de começar os testes e após cada stint.");
        lbl.setForeground( DARK_RED );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setFont( lbl.getFont().deriveFont( Font.BOLD | Font.ITALIC ) );
        builder.append( lbl, 23 );

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getMaximumSize().width, panel.getPreferredSize().height ) );
        return panel;
    }

    private void addLabel(DefaultFormBuilder builder,
                          JLabel lbl,
                          int align) {
        builder.append( lbl );
        lbl.setHorizontalAlignment( align );
    }

    private JPanel buildLapsPanel() {
        FormLayout layout = new FormLayout( "14dlu, 4dlu, 20dlu, 4dlu, 35dlu, 4dlu, 35dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, " +
                                            "20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 48dlu",
                                            "" );

        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        JLabel lbl = new JLabel( String.format( TEST_SESSION_MASK, 0, 0 ) );
        plain = lbl.getFont();
        bold = plain.deriveFont( Font.BOLD );
        lapsLabel = lbl;

        UIUtils.createColumnTitle( builder, lbl, 29, Color.BLACK, Color.WHITE, bold );
        builder.nextLine();

        UIUtils.createColumnTitle( builder, new JLabel( "Voltas" ), 7, Color.DARK_GRAY, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Ajustes" ), 11, Color.DARK_GRAY, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Início" ), 3, Color.DARK_GRAY, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Fim" ), 3, Color.DARK_GRAY, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Prioridade" ), 1, Color.DARK_GRAY, Color.WHITE, bold );
        builder.nextLine();

        String[] titles = new String[]{"", "#/#", "Melhor", "Média", "AsD", "AsT", "Mot", "Fre", "Câm", "Sus", "Pneu", "Com", "Pneu", "Com", ""};
        for ( int i = 0; i < titles.length; i++ ) {
            lbl = builder.append( titles[i] );
            lbl.setFont( bold );
            if ( i == titles.length - 1 ) {
                lbl.setHorizontalAlignment( SwingConstants.LEFT );
            } else {
                lbl.setHorizontalAlignment( SwingConstants.CENTER );
            }
        }
        builder.nextLine();

        for ( int i = 0; i < tbest.length; i++ ) {
            lbl = builder.append( String.valueOf( i + 1 ) );
            lbl.setHorizontalAlignment( SwingConstants.CENTER );
            lbl.setFont( bold );
            if ( i % 2 != 0 ) {
                lbl.setOpaque( true );
                lbl.setBackground( Color.LIGHT_GRAY );
            }

            addLabel( builder, i, tlaps );
            addLabel( builder, i, tbest );
            addLabel( builder, i, tmean );
            addLabel( builder, i, tfwg );
            addLabel( builder, i, trwg );
            addLabel( builder, i, teng );
            addLabel( builder, i, tbra );
            addLabel( builder, i, tgea );
            addLabel( builder, i, tsus );
            addLabel( builder, i, ttyre );
            addLabel( builder, i, tfuel );
            addLabel( builder, i, ttyreEnd );
            addLabel( builder, i, tfuelEnd );
            addLabel( builder, i, tprio );
            builder.nextLine();
        }

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getMaximumSize().width, panel.getPreferredSize().height ) );
        return panel;
    }

    private void addLabel(DefaultFormBuilder builder,
                          int i,
                          JLabel[] lbla) {
        lbla[i] = new JLabel();
//        if ( lbla == tprio ) {
//            lbla[i].setHorizontalAlignment( SwingConstants.LEFT );
//        } else {
            lbla[i].setHorizontalAlignment( SwingConstants.CENTER );
//        }
        if ( i % 2 != 0 ) {
            lbla[i].setOpaque( true );
            lbla[i].setBackground( Color.LIGHT_GRAY );
        }
        builder.append( lbla[i] );
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            nextRace = db.getNextRace();
            if ( nextRace != null ) {
                TestSession session = nextRace.getTestSession();
                if( session != null ) {
                    if ( helper == null ) {
                        helper = new PracticeHelper();
                    }
                    Track testTrack = session != null ? session.getTrack() : null;
                    helper.setTrack( testTrack );
                    
                    Forecast[] forecast = new Forecast[6]; 
                    System.arraycopy( nextRace.getForecast(), 0, forecast, 0, forecast.length );
                    forecast[0] = new Forecast( session.getWeather(), session.getTemperature(), session.getTemperature(), session.getHumidity(), session.getHumidity(), 0, 0 );
                    helper.setForecast( forecast );
                    helper.setCar( session.getCurrentCar() );
                    Driver driver = nextRace.getDriverStart();
                    helper.setDriver( driver );
                    helper.setTd( nextRace.getTDStart() );
                    helper.setPractice( nextRace.getPractice() );
                    helper.update();

                    DriverWearWeight dww = db.getDriverAttributesWearWeight();
                    WearCoefs coefs = db.getWearCoefs();
                    Car car = session != null ? session.getCurrentCar() : null;
                    if ( testTrack != null && driver != null && dww != null && coefs != null && session != null && car != null ) {
                        wearCalc = new CarWearCalculator( dww,
                                                          coefs,
                                                          driver,
                                                          testTrack,
                                                          car );
                    } else {
                        wearCalc = null;
                    }

                    updateTest( nextRace );
                    refresh.setEnabled( true );
                } else {
                    cleanupGUI();
                }
            } else {
                refresh.setEnabled( false );
                cleanupGUI();
            }
            setDirty( false );
        }
    }

    private void updateTest(Race nextRace) {
        TestSession ts = nextRace.getTestSession();
        if ( ts != null ) {
            lapsLabel.setText( String.format( TEST_SESSION_MASK, ts.getLapsDone(), ts.getStintsDone() ) );
            
            if ( ts.getTrack() != null ) {
                track.setText( ts.getTrack().getName() );
            } else {
                track.setText( "Pista" );
            }
            if ( ts.getWeather() != null ) {
                weather.setIcon( ts.getWeather().getIcon() );
                weather.setToolTipText( ts.getWeather().getToolTip() );
            }
            if ( ts.getTemperature() != null ) {
                temp.setText( ts.getTemperature() + "°C" );
            }
            if ( ts.getHumidity() != null ) {
                humidity.setText( ts.getHumidity() + "%" );
            }
            if ( helper != null ) {
                int[] is = helper.getInitialSetup();
                int[] wings = helper.calculateWings( is[0], is[1], ts.getTrack().getWingSplit() != null ? ts.getTrack().getWingSplit() : 0 );
                is[0] = wings[0];
                is[1] = wings[1];
                for ( int i = 0; i < adj.length; i++ ) {
                    adj[i].setText( String.valueOf( is[i] ) );
                }
            }
            if ( ts.getCurrentCar() != null ) {
                CarPart[] parts = ts.getCurrentCar().getParts();
                for ( int i = 0; i < Car.PARTS_COUNT; i++ ) {
                    partLvl[i].setText( String.valueOf( parts[i].getLevel() ) );
                    setPercent( parts[i].getWear(), partWearC[i] );
                }
            }
            updateStint();

            int i = 0;
            for ( TestStint stint : ts.getStints() ) {
                tlaps[i].setText( String.format( "%d/%d", stint.getLapsDone(), stint.getLapsPlanned() ) );
                tbest[i].setText( formatTime( stint.getBestTime() ) );
                tmean[i].setText( formatTime( stint.getMeanTime() ) );
                tfwg[i].setText( String.valueOf( stint.getSettings().getFrontWing() ) );
                trwg[i].setText( String.valueOf( stint.getSettings().getRearWing() ) );
                teng[i].setText( String.valueOf( stint.getSettings().getEngine() ) );
                tbra[i].setText( String.valueOf( stint.getSettings().getBrakes() ) );
                tgea[i].setText( String.valueOf( stint.getSettings().getGear() ) );
                tsus[i].setText( String.valueOf( stint.getSettings().getSuspension() ) );
                ttyre[i].setText( stint.getSettings().getTyre().symbol );
                tfuel[i].setText( String.format( "%d", stint.getFuelStart() ) );
                ttyreEnd[i].setText( String.format( "%d%%", stint.getTyresEnd() ) );
                tfuelEnd[i].setText( String.format( "%d", stint.getFuelEnd() ) );
                tprio[i].setText( stint.getPriority().mnemPtBr );
                i++;
            }
            for ( ; i< tlaps.length; i++ ) {
                // cleanup the rest of the lap fields in the gui
                tlaps[i].setText( "" );
                tbest[i].setText( "" );
                tmean[i].setText( "" );
                tfwg[i].setText( "" );
                trwg[i].setText( "" );
                teng[i].setText( "" );
                tbra[i].setText( "" );
                tgea[i].setText( "" );
                tsus[i].setText( "" );
                ttyre[i].setText( "" );
                tfuel[i].setText( "" );
                ttyreEnd[i].setText( "" );
                tfuelEnd[i].setText( "" );
                tprio[i].setText( "" );
                tprio[i].setToolTipText( "" );
            }
        } else {
            cleanupGUI();
        }
    }

    private void updateStint() {
        TestSession ts = nextRace != null ? nextRace.getTestSession() : null;
        if ( ts != null && ts.getTrack() != null && ts.getCurrentCar() != null && ts.getWeather() != null ) {
            int stintLaps = ((Number) laps.getValue()).intValue();

            // Fuel Consumption
            double fuelPerLap = FuelCalculator.predictConsumptionPerLap( ts.getWeather(),
                                                                         ts.getCurrentCar().getEngine().getLevel(),
                                                                         ts.getCurrentCar().getElectronics().getLevel(),
                                                                         ts.getTrack() );
            double fuelConsumption = stintLaps * fuelPerLap + 2; // 2 extra litres for safety
            this.fuel.setText( String.format( "%3.0f lts", Math.ceil( fuelConsumption ) ) );
            if ( fuelConsumption >= 170 ) {
                this.fuel.setForeground( DARK_RED );
            } else {
                this.fuel.setForeground( Color.BLACK );
            }

            updateTyreWear();

            if ( wearCalc != null ) {
                BigDecimal risk = ((TestPriority) this.priority.getSelectedItem()).risk;
                CarPart[] parts = wearCalc.predictWearForTestLaps( risk, stintLaps );
                for ( int i = 0; i < parts.length; i++ ) {
                    setPercent( Math.ceil( parts[i].getWear() ), partWearE[i] );
                    setPercent( Math.ceil( parts[i].getWear() + ts.getCurrentCar().getParts()[i].getWear() ), partWearF[i] );
                }
            }

        } else {
            this.fuel.setText( "N/D" );
            this.tyre.setText( "N/D" );
            this.fuel.setForeground( Color.BLACK );
            this.tyre.setForeground( Color.BLACK );
        }
    }

    private void updateTyreWear() {
        TestSession ts = nextRace != null ? nextRace.getTestSession() : null;
        if ( ts != null && ts.getTrack() != null && ts.getCurrentCar() != null && ts.getWeather() != null ) {
            int stintLaps = ((Number) laps.getValue()).intValue();

            // tyre wear
            int durability = ((Number) this.durability.getValue()).intValue();
            double distance = stintLaps * ts.getTrack().getLapDistance();
            double result = Math.floor( (1 - distance / durability) * 100 );
            this.tyre.setText( String.format( "%2.0f%%", result ) );
            if ( result <= 4 ) {
                this.tyre.setBackground( DARK_RED );
                this.tyre.setOpaque( true );
                this.tyre.setForeground( Color.WHITE );
            } else if ( result <= 10 ) {
                this.tyre.setForeground( DARK_RED );
                this.tyre.setOpaque( false );
            } else if ( result <= 20 ) {
                this.tyre.setForeground( DARK_ORANGE );
                this.tyre.setOpaque( false );
            } else {
                this.tyre.setForeground( Color.BLACK );
                this.tyre.setOpaque( false );
            }
        }
    }

    private void setPercent(double wear,
                            JLabel lbl) {
        lbl.setText( String.format( "%3.0f%%", wear ) );
        if ( wear >= 90 ) {
            lbl.setForeground( DARK_RED );
        } else if ( wear >= 80 ) {
            lbl.setForeground( DARK_ORANGE );
        } else if ( wear >= 30 ) {
            lbl.setForeground( Color.black );
        } else {
            lbl.setForeground( DARK_GREEN );
        }
    }

    private void cleanupGUI() {
        track.setText( "" );
        weather.setText( "" );
        temp.setText( "" );
        humidity.setText( "" );
        for ( int i = 0; i < tlaps.length; i++ ) {
            tlaps[i].setText( "" );
            tbest[i].setText( "" );
            tmean[i].setText( "" );
            tfwg[i].setText( "" );
            trwg[i].setText( "" );
            teng[i].setText( "" );
            tbra[i].setText( "" );
            tgea[i].setText( "" );
            tsus[i].setText( "" );
            ttyre[i].setText( "" );
            tfuel[i].setText( "" );
            ttyreEnd[i].setText( "" );
            tfuelEnd[i].setText( "" );
            tprio[i].setText( "" );
            tprio[i].setToolTipText( "" );
        }
    }

    private void fetchPracticeLaps() {
        if ( nextRace != null ) {
            refresh.setEnabled( false );
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    try {
                        logger.info( "Refreshing test session data..." );
                        GPROUtil gpro = gproManFrame.getGPRO();
                        HtmlPage page = gpro.getPage( gproManFrame.getConfiguration().getGproUrl()+TestSessionWorker.TEST_SESSION_URL_SUFFIX );
                        TestSessionWorker worker = new TestSessionWorker( page, db );
                        TestSession ts = worker.call();
                        logger.info( "Test session data retrieved... saving into the database..." );
                        // requires merge
                        if ( nextRace.getTestSession() != null ) {
                            nextRace.getTestSession().merge( ts );
                        } else {
                            nextRace.setTestSession( ts );
                        }
                        logger.info( "Practice data successfully updated." );
                    } catch ( Exception e ) {
                        logger.error( "Error trying to refresh practice laps...", e );
                    }
                    return null;
                }

                @Override
                public void done() {
                    db.store( db.getManager().getName(), nextRace );
                    setDirty( true );
                    update();
                    setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    refresh.setEnabled( true );
                }
            };
            worker.execute();
        }
    }

    private String formatTime(Integer time) {
        int seg = time / 60000;
        int sec = (time % 60000) / 1000;
        int ms = time % 1000;
        return String.format( "%d:%02d.%03d",
                              seg,
                              sec,
                              ms );
    }

    @Override
    public String getTitle() {
        return "Testes ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/analysis_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/analysis_16.png" );
    }

    @Override
    public String getDescription() {
        return "Sessões de testes";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_S;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    public static class PriorityComboBoxRenderer extends JLabel
            implements
            ListCellRenderer {
        private static final long serialVersionUID = 1545037048456885661L;

        public PriorityComboBoxRenderer(Dimension size) {
            setOpaque( true );
            setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
            setPreferredSize( new Dimension( size.width, size.height - 6 ) );
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            } else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }
            if ( value != null ) {
                TestPriority tp = (TestPriority) value;
                setText( tp.mnemPtBr );
            } else {
                setText( "" );
            }
            return this;
        }
    }
}
