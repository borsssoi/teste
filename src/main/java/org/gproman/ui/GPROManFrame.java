package org.gproman.ui;

import static org.gproman.util.CredentialsManager.loadCredentials;
import static org.gproman.util.CredentialsManager.saveCredentials;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.GproManager;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.race.Race;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.scrapper.GPROScrapper;
import org.gproman.scrapper.GPROUtil;
import org.gproman.ui.ReportPanel.SetupReportPanel;
import org.gproman.ui.ReportPanel.TelemetryReportPanel;
import org.gproman.ui.ReportPanel.TestReportPanel;
import org.gproman.ui.UIPlugin.UIPluginContext;
import org.gproman.util.BareBonesBrowserLaunch;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.PluginContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class GPROManFrame {

    final static Logger           logger = LoggerFactory.getLogger(GPROManFrame.class);

    private final static String[] thanks = new String[]{
                                         "Ayrton Pasquini", "Diego Borçoi", "Edson Henrik", "Fábio Soares",
                                         "Fabrício Gadelha", "Felipe Seolin", "Igor Nichele", "José Nunes",
                                         "John Arthur", "Lucas Lobo", "Luis C. M. Machado",
                                         "Rogério Pereira", "Renan Henrique"
                                         };

    private final class DefaultSingleSelectionModelExtension extends DefaultSingleSelectionModel {

        private static final long serialVersionUID = 1L;

        @Override
        public void fireStateChanged() {
            super.fireStateChanged();
        }
    }

    private final JFrame               frame;
    private final GproManager          app;

    private List<UIPlugin>             plugins;
    private Map<Category, JTabbedPane> categories;

    //    private JLabel                     status;
    private UserCredentials            credentials;
    private GPROUtil                   gpro;
    private GPROBrUtil                 gprobr;

    private JMenu                      pluginsMenu;
    private Map<Category, JMenu>       categoryMenus;
    private JMenuItem                  fetchMenuItem;

    private JToolBar                   toolBar;

    private JButton                    fetchButton;

    private boolean                    everestEnabled = false;

    public GPROManFrame(GproManager app) {
        this.app = app;
        this.plugins = new ArrayList<UIPlugin>();
        this.frame = buildFrame();
    }

    public void show() {
        this.frame.setVisible( true );
    }

    public void start() {
        checkCredentials();
        createBrowsers();
        updateData( false );
        updateData( true );
        loadPluginContexts();
    }

    private void checkCredentials() {
        credentials = loadCredentials();
        if (credentials == null) {
            logger.info("Requesting credentials from the user.");
            UserCredentials credentials = requestUserCredentials();
            if (credentials == null) {
                logger.info("Can't work without proper credentials.");
                app.terminateApplication();
                System.exit(0);
            }
            this.credentials = credentials;
            saveCredentials(credentials);
        }
        if (credentials != null) {
            showHideEverest();
        }
    }

    public void updateData( boolean everest ) {
        Map<String, JCheckBoxMenuItem> errors = new HashMap<String, JCheckBoxMenuItem>();
        for (UIPlugin plugin : plugins) {
            if (plugin.isEnabled() && !( everest ^ Category.EVEREST.equals(plugin.getCategory())) ) {
                try {
                    plugin.setDirty(true);
                    plugin.update();
                } catch (Exception e) {
                    logger.error("Error updating plugin. Disabling it.", e);
                    JCheckBoxMenuItem mi = null;
                    for (Component c : pluginsMenu.getMenuComponents()) {
                        if (c instanceof JCheckBoxMenuItem) {
                            mi = (JCheckBoxMenuItem) c;
                            if (mi.getText().equals(plugin.getTitle())) {
                                break;
                            }
                        }
                    }
                    errors.put(plugin.getTitle(), mi);
                }
            }
        }
        enableFetchAction(true);
        if (!errors.isEmpty()) {
            Race nextRace = app.getDataService().getNextRace();
            String msg = "";
            if( nextRace != null && nextRace.getDriverStart() == null ) {
                msg = "Você não possui um piloto contratado. Por isso, os seguintes\n"+
                      "plugins foram desabilitados:\n"+
                      errors.keySet().toString()+
                      "\n\nContrate um piloto e atualize os dados e estes plugins\n"+
                      "serão automaticamente reabilitados.\n";
            } else {
                msg = "Ocorreu um erro ao atualizar o(s) seguinte(s) plugins:\n" + errors.keySet().toString() +
                      "\n\nEstes plugins foram desabilitados. Por favor contacte\n" +
                      "o desenvolvedor no forum GPRO Brasil para providenciar a\n" +
                      "correção do problema. Não esqueça de anexar o seu arquivo\n" +
                      "gmt.log junto com a descrição do problema.";
            }
            JOptionPane.showMessageDialog(this.getFrame(),
                    msg,
                    "Erro atualizando dados dos plugins",
                    JOptionPane.ERROR_MESSAGE);
            for (JCheckBoxMenuItem mi : errors.values()) {
                if (mi != null) {
                    mi.doClick();
                }
            }
        }
    }

    public void createBrowsers() {
        this.gpro = new GPROUtil(getCredentials(), getConfiguration());
        this.gprobr = new GPROBrUtil(getCredentials(), getConfiguration());
    }

    private JFrame buildFrame() {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                savePluginContexts();
                app.terminateApplication();
            };
        });
        frame.setTitle("GPRO Manager's Toolbox");
        frame.setResizable(true);
        frame.setPreferredSize(new Dimension(800, 740));

        toolBar = new JToolBar("Atalhos");

        frame.setJMenuBar(buildMenu());
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        frame.getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        //frame.getContentPane().add(buildStatusBarPanel(), BorderLayout.SOUTH);
        frame.pack();

        frame.setLocationRelativeTo(null);

        return frame;
    }

    //    private Component buildStatusBarPanel() {
    //        status = new JLabel();
    //        status.setPreferredSize(new Dimension(780, status.getFontMetrics(status.getFont()).getHeight() + 8));
    //        status.setHorizontalAlignment(SwingConstants.LEFT);
    //        status.setVerticalAlignment(SwingConstants.CENTER);
    //        status.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    //        return status;
    //    }

    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("GMT");
        menu.setMnemonic('G');
        menuBar.add(menu);

        FetchGPROData fetch = new FetchGPROData(this, app);
        fetchMenuItem = new JMenuItem(fetch);
        menu.add(fetchMenuItem);

        menu.addSeparator();

        ChangeUserConfigurations cuc = new ChangeUserConfigurations(this, app);
        JMenuItem changeUserConfigurations = new JMenuItem(cuc);
        menu.add(changeUserConfigurations);

        menu.addSeparator();

        ExitAction ea = new ExitAction(this, this.app);
        JMenuItem exit = new JMenuItem(ea);
        menu.add(exit);

        menuBar.add(Box.createRigidArea(new Dimension(10, 0)));
        pluginsMenu = new JMenu("Ferramentas");
        pluginsMenu.setMnemonic('F');
        menuBar.add(pluginsMenu);

        menuBar.add(Box.createRigidArea(new Dimension(10, 0)));
        JMenu help = new JMenu("Ajuda");
        help.setMnemonic('A');
        menuBar.add(help);

        DocumentationAction da = new DocumentationAction(this);
        JMenuItem docs = new JMenuItem(da);
        help.add(docs);

        help.addSeparator();

        AboutAction aa = new AboutAction(this);
        JMenuItem about = new JMenuItem(aa);
        help.add(about);

        fetchButton = new JButton(fetch);
        fetchButton.setText("");
        JButton cucb = new JButton(cuc);
        cucb.setText("");
        JButton dab = new JButton(da);
        dab.setText("");
        JButton aab = new JButton(aa);
        aab.setText("");
        JButton eab = new JButton(ea);
        eab.setText("");

        toolBar.add(fetchButton);
        toolBar.addSeparator();
        toolBar.add(cucb);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.addSeparator();
        toolBar.add(dab);
        toolBar.add(aab);
        toolBar.addSeparator();
        toolBar.add(eab);

        return menuBar;
    }

    private JTabbedPane buildMainPanel() {
        this.categories = new HashMap<Category, JTabbedPane>();
        this.categoryMenus = new LinkedHashMap<Category, JMenu>();
        final JTabbedPane topPane = new JTabbedPane();
        topPane.setModel(new DefaultSingleSelectionModelExtension());
        final JTabbedPane seasonPane = new JTabbedPane();
        seasonPane.setModel(new DefaultSingleSelectionModelExtension());
        final JTabbedPane teamPane = new JTabbedPane();
        teamPane.setModel(new DefaultSingleSelectionModelExtension());
        final JTabbedPane toolsPane = new JTabbedPane();
        toolsPane.setModel(new DefaultSingleSelectionModelExtension());
        final JTabbedPane reportPane = new JTabbedPane();
        reportPane.setModel(new DefaultSingleSelectionModelExtension());
        final JTabbedPane calculatorPane = new JTabbedPane();
        calculatorPane.setModel(new DefaultSingleSelectionModelExtension());

        this.categories.put(Category.TOP, topPane);
        this.categories.put(Category.SEASON, seasonPane);
        this.categories.put(Category.TEAM, teamPane);
        this.categories.put(Category.TOOLS, toolsPane);
        this.categories.put(Category.REPORT, reportPane);
        this.categories.put(Category.CALC, calculatorPane);

        this.categoryMenus.put(Category.SEASON, createCategoryMenu(Category.SEASON));
        this.categoryMenus.put(Category.TEAM, createCategoryMenu(Category.TEAM));
        this.categoryMenus.put(Category.TOOLS, createCategoryMenu(Category.TOOLS));
        this.categoryMenus.put(Category.REPORT, createCategoryMenu(Category.REPORT));
        this.categoryMenus.put(Category.CALC, createCategoryMenu(Category.CALC));

        for (JMenu m : this.categoryMenus.values()) {
            this.pluginsMenu.add(m);
        }

        //        JPanel panel = new JPanel( new BorderLayout() );
        //        panel.add( teamPane, BorderLayout.CENTER );

        topPane.addTab(Category.SEASON.getLabel(),
                Category.SEASON.getLargeIcon(),
                seasonPane,
                Category.SEASON.getDesc());
        topPane.setMnemonicAt(topPane.indexOfComponent(seasonPane),
                KeyEvent.VK_T);
        topPane.addTab(Category.TEAM.getLabel(),
                Category.TEAM.getLargeIcon(),
                teamPane,
                Category.TEAM.getDesc());
        topPane.setMnemonicAt(topPane.indexOfComponent(teamPane),
                KeyEvent.VK_Q);
        topPane.addTab(Category.TOOLS.getLabel(),
                Category.TOOLS.getLargeIcon(),
                toolsPane,
                Category.TOOLS.getDesc());
        topPane.setMnemonicAt(topPane.indexOfComponent(toolsPane),
                KeyEvent.VK_M);
        topPane.addTab(Category.REPORT.getLabel(),
                Category.REPORT.getLargeIcon(),
                reportPane,
                Category.REPORT.getDesc());
        topPane.setMnemonicAt(topPane.indexOfComponent(reportPane),
                KeyEvent.VK_L);

        topPane.addTab(Category.CALC.getLabel(),
                Category.CALC.getLargeIcon(),
                calculatorPane,
                Category.CALC.getDesc());
        topPane.setMnemonicAt(topPane.indexOfComponent(calculatorPane),
                KeyEvent.VK_U);

        topPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (topPane.getSelectedComponent() instanceof JTabbedPane) {
                    ((DefaultSingleSelectionModelExtension) ((JTabbedPane) topPane.getSelectedComponent()).getModel()).fireStateChanged();
                }
            }
        });

        //addPlugin( new GPROBrasilPanel( this, app.getDataService() ) );
        addPlugin(new MessagePanel(this, app.getDataService()));
        addPlugin(new SeasonDataPanel(this, app.getDataService()));
        addPlugin(new SeasonTracksPanel(this, app.getDataService()));
        addPlugin(new TyreSuppliersPanel(this, app.getDataService()));
        addPlugin(new DriverPanel(this, app.getDataService()));
        addPlugin(new TechDirectorPanel(this, app.getDataService()));
        addPlugin(new FacilitiesPanel(this, app.getDataService()));
        addPlugin(new CarPanel(this, app.getDataService()));
        addPlugin(new WearPlanningPanel(this, app.getDataService()));
        addPlugin(new StrategyPanel(this, app.getDataService()));
        addPlugin(new PracticeSupportPanel(this, app.getDataService()));
        addPlugin(new ConversionPanel(this, app.getDataService()));
        addPlugin(new TestSessionPanel(this, app.getDataService()));
        addPlugin(new TestReportPanel(this, app.getDataService()));
        addPlugin(new SetupReportPanel(this, app.getDataService()));
        addPlugin(new TelemetryReportPanel(this, app.getDataService()));
        addPlugin(new DriverOACalcPanel(this, app.getDataService()));
        addPlugin(new PHACalcPanel(this, app.getDataService()));
        addPlugin(new WingSplitCalcPanel(this, app.getDataService()));
        addPlugin(new FuelCalcPanel(this, app.getDataService()));
        addPlugin(new TelemetrySearchPanel(this, app.getDataService()));

        //The following line enables to use scrolling tabs.
        topPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // just in case the plugins configuration was not persisted before
        ConfigurationManager.saveConfiguration(getConfiguration());

        return topPane;
    }

    private JMenu createCategoryMenu(Category category) {
        JMenu menu = new JMenu(category.getLabel());
        menu.setIcon(category.getSmallIcon());
        return menu;
    }

    public void showHideEverest() {
        if ( credentials != null &&
                (UserCredentials.UserRole.ADVANCED.equals(credentials.getRole()) ||
                UserCredentials.UserRole.ADMIN.equals(credentials.getRole()))) {
            if( !everestEnabled ) {
                everestEnabled = true;
                if( app.getEverestService() == null ) {
                    app.startEverest( credentials );
                }
                // power users have access to the Everest tab
                final JTabbedPane everestPane = new JTabbedPane();
                everestPane.setModel(new DefaultSingleSelectionModelExtension());
                this.categories.put(Category.EVEREST, everestPane);
                this.categoryMenus.put(Category.EVEREST, createCategoryMenu(Category.EVEREST));
                this.pluginsMenu.add(this.categoryMenus.get(Category.EVEREST));

                final JTabbedPane topPane = this.categories.get(Category.TOP);
                topPane.addTab(Category.EVEREST.getLabel(),
                        Category.EVEREST.getLargeIcon(),
                        everestPane,
                        Category.EVEREST.getDesc());
                topPane.setMnemonicAt(topPane.indexOfComponent(everestPane),
                        KeyEvent.VK_V);

                addPlugin(new EverestDashboardPanel(this, app.getDataService()));
                addPlugin(new TelemetryMinerPanel(this, app.getDataService()));
                addPlugin(new EverestSearchPanel(this, app.getDataService()));
                if( UserCredentials.UserRole.ADMIN.equals(credentials.getRole())) {
                    addPlugin(new GPROBrUtilsPanel(this, app.getDataService()));
                }
                // just in case the plugins configuration was not persisted before
                ConfigurationManager.saveConfiguration(getConfiguration());
            }
        } else {
            if( everestEnabled ) {
                JTabbedPane topPane = this.categories.get(Category.TOP);
                JTabbedPane everestPane = this.categories.get(Category.EVEREST);
                if( everestPane != null ) {
                    everestPane.removeAll();
                    topPane.remove(everestPane);
                }
                JMenu everestMenu = this.categoryMenus.get(Category.EVEREST);
                if( everestMenu != null ) {
                    everestMenu.removeAll();
                    this.pluginsMenu.remove(everestMenu);
                }
                for( Iterator<UIPlugin> it = this.plugins.iterator(); it.hasNext(); ) {
                    UIPlugin plugin = it.next();
                    if( Category.EVEREST.equals( plugin.getCategory() ) ) {
                        it.remove();
                    }
                }
                everestEnabled = false;
            }
        }
    }

    private void addPlugin(final UIPlugin plugin) {
        final JTabbedPane tabbedPane = this.categories.get(plugin.getCategory());
        Component component = plugin.getComponent();
        if (plugin.requiresScrollPane()) {
            component = new JScrollPane(plugin.getComponent(),
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        final Component actualComponent = component;

        JCheckBoxMenuItem mi = new JCheckBoxMenuItem(plugin.getTitle(),
                plugin.getSmallIcon(),
                plugin.isEnabled());
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
                    try {
                        addPluginToGUI(tabbedPane, plugin, actualComponent);
                        plugin.setDirty(true);
                        plugin.update();
                        plugin.setEnabled(true);
                    } catch (Exception ex) {
                        logger.error("Error enabling plugin: " + plugin.getTitle(), ex);
                        JOptionPane.showMessageDialog(GPROManFrame.this.getFrame(),
                                "Ocorreu um erro ao atualizar o plugin:\n" + plugin.getTitle() +
                                        "\n\nEste plugin foi desabilitado. Por favor contacte\n" +
                                        "o desenvolvedor no forum GPRO Brasil para providenciar a\n" +
                                        "correção do problema. Não esqueça de anexar o seu arquivo\n" +
                                        "gmt.log junto com a descrição do problema.",
                                "Erro atualizando dado do plugin",
                                JOptionPane.ERROR_MESSAGE);
                        plugin.setEnabled(false);
                        removePluginFromGUI(tabbedPane, plugin, actualComponent);
                    }
                } else {
                    try {
                        removePluginFromGUI(tabbedPane, plugin, actualComponent);
                    } catch (Exception ex) {
                        logger.error("Error disabling plugin: " + plugin.getTitle(), ex);
                    } finally {
                        plugin.setEnabled(false);
                    }
                }
            }
        });
        this.categoryMenus.get(plugin.getCategory()).add(mi);

        boolean dirty = false;
        if (plugin.requiresConfiguration()) {
            Properties def = plugin.getConfigurationPlugin().getDefaultConfiguration();
            for (Object prop : def.keySet()) {
                if (app.getConfiguration().getProperty((String) prop) == null) {
                    app.getConfiguration().setProperty((String) prop, def.getProperty((String) prop));
                    dirty = true;
                }
            }
        }

        if (plugin.isEnabled()) {
            addPluginToGUI(tabbedPane, plugin, actualComponent);
        }

        if (dirty) {
            app.saveConfiguration();
        }
    }

    private void addPluginToGUI(final JTabbedPane tabbedPane,
            final UIPlugin plugin,
            final Component actualComponent) {
        tabbedPane.addTab(plugin.getTitle(),
                plugin.getIcon(),
                actualComponent,
                plugin.getDescription());
        int index = tabbedPane.indexOfComponent(actualComponent);
        tabbedPane.setMnemonicAt(index,
                plugin.getMnemonic());

        ChangeListener listener = plugin.getChangeListener(actualComponent);
        if (listener != null) {
            tabbedPane.addChangeListener(listener);
        }

        plugins.add(plugin);
    }

    private void removePluginFromGUI(final JTabbedPane tabbedPane,
            final UIPlugin plugin,
            final Component actualComponent) {
        tabbedPane.remove(actualComponent);
        plugins.remove(plugin);
    }

    public void setStatusMT(final String status) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setStatus(status);
            }
        });
    }

    public void setStatus(final String status) {
        //        this.status.setText(status);
    }

    public void savePluginContexts() {
        for( UIPlugin p : plugins ) {
            if( p.hasContext() ) {
                UIPluginContext context = p.getContext();
                if( context != null ) {
                    PluginContextManager.saveContext( p, context );
                }
            }
        }
    }

    public void loadPluginContexts() {
        for( UIPlugin p : plugins ) {
            if( p.hasContext() ) {
                try {
                    UIPluginContext context = PluginContextManager.loadContext( p );
                    if( context != null ) {
                        p.setContext(context);
                    }
                } catch( Exception e ) {
                    logger.error("Error loading plugin context for plugin: "+p.getId(), e);
                }
            }
        }
    }

    private static class ExitAction extends AbstractAction {

        private static final long serialVersionUID = -1357497742363234052L;
        private GproManager       app;
        private GPROManFrame      frame;

        public ExitAction(GPROManFrame frame,
                GproManager app) {
            super("Sair");
            this.frame = frame;
            this.app = app;
            putValue(SHORT_DESCRIPTION, "Sai da aplicação");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            ImageIcon icon = UIUtils.createImageIcon("/icons/exit_16.png");
            putValue(SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int n = JOptionPane.showConfirmDialog(frame.getFrame(),
                    "Deseja fechar a aplicação?",
                    "Sair da applicação?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (n == JOptionPane.YES_OPTION) {
                this.frame.savePluginContexts();
                this.app.terminateApplication();
                System.exit(0);
            }
        }
    }

    private static class DocumentationAction extends AbstractAction {

        private static final String DOC_URL          = "http://snord.org/gmtj";
        private static final long   serialVersionUID = -1357497742363234052L;
        private GPROManFrame        frame;

        public DocumentationAction(GPROManFrame frame) {
            super("Manual ...");
            this.frame = frame;
            putValue(SHORT_DESCRIPTION, "Manual e tutoriais sobre o GMT...");
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            ImageIcon icon = UIUtils.createImageIcon("/icons/manual_16.png");
            putValue(SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                BareBonesBrowserLaunch.openURL(DOC_URL);
            } catch (Exception e1) {
                logger.error("Error opening browser to the documentation page...", e1);
                JOptionPane.showMessageDialog(frame.getFrame(),
                        "Não foi possível abrir o navegador automaticamente. Para acessar a\n" +
                                "documentação, por favor abra o navegador e acesse o seguinte endereço:\n" +
                                DOC_URL,
                        "Erro abrindo documentação",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class AboutAction extends AbstractAction {

        private static final long serialVersionUID = -1357497742363234052L;
        private GPROManFrame      frame;

        public AboutAction(GPROManFrame frame) {
            super("Sobre");
            this.frame = frame;
            putValue(SHORT_DESCRIPTION, "Sobre o GPRO Manager's Toolbox...");
            putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            ImageIcon icon = UIUtils.createImageIcon("/icons/help_16.png");
            putValue(SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ImageIcon icon = UIUtils.createImageIcon("/icons/splash2.png");

            FormLayout layout = new FormLayout("right:max(50dlu;p), 4dlu, fill:max(140dlu;p)",
                    "");

            // add rows dynamically
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            JLabel lbl = new JLabel("GMT - GPRO Manager's Toolbox");
            Font bold = lbl.getFont().deriveFont(Font.BOLD);
            Font italic = lbl.getFont().deriveFont(Font.ITALIC);
            Font bold14 = lbl.getFont().deriveFont(Font.BOLD, 14);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(bold14);
            builder.append(lbl, 3);
            builder.nextLine();
            lbl = new JLabel(GproManager.getVersionString());
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(italic);
            builder.append(lbl, 3);
            builder.nextLine();
            builder.append("");
            builder.nextLine();
            builder.append("");
            builder.nextLine();
            lbl = builder.append("Desenvolvimento:");
            lbl.setFont(bold);
            builder.append("Edson Tirelli, Eduardo Morás, Rodrigo A. Borges e Renan Henrique");
            builder.nextLine();
            lbl = builder.append("Projeção de desgaste:");
            lbl.setFont(bold);
            builder.append("André Silva");
            builder.nextLine();
            lbl = builder.append("Simulação de estratégia:");
            lbl.setFont(bold);
            builder.append("Fernando Mendonça e Flávio de Almeida");
            builder.nextLine();
            lbl = builder.append("Cálculo de Combustível:");
            lbl.setFont(bold);
            builder.append("Fernando Mendonça, Flávio de Almeida, Rogério Pereira e Rony Resende");
            builder.nextLine();
            lbl = builder.append("Suporte a treinos:");
            lbl.setFont(bold);
            builder.append("Luiz Oliveira, Flávio de Almeida e Fernando Mendonça");
            builder.nextLine();
            lbl = builder.append("Documentação:");
            lbl.setFont(bold);
            builder.append("Paulo Weber");
            builder.nextLine();
            lbl = builder.append("Pesquisa de Telemetrias:");
            lbl.setFont(bold);
            builder.append("Paulo Weber");
            builder.nextLine();
            lbl = builder.append("Administrador Fórum GPROBrasil:");
            lbl.setFont(bold);
            builder.append("Rodrigo A. Borges e Edson Tirelli");
            builder.nextLine();
            builder.append("");
            builder.nextLine();
            builder.append("");
            builder.nextLine();

            lbl = builder.append("Agradecimentos:");
            lbl.setFont(bold);
            Arrays.sort(thanks);
            int i = 0;
            for (; i < thanks.length - 3; i += 3) {
                lbl = builder.append(String.format("%s, %s, %s,", thanks[i], thanks[i + 1], thanks[i + 2]));
                builder.nextLine();
                builder.append("");
            }
            String remaining = "";
            for (; i < thanks.length; i++) {
                remaining += thanks[i];
                remaining += (i < thanks.length - 1) ? ", " : "";
            }
            lbl = builder.append(remaining);
            builder.nextLine();

            JOptionPane.showMessageDialog(frame.getFrame(),
                    builder.getPanel(),
                    "Sobre o GPRO Manager's Toolbox",
                    JOptionPane.PLAIN_MESSAGE,
                    icon);
        }
    }

    private static class FetchGPROData extends AbstractAction {

        private static final long serialVersionUID = -6617401563348257613L;
        private GPROScrapper      scrapper;
        private GPROManFrame      frame;

        public FetchGPROData(GPROManFrame frame,
                GproManager app) {
            super("Buscar dados do GPRO");
            this.frame = frame;
            this.scrapper = new GPROScrapper(frame, app.getDataService());
            putValue(SHORT_DESCRIPTION, "Buscar dados atualizados do GPRO");
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            ImageIcon icon = UIUtils.createImageIcon("/icons/FetchData_16.png");
            putValue(SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int n = JOptionPane.showConfirmDialog(frame.getFrame(),
                    "Deseja fazer o download de dados do GPRO?",
                    "Confirmação de Download",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (n == JOptionPane.YES_OPTION) {
                frame.enableFetchAction(false);
                scrapper.fetchGPROData();
            }
        }
    }

    private static class ChangeUserConfigurations extends AbstractAction {

        private static final long serialVersionUID = -6617401563348257613L;
        private GPROManFrame      frame;

        public ChangeUserConfigurations(GPROManFrame frame,
                GproManager app) {
            super("Configurações");
            putValue(SHORT_DESCRIPTION, "Alterar Configurações");
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            ImageIcon icon = UIUtils.createImageIcon("/icons/settings_16.png");
            putValue(SMALL_ICON, icon);
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.requestUserConfigurations();
        }
    }

    private void requestUserConfigurations() {
        ConfigurationDialog confDialog = new ConfigurationDialog(this,
                this.credentials,
                this.app.getConfiguration());
        for (UIPlugin plugin : plugins) {
            if (plugin.requiresConfiguration()) {
                confDialog.addPluginToGUI(plugin.getConfigurationPlugin());
            }
        }

        int result = JOptionPane.showConfirmDialog(this.frame,
                confDialog,
                "Configurações",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            confDialog.commit();
            this.credentials = confDialog.getCredentials();
            saveCredentials(credentials);
            this.app.setConfiguration(confDialog.getConfiguration());
            this.app.saveConfiguration();
        } else {
            confDialog.rollback();
        }
    }

    public void enableFetchAction(final boolean enable) {
        fetchMenuItem.setEnabled(enable);
        fetchButton.setEnabled(enable);
    }

    private UserCredentials requestUserCredentials() {
        FormLayout layout = new FormLayout("fill:p",
                "fill:p");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        JLabel image = new JLabel(UIUtils.createImageIcon("/icons/splash2.png"));
        image.setPreferredSize(new Dimension(289, 200));
        builder.append(image);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(builder.getPanel(), BorderLayout.WEST);

        layout = new FormLayout("right:max(50dlu;p), 4dlu, fill:max(140dlu;p)",
                "");

        // add rows dynamically
        builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        JLabel lbl1 = new JLabel("Esta parece ser a primeira vez em que você utiliza");
        lbl1.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl1, 3);
        builder.nextLine();
        JLabel lbl2 = new JLabel("o GPRO Manager's Toolbox. Por favor entre com o login");
        lbl2.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl2, 3);
        builder.nextLine();
        JLabel lbl3 = new JLabel("e senha do seu usuário no site do GPRO. Estes dados");
        lbl3.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl3, 3);
        builder.nextLine();
        JLabel lbl4 = new JLabel("serão armazenados localmente criptografados e serão");
        lbl4.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl4, 3);
        builder.nextLine();
        JLabel lbl5 = new JLabel("utilizados para obter os dados do seu gerente no site.");
        lbl5.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl5, 3);
        builder.nextLine();
        builder.appendSeparator();
        JTextField user = new JTextField();
        builder.append("Login:", user);
        builder.nextLine();
        JPasswordField password = new JPasswordField();
        builder.append("Senha:", password);
        builder.nextLine();
        builder.appendSeparator();

        //GPROBR
        JLabel lbl6 = new JLabel("Agora entre com o usuário e senha do GPROBrasil.");
        lbl6.setHorizontalAlignment(SwingConstants.CENTER);
        builder.append(lbl6, 3);
        builder.nextLine();
        builder.appendSeparator();
        JTextField GPROBrasiluser = new JTextField();
        builder.append("Login:", GPROBrasiluser);
        builder.nextLine();
        JPasswordField GPROBrasilpassword = new JPasswordField();
        builder.append("Senha:", GPROBrasilpassword);
        builder.nextLine();

        panel.add(builder.getPanel(), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this.frame,
                panel,
                "Bem Vindo ao GPRO Manager's Toolbox",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new UserCredentials(user.getText(),
                    new String(password.getPassword()),
                    GPROBrasiluser.getText(),
                    new String(GPROBrasilpassword.getPassword()),
                    UserCredentials.UserRole.STANDARD);
        }
        return null;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public JFrame getFrame() {
        return frame;
    }

    public GproManager getApplication() {
        return app;
    }

    public GPROUtil getGPRO() {
        return this.gpro;
    }

    public GPROBrUtil getGPROBr() {
        return this.gprobr;
    }

    public void checkFirstExecution() {
        if (app.getDataService().isFirstExecutionForSeason()) {
            fetchMenuItem.doClick();
        }
    }

    public UserConfiguration getConfiguration() {
        return app.getConfiguration();
    }

}
