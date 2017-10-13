package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.report.ReportGenerator;
import org.gproman.report.SetupReportGenerator;
import org.gproman.report.TelemetryReportGenerator;
import org.gproman.report.TestReportGenerator;
import org.gproman.scrapper.ReportPublisher;
import org.gproman.scrapper.ReportPublisher.ReportType;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class ReportPanel extends UIPluginBase {

    private static final long    serialVersionUID      = -5241155966268876681L;

    public static final String   PROP_REPORT_SETUP     = "gmt.report.setup";
    public static final String   PROP_REPORT_TELEMETRY = "gmt.report.telemetria";
    public static final String   PROP_REPORT_TEST      = "gmt.report.test";


    private SetupReportPanel     setupPlugin;
    private TelemetryReportPanel telemetryPlugin;

    public ReportPanel(GPROManFrame gproManFrame,
                       DataService db) {
        super( gproManFrame, db );
        setLayout( new BorderLayout() );
        JTabbedPane tabbedPane = new JTabbedPane();

        setupPlugin = new SetupReportPanel( gproManFrame, db );
        addPlugin( tabbedPane, setupPlugin );
        telemetryPlugin = new TelemetryReportPanel( gproManFrame, db );
        addPlugin( tabbedPane, telemetryPlugin );

        add( tabbedPane, BorderLayout.CENTER );

    }

    private void addPlugin(final JTabbedPane tabbedPane,
                           final UIPlugin plugin) {
        Component component = plugin.getComponent();
        if ( plugin.requiresScrollPane() ) {
            component = new JScrollPane( plugin.getComponent(),
                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        }
        final Component actualComponent = component;
        tabbedPane.addTab( plugin.getTitle(),
                           plugin.getIcon(),
                           actualComponent,
                           plugin.getDescription() );
        tabbedPane.setMnemonicAt( tabbedPane.indexOfComponent( actualComponent ),
                                  plugin.getMnemonic() );
    }

    @Override
    public void update() {
        setupPlugin.setDirty( true );
        setupPlugin.update();
        telemetryPlugin.setDirty( true );
        telemetryPlugin.update();
    }

    @Override
    public String getTitle() {
        return "Relatórios ";
    }

    @Override
    public Category getCategory() {
        return Category.TOP;
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/reports_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/reports_16.png" );
    }

    @Override
    public String getDescription() {
        return "Relatórios do GPRO";
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_R;
    }

    @Override
    public boolean requiresScrollPane() {
        return false;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public boolean requiresConfiguration() {
        return true;
    }

    @Override
    public UIConfPlugin getConfigurationPlugin() {
        return new ReportConfigurationPlugin( gproManFrame.getCredentials(),
                                              gproManFrame.getConfiguration() );
    }

    public abstract static class ReportPanelBase extends UIPluginBase {

        private static final long   serialVersionUID = 210232127277861273L;

        private static final Logger logger           = Logger.getLogger( ReportPanel.class );

        private JComboBox           seasonCB;
        private JComboBox           raceCB;
        private JRadioButton        table;
        private JRadioButton        text;
        private JButton             generate;
        private JButton             copy;
        private JButton             publish;
        private JEditorPane         reportPane;

        private String              title;
        private Race                race;

        public ReportPanelBase(GPROManFrame gproManFrame,
                               DataService db,
                               String title) {
            super( gproManFrame,
                   db );
            this.title = title;
            setLayout( new BorderLayout() );

            seasonCB = new JComboBox();
            seasonCB.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    populateRacesCB();
                }
            } );

            raceCB = new JComboBox();
            raceCB.setRenderer( new ComboBoxRenderer() );
            raceCB.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearReport();
                }
            } );

            table = new JRadioButton( "Tabelado" );
            table.setMnemonic( KeyEvent.VK_B );
            table.setSelected( true );
            text = new JRadioButton( "Texto" );
            text.setMnemonic( KeyEvent.VK_X );
            ButtonGroup group = new ButtonGroup();
            group.add( table );
            group.add( text );

            generate = new JButton( "Criar" );
            generate.setIcon( UIUtils.createImageIcon( "/icons/Play_16.png" ) );
            generate.setMnemonic( KeyEvent.VK_R );
            generate.setToolTipText( "Cria o relatório no formato selecionado." );
            generate.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateReport();
                }
            } );

            final Clipboard clipboard = this.getToolkit().getSystemClipboard();
            copy = new JButton( "Copiar" );
            copy.setIcon( UIUtils.createImageIcon( "/icons/Copy_16.png" ) );
            copy.setMnemonic( KeyEvent.VK_I );
            copy.setToolTipText( "Copia o relatório para a área de transferência." );
            copy.setEnabled( false );
            copy.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String content = reportPane.getText();
                    StringSelection data = new StringSelection( content );
                    clipboard.setContents( data, data );
                }
            } );

            publish = new JButton( "Publicar" );
            publish.setIcon( UIUtils.createImageIcon( "/icons/upload_16.png" ) );
            publish.setMnemonic( KeyEvent.VK_U );
            publish.setToolTipText( "Publica o relatório no forum do GPROBrasil." );
            publish.setEnabled( false );
            publish.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    publishReportToGPROBrasil();
                }
            } );

            reportPane = new JEditorPane();
            Font f = reportPane.getFont();
            reportPane.setFont( new Font( Font.MONOSPACED, f.getStyle(), f.getSize() ) );
            reportPane.setEditable( false );
            JScrollPane editorScrollPane = new JScrollPane( reportPane );
            editorScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
            editorScrollPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ),
                                                                            BorderFactory.createLineBorder( Color.BLACK ) ) );

            FormLayout layout = new FormLayout( "50dlu, 4dlu, 35dlu, 10dlu, 35dlu, 4dlu, 100dlu, 10dlu, 55dlu, 4dlu, 55dlu, 4dlu, 55dlu ",
                                                "" );

            // add rows dynamically
            DefaultFormBuilder builder = new DefaultFormBuilder( layout );
            builder.border( Borders.DIALOG );
            builder.appendSeparator( this.title );

            JLabel lbl = builder.append( "Temporada: ", seasonCB );
            Font bold = lbl.getFont().deriveFont( Font.BOLD );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.RIGHT );
            lbl = builder.append( "Corrida: ", raceCB );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.RIGHT );
            lbl = builder.append( "Formato: ", table, text );
            lbl.setFont( bold );
            lbl.setHorizontalAlignment( SwingConstants.RIGHT );
            builder.nextLine();

            add( builder.build(), BorderLayout.NORTH );
            add( editorScrollPane, BorderLayout.CENTER );

            layout = new FormLayout( "238dlu, 10dlu, 55dlu, 4dlu, 55dlu, 4dlu, 55dlu ",
                                     "" );
            builder = new DefaultFormBuilder( layout );
            builder.border( Borders.DIALOG );
            builder.append( "", generate, copy, publish );

            add( builder.build(), BorderLayout.SOUTH );
        }

        @Override
        public void update() {
            if ( isDirty() ) {
                populateSeasonsCB();
                populateRacesCB();
                setDirty( false );
            }
        }

        private void clearReport() {
            reportPane.setText( "" );
            copy.setEnabled( false );
            publish.setEnabled( false );
        }

        @Override
        public boolean requiresScrollPane() {
            return false;
        }

        public void generateReport() {
            Race race = (Race) raceCB.getSelectedItem();
            if ( race != null ) {
                try {
                    Manager manager = db.getManager();
                    Season season = db.getSeason( manager.getName(), race.getSeasonNumber() );
                    TyreSupplierAttrs supplier = db.getTyreSupplier(season.getNumber(), season.getSupplier().toString() );
                    if ( text.isSelected() ) {
                        String report = getTextReportGenerator().generate( manager, season, race, supplier );
                        reportPane.setContentType( "text/plain" );
                        reportPane.setText( report );
                    } else if ( table.isSelected() ) {
                        String report = getTabbledReportGenerator().generate( manager, season, race, supplier );
                        reportPane.setContentType( "text/plain" );
                        reportPane.setText( report );
                    }
                    this.race = race;
                    copy.setEnabled( true );
                    publish.setEnabled( true );
                } catch ( Exception e ) {
                    logger.error( "Error generating the report", e );
                }
            } else {
                clearReport();
            }
        }

        protected void publishReportToGPROBrasil() {
            ReportType reportType = getReportType();
            if(( ReportType.SETUP.equals( reportType ) && race.getStatus().getSetupPublished() != null ) ||
                    ( ReportType.TELEMETRY.equals( reportType ) && race.getStatus().getTelemetryPublished() != null ) ||
                    ( ReportType.TEST.equals( reportType ) && race.getStatus().getTestsPublished() != null ) ) {
                JOptionPane.showMessageDialog( gproManFrame.getFrame(),
                        "Este relatório já foi publicado no Forum GPROBrasil. Obrigado!",
                        "Relatório já Publicado",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                //default icon, custom title
                int n = JOptionPane.showConfirmDialog( gproManFrame.getFrame(),
                                                       "Deseja publicar este relatório no Forum GPROBrasil?",
                                                       "Confirmação de Publicação",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE );
                if ( n == JOptionPane.YES_OPTION ) {
                    final Race raceToPublish = race;
                    final ReportPublisher publisher = new ReportPublisher( reportType,
                                                                     gproManFrame.getFrame(),
                                                                     getForumURL(),
                                                                     gproManFrame.getCredentials(),
                                                                     gproManFrame.getConfiguration(),
                                                                     raceToPublish,
                                                                     reportPane.getText() );
                    new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            publisher.run();
                            return Boolean.TRUE;
                        }
                        @Override
                        protected void done() {
                            db.store( db.getManager().getName(), raceToPublish);
                        }
                    }.execute();
                }
            }
        }

        protected abstract ReportGenerator getTabbledReportGenerator();

        protected abstract ReportGenerator getTextReportGenerator();

        protected abstract String getForumURL();
        
        protected abstract ReportPublisher.ReportType getReportType();

        private void populateSeasonsCB() {
            List<Integer> seasons = getSeasons();
            if ( seasons != null ) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) seasonCB.getModel();
                model.removeAllElements();
                for ( Integer s : seasons ) {
                    model.addElement( s );
                }
            }
        }

        private void populateRacesCB() {
            List<Race> races = getRaces( (Integer) seasonCB.getSelectedItem() );
            if ( races != null ) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) raceCB.getModel();
                model.removeAllElements();
                for ( Race r : races ) {
                    if( r.getTrack() != null ) {
                        model.addElement( r );
                    }
                }
            }
        }

        protected abstract List<Integer> getSeasons();

        protected abstract List<Race> getRaces(Integer season);
        
    }

    public static class TelemetryReportPanel extends ReportPanelBase {
        private static final long  serialVersionUID    = 6763279852601255183L;

        public static final String TELEMETRY_FORUM_URL = "http://s3.zetaboards.com/Grand_Prix_RO/forum/18998/";

        public TelemetryReportPanel(GPROManFrame frame,
                                    DataService db) {
            super( frame,
                   db,
                   "Relatório de Telemetria" );
        }

        @Override
        protected List<Integer> getSeasons() {
            return db.getSeasonsForTelemetry();
        }

        @Override
        public List<Race> getRaces(Integer season) {
            return season != null ? db.getRacesForTelemetry( season ) : Collections.<Race> emptyList();
        }

        @Override
        public String getDescription() {
            return "Relatório de Telemetria da Corrida";
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon( "/icons/Telemetry_32.png" );
        }

        @Override
        public ImageIcon getSmallIcon() {
            return UIUtils.createImageIcon( "/icons/Telemetry_16.png" );
        }

        @Override
        public String getTitle() {
            return "Telemetria ";
        }

        @Override
        public Category getCategory() {
            return Category.REPORT;
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_I;
        }

        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        protected ReportGenerator getTabbledReportGenerator() {
            return new TelemetryReportGenerator.BBReportGenerator();
        }

        protected ReportGenerator getTextReportGenerator() {
            return new TelemetryReportGenerator.TextReportGenerator();
        }

        @Override
        public boolean requiresConfiguration() {
            return true;
        }

        @Override
        public UIConfPlugin getConfigurationPlugin() {
            return new ReportConfigurationPlugin( gproManFrame.getCredentials(),
                                                  gproManFrame.getConfiguration() );
        }

        @Override
        protected String getForumURL() {
            return gproManFrame.getConfiguration().getProperty( PROP_REPORT_TELEMETRY );
        }
        
        @Override
        protected ReportType getReportType() {
            return ReportPublisher.ReportType.TELEMETRY;
        }
    }

    public static class SetupReportPanel extends ReportPanelBase {
        private static final long  serialVersionUID = 2776576483113896529L;

        public static final String SETUP_FORUM_URL  = "http://s3.zetaboards.com/Grand_Prix_RO/forum/18997/";

        public SetupReportPanel(GPROManFrame frame,
                                DataService db) {
            super( frame,
                   db,
                   "Relatório de Setup" );
        }

        @Override
        protected List<Integer> getSeasons() {
            return db.getSeasonsForSetup();
        }

        @Override
        public List<Race> getRaces(Integer season) {
            return season != null ? db.getRacesForSetup( season ) : Collections.<Race> emptyList();
        }

        @Override
        public String getDescription() {
            return "Relatório de Setup da Corrida";
        }

        @Override
        public Category getCategory() {
            return Category.REPORT;
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon( "/icons/wrench_32.png" );
        }

        @Override
        public ImageIcon getSmallIcon() {
            return UIUtils.createImageIcon( "/icons/wrench_16.png" );
        }

        @Override
        public String getTitle() {
            return "Setup ";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_S;
        }

        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        protected ReportGenerator getTabbledReportGenerator() {
            return new SetupReportGenerator.BBReportGenerator();
        }

        protected ReportGenerator getTextReportGenerator() {
            return new SetupReportGenerator.TextReportGenerator();
        }

        @Override
        protected String getForumURL() {
            return gproManFrame.getConfiguration().getProperty( PROP_REPORT_SETUP );
        }
        
        @Override
        protected ReportType getReportType() {
            return ReportPublisher.ReportType.SETUP;
        }
    }

    public static class TestReportPanel extends ReportPanelBase {
        private static final long  serialVersionUID = 2776576483113896529L;

        public static final String TEST_FORUM_URL  = "http://s3.zetaboards.com/Grand_Prix_RO/forum/3005390/";

        public TestReportPanel(GPROManFrame frame,
                               DataService db) {
            super( frame,
                   db,
                   "Relatório de Testes" );
        }

        @Override
        protected List<Integer> getSeasons() {
            return db.getSeasonsForTest();
        }

        @Override
        public List<Race> getRaces(Integer season) {
            return season != null ? db.getRacesForTest( season ) : Collections.<Race> emptyList();
        }

        @Override
        public String getDescription() {
            return "Relatório de Testes da Corrida";
        }

        @Override
        public Category getCategory() {
            return Category.REPORT;
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
        public String getTitle() {
            return "Testes ";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_E;
        }

        @Override
        public boolean isEnabledByDefault() {
            return true;
        }

        protected ReportGenerator getTabbledReportGenerator() {
            return new TestReportGenerator.BBReportGenerator();
        }

        protected ReportGenerator getTextReportGenerator() {
            return new TestReportGenerator.TextReportGenerator();
        }

        @Override
        protected String getForumURL() {
            return gproManFrame.getConfiguration().getProperty( PROP_REPORT_TEST );
        }
        
        @Override
        protected ReportType getReportType() {
            return ReportPublisher.ReportType.TEST;
        }
    }

    public static class ReportConfigurationPlugin extends UIConfPluginBase {

        private JTextField telemetryURL;
        private JTextField setupURL;
        private JTextField testURL;
        private JPanel     panel;

        public ReportConfigurationPlugin(UserCredentials credentials,
                                         UserConfiguration conf) {
            super( credentials, conf );
            panel = buildPanel();
        }

        private JPanel buildPanel() {
            FormLayout layout = new FormLayout( "right:80dlu, 4dlu, 150dlu",
                                                "" );
            DefaultFormBuilder builder = new DefaultFormBuilder( layout );
            // add rows dynamically
            builder = new DefaultFormBuilder( layout );
            builder.border( Borders.DIALOG );

            //GPRO Brasil
            builder.appendSeparator( "GPRO Brasil: " );
            testURL = new JTextField();
            testURL.setText( conf.getProperty( PROP_REPORT_TEST ) );
            builder.append( "Fórum de Testes:", testURL );
            builder.nextLine();
            setupURL = new JTextField();
            setupURL.setText( conf.getProperty( PROP_REPORT_SETUP ) );
            builder.append( "Fórum de Setups:", setupURL );
            builder.nextLine();
            telemetryURL = new JTextField();
            telemetryURL.setText( conf.getProperty( PROP_REPORT_TELEMETRY ) );
            builder.append( "Fórum de Telemetria:", telemetryURL );
            builder.nextLine();

            return builder.getPanel();
        }

        @Override
        public boolean commit() {
            conf.setProperty( PROP_REPORT_TEST, testURL.getText() );
            conf.setProperty( PROP_REPORT_SETUP, setupURL.getText() );
            conf.setProperty( PROP_REPORT_TELEMETRY, telemetryURL.getText() );
            return true;
        }

        @Override
        public boolean rollback() {
            return true; // nothing to do
        }

        @Override
        public String getTitle() {
            return "Relatórios ";
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon( "/icons/reports_16.png" );
        }

        @Override
        public String getDescription() {
            return "Configurações dos relatórios";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_R;
        }

        @Override
        public Properties getDefaultConfiguration() {
            Properties prop = new Properties();
            prop.setProperty( PROP_REPORT_TEST, TestReportPanel.TEST_FORUM_URL );
            prop.setProperty( PROP_REPORT_SETUP, SetupReportPanel.SETUP_FORUM_URL );
            prop.setProperty( PROP_REPORT_TELEMETRY, TelemetryReportPanel.TELEMETRY_FORUM_URL );
            return prop;
        }

        @Override
        public JComponent getPluginComponent() {
            return this.panel;
        }

    }

    public static class ComboBoxRenderer extends JLabel
            implements
            ListCellRenderer {
        private static final long serialVersionUID = 1545037048456885661L;

        public ComboBoxRenderer() {
            setOpaque( true );
            setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
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
                Race race = (Race) value;

                //Set the icon and text.  If icon was null, say so.
                String lbl = String.format( "S%02dR%02d - %s",
                                            race.getSeasonNumber(),
                                            race.getNumber(),
                                            race.getTrack().getName() );
                setText( lbl );
            } else {
                setText( "" );
            }
            return this;
        }
    }
}
