/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gproman;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import org.gproman.db.DataService;
import org.gproman.db.DatabaseUpdateManager;
import org.gproman.db.EverestService;
import org.gproman.db.JDBCDataService;
import org.gproman.db.JDBCEverestService;
import org.gproman.model.ApplicationStatus;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.ui.GPROManFrame;
import org.gproman.ui.SplashWindow;
import org.gproman.ui.UIUtils;
import org.gproman.update.GMTUpdateManager;
import org.gproman.update.UpdateInfo;
import org.gproman.util.BackupManager;
import org.gproman.util.BareBonesBrowserLaunch;
import org.gproman.util.ConfigurationManager;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class
 */
public class GproManager {

    private static final Version VERSION         = new Version( 3, 7, 14, "GA" );

    final static Logger          logger          = LoggerFactory.getLogger( GproManager.class );

    public static final String  DB_DOMAIN       = "jdbc:h2:file:%s/gprodata;CIPHER=AES";
    public static final String  EVEREST_DOMAIN  = "jdbc:h2:file:%s/everestdb;CIPHER=AES";
    public static final String  UPDATE_INFO_URL = "http://s3.zetaboards.com/Grand_Prix_RO/single/?p=8108479&t=7661416";

    private DataService          db;
    private EverestService       everest;
    private GPROManFrame         frame;
    private SplashWindow         splashFrame;

    private UserConfiguration    conf;

    public static void main(String[] args) throws ClassNotFoundException,
                                          InstantiationException,
                                          IllegalAccessException,
                                          UnsupportedLookAndFeelException {
        logger.info( "Starting up GPRO Manager " + getVersionString() );
        try {
            for ( LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                if ( "Nimbus".equals( info.getName() ) ) {
                    UIManager.setLookAndFeel( info.getClassName() );
                    break;
                }
            }
        } catch ( Exception e ) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
            logger.warn( "Nimbus look&feel not available." );
        }
        new GproManager().startApplication();
    }

    public GproManager() {
    }

    public void startApplication() {
        this.showSplash();
        updateSplash( 10, "Carregando configurações..." );
        conf = ConfigurationManager.loadConfiguration();
        updateSplash( 20, "Inicializando banco de dados..." );
        int previousSchemaVersion = startDatabase();

        if( previousSchemaVersion > DataService.CURRENT_SCHEMA_VERSION ) {
            logger.error( "Previous database schema ("+previousSchemaVersion+") greater than the supported ("+DataService.CURRENT_SCHEMA_VERSION+") by this GMT vesion." );
            JOptionPane.showMessageDialog( this.frame.getFrame(),
                                           "O banco de dados em uso é de uma versão mais nova\n"+
                                           "do que esta versão do GMT. Impossível continuar.\n"+
                                           "Por favor atualize a versão do GMT.",
                                           "Versão do banco de dados não suportada",
                                           JOptionPane.ERROR_MESSAGE );
            this.disposeSplash();
            terminateApplication();
        } else {
            updateSplash( 40, "Criando interface de usuário..." );
            this.frame = new GPROManFrame( this );
            updateSplash( 60, "Verificando integridade dos dados..." );
            DatabaseUpdateManager.initDatabase( this, db, previousSchemaVersion );
            updateSplash( 70, "Iniciando Everest..." );
            startEverest(null);
            updateSplash( 90, "Iniciando interface de usuário..." );
            this.frame.start();
            this.checkNewVersion();
            this.frame.show();
            this.disposeSplash();
            this.frame.checkFirstExecution();
        }
    }

    private int startDatabase() {
        char[] usr = new char[]{'g', 'p', 'r', 'o', 'm', 'a', 'n'};
        char[] pwd = new char[]{'f', 'e', '1', '3', ' ', 'g', 'm', 't', '1', '3'};
        String dbURL = String.format( DB_DOMAIN, conf.getDatabaseDir() );
        logger.info("Database URL : "+dbURL );
        this.db = new JDBCDataService( dbURL,
                                       new String( usr ),
                                       new String( pwd ) );
        int previousSchemaVersion = this.db.start();
        return previousSchemaVersion;
    }

    public int startEverest(UserCredentials credentials) {
        if( credentials == null ) {
            credentials = CredentialsManager.loadCredentials();
        }
        if ( credentials != null && (
                UserCredentials.UserRole.ADVANCED.equals(credentials.getRole()) ||
                UserCredentials.UserRole.ADMIN.equals(credentials.getRole())) ) {
            char[] usr = new char[]{'e', 'v', 'e', 'r', 'e', 's', 't', 'd', 'b'};
            char[] pwd = new char[]{'t', 'i', 'a', 'n', 'd', '1', '3',' ', 't', 'i', 'a', 'n', 'l'};
            String everestURL = String.format( EVEREST_DOMAIN, conf.getEverestDir() );
            logger.info("Everest URL : "+everestURL );
            this.everest = new JDBCEverestService( everestURL,
                                                    new String( usr ),
                                                    new String( pwd ) );
            int previousSchemaVersion = this.everest.start();
            DatabaseUpdateManager.initEverest(this, everest, previousSchemaVersion);
            return previousSchemaVersion;
        }
        return 0;
    }

    public void terminateApplication() {
        String sufix = null;
        if ( db.wasModified() ) {
            // get next race code to use as a sufix for the backup file
            ApplicationStatus status = db.getApplicationStatus();
            String nextRaceCode = status.getNextRaceCode() != null ? status.getNextRaceCode() : "NA";
            sufix = status != null ? getVersionString() + "." + nextRaceCode : null;
        }

        // close database and resources
        db.shutdown();
        if( everest != null ) {
            everest.shutdown();
        }

        if ( sufix != null ) {
            try {
                BackupManager.backup( new File( conf.getDatabaseDir() ),
                                      new File( conf.getBackupDir() ),
                                      sufix,
                                      conf.getBackupKeep() );
            } catch( Throwable t ) {
                logger.error( "Error trying to create backup file.", t );
                JOptionPane.showMessageDialog( this.frame.getFrame(),
                                               "Erro criando arquivo de backup. Backup não foi criado.\n"+
                                               "Verifique o arquivo gmt.log para maiores detalhes.",
                                               "Erro criando arquivo de backup",
                                               JOptionPane.ERROR_MESSAGE );
            }
        }
        logger.info( "Terminating execution. GMT " + getVersionString() + "." );
    }

    public DataService getDataService() {
        return this.db;
    }

    public EverestService getEverestService() {
        return this.everest;
    }

    public static String getVersionString() {
        return getVersion().toString();
    }

    public static Version getVersion() {
        return VERSION;
    }

    public void checkNewVersion() {
        if ( conf.isCheckUpdates() ) {
            SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    UpdateInfo info = null;
                    try {
                        final GMTUpdateManager manager = new GMTUpdateManager( frame.getGPROBr(), UPDATE_INFO_URL );
                        info = manager.checkLatestVersion().getUpdateInfo();

                        if ( info.getLatestVersion().compareTo( getVersion() ) > 0 ) {
                            // there is a newer version available
                            int n = JOptionPane.showConfirmDialog( frame.getFrame(),
                                                                   "A versao " + info.getLatestVersion() + " do GMT está disponível para download.\nDeseja fazer o download agora?",
                                                                   "Confirmação de Atualização",
                                                                   JOptionPane.YES_NO_OPTION,
                                                                   JOptionPane.QUESTION_MESSAGE );
                            if ( n == JOptionPane.YES_OPTION ) {
                                logger.info( "Launching browser to download GMT from: " + info.getLatestUrl() );
                                BareBonesBrowserLaunch.openURL(info.getLatestUrl());
                            } else {
                                logger.info( "User opted not to download new version." );
                            }
                        }
                    } catch ( Exception e ) {
                        logger.error( "Error downloading new version: " + info, e );
                    }
                    return null;
                }
            };
            worker.execute();
        }
    }

    public void showSplash() {
        splashFrame = null;
        ImageIcon image = UIUtils.createImageIcon( "/icons/splash2.png" );
        if ( image != null ) {
            splashFrame = SplashWindow.splash( null, image );
        } else {
            logger.error( "Splash image not found" );
        }
    }

    public void disposeSplash() {
        if ( splashFrame != null ) {
            splashFrame.dispose();
        }
    }

    public void updateSplash(int progress,
                             String message) {
        if ( splashFrame != null ) {
            splashFrame.update( progress, message );
        }
    }

    public UserConfiguration getConfiguration() {
        return this.conf;
    }

    public void setConfiguration(UserConfiguration configuration) {
        this.conf = configuration;
    }

    public void saveConfiguration() {
        ConfigurationManager.saveConfiguration( conf );
    }

}
