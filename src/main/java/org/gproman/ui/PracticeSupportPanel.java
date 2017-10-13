package org.gproman.ui;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gproman.calc.PracticeHelper;
import org.gproman.calc.PracticeHelper.PartSetup;
import org.gproman.db.DataService;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Race;
import org.gproman.scrapper.GPROUtil;
import org.gproman.scrapper.PracticeWorker;
import org.gproman.ui.comp.SetupGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PracticeSupportPanel extends UIPluginBase {

    private static final Logger logger = LoggerFactory.getLogger(PracticeSupportPanel.class);

    private static final String CAR_OK = "Carro está OK";
    private static final long serialVersionUID = 210232127277861273L;
    private static final Color DARK_GREEN = new Color(0, 128, 0);
    private static final Color DARK_RED = new Color(210, 0, 0);
    private static final Color DARK_ORANGE = new Color(210, 100, 100);

    private JLabel[] ptime = new JLabel[8];
    private JLabel[] perror = new JLabel[8];
    private JLabel[] pnet = new JLabel[8];
    private JLabel[] pfwg = new JLabel[8];
    private JLabel[] prwg = new JLabel[8];
    private JLabel[] peng = new JLabel[8];
    private JLabel[] pbra = new JLabel[8];
    private JLabel[] pgea = new JLabel[8];
    private JLabel[] psus = new JLabel[8];
    private JLabel[] ptyr = new JLabel[8];
    private JLabel[] pcom = new JLabel[8];
    private JLabel[] avg = new JLabel[8];

    private JLabel[] sz = new JLabel[6];
    private JLabel[] szDT = new JLabel[6];
    private JLabel[] ia = new JLabel[6];
    private JLabel[] err = new JLabel[6];
    private JComboBox action = null;
    private JSpinner[] adj = new JSpinner[6];
    private SetupGraph[] sg = new SetupGraph[6];
    private JButton refresh = new JButton();
    private JButton copy = new JButton();

    private Race nextRace;
    private PracticeHelper helper;

    private Font bold;
    private Font plain;

    private JLabel wingSplit;
    private JLabel wingSplitCalc;

    private JLabel avgTimeLabel;
    private JLabel avgErrorLabel;
    private JLabel avgNetLabel;

    public PracticeSupportPanel(GPROManFrame gproManFrame,
            DataService dataService) {
        super(gproManFrame,
                dataService);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(buildLapsPanel());
        add(buildSupportPanel());
    }

    private JPanel buildLapsPanel() {
        FormLayout layout = new FormLayout("12dlu, 4dlu, 35dlu, 4dlu, 35dlu, 4dlu, 35dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, "
                + "20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 20dlu, 4dlu, 51dlu, 5dlu, 51dlu",
                "");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        JLabel lbl = new JLabel("Voltas do treino");
        plain = lbl.getFont();
        bold = plain.deriveFont(Font.BOLD);
        createColumnTitle(builder, lbl, 25, Color.BLACK, Color.WHITE);

        //builder.appendSeparator( "Voltas do treino: " );
        builder.nextLine();

        String[] titles = new String[]{"#", "Tempo", "Erro", "Líquido", "AsD", "AsT", "Mot", "Fre", "Câm", "Sus", "Pneu", "Comentário"};
        for (int i = 0; i < titles.length; i++) {
            lbl = new JLabel( titles[i] );
            builder.append( lbl, i == titles.length - 1 ? 3 : 1 );
            lbl.setFont(bold);
            if (i == titles.length - 1) {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
            } else {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }
        builder.nextLine();

        for (int i = 0; i < ptime.length; i++) {
            lbl = builder.append(String.valueOf(i + 1));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(bold);
            if (i % 2 != 0) {
                lbl.setOpaque(true);
                lbl.setBackground(Color.LIGHT_GRAY);
            }

            addLabel(builder, i, ptime, 1 );
            addLabel(builder, i, perror, 1 );
            addLabel(builder, i, pnet, 1 );
            addLabel(builder, i, pfwg, 1 );
            addLabel(builder, i, prwg, 1 );
            addLabel(builder, i, peng, 1 );
            addLabel(builder, i, pbra, 1 );
            addLabel(builder, i, pgea, 1 );
            addLabel(builder, i, psus, 1 );
            addLabel(builder, i, ptyr, 1 );
            addLabel(builder, i, pcom, 3 );
            builder.nextLine();
        }

        lbl = new JLabel("M.");
        builder.append(lbl, 1);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);

        avgTimeLabel = new JLabel();
        builder.append(avgTimeLabel);
        avgErrorLabel = new JLabel();
        builder.append(avgErrorLabel);
        avgNetLabel = new JLabel();
        builder.append(avgNetLabel);
        builder.nextLine();

        lbl = new JLabel("Dif. de asas base: ");
        builder.append(lbl, 5);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);

        wingSplit = new JLabel();
        builder.append(wingSplit, 3);

        lbl = new JLabel("Sugerida: ");
        builder.append(lbl, 3);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);

        wingSplitCalc = new JLabel();
        builder.append(wingSplitCalc, 3);

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchPracticeLaps();
            }
        });
        refresh.setText("Atualizar");
        refresh.setIcon(UIUtils.createImageIcon("/icons/FetchData_16.png"));
        refresh.setEnabled(false);
        builder.add(refresh, CC.xy(23, builder.getRow()));
        copy.setText( "Copy" );
        copy.setIcon( UIUtils.createImageIcon( "/icons/copy_16.png" ) );
        //builder.add(copy, CC.xy( 25, builder.getRow() ) );

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel buildSupportPanel() {
        FormLayout layout = new FormLayout("60dlu, 4dlu, 25dlu, 4dlu, 25dlu, 4dlu, 25dlu, 4dlu, 25dlu, 10dlu, 174dlu, 4dlu, 40dlu"
                + "");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.appendSeparator();
        builder.nextLine();

        JLabel lbl;

        builder.append("");
        lbl = new JLabel("ZS");
        builder.append(lbl, 3);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(Color.DARK_GRAY);

        lbl = new JLabel("Ajuste Ideal");
        builder.append(lbl, 3);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(Color.DARK_GRAY);

        lbl = new JLabel("O que deseja fazer?");
        builder.append(lbl, 3);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(Color.DARK_GRAY);

        builder.nextLine();

        lbl = builder.append("");
        lbl = builder.append("Piloto");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl = builder.append("DT");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl = builder.append("Ajuste");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl = builder.append("± Erro");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        action = new JComboBox(new Object[]{new StartLapAction(), new RefineAction(), new BestSetupAction(), new WingSplitAction(this)});
        ((JLabel) action.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel) action.getRenderer()).setFont(bold);
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedItem = action.getSelectedItem();
                if (selectedItem != null && selectedItem instanceof PracticeAction) {
                    ((PracticeAction) selectedItem).execute(helper, adj, wingSplit);
                }
            }
        });
        builder.append(action);
        lbl = builder.append("Use:");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        builder.nextLine();

        String[] headers = new String[]{"Asa Dianteira: ", "Asa Traseira: ", "Motor: ", "Freio: ", "Câmbio: ", "Suspensão: "};
        for (int i = 0; i < sz.length; i++) {
            lbl = builder.append(headers[i]);
            lbl.setFont(bold);
            lbl.setHorizontalAlignment(SwingConstants.RIGHT);

            sz[i] = new JLabel();
            builder.append(sz[i]);
            sz[i].setHorizontalAlignment(SwingConstants.CENTER);

            szDT[i] = new JLabel();
            builder.append(szDT[i]);
            szDT[i].setHorizontalAlignment(SwingConstants.CENTER);

            ia[i] = new JLabel();
            builder.append(ia[i]);
            ia[i].setHorizontalAlignment(SwingConstants.RIGHT);

            err[i] = new JLabel();
            builder.append(err[i]);
            err[i].setHorizontalAlignment(SwingConstants.LEFT);

            sg[i] = new SetupGraph();
            builder.append(sg[i]);

            adj[i] = new JSpinner(new SpinnerNumberModel(500, 0, 999, 1));
            builder.append(adj[i]);
            builder.nextLine();
        }

        lbl = new JLabel("<html><center>AVISO: esta ferramenta ainda está em desenvolvimento. Use de forma consciente.</center></html>");
        lbl.setFont(lbl.getFont().deriveFont(Font.ITALIC | Font.BOLD));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setForeground(Color.RED);
        builder.append(lbl, 13);
        builder.nextLine();

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private void addLabel(
            DefaultFormBuilder builder,
            int i,
            JLabel[] lbla, int span) {
        lbla[i] = new JLabel();
        if (lbla == pcom) {
            lbla[i].setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            lbla[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
        if (i % 2 != 0) {
            lbla[i].setOpaque(true);
            lbla[i].setBackground(Color.LIGHT_GRAY);
        }
        builder.append(lbla[i], span);
    }

    @Override
    public void update() {
        if (isDirty()) {
            Race previous = nextRace;
            nextRace = db.getNextRace();
            if (nextRace != null) {
                if (helper == null || nextRace != previous ) {
                    helper = new PracticeHelper();
                }
                helper.setTrack(nextRace.getTrack());
                helper.setForecast(nextRace.getForecast());
                helper.setCar(nextRace.getCarStart());
                helper.setDriver(nextRace.getDriverStart());
                helper.setTd(nextRace.getTDStart());
                helper.setPractice(nextRace.getPractice());
                helper.update();
                updatePractice(nextRace);
                refresh.setEnabled(true);
            } else {
                refresh.setEnabled(false);
                cleanupGUI();
                helper = null;
            }
            setDirty(false);
        }
    }

    private void updatePractice(Race nextRace) {
        int avgTime = 0, avgError = 0, avgNet = 0, lapTotal = 0;
        Practice practice = nextRace.getPractice();
        if (practice != null) {
            if (!practice.getLaps().isEmpty()) {
                Lap first = practice.getLaps().get(0);
                Lap minT = first, minE = first, minN = first;
                for (Lap lap : practice.getLaps()) {
                    int i = lap.getNumber() - 1;
                    ptime[i].setForeground(Color.BLACK);
                    perror[i].setForeground(Color.BLACK);
                    pnet[i].setForeground(Color.BLACK);
                    ptime[i].setText(formatTime(lap.getTime()));
                    perror[i].setText(formatTime(lap.getMistake()));
                    pnet[i].setText(formatTime(lap.getNetTime()));
                    minT = lap.getTime() < minT.getTime() ? lap : minT;
                    minE = lap.getMistake() < minE.getMistake() ? lap : minE;
                    minN = lap.getNetTime() < minN.getNetTime() ? lap : minN;
                    CarSettings s = lap.getSettings();
                    pfwg[i].setText(String.valueOf(s.getFrontWing()));
                    prwg[i].setText(String.valueOf(s.getRearWing()));
                    peng[i].setText(String.valueOf(s.getEngine()));
                    pbra[i].setText(String.valueOf(s.getBrakes()));
                    pgea[i].setText(String.valueOf(s.getGear()));
                    psus[i].setText(String.valueOf(s.getSuspension()));
                    ptyr[i].setText(s.getTyre().symbol);
                    String comment = formatComment(lap);
                    pcom[i].setText(comment);
                    pcom[i].setToolTipText(comment);
                    if (CAR_OK == comment) {
                        pcom[i].setForeground(DARK_GREEN);
                        pcom[i].setFont(bold);
                    } else {
                        pcom[i].setForeground(Color.BLACK);
                        pcom[i].setFont(plain);
                    }
                    avgTime += lap.getTime();
                    avgError += lap.getMistake();
                    avgNet += lap.getNetTime();
                    lapTotal++;
                }
                avgTimeLabel.setText(formatTime(avgTime / lapTotal));
                avgErrorLabel.setText(formatTime(avgError / lapTotal));
                avgNetLabel.setText(formatTime(avgNet / lapTotal));

                for (int i = practice.getLaps().size(); i < ptime.length; i++) {
                    ptime[i].setText("");
                    perror[i].setText("");
                    pnet[i].setText("");
                    pfwg[i].setText("");
                    prwg[i].setText("");
                    peng[i].setText("");
                    pbra[i].setText("");
                    pgea[i].setText("");
                    psus[i].setText("");
                    ptyr[i].setText("");
                    pcom[i].setText("");
                    pcom[i].setToolTipText("");
                }
                ptime[minT.getNumber() - 1].setForeground(DARK_GREEN);
                perror[minE.getNumber() - 1].setForeground(DARK_GREEN);
                pnet[minN.getNumber() - 1].setForeground(DARK_GREEN);

                wingSplitCalc.setText(String.valueOf(helper.getBestWingSplit()));
            } else {
                cleanupGUI();
            }
        }

        if (nextRace.getTrack().getWingSplit() != null) {
            wingSplit.setText(String.valueOf(nextRace.getTrack().getWingSplit()));
        } else {
            wingSplit.setText("N/D");
        }

        if (nextRace.getDriverStart() != null) {
            int dsz = helper.getDriver().getSatisfactionZone();
            int[] esz = helper.getEffectiveSZ();
            PartSetup[] ps = helper.getPartSetup();
            for (int i = 0; i < sz.length; i++) {
                sz[i].setText(String.valueOf(dsz));
                if (helper.getTd() != null) {
                    szDT[i].setText(String.valueOf(esz[i]));
                } else {
                    szDT[i].setText("*");
                }
                if (ps[i].getHints().isEmpty()) {
                    ia[i].setText(String.valueOf(helper.getInitialSetup()[i]));
                } else {
                    ia[i].setText(String.valueOf(helper.getPartSetup()[i].getIdealAdjustment()));
                }
                err[i].setText(String.format("± %d", ps[i].getError()));
                err[i].setForeground(ps[i].getError() > 25 ? DARK_RED : (ps[i].getError() > 5 ? DARK_ORANGE : DARK_GREEN));
                err[i].setFont(bold);
                sg[i].updatePart(ps[i]);
            }
            if (helper.getPractice().getLaps().isEmpty()) {
                action.setSelectedIndex(0);
            } else if (helper.getPractice().getLaps().size() == 8) {
                action.setSelectedIndex(2);
            } else {
                action.setSelectedIndex(1);
            }
        }
    }

    private void cleanupGUI() {
        for (int i = 0; i < ptime.length; i++) {
            ptime[i].setText("");
            perror[i].setText("");
            pnet[i].setText("");
            pfwg[i].setText("");
            prwg[i].setText("");
            peng[i].setText("");
            pbra[i].setText("");
            pgea[i].setText("");
            psus[i].setText("");
            ptyr[i].setText("");
            pcom[i].setText("");
            pcom[i].setToolTipText("");
        }
        avgErrorLabel.setText("");
        avgNetLabel.setText("");
        avgTimeLabel.setText("");
        wingSplit.setText("");
        wingSplitCalc.setText("");
        for (int i = 0; i < sz.length; i++) {
            sz[i].setText("");
            szDT[i].setText("");
            ia[i].setText("");
            err[i].setText("");
        }
    }

    private void fetchPracticeLaps() {
        if (nextRace != null) {
            refresh.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    try {
                        logger.info("Refreshing practice laps information...");
                        GPROUtil gpro = gproManFrame.getGPRO();
                        HtmlPage page = gpro.getPage(gproManFrame.getConfiguration().getGproUrl() + PracticeWorker.PRACTICE_URL_SUFFIX);
                        PracticeWorker worker = new PracticeWorker(page);
                        Practice practice = worker.call();
                        logger.info("Practice laps retrieved... saving into the database...");
                        nextRace.setPractice(practice);
                        helper.setPractice(nextRace.getPractice());
                        helper.update();
                        logger.info("Practice data successfully updated.");
                    } catch (Exception e) {
                        logger.error("Error trying to refresh practice laps...", e);
                    }
                    return null;
                }

                @Override
                public void done() {
                    db.store(db.getManager().getName(), nextRace);
                    updatePractice(nextRace);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    refresh.setEnabled(true);
                    for (int i = 0; i < sg.length; i++) {
                        sg[i].repaint();
                    }
                }
            };
            worker.execute();
        }
    }

    private void createColumnTitle(DefaultFormBuilder builder,
            JLabel lbl,
            int span,
            Color bg,
            Color fg) {
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setFont(bold);
        if (bg != null) {
            lbl.setBackground(bg);
        }
        if (fg != null) {
            lbl.setForeground(fg);
        }
        builder.append(lbl, span);
    }

    private String formatComment(Lap lap) {
        String comments = lap.getComments();
        comments = comments.length() > 2 ? comments.substring(1, comments.length() - 1) : CAR_OK;
        return comments;
    }

    private String formatTime(Integer time) {
        int seg = time / 60000;
        int sec = (time % 60000) / 1000;
        int ms = time % 1000;
        return String.format("%d:%02d.%03d",
                seg,
                sec,
                ms);
    }

    @Override
    public String getTitle() {
        return "Treinos ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/Setup_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/Setup_16.png");
    }

    @Override
    public String getDescription() {
        return "Suporte aos treinos";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_E;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public ChangeListener getChangeListener(final Component container) {
        return new ChangeListener() {
            boolean warned = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                if (!warned && nextRace != null && nextRace.getTDStart() != null && pane.getSelectedComponent() == container) {
                    JOptionPane.showMessageDialog(PracticeSupportPanel.super.gproManFrame.getFrame(),
                            "Esta ferramenta ainda está em desenvolvimento. Em particular os\n"
                            + "valores da ZS com Diretor Técnico são aproximações. Estes cálculos\n"
                            + "funcionam bem o suficiente para DTs fracos e médios, mas podem gerar\n"
                            + "discrepâncias para DTs com atributos maiores que 200. Use a ferramenta\n"
                            + "conscientemente.",
                            "Diretor Técnico",
                            JOptionPane.WARNING_MESSAGE);
                    warned = true;
                }
            }
        };
    }

    private static interface PracticeAction {

        public void execute(PracticeHelper helper,
                JSpinner[] adj,
                JLabel wingSplit);
    }

    private static class StartLapAction
            implements
            PracticeAction {

        public String toString() {
            return "Desejo calcular valores para a volta inicial";
        }

        @Override
        public void execute(PracticeHelper helper,
                JSpinner[] adj,
                JLabel wingSplit) {
            int[] init = helper.getInitialSetup();
            for (int i = 0; i < init.length; i++) {
                adj[i].setValue(init[i]);
            }
        }
    }

    private static class RefineAction
            implements
            PracticeAction {

        public String toString() {
            return "Desejo refinar o ajuste";
        }

        @Override
        public void execute(PracticeHelper helper,
                JSpinner[] adj,
                JLabel wingSplit) {
            for (int i = 0; i < adj.length; i++) {
                adj[i].setValue(helper.getPartSetup()[i].getNextSetupValue());
            }
        }
    }

    private static class BestSetupAction
            implements
            PracticeAction {

        public String toString() {
            return "Desejo usar o ajuste ideal";
        }

        @Override
        public void execute(PracticeHelper helper,
                JSpinner[] adj,
                JLabel wingSplit) {
            PartSetup[] ps = helper.getPartSetup();
            for (int i = 0; i < adj.length; i++) {
                adj[i].setValue(ps[i].getIdealAdjustment());
            }
        }
    }

    private static class WingSplitAction
            implements
            PracticeAction {

        private PracticeSupportPanel panel;

        public WingSplitAction(PracticeSupportPanel panel) {
            this.panel = panel;
        }

        public String toString() {
            return "Desejo fazer uma volta de ajuste de asas";
        }

        @Override
        public void execute(PracticeHelper helper,
                JSpinner[] adj,
                JLabel wingSplit) {
            CarSettings s = helper.getWingSplitPracticeLap();
            if (s != null) {
                adj[0].setValue(s.getFrontWing());
                adj[1].setValue(s.getRearWing());
                adj[2].setValue(s.getEngine());
                adj[3].setValue(s.getBrakes());
                adj[4].setValue(s.getGear());
                adj[5].setValue(s.getSuspension());
            } else {
                JOptionPane.showMessageDialog(panel.gproManFrame.getFrame(),
                        "Não foi possível encontrar uma volta de ajuste de asas\n"
                        + "adequada. Escolha um ajuste de asas manualmente e faça\n"
                        + "uma volta. O GMT irá analisar e utilizar os resultados\n"
                        + "da volta normalmente.",
                        "Ajuste de asas",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
