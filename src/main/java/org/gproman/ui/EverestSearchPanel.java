package org.gproman.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.gproman.db.DataService;
import org.gproman.db.EverestService;
import org.gproman.db.everest.dao.SearchParams;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedStint;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Race;
import org.gproman.model.race.Tyre;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.Track;
import org.gproman.model.track.TyreWear;
import org.gproman.util.BareBonesBrowserLaunch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class EverestSearchPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;
    private static final Logger logger           = LoggerFactory.getLogger(EverestSearchPanel.class);

    // Visual Components
    private final JComboBox     track;
    private final JComboBox     fuelComsumption;
    private final JComboBox     tyreWear;

    private final JComboBox     tyre;
    private final JComboBox     weather;
    private final JComboBox     supplier;

    private final JSpinner      riskMin          = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner      riskMax          = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
    private final JSpinner      tempMin          = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
    private final JSpinner      tempMax          = new JSpinner(new SpinnerNumberModel(50, 0, 50, 1));
    private final JSpinner      humMin           = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner      humMax           = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));

    private final JSpinner      expMin           = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
    private final JSpinner      expMax           = new JSpinner(new SpinnerNumberModel(500, 0, 500, 1));
    private final JSpinner      tiMin            = new JSpinner(new SpinnerNumberModel(0, 0, 250, 1));
    private final JSpinner      tiMax            = new JSpinner(new SpinnerNumberModel(250, 0, 250, 1));
    private final JSpinner      aggrMin          = new JSpinner(new SpinnerNumberModel(0, 0, 250, 1));
    private final JSpinner      aggrMax          = new JSpinner(new SpinnerNumberModel(250, 0, 250, 1));

    private final JSpinner      lapsMin          = new JSpinner(new SpinnerNumberModel(10, 1, 80, 1));
    private final JSpinner      lapsMax          = new JSpinner(new SpinnerNumberModel(80, 1, 80, 1));
    private final JSpinner      suspMin          = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
    private final JSpinner      suspMax          = new JSpinner(new SpinnerNumberModel(9, 1, 9, 1));
    private final JSpinner      durabMin         = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private final JSpinner      durabMax         = new JSpinner(new SpinnerNumberModel(8, 1, 8, 1));

    private final JLabel        status           = new JLabel();

    private final JTable        resultsTable;

    private EverestService      everest;
    private SearchTableModel    rmodel;

    public EverestSearchPanel(final GPROManFrame gproManFrame, final DataService dataService) {
        super(gproManFrame, dataService);
        everest = gproManFrame.getApplication().getEverestService();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        FormLayout layout = new FormLayout(
                "50dlu, 4dlu, 30dlu, 4dlu, 8dlu, 4dlu, 30dlu, 7dlu, 50dlu, 4dlu, 30dlu, 4dlu, 8dlu, 4dlu, 30dlu, 7dlu, 50dlu, 4dlu, 30dlu, 4dlu, 8dlu, 4dlu, 30dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        this.fuelComsumption = new JComboBox(new String[]{null,
                FuelConsumption.VERY_LOW.portuguese,
                FuelConsumption.LOW.portuguese,
                FuelConsumption.MEDIUM.portuguese,
                FuelConsumption.HIGH.portuguese,
                FuelConsumption.VERY_HIGH.portuguese});

        this.tyreWear = new JComboBox(new String[]{null,
                TyreWear.VERY_LOW.portuguese,
                TyreWear.LOW.portuguese,
                TyreWear.MEDIUM.portuguese,
                TyreWear.HIGH.portuguese,
                TyreWear.VERY_HIGH.portuguese});

        this.tyre = new JComboBox(new String[]{null,
                Tyre.XSOFT.portuguese,
                Tyre.SOFT.portuguese,
                Tyre.MEDIUM.portuguese,
                Tyre.HARD.portuguese,
                Tyre.RAIN.portuguese});

        this.weather = new JComboBox(new String[]{null,
                WeatherType.DRY.portuguese,
                WeatherType.MOSTLY_DRY.portuguese,
                WeatherType.MIXED.portuguese,
                WeatherType.MOSTLY_WET.portuguese,
                WeatherType.WET.portuguese});

        this.supplier = new JComboBox(new String[]{null,
                TyreSupplier.PIPIRELLI.name,
                TyreSupplier.AVONN.name,
                TyreSupplier.YOKOMAMA.name,
                TyreSupplier.DUNNOLOP.name,
                TyreSupplier.CONTIMENTAL.name,
                TyreSupplier.BADYEAR.name,
                TyreSupplier.HANCOCK.name,
                TyreSupplier.MICHELINI.name,
                TyreSupplier.BRIDGEROCK.name});

        this.track = new JComboBox();
        this.track.addItem(null);
        for (Track t : everest.getAllTracks()) {
            this.track.addItem(t);
        }
        this.track.setRenderer(new TrackRenderer());

        // JLabel lWeather = new JLabel("Clima:");
        // JComboBox<Weather> weather = new JComboBox<Weather>(new
        // Weather[]{Weather.SUNNY, Weather.RAIN});

        //builder.appendSeparator("Filtro (passe o mouse sobre o nome do campo para saber mais): ");
        
        JLabel lbl = new JLabel("Filtro ( passe o mouse sobre o nome do campo para saber mais )");
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        UIUtils.createColumnTitle( builder, lbl, 23, Color.BLACK, Color.WHITE, bold );
        
        lbl = builder.append("Pista:");
        configureLabel(lbl, bold, "Filtra pelo nome da pista");
        builder.append(track, 5);
        lbl = builder.append("Consumo:");
        configureLabel(lbl, bold, "Filtra pelo consumo de combustível da pista");
        builder.append(fuelComsumption, 5);
        lbl = builder.append("Desgaste:");
        configureLabel(lbl, bold, "Filtra pelo desgaste de pneu da pista");
        builder.append(tyreWear, 5);
        builder.nextLine();

        lbl = builder.append("Temp:", tempMin);
        configureLabel(lbl, bold, "Filtra pela temperatura média do stint");
        lbl = builder.append("à", tempMax);
        configureLabel(lbl, bold, "Filtra pela temperatura média do stint");
        lbl = builder.append("Umidade:", humMin);
        configureLabel(lbl, bold, "Filtra pela umidade média do stint");
        lbl = builder.append("à", humMax);
        configureLabel(lbl, bold, "Filtra pela umidade média do stint");
        lbl = builder.append("Clima:");
        configureLabel(lbl, bold, "Filtra pelo clima do stint");
        builder.append(weather, 5);
        builder.nextLine();

        lbl = builder.append("Risco:", riskMin);
        configureLabel(lbl, bold, "Filtra pelo risco utilizado em pista livre");
        lbl = builder.append("à", riskMax);
        configureLabel(lbl, bold, "Filtra pelo risco utilizado em pista livre");
        lbl = builder.append("Voltas:", lapsMin);
        configureLabel(lbl, bold, "Filtra pelo número de voltas do stint");
        lbl = builder.append("à", lapsMax);
        configureLabel(lbl, bold, "Filtra pelo número de voltas do stint");
        lbl = builder.append("Suspensão:", suspMin);
        configureLabel(lbl, bold, "Filtra pelo nível da suspensão do carro");
        lbl = builder.append("à", suspMax);
        configureLabel(lbl, bold, "Filtra pelo nível da suspensão do carro");
        builder.nextLine();

        //builder.appendSeparator("Piloto: ");
        lbl = builder.append("Experiência:", expMin);
        configureLabel(lbl, bold, "Filtra pela experiência do piloto");
        lbl = builder.append("à", expMax);
        configureLabel(lbl, bold, "Filtra pela experiência do piloto");
        lbl = builder.append("C. Técnico:", tiMin);
        configureLabel(lbl, bold, "Filtra pelo conhecimento técnico do piloto");
        lbl = builder.append("à", tiMax);
        configureLabel(lbl, bold, "Filtra pelo conhecimento técnico do piloto");
        lbl = builder.append("Agressiv.:", aggrMin);
        configureLabel(lbl, bold, "Filtra pela agressividade do piloto");
        lbl = builder.append("à", aggrMax);
        configureLabel(lbl, bold, "Filtra pela agressividade do piloto");
        builder.nextLine();

        lbl = builder.append("Composto:");
        configureLabel(lbl, bold, "Filtra pelo composto do pneu");
        builder.append(tyre, 5);
        lbl = builder.append("Fornecedor:");
        configureLabel(lbl, bold, "Filtra pelo fornecedor do pneu");
        builder.append(supplier, 5);
        lbl = builder.append("Durabil.:", durabMin);
        configureLabel(lbl, bold, "Filtra pelo atributo de durabilidade do fornecedor do pneu");
        lbl = builder.append("à", durabMax);
        configureLabel(lbl, bold, "Filtra pelo atributo de durabilidade do fornecedor do pneu");
        builder.nextLine();

        JButton search = new JButton("Pesquisar");
        search.setIcon(UIUtils.createImageIcon("/icons/search_16.png"));
        search.setToolTipText("Atualiza a pesquisa de acordo com os campos preenchidos");
        search.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                search();
            }
        });
        JButton clear = new JButton("Limpar");
        clear.setIcon(UIUtils.createImageIcon("/icons/reload_16.png"));
        clear.setToolTipText("Limpa todos os campos");
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                clearParams();
            }
        });
        JButton prefill = new JButton("Próxima corrida");
        prefill.setIcon(UIUtils.createImageIcon("/icons/track_16.png"));
        prefill.setToolTipText("Inicializa o filtro com parâmetros da próxima corrida");
        prefill.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                prefill();
            }
        });
        JPanel btpanel = new JPanel();
        btpanel.setLayout(new BoxLayout(btpanel, BoxLayout.LINE_AXIS));
        btpanel.add(Box.createHorizontalGlue());
        btpanel.add(clear);
        btpanel.add(Box.createRigidArea(new Dimension(15, 0)));
        btpanel.add(prefill);
        btpanel.add(Box.createRigidArea(new Dimension(30, 0)));
        btpanel.add(search);
        builder.append(btpanel, 23);
        builder.nextLine();
        builder.append(new JLabel(""));
        builder.nextLine();

        //builder.appendSeparator("Resultados (duplo-clique na linha para abrir telemetria): ");
        lbl = new JLabel("Resultados ( duplo-clique na linha para abrir telemetria )");
        UIUtils.createColumnTitle( builder, lbl, 23, Color.BLACK, Color.WHITE, bold );

        rmodel = new SearchTableModel();
        resultsTable = new JTable(rmodel);
        JScrollPane sp = new JScrollPane(resultsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setPreferredScrollableViewportSize(new Dimension((int) resultsTable.getPreferredScrollableViewportSize().getWidth(), 240));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) resultsTable.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);        
        TableColumn column = null;
        for (int i = 0; i < SearchTableModel.COLUMN_NAMES.length; i++) {
            column = resultsTable.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                case 1:
                case 2:
                    column.setPreferredWidth(30);
                    break;
                case 3:
                    column.setPreferredWidth(130);
                    break;
                case 4:
                    column.setPreferredWidth(50);
                    break;
                case 5:
                    column.setPreferredWidth(100);
                    break;
                case 6:
                    column.setPreferredWidth(40);
                    break;
                case 7:
                    column.setPreferredWidth(60);
                    DefaultTableCellRenderer r = new DefaultTableCellRenderer();
                    r.setBackground(new Color(192, 255, 192));
                    r.setHorizontalAlignment(SwingConstants.RIGHT);
                    column.setCellRenderer(r);
                    break;
                case 8:
                    column.setPreferredWidth(70);
                    break;
                case 10:
                case 11:
                    column.setPreferredWidth(100);
                    break;
            }
        }
        
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               if (e.getClickCount() == 2) {
                   openForum( resultsTable.getSelectedRow() );
               }
            }
         });        

        add(builder.build());
        add(sp);
        add(buildStatusPanel());
    }

    private void configureLabel(JLabel lbl, Font bold, String tooltip) {
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setToolTipText(tooltip);
    }

    private JPanel buildStatusPanel() {
        FormLayout layout = new FormLayout("404dlu",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        status.setText("Nenhum stint encontrado!");
        status.setFont(status.getFont().deriveFont(Font.ITALIC));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        builder.append(status);
        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private SearchParams getParams() {
        SearchParams p = new SearchParams();
        p.setMinRisk((Integer) this.riskMin.getValue())
                .setMaxRisk((Integer) this.riskMax.getValue())
                .setMinTemp((Integer) this.tempMin.getValue())
                .setMaxTemp((Integer) this.tempMax.getValue())
                .setMinHum((Integer) this.humMin.getValue())
                .setMaxHum((Integer) this.humMax.getValue())
                .setMinExp((Integer) this.expMin.getValue())
                .setMaxExp((Integer) this.expMax.getValue())
                .setMinTI((Integer) this.tiMin.getValue())
                .setMaxTI((Integer) this.tiMax.getValue())
                .setMinAggr((Integer) this.aggrMin.getValue())
                .setMaxAggr((Integer) this.aggrMax.getValue())
                .setMinLaps((Integer) this.lapsMin.getValue())
                .setMaxLaps((Integer) this.lapsMax.getValue())
                .setMinSusp((Integer) this.suspMin.getValue())
                .setMaxSusp((Integer) this.suspMax.getValue())
                .setMinDurab((Integer) this.durabMin.getValue())
                .setMaxDurab((Integer) this.durabMax.getValue())
                .setTyre(Tyre.determineTyre((String) this.tyre.getSelectedItem()))
                .setSupplier(TyreSupplier.determineTyre((String) this.supplier.getSelectedItem()))
                .setWeather(WeatherType.determineWeather((String) this.weather.getSelectedItem()))
                .setTrack((Track) this.track.getSelectedItem())
                .setConsump(FuelConsumption.fromString((String) this.fuelComsumption.getSelectedItem()))
                .setWear(TyreWear.fromString((String) this.tyreWear.getSelectedItem()))
                .setLimit(50);
        return p;
    }

    private void clearParams() {
        track.setSelectedIndex(0);
        fuelComsumption.setSelectedIndex(0);
        tyreWear.setSelectedIndex(0);
        tyre.setSelectedIndex(0);
        weather.setSelectedIndex(0);
        supplier.setSelectedIndex(0);

        riskMin.setValue(0);
        riskMax.setValue(100);
        tempMin.setValue(0);
        tempMax.setValue(50);
        humMin.setValue(0);
        humMax.setValue(100);

        expMin.setValue(0);
        expMax.setValue(500);
        tiMin.setValue(0);
        tiMax.setValue(250);
        aggrMin.setValue(0);
        aggrMax.setValue(250);

        lapsMin.setValue(10);
        lapsMax.setValue(80);
        suspMin.setValue(1);
        suspMax.setValue(9);
        durabMin.setValue(1);
        durabMax.setValue(8);
    }

    public void search() {
        SearchParams params = getParams();
        logger.info("Searching " + params);
        List<NormalizedRace> races = everest.getRaces(params);
        int count = races.size();
        rmodel.updateContent(races, params);
        String val = count < params.getLimit() ? String.valueOf(count) : count + "+";
        int stints = rmodel.getStintsCount();
        logger.info("Found " + val + " races with "+stints+ " stints.");
        status.setText("Foram encontradas " + val + " corridas com "+stints+" stints!");
    }

    private void openForum(int selectedRow) {
        BareBonesBrowserLaunch.openURL( rmodel.getUrl( selectedRow ) );
    }
    
    @Override
    public void update() {
    }

    private void prefill() {
        Race nextRace = db.getNextRace();
        if( nextRace != null ) {
            if( nextRace.getTrack() != null ) {
                track.setSelectedItem(nextRace.getTrack());
            }
            
            Driver driver = nextRace.getDriverStart();
            if( driver != null ) {
                expMin.setValue(driver.getAttributes().getExperience());
                aggrMin.setValue(driver.getAttributes().getAggressiveness());
                tiMin.setValue(driver.getAttributes().getTechInsight());
            }
            
            Forecast[] forecast = nextRace.getForecast();
            int minTemp = 50, minHum = 100, maxTemp = 0, maxHum = 0;
            for( Forecast f : forecast ) {
                if( f != null ) {
                    minTemp = Math.min(minTemp, f.getTempMin() );
                    minHum = Math.min(minHum, f.getHumidityMin() );
                    maxTemp = Math.max(maxTemp, f.getTempMax() );
                    maxHum = Math.max(maxHum, f.getHumidityMax() );
                }
            }
            tempMin.setValue(minTemp);
            tempMax.setValue(maxTemp);
            humMax.setValue(maxHum);
            humMin.setValue(minHum);
            
            Car car = nextRace.getCarStart();
            if( car != null ) {
                CarPart cs = car.getSuspension();
                if( cs != null ) {
                    suspMin.setValue(Math.max(1, cs.getLevel()-1 ));
                    suspMax.setValue(Math.min(9, cs.getLevel()+1 ));
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return "Pesquisar ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/search_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/search_16.png");
    }

    @Override
    public String getDescription() {
        return "Pesquisa de Stints";
    }

    @Override
    public Category getCategory() {
        return Category.EVEREST;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_P;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    private static class SearchTableModel extends AbstractTableModel {

        private static final long     serialVersionUID = -7149522574914798965L;

        public static final String[]  COLUMN_NAMES     = {"S", "R", "#", "Pista", "Voltas", "Composto", "CT",
                                                               "Km", "°C", "Umidade", "Fornecedor", "Desgaste"};
        private List<NormalizedRace>  races            = Collections.emptyList();
        private List<NormalizedStint> stints           = Collections.emptyList();

        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        public String getUrl(int selectedRow) {
            return races.get(selectedRow).getUrl();
        }

        public int getRowCount() {
            return stints.size();
        }

        public String getColumnName(int col) {
            return COLUMN_NAMES[col];
        }

        public Object getValueAt(int row, int col) {
            if (col <= 1 || col == 3 || col == 6 || col >= 10) {
                NormalizedRace race = races.get(row);
                switch (col) {
                    case 0:
                        return race.getSeasonNumber();
                    case 1:
                        return race.getRaceNumber();
                    case 3:
                        return race.getTrack().getName();
                    case 6:
                        return race.getRiskClear();
                    case 10:
                        return race.getSupplier();
                    case 11:
                        return race.getTrack().getTyreWear();
                }
            } else {
                NormalizedStint stint = stints.get(row);
                switch (col) {
                    case 2:
                        return stint.getNumber() + 1;
                    case 4:
                        return stint.getLaps();
                    case 5:
                        return stint.getTyre().portuguese;
                    case 7:
                        return stint.getTyreDurability() != null ? String.format("%6.2f", stint.getTyreDurability().doubleValue()) : "N/D";
                    case 8:
                        return stint.getAvgTemp() != null ? String.format("%5.2f °C", stint.getAvgTemp().doubleValue()) : "N/D";
                    case 9:
                        return stint.getAvgHum() != null ? String.format("%5.2f %%", stint.getAvgHum().doubleValue()) : "N/D";
                }
            }
            return "";
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return false;
        }

        public void updateContent(List<NormalizedRace> data, SearchParams params) {
            this.races = new ArrayList<NormalizedRace>();
            this.stints = new ArrayList<NormalizedStint>();
            for (NormalizedRace r : data) {
                for (NormalizedStint s : r.getStints()) {
                    // have to filter stints here
                    if (s.getLaps() >= params.getMinLaps() && s.getLaps() <= params.getMaxLaps() &&
                            s.getAvgTemp() >= params.getMinTemp() && s.getAvgTemp() <= params.getMaxTemp() &&
                            s.getAvgHum() >= params.getMinHum() && s.getAvgHum() <= params.getMaxHum() &&
                            (params.getTyre() == null || params.getTyre().equals(s.getTyre())) &&
                            (params.getWeather() == null || params.getWeather().equals(s.getWeatherType()))) {
                        stints.add(s);
                        races.add(r);
                    }
                }
            }
            fireTableDataChanged();
        }

        public int getStintsCount() {
            return stints.size();
        }

    }
}
