package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.gproman.db.DataService;
import org.gproman.db.EverestService;
import org.gproman.miner.MiningResult;
import org.gproman.miner.ParsingResult;
import org.gproman.miner.TelemetryMiner;
import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.gproman.model.race.Race;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.util.EverestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class TelemetryMinerPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger(TelemetryMinerPanel.class);

    private final JButton       mineBt;
    private final JButton       buildIndex;
    private final JSpinner      startSeason;
    private final JSpinner      startRace;
    private final JSpinner      endSeason;
    private final JSpinner      endRace;
    private final JTextArea     result;

    public TelemetryMinerPanel(final GPROManFrame frame,
            final DataService db) {
        super(frame, db);
        setLayout(new BorderLayout());

        FormLayout layout = new FormLayout(
                "20dlu, 4dlu, 60dlu, 4dlu, 30dlu, 10dlu, 60dlu, 4dlu, 30dlu, 92dlu, 90dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        builder.appendSeparator("Parâmetros da mineração: ");

        startSeason = new JSpinner(new SpinnerNumberModel(40, 17, 60, 1));
        startRace = new JSpinner(new SpinnerNumberModel(1, 1, 17, 1));
        endSeason = new JSpinner(new SpinnerNumberModel(40, 17, 60, 1));
        endRace = new JSpinner(new SpinnerNumberModel(17, 1, 17, 1));

        JLabel lbl = builder.append("De ");
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        configureLabel(lbl, bold);
        lbl = builder.append("Temporada: ", startSeason);
        configureLabel(lbl, bold);
        lbl = builder.append("Corrida: ", startRace);
        configureLabel(lbl, bold);

        buildIndex = new JButton("Reconstruir Índice");
        buildIndex.setIcon(UIUtils.createImageIcon("/icons/reload_16.png"));
        buildIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rebuildIndex(gproManFrame, frame.getGPROBr(), frame.getApplication().getEverestService());
            }
        });
        builder.append(buildIndex);
        builder.nextLine();
        
        lbl = builder.append("Até ");
        configureLabel(lbl, bold);
        lbl = builder.append("Temporada: ", endSeason);
        configureLabel(lbl, bold);
        lbl = builder.append("Corrida: ", endRace);
        configureLabel(lbl, bold);
        mineBt = new JButton("Minerar");
        mineBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mine(frame, db, frame.getApplication().getEverestService());
            }
        });
        mineBt.setIcon(UIUtils.createImageIcon("/icons/FetchData_16.png"));
        builder.append(mineBt);
        builder.nextLine();

        result = new JTextArea();
        Font f = result.getFont();
        result.setFont( new Font( Font.DIALOG, f.getStyle(), f.getSize() ) );
        result.setEditable( false );
        JScrollPane resultScrollPane = new JScrollPane( result );
        resultScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        resultScrollPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ),
                                                                        BorderFactory.createLineBorder( Color.BLACK ) ) );
        
        
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BorderLayout());
        content.add(builder.build(), BorderLayout.NORTH);
        content.add(resultScrollPane, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private void configureLabel(JLabel lbl, Font bold) {
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void mine(final GPROManFrame frame, final DataService db, final EverestService everest) {
        SwingWorker<MiningResult, String> worker = new SwingWorker<MiningResult, String>() {
            @Override
            protected MiningResult doInBackground() throws Exception {
                ProgressMonitor monitor = new ProgressMonitor(frame.getFrame(),
                        "Minerando dados do GPRO Brasil...                          ",
                        "",
                        0,
                        100);
                monitor.setMillisToDecideToPopup(1);
                monitor.setMillisToPopup(1);
                try {
                    int ss = ((Number)startSeason.getValue()).intValue();
                    int se = ((Number)endSeason.getValue()).intValue();
                    int rs = ((Number)startRace.getValue()).intValue();
                    int re = ((Number)endRace.getValue()).intValue();
                    int season = ss;
                    int race = rs;
                    int races = ss == se ? re - rs + 1 : ((se - ss - 1) * 17 + re + 18 - rs );
                    races = races > 0 ? races : 0;
                    int percent = 100 / races;
                    int current = 0;

                    TelemetryMiner miner = new TelemetryMiner(db, frame.getGPROBr());
                    while( shouldContinue(se, re, season, race)) {
                        if (!updateProgress(monitor, current, "Processando dados da temporada "+season+ " corrida "+race ) ) {
                            logger.info("Mining cancelled by the user");
                            publish("Mineração cancelada pelo usuário");
                            break;
                        }
                        List<TyreSupplierAttrs> suppliers = everest.getTyreSuppliersForSeason(season);
                        if( suppliers == null || suppliers.isEmpty() ) {
                            logger.info("Tyre supplier information not found on Everest database for season: "+season+". Looking into GMT database.");
                            suppliers = db.getTyreSuppliersForSeason(season);
                            if( suppliers != null && ! suppliers.isEmpty() ) {
                                logger.info("Tyre supplier information found on GMT database for season: "+season+". Copying it to everest database.");
                                publish( "Carregando dados dos fornecedores de pneus da temporada "+season+ "." );
                                for( TyreSupplierAttrs attr : suppliers ) {
                                    // copy the supplier to everest DB
                                    attr.setId(null);
                                    everest.store(attr);
                                }
                            } else {
                                publish( "Dados dos fornecedores de pneus da temporada "+season+ " não encontrados no banco do GMT." );
                                logger.info("Tyre supplier information not found on GMT database for season: "+season+". Skipping.");
                            }
                        }
                        logger.info("Parsing topic for season "+season+ " race "+race );
                        publish( "Minerando dados da temporada "+season+ " corrida "+race );
                        ForumTopic topic = everest.getForumTopic(TopicType.TELEMETRY, season, race);
                        if( topic != null ) {
                            MiningResult mr = miner.mine(topic.getUrl(), monitor, current, percent, true);
                            List<ParsingResult> successful = mr.getSuccessfulResults();
                            publish( mr.getReportBr() );
                            logger.info("Saving "+successful.size()+" results.");
                            for( ParsingResult pr : successful ) {
                                everest.store(pr.getRace());
                            }
                        } else {
                            logger.error("Topic not found in the index...");
                            publish("    - URL da corrida não encontrada no índice.");
                            publish("    - Lembre-se de recriar o índice após cada reset de temporada do forum!");
                        }
                        race = ( race % 17 ) + 1;
                        season = race == 1 ? season + 1 : season;
                        current += percent;
                        if (!updateProgress(monitor, current, "Temporada "+season+ " corrida "+race +" processada.") ) {
                            logger.info("Mining cancelled by the user");
                            publish("Mineração cancelada pelo usuário");
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error mining telemetries.", e);
                } finally { 
                    monitor.close();
                }
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for( String chunk : chunks ) {
                    result.append(chunk + "\n");
                }
            }

            private boolean shouldContinue(int se, int re, int season, int race) {
                return season < se || 
                        (season == se && race <= re);
            }
            
            private boolean updateProgress(ProgressMonitor monitor,
                    int perc,
                    String note) {
                if( monitor != null ) {
                    if (monitor.isCanceled()) {
                        return false;
                    }
                    monitor.setNote(note);
                    monitor.setProgress(perc);
                }
                return true;
            }
            
            @Override
            protected void done() {
                gproManFrame.updateData( true );
            }
        };
        worker.execute();
    }

    private void rebuildIndex(final GPROManFrame frame, final GPROBrUtil browser, final EverestService db) {
        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {

            @Override
            protected Object doInBackground() throws Exception {
                EverestUtil.rebuildIndex(frame, browser, db);
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void update() {
        EverestService everest = gproManFrame.getApplication().getEverestService();
        EverestMetrics metrics = everest.getEverestMetrics();
        int latestSeason = 40;
        if( metrics.getLatestSeason() != null ) {
            latestSeason = metrics.getLatestSeason().intValue();
        }
        startSeason.setValue( latestSeason );
        endSeason.setValue( latestSeason );

        int lastRace = 17;
        Race nextRace = db.getNextRace();
        if( nextRace != null ) {
            lastRace = nextRace.getNumber() > 1 ? nextRace.getNumber()-1 : 17;
        }
        endRace.setValue( lastRace );
    }

    @Override
    public String getTitle() {
        return "Minerar Dados ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/data_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/data_16.png");
    }

    @Override
    public String getDescription() {
        return "Busca, extrai e filtra dados";
    }
    
    @Override
    public boolean requiresScrollPane() {
        return false;
    }

    @Override
    public Category getCategory() {
        return Category.EVEREST;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_E;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
