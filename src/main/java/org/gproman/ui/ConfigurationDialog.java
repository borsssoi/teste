package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.gproman.model.UserConfiguration;
import org.gproman.model.UserConfiguration.ProxyType;
import org.gproman.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class builds a main panel for a configuration dialog.
 * By default, it implements all the details of the system configuration,
 * but also allows for plugins to expose plugin specific configuration.
 */
public class ConfigurationDialog extends JPanel {

    private static final long   serialVersionUID = -8818792328043232610L;

    private static final Logger logger           = LoggerFactory.getLogger(ConfigurationDialog.class);

    private UserConfiguration   conf;
    private UserCredentials     credentials;
    private List<UIConfPlugin>  plugins;

    private JTabbedPane         tpane;

    private GPROManFrame        frame;

    public ConfigurationDialog(GPROManFrame frame,
            UserCredentials credentials,
            UserConfiguration conf) {
        this.frame = frame;
        this.credentials = credentials;
        this.conf = conf;
        this.plugins = new ArrayList<UIConfPlugin>();
        buildMainPanel();
    }

    private void buildMainPanel() {
        setLayout(new BorderLayout());
        tpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        addPluginToGUI(new GeneralConfiguration(credentials, conf));
        addPluginToGUI(new SecurityConfiguration(credentials, conf, frame));
        addPluginToGUI(new ProxyConfiguration(credentials, conf));

        add(tpane, BorderLayout.CENTER);
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public UserConfiguration getConfiguration() {
        return conf;
    }

    public void addPluginToGUI(final UIConfPlugin plugin) {
        plugin.setParent(frame.getFrame());
        Component component = plugin.getPluginComponent();
        if (plugin.requiresScrollPane()) {
            component = new JScrollPane(component,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        tpane.addTab(plugin.getTitle(),
                plugin.getIcon(),
                component,
                plugin.getDescription());
        tpane.setMnemonicAt(tpane.indexOfComponent(component),
                plugin.getMnemonic());
        plugins.add(plugin);
    }

    public boolean commit() {
        boolean result = true;
        for (UIConfPlugin plugin : plugins) {
            result &= plugin.commit();
        }
        return result;
    }

    public boolean rollback() {
        boolean result = true;
        for (UIConfPlugin plugin : plugins) {
            result &= plugin.rollback();
        }
        return result;
    }

    public static class GeneralConfiguration extends UIConfPluginBase {

        private JCheckBox  checkUpdates;
        private JTextField dbDirTF;
        private JButton    dbDirButton;
        private JPanel     panel;
        private JTextField bckDirTF;
        private JButton    bckDirButton;
        private JSpinner   bckKeep;
        private JTextField gproURL;

        public GeneralConfiguration(UserCredentials credentials,
                UserConfiguration conf) {
            super(credentials,
                    conf);
            panel = buildPanel();
        }

        private JPanel buildPanel() {
            FormLayout layout = new FormLayout("right:70dlu, 4dlu, 40dlu, 4dlu, 96dlu, 4dlu, 16dlu",
                    "");
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            builder.appendSeparator("Configurações gerais: ");
            checkUpdates = new JCheckBox();
            checkUpdates.setSelected(conf.isCheckUpdates());
            builder.append(checkUpdates);
            builder.append(new JLabel("Verificar atualizações ao iniciar"), 5);
            builder.nextLine();

            dbDirTF = new JTextField();
            dbDirButton = new JButton();
            dbDirTF.setText(conf.getDatabaseDir());
            dbDirTF.setCaretPosition(0);
            dbDirButton.setIcon(UIUtils.createImageIcon("/icons/folder_16.png"));
            builder.append("Banco de dados:");
            builder.append(dbDirTF, 3);
            builder.append(dbDirButton);
            builder.nextLine();

            dbDirButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    int returnVal = fc.showOpenDialog(getParent());

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            dbDirTF.setText(file.getCanonicalPath());
                        } catch (IOException e1) {
                            logger.error("Error selecting database directory", e1);
                        }
                    }
                }
            });

            builder.appendSeparator("Backups: ");
            bckDirTF = new JTextField();
            bckDirButton = new JButton();
            bckDirTF.setText(conf.getBackupDir());
            bckDirTF.setCaretPosition(0);
            bckDirButton.setIcon(UIUtils.createImageIcon("/icons/folder_16.png"));
            builder.append("Diretório de Backup:");
            builder.append(bckDirTF, 3);
            builder.append(bckDirButton);
            builder.nextLine();

            bckKeep = new JSpinner(new SpinnerNumberModel(17, 1, 100, 1));
            bckKeep.setValue(conf.getBackupKeep());
            builder.append("Manter últimos:", bckKeep);
            builder.nextLine();
            bckDirButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    int returnVal = fc.showOpenDialog(getParent());

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            bckDirTF.setText(file.getCanonicalPath());
                        } catch (IOException e1) {
                            logger.error("Error selecting backup directory", e1);
                        }
                    }
                }
            });

            builder.appendSeparator("URLs: ");
            gproURL = new JTextField();
            gproURL.setText( conf.getGproUrl() );
            builder.append( "GPRO:" );
            builder.append( gproURL, 3 );
            builder.nextLine();
            
            return builder.getPanel();
        }

        @Override
        public String getTitle() {
            return "Geral ";
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon("/icons/settings_16.png");
        }

        @Override
        public String getDescription() {
            return "Configurações gerais da aplicação";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_G;
        }

        @Override
        public boolean commit() {
            boolean warnDbChange = !conf.getDatabaseDir().equals(dbDirTF.getText());
            String bckdir = bckDirTF.getText();
            File bckdirf = new File(bckdir);
            if (!bckdirf.exists()) {
                logger.info("Backup directory does not exist (" + bckdir + "). Asking user if it should be created.");
                if (JOptionPane.showConfirmDialog(this.parent,
                        "O diretório de backup não existe.\n" +
                                "Deseja criá-lo?",
                        "Diretório não existe",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    try {
                        logger.info("Creating backup directory...");
                        bckdirf.mkdirs();
                    } catch (Exception e) {
                        logger.error("Error trying to create directory for backup: " + bckdir, e);
                    }
                } else {
                    logger.info("User chose not to create directory.");
                }
            }

            conf.setCheckUpdates(checkUpdates.isSelected());
            conf.setDatabaseDir(dbDirTF.getText());
            conf.setBackupDir(bckdir);
            conf.setBackupKeep(((Number) bckKeep.getValue()).intValue());
            conf.setGproUrl(gproURL.getText());
            if (warnDbChange) {
                JOptionPane.showMessageDialog(this.parent,
                        "Você alterou a pasta do banco de dados. Essa\n" +
                                "alteração terá efeito na próxima vez em que a\n" +
                                "aplicação for iniciada.",
                        "Alteração da pasta do banco de dados",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        }

        @Override
        public boolean rollback() {
            return true; // nothing to do
        }

        @Override
        public Properties getDefaultConfiguration() {
            return null;
        }

        @Override
        public JComponent getPluginComponent() {
            return this.panel;
        }

    }

    public static class SecurityConfiguration extends UIConfPluginBase {

        private JTextField     gproUser;
        private JPasswordField gproPassword;
        private JTextField     gproBrUser;
        private JPasswordField gproBrPassword;
        private JPanel         panel;
        private JLabel         userRole;
        private JLabel         lastAuth;
        private GPROManFrame   frame;

        public SecurityConfiguration(UserCredentials credentials,
                UserConfiguration conf,
                GPROManFrame frame) {
            super(credentials, conf);
            this.frame = frame;
            panel = buildPanel();
        }

        private JPanel buildPanel() {
            FormLayout layout = new FormLayout("right:90dlu, 4dlu, 116dlu, 4dlu, 20dlu",
                    "");
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            // add rows dynamically
            builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            //GPRO
            builder.appendSeparator("GPRO: ");
            gproUser = new JTextField();
            builder.append("Usuário:");
            builder.append(gproUser, 3);
            builder.nextLine();
            gproPassword = new JPasswordField();
            builder.append("Senha:");
            builder.append(gproPassword, 3);
            builder.nextLine();

            //GPROBR
            builder.appendSeparator("GPRO Brasil: ");
            gproBrUser = new JTextField();
            builder.append("Login:");
            builder.append(gproBrUser, 3);
            builder.nextLine();
            gproBrPassword = new JPasswordField();
            builder.append("Senha:");
            builder.append(gproBrPassword, 3);
            builder.nextLine();

            //Role
            userRole = new JLabel();
            userRole.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            builder.append("Grupo:");
            builder.append(userRole, 3);
            builder.nextLine();
            lastAuth = new JLabel();
            lastAuth.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            JButton reauth = new JButton();
            reauth.setIcon(UIUtils.createImageIcon("/icons/reload_16.png"));
            reauth.setToolTipText("Atualiza autenticação do usuário");
            reauth.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        commit();
                        setDirty(true);
                        frame.getGPROBr().authorize(true);
                        frame.showHideEverest();
                        frame.updateData( true );
                    } catch (Exception ex) {
                        logger.error("Error trying to re-authenticate user:", ex);
                        JOptionPane.showMessageDialog(parent,
                                "Erro tentando re-autenticar usuário. Verifique o log para maiores detalhes.",
                                "Erro re-autenticando usuário",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        updateFields();
                        panel.setCursor(Cursor.getDefaultCursor());
                    }
                }
            });
            builder.append("Última autenticação:", lastAuth, reauth);
            builder.nextLine();

            updateFields();

            return builder.getPanel();
        }

        @Override
        public boolean commit() {
            credentials.setGproUser(gproUser.getText());
            credentials.setGproPassword(new String(gproPassword.getPassword()));
            credentials.setGproBrUser(gproBrUser.getText());
            credentials.setGproBrPassword(new String(gproBrPassword.getPassword()));
            return true;
        }

        public void updateFields() {
            gproUser.setText(credentials.getGproUser());
            gproPassword.setText(credentials.getGproPassword());
            gproBrUser.setText(credentials.getGproBrUser());
            gproBrPassword.setText(credentials.getGproBrPassword());
            userRole.setText(credentials.getRole().portuguese);
            if( credentials.getLastAuthentication() != null ) {
                lastAuth.setText(new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss z").format(credentials.getLastAuthentication().getTime()));
            } else {
                lastAuth.setText("");
            }
        }

        @Override
        public boolean rollback() {
            return true; // nothing to do
        }

        @Override
        public String getTitle() {
            return "Segurança ";
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon("/icons/key_16.png");
        }

        @Override
        public String getDescription() {
            return "Configurações de segurança";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_S;
        }

        @Override
        public Properties getDefaultConfiguration() {
            return null;
        }

        @Override
        public JComponent getPluginComponent() {
            return this.panel;
        }

    }

    public static class ProxyConfiguration extends UIConfPluginBase implements ActionListener {

        private JRadioButton   noProxy;
        private JRadioButton   autoProxy;
        private JRadioButton   manualProxy;

        private JTextField     proxyAddr;
        private JTextField     proxyPort;
        private JTextField     proxyUser;
        private JPasswordField proxyPassword;
        private JPanel         panel;

        public ProxyConfiguration(UserCredentials credentials,
                UserConfiguration conf) {
            super(credentials, conf);
            panel = buildPanel();
        }

        private JPanel buildPanel() {
            FormLayout layout = new FormLayout("right:70dlu, 4dlu, 160dlu",
                    "");
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            // add rows dynamically
            builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            //GPRO
            builder.appendSeparator("Proxy: ");
            noProxy = new JRadioButton("Nenhum proxy", ProxyType.NO_PROXY.equals(conf.getProxyType()));
            noProxy.setMnemonic(KeyEvent.VK_N);
            noProxy.addActionListener(this);
            autoProxy = new JRadioButton("Detectar proxy automaticamente", ProxyType.AUTO.equals(conf.getProxyType()));
            autoProxy.setMnemonic(KeyEvent.VK_D);
            autoProxy.addActionListener(this);
            manualProxy = new JRadioButton("Configurar proxy", ProxyType.MANUAL.equals(conf.getProxyType()));
            manualProxy.setMnemonic(KeyEvent.VK_C);
            manualProxy.addActionListener(this);
            ButtonGroup group = new ButtonGroup();
            group.add(noProxy);
            group.add(autoProxy);
            group.add(manualProxy);

            builder.append(noProxy, 3);
            builder.nextLine();
            builder.append(autoProxy, 3);
            builder.nextLine();
            builder.append(manualProxy, 3);
            builder.nextLine();
            proxyAddr = new JTextField();
            proxyPort = new JTextField();
            proxyUser = new JTextField();
            proxyPassword = new JPasswordField();
            builder.append("Servidor:", proxyAddr);
            builder.nextLine();
            builder.append("Porta:", proxyPort);
            builder.nextLine();
            builder.append("Usuário:", proxyUser);
            builder.nextLine();
            builder.append("Senha:", proxyPassword);
            builder.nextLine();

            if (manualProxy.isSelected()) {
                proxyAddr.setText(conf.getProxyAddr());
                proxyPort.setText(String.valueOf(conf.getProxyPort()));
                proxyUser.setText(conf.getProxyUser());
                proxyPassword.setText(conf.getProxyPassword());
            }
            enableManualConfiguration(manualProxy.isSelected());

            return builder.getPanel();
        }

        @Override
        public boolean commit() {
            if (manualProxy.isSelected()) {
                conf.setProxyType(ProxyType.MANUAL);
                conf.setProxyAddr(proxyAddr.getText());
                try {
                    conf.setProxyPort(Integer.parseInt(proxyPort.getText()));
                } catch (Exception ex) {
                    conf.setProxyPort(1080);
                }
                conf.setProxyUser(proxyUser.getText());
                conf.setProxyPassword(new String(proxyPassword.getPassword()));
            } else {
                conf.setProxyType(noProxy.isSelected() ? ProxyType.NO_PROXY : ProxyType.AUTO);
                conf.setProxyAddr("");
                conf.setProxyPort(1080);
                conf.setProxyUser("");
                conf.setProxyPassword("");
            }
            return true;
        }

        @Override
        public boolean rollback() {
            return true; // nothing to do
        }

        @Override
        public String getTitle() {
            return "Proxy ";
        }

        @Override
        public ImageIcon getIcon() {
            return UIUtils.createImageIcon("/icons/proxy_16.png");
        }

        @Override
        public String getDescription() {
            return "Configurações de proxy";
        }

        @Override
        public int getMnemonic() {
            return KeyEvent.VK_P;
        }

        @Override
        public Properties getDefaultConfiguration() {
            return null;
        }

        @Override
        public JComponent getPluginComponent() {
            return this.panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            enableManualConfiguration(manualProxy.isSelected());
        }

        private void enableManualConfiguration(boolean enabled) {
            proxyAddr.setEnabled(enabled);
            proxyPort.setEnabled(enabled);
            proxyUser.setEnabled(enabled);
            proxyPassword.setEnabled(enabled);
        }

    }
}
