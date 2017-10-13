package org.gproman.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.gproman.calc.CarPHACalculator;
import org.gproman.calc.CarWearPlanner;
import org.gproman.calc.CarWearPlanner.PAction;
import org.gproman.calc.CarWearPlanner.PPlan;
import org.gproman.calc.CarWearPlanner.PStep;
import org.gproman.calc.CarWearPlanner.WearStep;
import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.PHA;
import org.gproman.model.car.WearPlan;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.track.Track;
import org.gproman.ui.table.ColumnGroup;
import org.gproman.ui.table.FixedColumnTable;
import org.gproman.ui.table.GroupableTableHeader;
import org.gproman.ui.table.LevelCellRenderer;
import org.gproman.ui.table.LevelComboBoxEditor;
import org.gproman.ui.table.LevelComboBoxRenderer;
import org.gproman.ui.table.MoneyRenderer;
import org.gproman.ui.table.PercentRenderer;
import org.gproman.ui.table.SpinnerEditor;
import org.gproman.ui.table.WearPlanTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.thoughtworks.xstream.XStream;

public class WearPlanningPanel extends UIPluginBase {

    private static final long            serialVersionUID    = 210232127277861273L;

    private static final Logger          logger              = LoggerFactory.getLogger(WearPlanningPanel.class);

    public static final int              FIXED_COLUMNS       = 5;

    //Top panel
    private JLabel                       seasonLbl;
    private JLabel                       planName;
    private JLabel                       racesToTheEnd;
    private JLabel                       totalCost;

    //Buttons panel
    private JButton                      load;
    private JButton                      save;
    private JButton                      delete;
    private JButton                      detail;
    private JButton                      reset;

    //Botton panel
    private JLabel                       raceLbl;
    private JLabel                       power;
    private JLabel                       handling;
    private JLabel                       acceleration;
    private JLabel                       raceCost;

    //Table
    private JTable                       table;
    private FixedColumnTable             fct;

    //Status Panel
    private JLabel                       status;

    //Data
    private CarWearPlanner               planner;
    private WearPlanTableModel           tmodel;

    //Other
    private String                       missingWearCoefsMsg = null;
    private CustomWearDetailDialog 		 detailDialog        = new CustomWearDetailDialog();
    private String                       detailDialogTitle;

    public WearPlanningPanel(GPROManFrame frame,
            DataService db) {
        super(frame, db);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // creates the panels
        JPanel topPanel = buildTopPanel();
        JPanel buttonsPanel = buildButtonsPanel();
        JPanel bottomPanel = buildBottomPanel();
        JPanel statusPanel = buildStatusPanel();

        // creates the base table
        table = initTable();
        table.setPreferredScrollableViewportSize(new Dimension((int) table.getPreferredScrollableViewportSize().getWidth() - 20, 265));

        // creates the table with the fixed columns on the left based on the original table 
        JScrollPane scrollPane = new JScrollPane(table);
        fct = new FixedColumnTable(FIXED_COLUMNS, scrollPane);
        DefaultTableCellRenderer dr = (DefaultTableCellRenderer) fct.getFixedTable().getTableHeader().getDefaultRenderer();
        dr.setHorizontalAlignment(SwingConstants.CENTER);
        table.getTableHeader().setDefaultRenderer(dr);

        // lays out the components
        add(topPanel);
        add(buttonsPanel);
        add(bottomPanel);
        add(scrollPane);
        add(statusPanel);
    }

    private JPanel buildTopPanel() {
        FormLayout layout = new FormLayout("70dlu, 4dlu, 60dlu, 10dlu, 77dlu, 4dlu, 25dlu, 10dlu, 70dlu, 4dlu, 70dlu",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        JComponent separator = builder.appendSeparator("Temporada: ");
        seasonLbl = (JLabel) separator.getComponent(0);
        builder.nextLine();

        planName = new JLabel("<sem nome>");
        JLabel lbl = builder.append("Nome do plano:", planName);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        racesToTheEnd = new JLabel();
        lbl = builder.append("Corridas restantes:", racesToTheEnd);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        totalCost = new JLabel();
        lbl = builder.append("Custo total:", totalCost);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.nextLine();

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel buildButtonsPanel() {
        FormLayout layout = new FormLayout("64dlu, 4dlu, 64dlu, 4dlu, 64dlu, 4dlu, 60dlu, 4dlu, 60dlu, 4dlu, 72dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        load = new JButton("Carregar...");
        load.setIcon(UIUtils.createImageIcon("/icons/open_16.png"));
        load.setMnemonic(KeyEvent.VK_C);
        load.setToolTipText("Carregar um plano previamente salvo...");
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavePlan(Op.LOAD);
            }
        });

        save = new JButton("Salvar...");
        save.setIcon(UIUtils.createImageIcon("/icons/save_16.png"));
        save.setMnemonic(KeyEvent.VK_V);
        save.setToolTipText("Salvar o plano...");
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavePlan(Op.SAVE);
            }
        });

        delete = new JButton("Excluir...");
        delete.setIcon(UIUtils.createImageIcon("/icons/delete_16.png"));
        delete.setMnemonic(KeyEvent.VK_X);
        delete.setToolTipText("Excluir um plano...");
        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadSavePlan(Op.DELETE);
            }
        });

        builder.append(load);
        builder.append(save);
        builder.append(delete);

        reset = new JButton("Recomeçar");
        reset.setIcon(UIUtils.createImageIcon("/icons/trash_16.png"));
        reset.setMnemonic(KeyEvent.VK_M);
        reset.setToolTipText("Recomeçar o planejamento...");
        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(gproManFrame.getFrame(),
                        "Tem certeza que deseja descartar o plano atual e\n" +
                                "recomeçar o planejamento?",
                        "Recomeçar o planejamento?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    tmodel.reset();
                }
            }
        });

        builder.add(reset, CC.xy(11, builder.getRow()));
        builder.nextLine();

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private void enableDetailButton(boolean enable, String labelRace, int selectedRow) {
        detail.setEnabled(enable);
        if (enable) {
            detailDialog.setSelectedRow(selectedRow);
            detailDialog.setPlanner(planner);
            detailDialog.setTmodel(tmodel);
            detail.setToolTipText("Visualizar o detalhe do desgaste para a corrida selecionada...");
            detailDialogTitle = "Visualizando detalhes do desgaste da corrida " + labelRace;
        }
    }

    private JPanel buildBottomPanel() {
        FormLayout layout = new FormLayout("50dlu, 4dlu, 40dlu, 4dlu, 60dlu, 4dlu, 30dlu, 4dlu, 60dlu, 4dlu, 30dlu, 42dlu, 72dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        JComponent separator = builder.appendSeparator("Corrida selecionada: ");
        raceLbl = (JLabel) separator.getComponent(0);

        power = new JLabel();
        JLabel lbl = builder.append("Potência:", power);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        handling = new JLabel();
        lbl = builder.append("Estabilidade:", handling);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        acceleration = new JLabel();
        lbl = builder.append("Aceleração:", acceleration);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);

        detail = new JButton("Detalhe...");
        detail.setIcon(UIUtils.createImageIcon("/icons/views_16.png"));
        detail.setMnemonic(KeyEvent.VK_D);
        detail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                detailDialog.generateDialog();
                JOptionPane.showMessageDialog(gproManFrame.getFrame(),
                        detailDialog,
                        detailDialogTitle,
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        builder.append(detail);

        builder.nextLine();
        raceCost = new JLabel();
        lbl = builder.append("Custo:", raceCost);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);

        JLabel testInformation = new JLabel();
        lbl = builder.append("IMPORTANTE:", testInformation, 6);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold.deriveFont(Font.ITALIC));
        lbl.setForeground(Color.RED);
        testInformation.setText("T1. Voltas antes da troca; T2. Voltas após a troca");
        testInformation.setForeground(Color.RED);
        testInformation.setFont(bold.deriveFont(Font.ITALIC));

        builder.nextLine();

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel buildStatusPanel() {
        FormLayout layout = new FormLayout("404dlu",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        status = new JLabel();
        status.setText("<html>*As projeções de PHA levam em conta os pontos de teste atuais do carro, sem aumentos ou reduções.</html>");
        status.setFont(status.getFont().deriveFont(Font.ITALIC));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        builder.append(status);
        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private void loadSavePlan(Op operation) {
        List<WearPlan> plans = db.loadWearPlans();
        PlanLoadSaveDialog dialog = new PlanLoadSaveDialog(plans,
                operation);

        String title = null;
        switch (operation) {
            case SAVE:
                title = "Salvar plano...";
                break;
            case LOAD:
                title = "Carregar plano...";
                break;
            case DELETE:
                title = "Excluir plano...";
                break;
        }
        int result = JOptionPane.showConfirmDialog(this.gproManFrame.getFrame(),
                dialog,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            dialog.commit();
            switch (operation) {
                case SAVE: {
                    // save it
                    PPlan pplan = planner.marshall();
                    XStream xstream = getXStream();
                    String xml = xstream.toXML(pplan);
                    WearPlan plan = dialog.getSelectedPlan();

                    if (plan == null) {
                        plan = new WearPlan();
                        plan.setName(dialog.getSelectedPlanName());
                    }
                    logger.info("Saving plan..." + plan.getName());
                    plan.setSeason(pplan.season);
                    plan.setRace(pplan.steps.get(0).race);
                    plan.setPlan(xml);
                    db.store(plan);
                    planName.setText(plan.getName());
                    break;
                }
                case LOAD: {
                    // load it
                    WearPlan plan = dialog.getSelectedPlan();
                    if (plan != null) {
                        logger.info("Loading plan..." + plan.getName());
                        XStream xstream = getXStream();
                        PPlan pplan = (PPlan) xstream.fromXML(plan.getPlan());
                        planner.unmarshall(pplan);
                        planner.projectWear(0);
                        tmodel.fireTableDataChanged();
                        planName.setText(plan.getName());
                    }
                    break;
                }
                case DELETE: {
                    // delete it
                    WearPlan plan = dialog.getSelectedPlan();
                    if (plan != null) {
                        logger.info("Deleting plan..." + plan.getName());
                        db.delete(plan);
                    }
                    break;
                }
            }
        } else {
            dialog.rollback();
        }
    }

    private XStream getXStream() {
        XStream xstream = new XStream();
        xstream.alias("pplan", PPlan.class);
        xstream.alias("pstep", PStep.class);
        xstream.alias("paction", PAction.class);
        return xstream;
    }

    /**
     * Creates the base table
     * @return
     */
    private JTable initTable() {
        tmodel = new WearPlanTableModel();
        // replaces the header renderer for one able to draw multi-line headers
        final JTable table = new JTable(tmodel) {

            private static final long serialVersionUID = -7558627309492562817L;

            protected JTableHeader createDefaultTableHeader() {
                GroupableTableHeader gth = new GroupableTableHeader(columnModel);
                return gth;
            }
        };
        table.setRowHeight((int) (table.getRowHeight() * 1.5));
        //table.setShowVerticalLines( true );

        // instantiates the combo box editor for actions
        JComboBox cbeditor = new JComboBox();
        cbeditor.setRenderer(new LevelComboBoxRenderer());
        LevelComboBoxEditor lcbe = new LevelComboBoxEditor(cbeditor);

        // sets all the table properties and renderers
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GroupableTableHeader header = (GroupableTableHeader) table.getTableHeader();
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(30); // race number
        cm.getColumn(1).setPreferredWidth(120); // race track name
        cm.getColumn(2).setPreferredWidth(50); // risk
        cm.getColumn(2).setCellEditor(new SpinnerEditor(new SpinnerNumberModel(0, 0, 100, 1)));

        cm.getColumn(3).setPreferredWidth(50); // T1
        cm.getColumn(4).setPreferredWidth(50); // T2
        cm.getColumn(3).setCellEditor(new SpinnerEditor(new SpinnerNumberModel(0, 0, 100, 1)));
        cm.getColumn(4).setCellEditor(new SpinnerEditor(new SpinnerNumberModel(0, 0, 100, 1)));

        // sets the parts table columns
        LevelCellRenderer lvlRenderer = new LevelCellRenderer();
        PercentRenderer pctRenderer = new PercentRenderer();
        for (int i = WearPlanTableModel.PARTS_FIRST_COLUMN; i < cm.getColumnCount() - 1; i += 2) {
            ColumnGroup g_name = new ColumnGroup(Car.PARTS_PTBR[(i - WearPlanTableModel.PARTS_FIRST_COLUMN) / 2]);
            g_name.add(cm.getColumn(i));
            g_name.add(cm.getColumn(i + 1));
            header.addColumnGroup(g_name);

            cm.getColumn(i).setCellRenderer(lvlRenderer);
            cm.getColumn(i).setCellEditor(lcbe);
            cm.getColumn(i).setPreferredWidth(100);
            cm.getColumn(i + 1).setCellRenderer(pctRenderer);
            cm.getColumn(i + 1).setPreferredWidth(50);
        }

        // sets the cost table column
        MoneyRenderer moneyRenderer = new MoneyRenderer();
        cm.getColumn(cm.getColumnCount() - 1).setCellRenderer(moneyRenderer);
        cm.getColumn(cm.getColumnCount() - 1).setPreferredWidth(100); // cost

        tmodel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                totalCost.setText(formatCurrency(planner.getTotalCost()));
                updateSelectedRow(table.getSelectedRow());
            }
        });

        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateSelectedRow(table.getSelectedRow());
            }
        });

        return table;
    }

    private void updateSelectedRow(int selectedRow) {
        if (selectedRow >= 0 && planner.getSteps().size() > selectedRow) {
            WearStep step = planner.getStep(selectedRow);
            PHA bonusPHA = planner.getStartCar(0).getBonusPHA();
            PHA startPHA = CarPHACalculator.calculateBasePHA(step.getStartCar().getParts());
            PHA basePHA = CarPHACalculator.calculateBasePHA(step.getBaseCar().getParts());
            PHA projPHA = basePHA.add(bonusPHA);
            raceLbl.setText("Corrida " + step.getRace().getNumber() + " - " + step.getTrack().getName() + ": ");
            raceCost.setText(formatCurrency(step.getCost()));
            formatPHA(power, startPHA.getP(), basePHA.getP(), projPHA.getP());
            formatPHA(handling, startPHA.getH(), basePHA.getH(), projPHA.getH());
            formatPHA(acceleration, startPHA.getA(), basePHA.getA(), projPHA.getA());

            enableDetailButton(true, step.getRace().getNumber() + " - " + step.getTrack().getName(), selectedRow);

        } else {
            raceLbl.setText("Corrida selecionada: ");
            raceCost.setText(formatCurrency(0));
            power.setText("");
            handling.setText("");
            acceleration.setText("");

            enableDetailButton(false, null, selectedRow);

        }
    }

    private static Color DARK_GREEN = new Color(0, 128, 0);
    private static Color DARK_RED   = new Color(210, 0, 0);

    private void formatPHA(JLabel lbl, int start, int base, int proj) {
        if (start == base) {
            lbl.setText(String.format("%d", proj));
            lbl.setForeground(Color.black);
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
        } else {
            int diff = base - start;
            lbl.setText(String.format("%d (%+d)", proj, diff));
            lbl.setForeground(diff > 0 ? DARK_GREEN : DARK_RED);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        }
    }

    private String formatCurrency(long amount) {
        return String.format("$%,d", amount);
    }

    public void updateSeason(Season season) {
        this.seasonLbl.setText("Temporada " + season.getNumber() + ":");

        Race nextRace = db.getNextRace();

        if (nextRace != null && nextRace.getTrack() != null && nextRace.getTestSession() != null && nextRace.getTestSession().getTrack() != null) {
            planner = new CarWearPlanner(db.getDriverAttributesWearWeight(),
                    db.getWearCoefs(),
                    nextRace.getTestSession().getTrack());

            List<Track> missingWearData = new ArrayList<Track>();
            for (Race race : season.getRaces()) {
                if (race.getTrack() == null) {
                    // can't plan...
                    this.planner = null;
                    logger.warn("No track record for race S" + race.getSeasonNumber() + " R" + race.getNumber());
                    return;
                }
                if (race.getNumber() >= nextRace.getNumber()) {
                    planner.addRace(race);
                    if (race.getTrack().getWearFactors().getEngineWF() == null) {
                        missingWearData.add(race.getTrack());
                    }
                }
            }
            racesToTheEnd.setText(String.valueOf(planner.getSteps().size()));

            Driver driver = nextRace.getDriverStart();
            if (driver != null) {
                planner.setDriver(driver);
            }
            Car previous = nextRace.getCarStart();
            if (previous != null) {
                planner.setCar(0, previous);
            }
            planner.projectWear(0);
            tmodel.setPlanner(planner);

            tmodel.fireTableRowsInserted(0, planner.getSteps().size() + 1);

            if (!missingWearData.isEmpty()) {
                int tracks = missingWearData.size();
                missingWearCoefsMsg = "Os coeficientes de desgaste não estão disponíveis para\n" +
                        (tracks == 1 ? "a pista de: " + missingWearData.get(0).getName() : "as pistas de:\n");
                if (tracks > 1) {
                    for (Track track : missingWearData) {
                        missingWearCoefsMsg += track.getName() + "\n";
                    }
                }
                missingWearCoefsMsg += "\nO desgaste será zerado " + (tracks == 1 ? "nesta pista.\n" : " nestas pistas.");
            } else {
                missingWearCoefsMsg = null;
            }
        }
    }

    @Override
    public void update() {
        if (isDirty()) {
            Manager manager = db.getManager();
            if (manager != null) {
                Season season = db.getCurrentSeason(manager.getName());
                if (season != null) {
                    logger.info("Updating screen with season data for season '" + season.getNumber() + "'");
                    updateSeason(season);
                }
            }
            setDirty(false);
        }
    }

    @Override
    public ChangeListener getChangeListener(final Component container) {
        return new ChangeListener() {

            boolean warned = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                if (!warned && missingWearCoefsMsg != null && pane.getSelectedComponent() == container) {
                    JOptionPane.showMessageDialog(WearPlanningPanel.super.gproManFrame.getFrame(),
                            missingWearCoefsMsg,
                            "Coeficientes de desgaste não disponíveis",
                            JOptionPane.WARNING_MESSAGE);
                    warned = true;
                }
            }
        };
    }

    @Override
    public String getTitle() {
        return "Planejamento ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/CarPlanning_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/CarPlanning_16.png");
    }

    @Override
    public String getDescription() {
        return "Planejamento de desgaste do carro para a Temporada";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_J;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    static enum Op {
        LOAD, SAVE, DELETE;
    }
}
