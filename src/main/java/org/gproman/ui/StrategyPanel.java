package org.gproman.ui;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gproman.calc.CompoundDiffCalculator;
import org.gproman.calc.FuelCalculator;
import org.gproman.calc.StrategyCalculator;
import org.gproman.calc.StrategyCalculator.TopStrategy;
import org.gproman.calc.TyreDurabilityCalculator;
import org.gproman.db.DataService;
import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Race;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategyPanel extends UIPluginBase {

    private static final String[] FORMULAS         = new String[]{"Fórmula 1", "Fórmula 2", "Fórmula 3"};

    private static final Logger   logger           = LoggerFactory.getLogger(StrategyPanel.class);

    private static final long     serialVersionUID = 210232127277861273L;

    private static final int      SUPPLIER_COUNT   = TyreSupplier.values().length;

    private RatingLabel           tfuel            = new RatingLabel();
    private RatingLabel           ttyre            = new RatingLabel();
    private RatingLabel           tovertake        = new RatingLabel();
    private JLabel                tdistance        = new JLabel();
    private JLabel                raceFuelDry      = new JLabel();
    private JLabel                raceFuelWet      = new JLabel();
    private JLabel                raceLaps         = new JLabel();
    private JLabel                tlapDistance     = new JLabel();
    private JLabel                lapFuelDry       = new JLabel();
    private JLabel                lapFuelWet       = new JLabel();
    private JLabel                carEngine        = new JLabel();
    private JLabel                carElectronics   = new JLabel();
    private JLabel                pitInOut         = new JLabel();
    private JLabel                provider         = new JLabel();
    private JComboBox             fuelFormula      = new JComboBox(FORMULAS);

    private JLabel[]              pWeather         = new JLabel[2];
    private JLabel[]              pRain            = new JLabel[7];
    private JLabel[]              pTemp            = new JLabel[7];
    private JLabel[]              pHumidity        = new JLabel[7];
    private JLabel[]              pDiff            = new JLabel[7];

    private JLabel[][]            otherDiffs       = new JLabel[SUPPLIER_COUNT - 1][];
    private JLabel[]              otherSup         = new JLabel[SUPPLIER_COUNT - 1];

    private JSpinner              baseDurab        = new JSpinner(new SpinnerNumberModel(80.0, 20.0, 200.0, 0.1));
    private JSpinner              baseRisk         = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private JSpinner              baseLapTime;
    private JSpinner              baseTemp         = new JSpinner(new SpinnerNumberModel(20.0, 0.0, 50.0, 0.1));
    private JSpinner              basePitTime;
    private JSpinner              baseCompoundDiff;                                                               // = new JSpinner(new SpinnerNumberModel(500, 0, 5000, 1));

    private JLabel[]              durabLaps        = new JLabel[5];
    private JLabel[]              durabDist        = new JLabel[5];

    private JLabel[]              topStrat         = new JLabel[5];
    private JLabel[]              topTime          = new JLabel[5];
    private JLabel[]              topWear          = new JLabel[5];

    private JLabel                track;

    private Race                  nextRace;
    private StrategyCalculator    stratCalc;

    private Date                  start;

    private JSpinner              stPits           = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
    private JSpinner              stCompound       = new JSpinner(new SpinnerListModel(new Object[]{Tyre.XSOFT.portuguese.toUpperCase(),
                                                   Tyre.SOFT.portuguese.toUpperCase(),
                                                   Tyre.MEDIUM.portuguese.toUpperCase(),
                                                   Tyre.HARD.portuguese.toUpperCase(),
                                                   Tyre.RAIN.portuguese.toUpperCase()}));
    private JCheckBox[] stCompoundRain = new JCheckBox[5];
    
    private JButton               stUseTop         = new JButton("Usar Melhor Estratégia");
    private JLabel[]              stLabel          = new JLabel[5];
    private JLabel[]              stRemainingLaps  = new JLabel[5];
    private JSpinner[]            stLaps           = new JSpinner[5];
    private ChangeListener[]      stLapsListener   = new ChangeListener[5];
    private JLabel[]              stWear           = new JLabel[5];
    private JLabel[]              stDryFuel        = new JLabel[5];
    private JLabel[]              stWetFuel        = new JLabel[5];

    private double                lapWetConsumption;
    private double                lapDryConsumption;

    private double[]              durability       = new double[5];

    private Font                  bold;

    private String                warningMsg       = null;
    private String                warningTitle     = null;

    private double                avgHum;

    private TyreSupplier supplier;
    
    private AtomicBoolean         settingContext = new AtomicBoolean(false);     
    

    public StrategyPanel(GPROManFrame gproManFrame,
            DataService dataService) {
        super(gproManFrame,
                dataService);
        ToolTipManager.sharedInstance().setDismissDelay(60000);
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(buildTrackPanel());
        panel.add(buildPartialsPanel());

        tabbedPane.addTab("Informações ",
                UIUtils.createImageIcon("/icons/wheel_24.png"),
                panel,
                "Cálculo do consumo de combustível e diferença de composto");
        tabbedPane.setMnemonicAt(0,
                KeyEvent.VK_N);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(buildTyresPanel());
        panel.add(buildStintsPanel());

        tabbedPane.addTab("Simulação ",
                UIUtils.createImageIcon("/icons/tyres_24.png"),
                panel,
                "Simulação do desgaste de pneus e stints");
        tabbedPane.setMnemonicAt(1,
                KeyEvent.VK_U);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildTrackPanel() {
        FormLayout layout = new FormLayout("73dlu, 4dlu, 53dlu, 7dlu, 63dlu, 4dlu, 63dlu, 7dlu, 63dlu, 4dlu, 63dlu",
                "");
        bold = tdistance.getFont().deriveFont(Font.BOLD);

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        JComponent separator = builder.appendSeparator("Próxima Corrida: ");
        track = (JLabel) separator.getComponent(0);

        JLabel lbl;

        lbl = builder.append("Distância: ", tdistance);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Motor: ", carEngine);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        fuelFormula.setSelectedItem(FORMULAS[2]);
        fuelFormula.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( !settingContext.get() ) {
                    updateFuel();
                    updateStints();
                }
            }
        });
        lbl = builder.append("Cálc. Comb.: ", fuelFormula);
        lbl.setIcon(UIUtils.createImageIcon("/icons/help_16.png"));
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl.setToolTipText("<html>Existem 3 fórmulas de cálculo de combustível "
                + "implementadas no GMT:<br/>"
                + "<ul><li> <b>Fórmula 1:</b> esta é a fórmula antiga usada pelo GMT, que leva<br/>"
                + "somente os níveis do motor e eletrônicos em consideração<br/></li>"
                + "<li> <b>Fórmula 2:</b> nova fórmula que leva em conta a pista, piloto e clima<br/></li>"
                + "<li> <b>Fórmula 3:</b> fórmula mais precisa de todas, também leva em conta pista e piloto<br/></li>"
                + "</ul> Escolha a fórmula que melhor funciona para você. Recomendamos a <b>Fórmula 3</b>, mas<br/>"
                + "use margens de segurança adequadas no início.<br/>"
                + "</html>");
        builder.nextLine();

        lbl = builder.append("Consumo: ", tfuel);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Eletrônicos: ", carElectronics);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Lts/volta seco: ", lapFuelDry);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.nextLine();

        lbl = builder.append("Desgaste: ", ttyre);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Voltas: ", raceLaps);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Lts/volta chuva: ", lapFuelWet);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.nextLine();

        lbl = builder.append("Ultrapassagem: ", tovertake);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Volta: ", tlapDistance);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        //raceFuelDry.setOpaque( true );
        raceFuelDry.setForeground(Color.BLUE);
        raceFuelDry.setFont(bold);
        lbl = builder.append("Comb. seco: ", raceFuelDry);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.nextLine();

        lbl = builder.append("Pit: ", pitInOut);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        lbl = builder.append("Fornecedor: ", provider);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        //raceFuelWet.setOpaque( true );
        raceFuelWet.setForeground(Color.BLUE);
        raceFuelWet.setFont(bold);
        lbl = builder.append("Comb. chuva: ", raceFuelWet);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.nextLine();

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel buildPartialsPanel() {
        FormLayout layout = new FormLayout("61dlu, 4dlu, 45dlu, 4dlu, 45dlu, 4dlu, 45dlu, 4dlu, 45dlu, 4dlu, 45dlu, 4dlu, 45dlu, 4dlu, 45dlu",
                "");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.appendSeparator("Parciais: ");

        builder.append("");
        JLabel lbl = builder.append("Q1");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        lbl = builder.append("Q2");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        lbl = new JLabel("Corrida");
        builder.append(lbl, 9);
        lbl.setFont(bold);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        lbl.setForeground(Color.WHITE);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        builder.nextLine();
        lbl = builder.append("");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < pWeather.length; i++) {
            pWeather[i] = new JLabel();
            pWeather[i].setHorizontalAlignment(SwingConstants.CENTER);
            builder.append(pWeather[i]);
        }
        String[] titles = new String[]{"R.1", "R.2", "R.3", "R.4", "Média"};
        for (int i = 0; i < titles.length; i++) {
            lbl = builder.append(titles[i]);
            lbl.setFont(bold);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
        }
        builder.nextLine();
        lbl = builder.append("Temperatura:");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < pTemp.length; i++) {
            pTemp[i] = new JLabel();
            pTemp[i].setHorizontalAlignment(SwingConstants.CENTER);
            builder.append(pTemp[i]);
        }
        builder.nextLine();
        lbl = builder.append("Chuva:");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < pRain.length; i++) {
            pRain[i] = new JLabel();
            pRain[i].setHorizontalAlignment(SwingConstants.CENTER);
            builder.append(pRain[i]);
        }
        builder.nextLine();
        lbl = builder.append("Humidade:");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < pHumidity.length; i++) {
            pHumidity[i] = new JLabel();
            pHumidity[i].setHorizontalAlignment(SwingConstants.CENTER);
            builder.append(pHumidity[i]);
        }
        builder.nextLine();
        lbl = builder.append("Dif. Composto:");
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < pDiff.length; i++) {
            pDiff[i] = new JLabel();
            pDiff[i].setHorizontalAlignment(SwingConstants.CENTER);
            pDiff[i].setBackground(Color.YELLOW);
            pDiff[i].setOpaque(true);
            builder.append(pDiff[i]);
        }

        builder.appendSeparator("Outros fornecedores: ");
        for (int i = 0; i < SUPPLIER_COUNT - 1; i++) {
            otherSup[i] = new JLabel();
            builder.append(otherSup[i]);
            otherSup[i].setHorizontalAlignment(SwingConstants.CENTER);
            otherDiffs[i] = new JLabel[7];
            for (int j = 0; j < otherDiffs[i].length; j++) {
                otherDiffs[i][j] = new JLabel();
                otherDiffs[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                builder.append(otherDiffs[i][j]);
            }
            builder.nextLine();
        }

        JPanel panel = builder.getPanel();
        return panel;
    }

    private JPanel buildTyresPanel() {
        FormLayout layout = new FormLayout("62dlu, 4dlu, 54dlu, 4dlu, 50dlu, 8dlu, 10dlu, 2dlu, 46dlu, 4dlu, 62dlu, 4dlu, 50dlu, 4dlu, 40dlu",
                "");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.appendSeparator("Estratégia: ");

        JLabel lbl = new JLabel("Durabilidade super-macio (risco 0 - Kms): ");
        builder.append(lbl, 5);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(baseDurab, 3);
        baseDurab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateTyres();
                }
            }
        });

        lbl = new JLabel("Risco Planejado: ");
        builder.append(lbl, 3);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(baseRisk);
        builder.nextLine();
        baseRisk.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateTyres();
                }
            }
        });

        lbl = new JLabel("Estimativa de tempo de uma volta (seco): ");
        builder.append(lbl, 5);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);

        createDateSpinners();

        builder.append(baseLapTime, 3);
        baseLapTime.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateTyres();
                }
            }
        });
        lbl = new JLabel("Temperatura Média: ");
        builder.append(lbl, 3);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(baseTemp);
        builder.nextLine();
        baseTemp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    if( nextRace != null && nextRace.getTrack() != null && supplier != null ) {
                        BigDecimal compoundDiff = CompoundDiffCalculator.predictDiff( nextRace.getTrack(), 
                                ((Number)baseTemp.getValue()).doubleValue(), 
                                supplier );
                        baseCompoundDiff.setValue(new Date(start.getTime() + ((long) (Math.round(compoundDiff.doubleValue() * 1000)))));
                    }
                    updateTyres();
                }
            }
        });

        lbl = new JLabel("Estimativa de tempo médio dos pits: ");
        builder.append(lbl, 5);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(basePitTime, 3);
        basePitTime.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateTyres();
                }
            }
        });
        lbl = new JLabel("Dif. Composto (s): ");
        builder.append(lbl, 3);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(baseCompoundDiff, 1);
        baseCompoundDiff.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateTyres();
                }
            }
        });
        builder.nextLine();

        lbl = new JLabel("Durabilidade");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        lbl.setForeground(Color.white);
        builder.append(lbl, 5);

        lbl = new JLabel("Melhores Estratégias");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        lbl.setForeground(Color.white);
        builder.append(lbl, 9);
        builder.nextLine();

        lbl = builder.append("");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        lbl = builder.append("Voltas");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        lbl = builder.append("Kms");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        lbl = new JLabel("Estratégia");
        builder.append(lbl, 5);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        lbl = builder.append("Tempo");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        lbl = builder.append("Desgaste");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(bold);
        builder.nextLine();

        String[] compounds = new String[]{"Super-macio", "Macio", "Médio", "Duro", "Chuva"};
        for (int i = 0; i < 5; i++) {
            durabLaps[i] = new JLabel();
            durabLaps[i].setHorizontalAlignment(SwingConstants.CENTER);
            durabDist[i] = new JLabel();
            topStrat[i] = new JLabel();
            topTime[i] = new JLabel();
            topTime[i].setHorizontalAlignment(SwingConstants.CENTER);
            topWear[i] = new JLabel();
            topWear[i].setOpaque(true);
            topWear[i].setHorizontalAlignment(SwingConstants.CENTER);
            durabDist[i].setHorizontalAlignment(SwingConstants.RIGHT);
            durabLaps[i].setHorizontalAlignment(SwingConstants.CENTER);
            lbl = builder.append(compounds[i], durabLaps[i], durabDist[i]);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(bold);
            lbl = builder.append((i + 1) + ". ");
            lbl.setFont(bold);
            builder.append(topStrat[i], 3);
            builder.append(topTime[i], topWear[i]);
        }

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private void createDateSpinners() {
        // Java Spinner Date Model has a bug... to work around it, have to be instantiated like this
        Date value = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 1, 30).getTime();
        start = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0).getTime();
        Date end = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 3, 0).getTime();
        baseLapTime = new JSpinner(new SpinnerDateModel(value, start, end, Calendar.SECOND));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(baseLapTime, "m:ss.SSS");
        baseLapTime.setEditor(editor);
        JFormattedTextField tf = editor.getTextField();
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        tf.setEditable(true);

        value = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 25).getTime();
        basePitTime = new JSpinner(new SpinnerDateModel(value, start, end, Calendar.SECOND));
        editor = new JSpinner.DateEditor(basePitTime, "m:ss.SSS");
        basePitTime.setEditor(editor);
        tf = editor.getTextField();
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        tf.setEditable(true);

        value = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 1).getTime();
        baseCompoundDiff = new JSpinner(new SpinnerDateModel(value, start, end, Calendar.SECOND));
        editor = new JSpinner.DateEditor(baseCompoundDiff, "s.SSS");
        baseCompoundDiff.setEditor(editor);
        tf = editor.getTextField();
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        tf.setEditable(true);
    }

    private JPanel buildStintsPanel() {
        FormLayout layout = new FormLayout("60dlu, 4dlu, 64dlu, 4dlu, 64dlu, 4dlu, 64dlu, 4dlu, 64dlu, 4dlu, 64dlu, 4dlu",
                "");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.appendSeparator("Stints: ");

        JLabel lbl = new JLabel("Pits: ");
        builder.append(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(stPits);
        stPits.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    resetStints();
                }    
            }
        });

        lbl = new JLabel("Composto: ");
        builder.append(lbl);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(bold);
        builder.append(stCompound);
        stCompound.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if( !settingContext.get() ) {
                    updateStints();
                }
            }
        });
        
        stUseTop.setIcon(UIUtils.createImageIcon("/icons/award_16.png"));
        builder.append(stUseTop, 3);
        stUseTop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( !settingContext.get() ) {
                    useTopStrategy();
                }
            }
        });
        builder.nextLine();

        builder.append("");
        createColumnTitle(builder, "Voltas", 3);
        createColumnTitle(builder, "Combustível", 3);
        createColumnTitle(builder, "Pneu", 1);
//        createColumnTitle(builder, "Combustível", 1);
//        createColumnTitle(builder, "Pneu", 3);

        String[] titles = new String[]{"", "Restantes", "Stint", "Seco", "Chuva", "Restante"};
//        String[] titles = new String[]{"", "Restantes", "Stint", "Seco | Chuva", "Chuva?", "Restante"};
        for (int i = 0; i < titles.length; i++) {
            lbl = builder.append(titles[i]);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(bold);
        }
        builder.nextLine();

        for (int i = 0; i < stLabel.length; i++) {
            stLabel[i] = new JLabel((i + 1) + "o Stint:");
            stLabel[i].setFont(bold);
            stLabel[i].setHorizontalAlignment(SwingConstants.RIGHT);
            builder.append(stLabel[i]);

            stRemainingLaps[i] = new JLabel();
            stRemainingLaps[i].setHorizontalAlignment(SwingConstants.CENTER);
            builder.append(stRemainingLaps[i]);
            
            stLaps[i] = new JSpinner(new SpinnerNumberModel(1, 1, 80, 1));
            final int fi = i;
            stLapsListener[i] = new ChangeListener() {

                final int index = fi;

                @Override
                public void stateChanged(ChangeEvent e) {
                    JSpinner spinner = (JSpinner) e.getSource();
                    if (spinner.isEnabled()) {
                        int current = ((Number) spinner.getValue()).intValue();
                        int remainingLaps = Integer.parseInt(stRemainingLaps[index].getText());
                        updateRow(index, remainingLaps, current);
                        int last = ((Number) stPits.getValue()).intValue();
                        for (int i = index + 1; i <= last; i++) {
                            remainingLaps -= current;
                            stRemainingLaps[i].setText(String.valueOf(remainingLaps > 0 ? remainingLaps : 0));
                            current = ((Number) stLaps[i].getValue()).intValue();
                        }
                    }
                }
            };
            stLaps[i].addChangeListener(stLapsListener[i]);
            builder.append(stLaps[i]);
            
            stDryFuel[i] = new JLabel();
            stDryFuel[i].setHorizontalAlignment(SwingConstants.CENTER);
            stDryFuel[i].setOpaque(true);
            builder.append(stDryFuel[i]);

            stWetFuel[i] = new JLabel();
            stWetFuel[i].setHorizontalAlignment(SwingConstants.CENTER);
            stWetFuel[i].setOpaque(true);
            builder.append(stWetFuel[i]);
            
//            stCompoundRain[i] = new JCheckBox();
//            stCompoundRain[i].setHorizontalAlignment(SwingConstants.CENTER);
//            stCompoundRain[i].setOpaque(true);
//
//            if (stCompoundRain[i].isSelected()) {
//                builder.append(stWetFuel[i]);
//            }
//            builder.append(stDryFuel[i]);
//            builder.append(stCompoundRain[i]);
                        
            stWear[i] = new JLabel();
            stWear[i].setHorizontalAlignment(SwingConstants.CENTER);
            stWear[i].setOpaque(true);
            builder.append(stWear[i]);
            

            if (i > 0) {
                stLabel[i].setEnabled(false);
                stRemainingLaps[i].setEnabled(false);
                stLaps[i].setEnabled(false);
                stDryFuel[i].setEnabled(false);
                stWetFuel[i].setEnabled(false);
                stWear[i].setEnabled(false);
            } 

            builder.nextLine();
        }

        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private void createColumnTitle(DefaultFormBuilder builder,
            String title,
            int span) {
        JLabel lbl = new JLabel(title);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        lbl.setForeground(Color.white);
        builder.append(lbl, span);
    }

    private void updateRace(TyreSupplier supplier) {
        Car start = nextRace.getCarStart();
        Track t = nextRace.getTrack();
        Driver driver = nextRace.getDriverStart();
        if (driver != null && t != null && driver.getFavoriteTracks().contains(t)) {
            track.setForeground(Color.BLUE);
            track.setText(String.format("Corrida %d - %s *: ", nextRace.getNumber(), t.getName()));
            track.setToolTipText("Pista favorita do piloto: " + driver.getName());
        } else {
            track.setForeground(Color.BLACK);
            track.setText(String.format("Corrida %d - %s: ", nextRace.getNumber(), t.getName()));
            track.setToolTipText(null);
        }
        tdistance.setText(String.format("%4.1f Kms", t.getDistance()));
        setRating(tfuel, t.getFuelConsumption());
        setRating(ttyre, t.getTyreWear());
        setRating(tovertake, t.getOvertaking());
        pitInOut.setText(String.format("%4.1fs", ((double) t.getTimeInOut()) / 1000.0));
        if( start != null ) {
            carEngine.setText(String.valueOf(start.getEngine().getLevel()));
            carElectronics.setText(String.valueOf(start.getElectronics().getLevel()));
        }
        raceLaps.setText(String.valueOf(t.getLaps()));
        tlapDistance.setText(String.format("%5.3f Kms", t.getLapDistance()));

        updateFuel();
        if (supplier != null) {
            provider.setIcon(db.getCurrentSeason(db.getManager().getName()).getSupplier().getIcon());
        }
    }

    private void updateFuel() {
        if (nextRace != null) {
            Car start = nextRace.getCarStart();
            Track t = nextRace.getTrack();

            String algorithm = null;
            if (nextRace.getTrack().getFCon() == null) {
                fuelFormula.setSelectedItem(FORMULAS[0]);
                fuelFormula.setEnabled(false);
                algorithm = FORMULAS[0];
            } else {
                fuelFormula.setEnabled(true);
                algorithm = (String) fuelFormula.getSelectedItem();
            }

            double raceDryConsumption = 0;
            double raceWetConsumption = 0;
            if (FORMULAS[0].equalsIgnoreCase(algorithm)) {
                BigDecimal dryConsumption = FuelCalculator.predictConsumption(Weather.SUNNY,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        t);
                raceDryConsumption = dryConsumption.doubleValue() * t.getDistance();
                BigDecimal wetConsumption = FuelCalculator.predictConsumption(Weather.RAIN,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        t);
                raceWetConsumption = wetConsumption.doubleValue() * t.getDistance();
            } else if (FORMULAS[1].equalsIgnoreCase(algorithm)) {
                raceDryConsumption = FuelCalculator.predictConsumption2(Weather.SUNNY,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        avgHum,
                        t,
                        nextRace.getDriverStart());
                raceWetConsumption = FuelCalculator.predictConsumption2(Weather.RAIN,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        avgHum,
                        t,
                        nextRace.getDriverStart());
            } else if (FORMULAS[2].equalsIgnoreCase(algorithm)) {
                raceDryConsumption = FuelCalculator.predictConsumption3(Weather.SUNNY,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        t,
                        nextRace.getDriverStart());
                raceWetConsumption = FuelCalculator.predictConsumption3(Weather.RAIN,
                        start.getEngine().getLevel(),
                        start.getElectronics().getLevel(),
                        t,
                        nextRace.getDriverStart());
            }

            lapDryConsumption = raceDryConsumption / t.getLaps();
            lapWetConsumption = raceWetConsumption / t.getLaps();
            raceFuelDry.setText(String.format("%6.2f lts", raceDryConsumption));
            lapFuelDry.setText(String.format("%4.2f lts/volta", lapDryConsumption));
            raceFuelWet.setText(String.format("%6.2f lts", raceWetConsumption));
            lapFuelWet.setText(String.format("%4.2f lts/volta", lapWetConsumption));
        }
    }

    private void setRating(RatingLabel rl,
            Enum<?> e) {
        rl.setValue(e.ordinal() + 1);
        rl.setToolTipText(e.toString());
    }

    private void updatePartials(TyreSupplier supplier) {
        double avgTemp = 0;
        avgHum = 0;
        double avgRain = 0;
        Track t = nextRace.getTrack();
        for (int i = 0; i < 6; i++) {
            Forecast forecast = nextRace.getForecast()[i];
            if (forecast != null) {
                if (forecast.getWeather() != null) {
                    pWeather[i].setIcon(forecast.getWeather().getIcon());
                    pWeather[i].setToolTipText(forecast.getWeather().getToolTip());
                }
                double lat = (forecast.getTempMin() + forecast.getTempMax()) / 2.0;
                pTemp[i].setText(formatValue(forecast.getTempMin(), forecast.getTempMax(), "°C"));
                pHumidity[i].setText(formatValue(forecast.getHumidityMin(), forecast.getHumidityMax(), "%"));
                pRain[i].setText(formatValue(forecast.getRainMin(), forecast.getRainMax(), "%"));
                if (supplier != null) {
                    pDiff[i].setText(String.format("%5.3fs", CompoundDiffCalculator.predictDiff(t, lat, supplier)));

                    int j = 0;
                    for (TyreSupplier s : TyreSupplier.values()) {
                        if (!supplier.equals(s)) {
                            otherDiffs[j][i].setText(String.format("%5.3fs", CompoundDiffCalculator.predictDiff(t, lat, s)));
                            j++;
                        }
                    }
                }
                if (i > 1) {
                    avgTemp += lat;
                    avgHum += (forecast.getHumidityMin() + forecast.getHumidityMax()) / 2.0;
                    avgRain += (forecast.getRainMin() + forecast.getRainMax()) / 2.0;
                }
            }
        }
        double lat = avgTemp / 4.0;
        pTemp[6].setText(String.format("%4.1f°C", lat));
        avgHum /= 4.0;
        pHumidity[6].setText(String.format("%4.1f%%", avgHum));
        pRain[6].setText(String.format("%4.1f%%", avgRain / 4.0));
        if (supplier != null) {
            BigDecimal compoundDiff = CompoundDiffCalculator.predictDiff(t, lat, supplier);
            pDiff[6].setText(String.format("%5.3fs", compoundDiff));

            baseCompoundDiff.setValue(new Date(start.getTime() + ((long) (Math.round(compoundDiff.doubleValue() * 1000)))));

            int j = 0;
            for (TyreSupplier s : TyreSupplier.values()) {
                if (!supplier.equals(s)) {
                    otherSup[j].setIcon(s.getIcon());
                    otherDiffs[j][6].setText(String.format("%5.3fs", CompoundDiffCalculator.predictDiff(t, lat, s)));
                    j++;
                }
            }
        }
    }

    private void updateTyres() {
        if (nextRace != null && stratCalc != null ) {
            double base = ((SpinnerNumberModel) baseDurab.getModel()).getNumber().doubleValue();
            int risk = ((SpinnerNumberModel) baseRisk.getModel()).getNumber().intValue();
            int avgPitTime = (int) (((SpinnerDateModel) basePitTime.getModel()).getDate().getTime() - start.getTime());
            int avgLapTime = (int) (((SpinnerDateModel) baseLapTime.getModel()).getDate().getTime() - start.getTime());
            int avgTemp = ((SpinnerNumberModel) baseTemp.getModel()).getNumber().intValue();
            int baseCompDiff = (int) (((SpinnerDateModel) baseCompoundDiff.getModel()).getDate().getTime() - start.getTime());
            stratCalc.setBaseDurability(base)
                    .setRisk(risk)
                    .setAveragePitTime(avgPitTime)
                    .setLapTime(avgLapTime)
                    .setAverageTemperature(avgTemp)
                    .setBaseCompoundDiff(baseCompDiff);

            for (int i = 0; i < durabLaps.length; i++) {
                durability[i] = TyreDurabilityCalculator.predictDurability(base,
                        Tyre.values()[i],
                        risk);
                durabDist[i].setText(String.format("%5.1f Kms", durability[i]));
                durabLaps[i].setText(String.format("%2.0f", Math.floor(durability[i] / nextRace.getTrack().getLapDistance())));
            }
            Iterator<TopStrategy> it = stratCalc.getTopStrategies().iterator();
            TopStrategy previous = null;
            double timeLoss = 0;
            for (int i = 0; i < topStrat.length; i++) {
                if (it.hasNext()) {
                    TopStrategy top = it.next();
                    this.topStrat[i].setText(String.format("Pneu %s com %d pits",
                            top.getCompound().portuguese.toUpperCase(),
                            top.getPits()));
                    if (i == 0) {
                        this.topTime[i].setText("-");
                    } else {
                        timeLoss += top.getTotalTimeLoss() - previous.getTotalTimeLoss();
                        this.topTime[i].setText(String.format("%+5.1fs",
                                timeLoss));
                    }
                    double wear = Math.floor(top.getRemainingTyres() * 100);
                    this.topWear[i].setText(String.format("%2.0f%%",
                            wear));
                    updateWearColor(topWear[i], wear);
                    previous = top;
                } else {
                    this.topStrat[i].setText("");
                    this.topTime[i].setText("");
                    this.topWear[i].setText("");
                    this.topWear[i].setBackground(topTime[i].getBackground());
                }

            }
            updateStints();
        }
    }

    private String formatValue(int min,
            int max,
            String suffix) {
        if (min != max) {
            return String.format("%d%s - %d%s", min, suffix, max, suffix);
        }
        return String.format("%d%s", min, suffix);
    }

    private void useTopStrategy() {
        if (nextRace != null) {
            SortedSet<TopStrategy> topStrategies = stratCalc.getTopStrategies();
            if (!topStrategies.isEmpty()) {
                TopStrategy top = topStrategies.first();
                int value = top.getPits();
                int remainingLaps = nextRace.getTrack().getLaps();

                this.stPits.setValue(top.getPits());
                this.stCompound.setValue(top.getCompound().portuguese.toUpperCase());

                for (int i = 0; i < stLabel.length; i++) {
                    stLaps[i].removeChangeListener(stLapsListener[i]);
                    if (i <= value) {
                        int laps = ((int) Math.ceil(((double) remainingLaps) / ((double) value + 1 - i)));
                        stLaps[i].setValue(laps);
                        updateRow(i, remainingLaps, laps);
                        remainingLaps -= laps;
                    } else {
                        resetRow(i);
                    }
                    stLabel[i].setEnabled(i <= value);
                    stRemainingLaps[i].setEnabled(i <= value);
                    stDryFuel[i].setEnabled(i <= value);
                    stWetFuel[i].setEnabled(i <= value);
                    stWear[i].setEnabled(i <= value);
                    stLaps[i].setEnabled(i <= value);
                    stLaps[i].addChangeListener(stLapsListener[i]);
                }
            }
        }
    }

    private void resetStints() {
        if (nextRace != null) {
            int value = ((Number) stPits.getValue()).intValue();
            int remainingLaps = nextRace.getTrack().getLaps();

            for (int i = 0; i < stLabel.length; i++) {
                stLaps[i].removeChangeListener(stLapsListener[i]);
                if (i <= value) {
                    int laps = ((int) Math.ceil(((double) remainingLaps) / ((double) value + 1 - i)));
                    stLaps[i].setValue(laps);
                    updateRow(i, remainingLaps, laps);
                    remainingLaps -= laps;
                } else {
                    resetRow(i);
                }
                stLabel[i].setEnabled(i <= value);
                stRemainingLaps[i].setEnabled(i <= value);
                stDryFuel[i].setEnabled(i <= value);
                stWetFuel[i].setEnabled(i <= value);
                stWear[i].setEnabled(i <= value);
                stLaps[i].setEnabled(i <= value);
                stLaps[i].addChangeListener(stLapsListener[i]);
            }
        }
    }

    private void updateStints() {
        if (nextRace != null) {
            int value = ((Number) stPits.getValue()).intValue();
            int remainingLaps = nextRace.getTrack().getLaps();
            for (int i = 0; i < stLabel.length; i++) {
                if (i <= value) {
                    int laps = ((SpinnerNumberModel) stLaps[i].getModel()).getNumber().intValue();
                    updateRow(i, remainingLaps, laps);
                    remainingLaps -= laps;
                } else {
                    resetRow(i);
                }
            }
        }
    }
    
    private void updateRow(int i,
            int remainingLaps,
            int stint) {
        stRemainingLaps[i].setText(String.valueOf(remainingLaps));
        double dryFuel = Math.ceil(lapDryConsumption * stint);
        double wetFuel = Math.ceil(lapWetConsumption * stint);
        stDryFuel[i].setText(String.format("%1.0f lts", dryFuel));
        stWetFuel[i].setText(String.format("%1.0f lts", wetFuel));
        int compIndex = Tyre.determineTyre(((String) stCompound.getValue())).diffFactor;
        double wear = Math.floor((1.0 - (stint * nextRace.getTrack().getLapDistance()) / Math.max(durability[compIndex], 1)) * 100.0);
        stWear[i].setText(String.format("%2.0f%%", wear));
        updateWearColor(stWear[i], wear);
        updateFuelColor(stDryFuel[i], dryFuel);
        updateFuelColor(stWetFuel[i], wetFuel);
    }

    private void resetRow(int i) {
        stRemainingLaps[i].setText("");
        stLaps[i].setValue(0);
        stDryFuel[i].setText("");
        stWetFuel[i].setText("");
        stWear[i].setText("");
        updateWearColor(stWear[i], 100);
        updateFuelColor(stDryFuel[i], 0);
        updateFuelColor(stWetFuel[i], 0);
    }

    private void updateWearColor(JLabel component,
            double value) {
        component.setOpaque(value < 18);
        if (value >= 18) {
            // nothing to do
            //component.setBackground( Color.GREEN );
        } else if (value >= 12) {
            component.setBackground(Color.YELLOW);
        } else if (value >= 8) {
            component.setBackground(Color.ORANGE);
        } else {
            component.setBackground(Color.RED);
        }
    }

    private void updateFuelColor(JLabel component,
            double value) {
        component.setOpaque(value >= 90);
        if (value < 90) {
            // nothing to do
            //component.setBackground( Color.GREEN );
        } else if (value < 130) {
            component.setBackground(Color.YELLOW);
        } else if (value < 180) {
            component.setBackground(Color.ORANGE);
        } else {
            component.setBackground(Color.RED);
        }
    }

    @Override
    public void update() {
        if (isDirty()) {
            Race tmp = db.getNextRace();
            if (nextRace != null  && tmp != null && (nextRace.getSeasonNumber() != tmp.getSeasonNumber() || nextRace.getNumber() != tmp.getNumber())) {
                resetStints();
            }
            nextRace = tmp;
            if (nextRace != null) {
                supplier = db.getCurrentSeason(db.getManager().getName()).getSupplier();
                if (supplier == null) {
                    logger.warn("Tyre provider contract not signed yet. Impossible to use the strategy plugin without it.");
                    warningMsg = "Você ainda não assinou o contrato com um provedor de pneus.\n" +
                            "\n\nNão é possível utilizar o plugin de estratégia enquanto o\n" +
                            "contrato não for assinado. Assine o contrato e atualize os\n" +
                            "dados para utilizar o plugin.";
                    warningTitle = "Contrato com provedor de pneus não assinado";
                    updateRace(supplier);
                    enableControls(false);
                } else if (nextRace.getTrack().getCompoundCoef() == null) {
                    logger.warn("Compound coef unavailable for track " + nextRace.getTrack().getName());
                    warningMsg = "Os dados de diferença de composto não estão disponíveis para\n" +
                            "a pista de " + nextRace.getTrack().getName() + ".\n\n" +
                            "Não é possível utilizar o plugin de estratégia sem estes\n" +
                            "dados. Provavelmente esta é uma pista nova e estes dados serão\n" +
                            "adicionados no futuro.\n";
                    warningTitle = "Diferença de compostos não disponível";
                    updateRace(supplier);
                    enableControls(false);
                } else {
                    enableControls(true);
                    warningMsg = null;
                    warningTitle = null;
                    Car carStart = nextRace.getCarStart();
                    stratCalc = new StrategyCalculator(nextRace,
                            carStart.getEngine().getLevel(),
                            carStart.getElectronics().getLevel(),
                            supplier);
                    baseTemp.setValue(Math.round(nextRace.getAverageTemperature()));
                    updatePartials(supplier);
                    updateRace(supplier);
                    updateTyres();
                    resetStints();
                }
            }
            setDirty(false);
        }
    }

    private void enableControls(boolean enable) {
        baseDurab.setEnabled(enable);
        baseRisk.setEnabled(enable);
        baseLapTime.setEnabled(enable);
        baseTemp.setEnabled(enable);
        basePitTime.setEnabled(enable);
        stPits.setEnabled(enable);
        stCompound.setEnabled(enable);
        baseCompoundDiff.setEnabled(enable);
        int value = ((Number) stPits.getValue()).intValue();
        for (int i = 0; i < stLaps.length; i++) {
            stLaps[i].setEnabled(enable && i <= value);
        }
    }

    @Override
    public ChangeListener getChangeListener(final Component container) {
        return new ChangeListener() {

            boolean warned = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                if (!warned && warningMsg != null && pane.getSelectedComponent() == container) {
                    JOptionPane.showMessageDialog(StrategyPanel.super.gproManFrame.getFrame(),
                            warningMsg,
                            warningTitle,
                            JOptionPane.WARNING_MESSAGE);
                    warned = true;
                }
            }
        };
    }

    @Override
    public String getTitle() {
        return "Estratégia ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/strategy_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/strategy_16.png");
    }

    @Override
    public String getDescription() {
        return "Estratégia para a corrida";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_R;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public boolean hasContext() {
        return true;
    }

    @Override
    public UIPluginContext getContext() {
        if (nextRace != null) {
            int[] laps = new int[stLaps.length];
            for (int i = 0; i < laps.length; i++) {
                laps[i] = ((SpinnerNumberModel) stLaps[i].getModel()).getNumber().intValue();
            }
            StrategyPanelContext ctx = new StrategyPanelContext(nextRace.getSeasonNumber(),
                    nextRace.getNumber(),
                    (String) fuelFormula.getSelectedItem(),
                    ((SpinnerNumberModel) baseDurab.getModel()).getNumber().doubleValue(),
                    (long) (((SpinnerDateModel) baseLapTime.getModel()).getDate().getTime() - start.getTime()),
                    (long) (((SpinnerDateModel) basePitTime.getModel()).getDate().getTime() - start.getTime()),
                    ((SpinnerNumberModel) baseRisk.getModel()).getNumber().intValue(),
                    ((SpinnerNumberModel) baseTemp.getModel()).getNumber().intValue(),
                    (long) (((SpinnerDateModel) baseCompoundDiff.getModel()).getDate().getTime() - start.getTime()),
                    ((SpinnerNumberModel) stPits.getModel()).getNumber().intValue(),
                    (String) stCompound.getValue(),
                    laps);
            return ctx;
        }
        return null;
    }

    @Override
    public void setContext(UIPluginContext ctx) {
        if (ctx != null && ctx instanceof StrategyPanelContext) {
            StrategyPanelContext c = (StrategyPanelContext) ctx;
            if (nextRace != null && nextRace.getSeasonNumber() == c.getSeason() && nextRace.getNumber() == c.getRace()) {
                if( settingContext.compareAndSet(false, true) ) {
                    try {
                        fuelFormula.setSelectedItem(c.getFormula());
                        baseDurab.setValue(c.getDurability());
                        baseLapTime.setValue(new Date(start.getTime() + c.getLapDuration()));
                        basePitTime.setValue(new Date(start.getTime() + c.getPitDuration()));
                        baseCompoundDiff.setValue(new Date(start.getTime() + c.getCompoundDiff()));
                        baseRisk.setValue(c.getRisk());
                        baseTemp.setValue(c.getAvgTemp());
                        stPits.setValue(c.getPits());
                        if (c.getCompound() != null) {
                            stCompound.setValue(c.getCompound());
                        }
                        if (c.getLaps() != null) {
                            for (int i = 0; i < stLaps.length && i < c.getLaps().length; i++) {
                                stLaps[i].setValue(c.getLaps()[i]);
                            }
                        }
                    } finally {
                        settingContext.compareAndSet(true, false);
                    }
                    updateFuel();
                    updateTyres();
                    resetStints();
                }
            }
        }
    }

    public static class StrategyPanelContext implements UIPluginContext {

        private static final long serialVersionUID = -4874490076518756252L;
        private int               season;
        private int               race;
        private String            formula;
        private double            durability;
        private long              lapDuration;
        private long              pitDuration;
        private int               risk;
        private double            avgTemp;
        private long              compoundDiff;

        private int               pits;
        private String            compound;
        private int[]             laps;

        public StrategyPanelContext() {
        }

        public StrategyPanelContext(int season,
                int race,
                String formula,
                double durability,
                long lapDuration,
                long pitDuration,
                int risk,
                double avgTemp,
                long compoundDiff,
                int pits,
                String compound,
                int[] laps) {
            super();
            this.season = season;
            this.race = race;
            this.formula = formula;
            this.durability = durability;
            this.lapDuration = lapDuration;
            this.pitDuration = pitDuration;
            this.risk = risk;
            this.avgTemp = avgTemp;
            this.compoundDiff = compoundDiff;
            this.pits = pits;
            this.compound = compound;
            this.laps = laps;
        }

        public int getSeason() {
            return season;
        }

        public void setSeason(int season) {
            this.season = season;
        }

        public int getRace() {
            return race;
        }

        public void setRace(int race) {
            this.race = race;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public double getDurability() {
            return durability;
        }

        public void setDurability(double durability) {
            this.durability = durability;
        }

        public long getLapDuration() {
            return lapDuration;
        }

        public void setLapDuration(long lapDuration) {
            this.lapDuration = lapDuration;
        }

        public long getPitDuration() {
            return pitDuration;
        }

        public void setPitDuration(long pitDuration) {
            this.pitDuration = pitDuration;
        }

        public int getRisk() {
            return risk;
        }

        public void setRisk(int risk) {
            this.risk = risk;
        }

        public double getAvgTemp() {
            return avgTemp;
        }

        public void setAvgTemp(double avgTemp) {
            this.avgTemp = avgTemp;
        }

        public long getCompoundDiff() {
            return compoundDiff;
        }

        public void setCompoundDiff(long compoundDiff) {
            this.compoundDiff = compoundDiff;
        }

        public int getPits() {
            return pits;
        }

        public void setPits(int pits) {
            this.pits = pits;
        }

        public String getCompound() {
            return compound;
        }

        public void setCompound(String compound) {
            this.compound = compound;
        }

        public int[] getLaps() {
            return laps;
        }

        public void setLaps(int[] laps) {
            this.laps = laps;
        }

    }
}
