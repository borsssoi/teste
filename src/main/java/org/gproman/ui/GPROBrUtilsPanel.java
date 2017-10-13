package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import org.gproman.miner.TelemetryMiner;
import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.track.Track;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.scrapper.GPROBrUtil.GproBrMember;
import org.gproman.scrapper.TrackInfoWorker;
import org.gproman.scrapper.TrackInfoWorker.PreviousRace;
import org.gproman.util.EverestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class GPROBrUtilsPanel extends UIPluginBase {

    private static final long   serialVersionUID   = 210232127277861273L;

    private static final Logger logger             = LoggerFactory.getLogger(GPROBrUtilsPanel.class);

    private static final String STRATEGY_FORUM_URL = "http://s3.zetaboards.com/Grand_Prix_RO/forum/18996/";
    private static final String RESULTS_FORUM_URL  = "http://s3.zetaboards.com/Grand_Prix_RO/forum/3009454/";

    private final JButton       buildIndex;
    private final JButton       initSeason;
    private final JButton       telemetryBt;
    private final JTextArea     result;

    private JSpinner seasonSpinner;

    public GPROBrUtilsPanel(final GPROManFrame frame,
            final DataService db) {
        super(frame, db);
        setLayout(new BorderLayout());

        FormLayout layout = new FormLayout(
                "60dlu, 4dlu, 34dlu, 4dlu, 98dlu, 4dlu, 98dlu, 4dlu, 98dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        builder.appendSeparator("Início da Temporada: ");

        buildIndex = new JButton("Reconstruir Índice");
        buildIndex.setIcon(UIUtils.createImageIcon("/icons/reload_16.png"));
        buildIndex.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                rebuildIndex(gproManFrame, frame.getGPROBr(), frame.getApplication().getEverestService());
            }
        });
        builder.append(buildIndex, 3);

        initSeason = new JButton("Inicializar Temporada");
        initSeason.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                initializeSeason(frame, db, frame.getApplication().getEverestService());
            }
        });
        initSeason.setIcon(UIUtils.createImageIcon("/icons/FetchData_16.png"));
        builder.append(initSeason);
        builder.nextLine();

        builder.appendSeparator("Relatórios: ");
        
        seasonSpinner = new JSpinner(new SpinnerNumberModel(40, 17, 50, 1));
        JLabel lbl = builder.append("Temporada: ", seasonSpinner);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        configureLabel(lbl, bold);

        telemetryBt = new JButton("Telemetrias");
        telemetryBt.setIcon(UIUtils.createImageIcon("/icons/Telemetry_16.png"));
        telemetryBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTelemetriesReport(gproManFrame, frame.getGPROBr(), db, frame.getApplication().getEverestService());
            }
        });
        builder.append(telemetryBt);
        
        builder.nextLine();

        builder.appendSeparator("Resultados: ");

        result = new JTextArea();
        Font f = result.getFont();
        result.setFont(new Font(Font.MONOSPACED, f.getStyle(), f.getSize()));
        result.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(result);
        resultScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.BLACK)));

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

    private void initializeSeason(final GPROManFrame frame, final DataService db, final EverestService everest) {
        final Season season = db.getCurrentSeason(db.getManager().getName());
        final List<Race> races = season.getRaces();
        result.append("Calendário da Temporada " + season.getNumber() + ":\n");
        for (Race race : races) {
            result.append(String.format("    %2d. %s\n", race.getNumber(), race.getTrack().getName()));
        }

        int n = JOptionPane.showConfirmDialog(frame.getFrame(),
                "Deseja criar os tópicos no forum para a temporada " + season.getNumber() + "?",
                "Criar Tópicos?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (n == JOptionPane.YES_OPTION) {
            SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {

                @Override
                protected Object doInBackground() throws Exception {
                    ProgressMonitor monitor = new ProgressMonitor(frame.getFrame(),
                            "Inicializando o fórum para a temporada " + season.getNumber() + "...               ",
                            "",
                            0,
                            100);
                    monitor.setMillisToDecideToPopup(1);
                    monitor.setMillisToPopup(1);
                    try {
                        TrackInfoWorker tiw = new TrackInfoWorker(null);
                        Map<Race, List<PreviousRace>> cal = new HashMap<Race, List<PreviousRace>>();
                        for (Race race : races) {
                            if (!updateProgress(monitor, race.getNumber(), "Buscando detalhes da corrida " + race.getNumber() + " - " + race.getTrack().getName())) {
                                logger.info("Season initialization cancelled byt the user.");
                                publish("Iniciazação da temporada cancelada pelo usuário");
                                return null;
                            }
                            HtmlPage page = frame.getGPRO().getPage(frame.getConfiguration().getGproUrl()+"/gb/TrackDetails.asp?id=" + race.getTrack().getId());
                            List<PreviousRace> p = tiw.parserPreviousRaces(page);
                            cal.put(race, p);
                        }

                        for (int i = races.size() - 1; i >= 0; i--) {
                            Race race = races.get(i);
                            logger.info("Creating topic for race: " + race.getNumber() + " - " + race.getTrack().getName());
                            publish("Criando tópicos para a corrida " + race.getNumber() + " - " + race.getTrack().getName());

                            String title = race.getTrack().getName();
                            String desc = "Season " + race.getSeasonNumber() + " - Race " + race.getNumber();

                            // reservando post no forum de telemetria
                            if (!waitSixSeconds(monitor, 90-(i*4), race)) {
                                return null;
                            }
                            HtmlPage telemetry = postToForum(frame,
                                    "Telemetria",
                                    gproManFrame.getConfiguration().getProperty(ReportPanel.PROP_REPORT_TELEMETRY),
                                    title,
                                    desc,
                                    "Reservado");

                            // reservando post no forum de setup
                            if (!waitSixSeconds(monitor, 90-(i*4), race)) {
                                return null;
                            }
                            HtmlPage setup = postToForum(frame,
                                    "Setup",
                                    gproManFrame.getConfiguration().getProperty(ReportPanel.PROP_REPORT_SETUP),
                                    title,
                                    desc,
                                    "Reservado");

                            String telemetryURL = telemetry.getUrl().toString();
                            String setupURL = setup.getUrl().toString();
                            String content = generateContent(race, cal.get(race), telemetryURL, setupURL);

                            // edit telemetry content
                            if(! editContent(frame, monitor, 90-(i*4)+2, race, "Telemetria", title, desc, telemetry, content) ) {
                                return null;
                            }
                            // edit setup content
                            if(! editContent(frame, monitor, 90-(i*4)+2, race, "Setup", title, desc, setup, content) ) {
                                return null;
                            }
                            // postando no forum de estratégia
                            if (!waitSixSeconds(monitor, 90-(i*4)+3, race)) {
                                return null;
                            }
                            postToForum(frame,
                                    "Estratégia",
                                    STRATEGY_FORUM_URL,
                                    title,
                                    desc,
                                    content);

                            // postando no forum de resultados
                            if (!waitSixSeconds(monitor, 90-(i*4)+3, race)) {
                                return null;
                            }
                            postToForum(frame,
                                    "Resultados",
                                    RESULTS_FORUM_URL,
                                    title,
                                    desc,
                                    content);
                        }

                    } catch (Exception e) {
                        logger.error("Error initializing season.", e);
                    } finally {
                        monitor.setProgress(100);
                        monitor.close();
                    }
                    return null;
                }

                private HtmlPage postToForum(final GPROManFrame frame,
                        final String msg,
                        final String forumUrl,
                        String title,
                        String desc, 
                        String content) {
                    HtmlPage post = frame.getGPROBr().postNewTopic(forumUrl,
                            title,
                            desc,
                            "",
                            content);
                    if (post != null) {
                        publish("    - " + msg + " criado: " + post.getUrl());
                    } else {
                        publish("    ! Erro criando " + msg + ".");
                    }
                    return post;
                }

                private boolean editContent(final GPROManFrame frame, 
                        ProgressMonitor monitor, 
                        int progress, 
                        Race race, 
                        String msg, 
                        String title, 
                        String desc, 
                        HtmlPage telemetry, 
                        String content) throws InterruptedException {
                    String editUrl = frame.getGPROBr().findEditPostUrl(telemetry);
                    if (editUrl != null) {
                        if (!waitSixSeconds(monitor, progress, race)) {
                            return false;
                        }
                        HtmlPage post = frame.getGPROBr().editTopic(editUrl,
                                title,
                                desc,
                                "",
                                content);
                        if (post != null) {
                            publish("    - " + msg + " editado: " + post.getUrl());
                        } else {
                            publish("    ! Erro editando " + msg + ".");
                        }
                    }
                    return true;
                }

                private String generateContent(Race race, List<PreviousRace> previous, String telemetryURL, String setupURL) {
                    Track track = race.getTrack();
                    StringBuilder content = new StringBuilder();
                    content.append("[color=blue][b]")
                            .append(race.getTrack().getName())
                            .append(" GP[/b][/color]\n")
                            .append(race.getTrack().getName().toUpperCase().replaceAll("\\s+", ""))
                            .append("\n[spoiler][b]")
                            .append(String.format("Race distance: %5.1f\n", track.getDistance()))
                            .append(String.format("Laps: %2d \n", track.getLaps()))
                            .append(String.format("Lap distance: %5.3f kms \n", track.getLapDistance()))
                            .append(String.format("Downforce: %s\n", track.getDownforce().english))
                            .append(String.format("Overtaking: %s\n", track.getOvertaking().english))
                            .append(String.format("Suspension rigidity: %s\n", track.getSuspension().english))
                            .append(String.format("Fuel consumption: %s \n", track.getFuelConsumption().english))
                            .append(String.format("Tyre wear: %s \n", track.getTyreWear().english))
                            .append(String.format("Time in/out of pits: %4.1fs[/b][/spoiler]\n", track.getTimeInOut() / 1000.0))
                            .append("[b][u]Weather forecast[/u][/b]\n")
                            .append("[spoiler][b]Practice / Qualify 1 [/b]\n")
                            .append(" WC \n")
                            .append("Temp: \n")
                            .append("Umidade: \n\n")
                            .append("[b]Qualify 2 / Race Start[/b]\n")
                            .append(" WC \n")
                            .append("Temp: \n")
                            .append("Umidade: \n\n")
                            .append("[u][b]Race Forecast[/b][/u]\n")
                            .append("Início - 0h30m \n")
                            .append("Temp: \n")
                            .append("Umidade: \n")
                            .append("Prob. de chuva: \n\n")
                            .append("0h30m - 1h\n")
                            .append("Temp: \n")
                            .append("Umidade: \n")
                            .append("Prob. de chuva: \n\n")
                            .append("1h - 1h30m \n")
                            .append("Temp: \n")
                            .append("Umidade: \n")
                            .append("Prob. de chuva: \n\n")
                            .append("1h30m - 2h\n")
                            .append("Temp: \n")
                            .append("Umidade: \n")
                            .append("Prob. de chuva: \n\n")
                            .append("[/spoiler]\n")
                            .append("[b][u][color=blue]Seasons Anteriores[/color][/u][/b]\n")
                            .append("[spoiler][b]Setup:[/b]\n");

                    content.append("[url=")
                            .append(setupURL)
                            .append("]S")
                            .append(race.getSeasonNumber())
                            .append(" - R")
                            .append(race.getNumber())
                            .append("[/url] Atual\n");
                    for (int i = previous.size() - 1; i >= 0; i--) {
                        PreviousRace p = previous.get(i);
                        ForumTopic topic = everest.getForumTopic(TopicType.SETUP, p.season, p.race);
                        if (topic != null) {
                            content.append("[url=")
                                    .append(topic.getUrl())
                                    .append("]S")
                                    .append(p.season)
                                    .append(" - R")
                                    .append(p.race)
                                    .append("[/url]\n");
                        } else {
                            logger.error("Setup topic not found for season " + p.season + " race " + p.race);
                            content.append("S")
                                    .append(p.season)
                                    .append(" - R")
                                    .append(p.race)
                                    .append("\n");
                        }
                    }
                    content.append("[b]Telemetria:[/b]\n");
                    content.append("[url=")
                            .append(telemetryURL)
                            .append("]S")
                            .append(race.getSeasonNumber())
                            .append(" - R")
                            .append(race.getNumber())
                            .append("[/url] Atual\n");
                    for (int i = previous.size() - 1; i >= 0; i--) {
                        PreviousRace p = previous.get(i);
                        ForumTopic topic = everest.getForumTopic(TopicType.TELEMETRY, p.season, p.race);
                        if (topic != null) {
                            content.append("[url=")
                                    .append(topic.getUrl())
                                    .append("]S")
                                    .append(p.season)
                                    .append(" - R")
                                    .append(p.race)
                                    .append("[/url]\n");
                        } else {
                            logger.error("Telemetry topic not found for season " + p.season + " race " + p.race);
                            content.append("S")
                                    .append(p.season)
                                    .append(" - R")
                                    .append(p.race)
                                    .append("\n");
                        }
                    }
                    content.append("[/spoiler]\n");
                    return content.toString();
                }

                private boolean waitSixSeconds(ProgressMonitor monitor, int progress, Race race) throws InterruptedException {
                    if (!updateProgress(monitor,
                            progress,
                            "Criando e editando corrida " + race.getNumber() + " - " + race.getTrack().getName())) {
                        logger.info("Season initialization cancelled byt the user.");
                        publish("Iniciazação da temporada cancelada pelo usuário");
                        return false;
                    }
                    Thread.sleep(6000); // have to wait 6 seconds to post next topic
                    return true;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String chunk : chunks) {
                        result.append(chunk + "\n");
                    }
                }

                private boolean updateProgress(ProgressMonitor monitor,
                        int perc,
                        String note) {
                    if (monitor != null) {
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
                }
            };
            worker.execute();
        }
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

    private void createTelemetriesReport(final GPROManFrame frame, final GPROBrUtil browser, final DataService db, final EverestService everest) {
        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                int s = ((Number)seasonSpinner.getValue()).intValue();
                final Season season = db.getSeason(db.getManager().getName(), s);
                
                int n = JOptionPane.showConfirmDialog(frame.getFrame(),
                        "Deseja gerar o relatório de usuários para a temporada " + season.getNumber() + "?",
                        "Relatório de usuários?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (n == JOptionPane.YES_OPTION) {
                    SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {

                        @Override
                        protected Object doInBackground() throws Exception {
                            ProgressMonitor monitor = new ProgressMonitor(frame.getFrame(),
                                    "Gerando relatório de telemetrias para a temporada " + season.getNumber() + "...      ",
                                    "",
                                    0,
                                    100);
                            monitor.setMillisToDecideToPopup(1);
                            monitor.setMillisToPopup(1);
                            try {
                                publish("=====================================================================================================");
                                publish("                       Relatório de Postagem de Telemetrias (temporada "+season.getNumber()+")                           ");
                                publish("=====================================================================================================");
                                List<GproBrMember> list = browser.fetchListOfMembers(monitor, 0, 15);
                                publish("\nMembros do forum: "+list.size()+"\n");
                                Map<String, GproBrMember> members = new HashMap<String, GproBrMember>();
                                for( GproBrMember m : list ) {
                                    members.put(m.name, m);
                                }

                                TelemetryMiner miner = new TelemetryMiner(db, browser);
                                publish("Postagens por corrida:");
                                for( int race = 1; race <= 17 && !monitor.isCanceled(); race++ ) {
                                    ForumTopic topic = everest.getForumTopic(TopicType.TELEMETRY, season.getNumber(), race);
                                    if( topic != null ) {
                                        MiningResult mr = miner.mine(topic.getUrl(), monitor, 15+((race-1)*5), 5, false);
                                        
                                        Set<String> memberNames = mr.getMemberNames();
                                        for( String name : memberNames ) {
                                            GproBrMember member = members.get(name);
                                            if( member != null ) {
                                                member.telemetries++;
                                            }
                                        }
                                        publish( String.format("    #%02d: %3d membros postaram", race, memberNames.size()));
                                    } else {
                                        logger.error("Topic not found in the index...");
                                        publish("    - URL da corrida "+race+" não encontrada no índice.");
                                    }
                                }
                                
                                Collections.sort(list, new Comparator<GproBrMember>() {
                                    @Override
                                    public int compare(GproBrMember o1, GproBrMember o2) {
                                        int group = o1.group.compareTo(o2.group);
                                        return group == 0 ? o1.telemetries - o2.telemetries : group;
                                    }
                                });
                                publish("\nTelemetrias postadas na temporada "+season.getNumber()+":\n");
                                for( GproBrMember member : list ) {
                                    if( ! member.group.equals("Suspensos") && 
                                        ! member.group.equals("Validação") && 
                                        ! member.group.equals("Retired") && 
                                        ! member.group.equals("Férias") ) {
                                        publish(String.format("%-30s (%-15s, %td/%tm/%ty): %2d", member.name, member.group, member.joinDate, member.joinDate, member.joinDate, member.telemetries) );
                                    }
                                }
                                
                            } catch (Exception e) {
                                logger.error("Error counting member's telemetry posts.", e);
                            } finally {
                                monitor.setProgress(100);
                                monitor.close();
                            }
                            return null;
                        }

                        @Override
                        protected void process(List<String> chunks) {
                            for (String chunk : chunks) {
                                result.append(chunk + "\n");
                            }
                        }

                        private boolean updateProgress(ProgressMonitor monitor,
                                int perc,
                                String note) {
                            if (monitor != null) {
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
                        }
                    };
                    worker.execute();
                }
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
        seasonSpinner.setValue( latestSeason );
    }

    @Override
    public String getTitle() {
        return "GPROBrasil ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/gprobr_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/gprobr_16.png");
    }

    @Override
    public String getDescription() {
        return "Ferramentas do GPROBrasil";
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
