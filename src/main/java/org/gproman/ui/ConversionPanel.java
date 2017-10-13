package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.ConversionCalculator;
import org.gproman.db.DataService;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Qualify;
import org.gproman.model.race.Race;
import org.gproman.model.race.Weather;
import org.gproman.scrapper.GPROUtil;
import org.gproman.scrapper.Q1Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class ConversionPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;
    private static final Logger logger           = LoggerFactory.getLogger( ConversionPanel.class );

    private JLabel[]            pTemp            = new JLabel[6];
    private JLabel[]            pWeather         = new JLabel[12];
    private JLabel[][]          pPart            = new JLabel[6][];
    private JLabel[][]          aPart            = new JLabel[6][];
    private JSpinner            sTemp            = new JSpinner( new SpinnerNumberModel( 25, 0, 50, 1 ) );
    private JButton             refresh          = new JButton();

    private Race                nextRace;

    private Font                bold;

    public ConversionPanel(GPROManFrame gproManFrame,
                           DataService dataService) {
        super( gproManFrame,
               dataService );
        setLayout( new BorderLayout() );
        add( buildConversionPanel(), BorderLayout.CENTER );
    }

    private JPanel buildConversionPanel() {
        FormLayout layout = new FormLayout( "44dlu, 4dlu, 26dlu, 4dlu, 26dlu, 4dlu, " + // part, q1, q2
        "26dlu, 4dlu, 26dlu, 4dlu, 26dlu, 4dlu, 26dlu, 4dlu, " + // r1, r2
        "26dlu, 4dlu, 26dlu, 4dlu, 26dlu, 4dlu, 26dlu, 4dlu, " + // r3, r4
        "26dlu, 4dlu, 26dlu ", // avg
        "" );

        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        JLabel lbl = new JLabel( "" );
        bold = lbl.getFont().deriveFont( Font.BOLD );
        builder.append( lbl, 5 );
        createColumnTitle( builder, "Corrida", 15, Color.BLACK );
        builder.nextLine();

        String[] titles = new String[]{"", "Q1", "Q2", "R.1", "R.2", "R.3", "Média", "Arbitrária"};
        for ( int i = 0; i < titles.length; i++ ) {
            createColumnTitle( builder, titles[i], i <= 2 ? 1 : 3, Color.DARK_GRAY );
        }
        builder.nextLine();

        lbl = builder.append( "Temp.:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < pTemp.length; i++ ) {
            pTemp[i] = new JLabel();
            pTemp[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( pTemp[i], i < 2 ? 1 : 3 );
        }
        builder.append( sTemp, 3 );
        sTemp.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateCustomTemp();
            }
        } );
        builder.nextLine();

        lbl = builder.append( "Clima:" );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        for ( int i = 0; i < pWeather.length; i++ ) {
            pWeather[i] = new JLabel();
            pWeather[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( pWeather[i] );
        }
        builder.nextLine();

        builder.appendSeparator();
        final String[] parts = new String[]{"Asa D:", "Asa T:", "Motor:", "Freio:", "Câmbio:", "Susp.:"};
        for ( int i = 0; i < pPart.length; i++ ) {
            lbl = builder.append( parts[i] );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.RIGHT );
            if ( i % 2 != 0 ) {
                lbl.setOpaque( true );
                lbl.setBackground( Color.LIGHT_GRAY );
            }

            pPart[i] = new JLabel[12];
            for ( int j = 0; j < pPart[i].length; j++ ) {
                pPart[i][j] = new JLabel();
                pPart[i][j].setHorizontalAlignment( SwingConstants.CENTER );
                if ( i % 2 != 0 ) {
                    pPart[i][j].setOpaque( true );
                    pPart[i][j].setBackground( Color.LIGHT_GRAY );
                }
                builder.append( pPart[i][j] );
            }
            builder.nextLine();
        }
        builder.appendSeparator();

        for ( int i = 0; i < aPart.length; i++ ) {
            lbl = builder.append( parts[i] );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.RIGHT );
            if ( i % 2 != 0 ) {
                lbl.setOpaque( true );
                lbl.setBackground( Color.LIGHT_GRAY );
            }

            aPart[i] = new JLabel[12];
            for ( int j = 0; j < aPart[i].length; j++ ) {
                aPart[i][j] = new JLabel();
                aPart[i][j].setHorizontalAlignment( SwingConstants.CENTER );
                if ( i % 2 != 0 ) {
                    aPart[i][j].setOpaque( true );
                    aPart[i][j].setBackground( Color.LIGHT_GRAY );
                }
                builder.append( aPart[i][j] );
            }
            builder.nextLine();
        }

        builder.appendSeparator();
        
        builder.append( "" );
        refresh.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchQ1();
            }
        } );
        refresh.setText( "Atualizar Q1" );
        refresh.setIcon( UIUtils.createImageIcon( "/icons/FetchData_16.png" ) );
        refresh.setEnabled( true );
        builder.add( refresh, CC.xyw( 19, builder.getRow(), 7 ) );

        JPanel panel = builder.getPanel();
        panel.setMaximumSize( new Dimension( panel.getMaximumSize().width, panel.getPreferredSize().height ) );
        return panel;
    }

    private void createColumnTitle(DefaultFormBuilder builder,
                                   String title,
                                   int span,
                                   Color bg) {
        JLabel lbl = new JLabel( title );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setOpaque( true );
        lbl.setBackground( bg );
        lbl.setForeground( Color.white );
        builder.append( lbl, span );
    }

    private String formatValue(int min,
                               int max,
                               String suffix) {
        return String.format( "%d%s", (min + max) / 2, suffix );
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            nextRace = db.getNextRace();
            if ( nextRace != null ) {
                updateConversions();
            }
            setDirty( false );
        }
    }

    private void updateConversions() {
        Forecast[] forecast = nextRace.getForecast();
        if ( forecast[0] != null && forecast[1] != null &&
             forecast[2] != null && forecast[3] != null &&
             forecast[4] != null && forecast[5] != null && 
             nextRace.getTrack() != null && nextRace.getCarStart() != null && nextRace.getDriverStart() != null ) {
            // update temperature
            for ( int i = 0; i < 5; i++ ) {
                pTemp[i].setText( formatValue( forecast[i].getTempMin(), forecast[i].getTempMax(), "°C" ) );
            }
            int avg = getAverageRaceTemp( forecast );
            pTemp[5].setText( String.format( "%d°C", avg ) );
            sTemp.setValue( avg );

            // update weather
            pWeather[0].setIcon( forecast[0].getWeather().getIcon() );
            pWeather[0].setToolTipText( forecast[0].getWeather().getToolTip() );
            pWeather[1].setIcon( forecast[1].getWeather().getIcon() );
            pWeather[1].setToolTipText( forecast[1].getWeather().getToolTip() );
            for ( int i = 2; i < pWeather.length; i += 2 ) {
                pWeather[i].setIcon( Weather.SUNNY.getIcon() );
                pWeather[i].setToolTipText( "Seco" );
                pWeather[i + 1].setIcon( Weather.RAIN.getIcon() );
                pWeather[i + 1].setToolTipText( "Chuva" );
            }

            // calculate conversions
            final int[][] conv = ConversionCalculator.convertAll( nextRace.getTrack(),
                                                                   nextRace.getCarStart(),
                                                                   nextRace.getDriverStart(),
                                                                   forecast, avg );
            for ( int i = 0; i < conv.length; i++ ) {
                pPart[i][0].setText( "→" );
                for ( int j = 0; j < conv[i].length; j++ ) {
                    pPart[i][j + 1].setText( String.format( "%+3d", conv[i][j] ) );
                }
            }

            // calculate adjustments
            Qualify q1 = nextRace.getQualify1();
            if ( q1 != null && q1.getLap() != null && q1.getLap().getSettings() != null ) {
                CarSettings s = q1.getLap().getSettings();
                int[] settings = new int[]{s.getFrontWing(), s.getRearWing(), s.getEngine(), s.getBrakes(), s.getGear(), s.getSuspension()};
                // wings have to be adjusted in pairs
                aPart[0][0].setText( String.format( "%3d", settings[0] ) );
                aPart[1][0].setText( String.format( "%3d", settings[1] ) );
                for ( int j = 0; j < conv[0].length; j++ ) {
                    int adjFw = settings[0] + conv[0][j];
                    int adjRw = settings[1] + conv[1][j];
                    int mean = (adjFw + adjRw)/2;
                    int fw = Math.max( Math.min( adjFw , 999 ), 0 );
                    int rw = Math.max( Math.min( adjRw , 999 ), 0 );
                    int delta = ( mean - ((fw+rw)/2) ) * 2;
                    if( fw == 999 || fw == 0 ) {
                        rw = Math.max( Math.min( 999, rw + delta ), 0 );
                    } else {
                        fw = Math.max( Math.min( 999, fw + delta ), 0 );
                    }
                    aPart[0][j + 1].setText( String.format( "%3d", fw ) );
                    aPart[1][j + 1].setText( String.format( "%3d", rw ) );
                }
                // other components can be adjusted individually
                for ( int i = 2; i < conv.length; i++ ) {
                    aPart[i][0].setText( String.format( "%3d", settings[i] ) );
                    for ( int j = 0; j < conv[i].length; j++ ) {
                        int adj = Math.max( Math.min( settings[i] + conv[i][j], 999 ), 0 );
                        aPart[i][j + 1].setText( String.format( "%3d", adj ) );
                    }
                }
            } else {
                for( int i = 0; i < aPart.length; i++ ) {
                    for( int j = 0; j < aPart[i].length; j++ ) {
                        aPart[i][j].setText( "" );
                    }
                }
            }
        }
    }

    private void updateCustomTemp() {
        Forecast fc = nextRace.getForecast()[0];
        if ( nextRace != null && fc != null && nextRace.getTrack() != null && nextRace.getCarStart() != null && nextRace.getDriverStart() != null) {
            int customTemp = ((Number) sTemp.getValue()).intValue();
            int[][] conv = ConversionCalculator.convert( nextRace.getTrack(),
                                                         nextRace.getCarStart(),
                                                         nextRace.getDriverStart(),
                                                         fc, customTemp );

            int si = pPart[0].length - 2; // sunny index
            int wi = pPart[0].length - 1; // wet index
            for ( int i = 0; i < conv.length; i++ ) {
                pPart[i][si].setText( String.format( "%+3d", conv[i][0] ) );
                pPart[i][wi].setText( String.format( "%+3d", conv[i][1] ) );
            }

            // if Q1 is done, update car adjustments as well
            Qualify q1 = nextRace.getQualify1();
            if ( q1 != null && q1.getLap() != null && q1.getLap().getSettings() != null ) {
                CarSettings s = q1.getLap().getSettings();
                int[] settings = new int[]{s.getFrontWing(), s.getRearWing(), s.getEngine(), s.getBrakes(), s.getGear(), s.getSuspension()};
                // wings have to be adjusted in pairs
                // SUNNY
                int[] wings = adjustWings( settings[0], settings[1], conv[0][0], conv[1][0] );
                aPart[0][si].setText( String.format( "%3d", wings[0] ) );
                aPart[1][si].setText( String.format( "%3d", wings[1] ) );
                // RAINNY
                wings = adjustWings( settings[0], settings[1], conv[0][1], conv[1][1] );
                aPart[0][wi].setText( String.format( "%3d", wings[0] ) );
                aPart[1][wi].setText( String.format( "%3d", wings[1] ) );
                
                // other components
                for ( int i = 2; i < conv.length; i++ ) {
                    int adj = Math.max( Math.min( settings[i] + conv[i][0], 999 ), 0 );
                    aPart[i][si].setText( String.format( "%3d", adj ) );
                    adj = Math.max( Math.min( settings[i] + conv[i][1], 999 ), 0 );
                    aPart[i][wi].setText( String.format( "%3d", adj ) );
                }
            }
        }
    }

    private int[] adjustWings(int fw,
                              int rw,
                              int fwAdj,
                              int rwAdj) {
        int adjFw = fw + fwAdj;
        int adjRw = rw + rwAdj;
        int[] wings = new int[2];
        int mean = (adjFw + adjRw)/2;
        wings[0] = Math.max( Math.min( adjFw , 999 ), 0 );
        wings[1] = Math.max( Math.min( adjRw , 999 ), 0 );
        int delta = ( mean - ((wings[0]+wings[1])/2) ) * 2;
        if( wings[0] == 999 || wings[0] == 0 ) {
            wings[1] = Math.max( Math.min( 999, wings[1] + delta ), 0 );
        } else {
            wings[0] = Math.max( Math.min( 999, wings[0] + delta ), 0 );
        }
        return wings;
    }

    private int getAverageRaceTemp(Forecast[] forecast) {
        int sum = 0;
        for ( int i = 2; i < 5; i++ ) {
            sum += (forecast[i].getTempMin() + forecast[i].getTempMax()) / 2;
        }
        return (int) Math.round( sum / 3.0 );
    }

    private void fetchQ1() {
        nextRace = db.getNextRace();
        if ( nextRace != null ) {
            Qualify q1 = nextRace.getQualify1();
            if ( q1 != null && q1.getLap() != null && q1.getLap().getSettings() != null ) {
                updateConversions();
            } else {
                refresh.setEnabled( false );
                setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    private boolean done = false;
                    @Override
                    public Void doInBackground() {
                        try {
                            logger.info( "Refreshing Q1 information..." );
                            GPROUtil gpro = gproManFrame.getGPRO();
                            HtmlPage page = gpro.getPage( gproManFrame.getConfiguration().getGproUrl()+Q1Worker.Q1_URL_SUFFIX );
                            Q1Worker worker = new Q1Worker( page );
                            Qualify q1 = worker.call();
                            if( q1 != null ) {
                                logger.info( "Q1 lap retrieved... saving into the database..." );
                                nextRace.setQualify1( q1 );
                                done = true;
                                logger.info( "Q1 data successfully updated." );
                            } else {
                                logger.info( "Q1 lap not done yet..." );
                            }
                        } catch ( Exception e ) {
                            logger.error( "Error trying to refresh Q1 lap...", e );
                        }
                        return null;
                    }

                    @Override
                    public void done() {
                        if(done) {
                            db.store( db.getManager().getName(), nextRace );
                            updateConversions();
                        } else {
                            JOptionPane.showMessageDialog( gproManFrame.getFrame(), 
                                                           "Q1 não realizado ainda!",
                                                           "Q1 não realizado",
                                                           JOptionPane.INFORMATION_MESSAGE );
                        }
                        setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                        refresh.setEnabled( true );
                    }
                };
                worker.execute();
            }
        }
    }

    @Override
    public String getTitle() {
        return "Conversões ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/cloudy.gif" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/cloudy_16.gif" );
    }

    @Override
    public String getDescription() {
        return "Conversões dos ajustes do carro";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_V;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
