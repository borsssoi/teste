package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.gproman.db.DataService;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Race;
import org.gproman.model.race.Race.Stint;
import org.gproman.model.race.Tyre;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.TyreWear;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class TelemetrySearchPanel extends UIPluginBase {

    private static final long       serialVersionUID = 210232127277861273L;

    private final List<Race>        races            = new ArrayList<Race>();
    private RaceSpecifications      specifications;

    // Visual Components
    private final JComboBox fuelComsumption;
    private final JComboBox tyreWear;
    private final JComboBox tyre;

    private final JSpinner          riskMin          = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner          riskMax          = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
    private final JSpinner          tempMin          = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
    private final JSpinner          tempMax          = new JSpinner(new SpinnerNumberModel(50, 0, 50, 1));
    private final JSpinner          humMin           = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
    private final JSpinner          humMax           = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));

    private JTable                  gridRaces        = null;
    private final String[]         columns;
    private static final int      columnsCount     = 16;
    private JLabel                  status;

    public TelemetrySearchPanel(final GPROManFrame gproManFrame, final DataService dataService) {
        super(gproManFrame, dataService);
        //setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setLayout( new BorderLayout());

        FormLayout layout = new FormLayout(
                "60dlu, 4dlu, 70dlu, 10dlu, 70dlu, 4dlu, 30dlu, 4dlu, 10dlu, 4dlu, 30dlu, 20dlu, 100dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        this.fuelComsumption = new JComboBox(new String[]{null,
                FuelConsumption.VERY_LOW.portuguese.toUpperCase(),
                FuelConsumption.LOW.portuguese.toUpperCase(),
                FuelConsumption.MEDIUM.portuguese.toUpperCase(),
                FuelConsumption.HIGH.portuguese.toUpperCase(),
                FuelConsumption.VERY_HIGH.portuguese.toUpperCase()});

        this.tyreWear = new JComboBox(new String[]{null,
                TyreWear.VERY_LOW.portuguese.toUpperCase(),
                TyreWear.LOW.portuguese.toUpperCase(),
                TyreWear.MEDIUM.portuguese.toUpperCase(),
                TyreWear.HIGH.portuguese.toUpperCase(),
                TyreWear.VERY_HIGH.portuguese.toUpperCase()});

        this.tyre = new JComboBox(new String[]{null,
                Tyre.XSOFT.portuguese.toUpperCase(),
                Tyre.SOFT.portuguese.toUpperCase(),
                Tyre.MEDIUM.portuguese.toUpperCase(),
                Tyre.HARD.portuguese.toUpperCase(),
                Tyre.RAIN.portuguese.toUpperCase()});

        // JLabel lWeather = new JLabel("Clima:");
        // JComboBox<Weather> weather = new JComboBox<Weather>(new
        // Weather[]{Weather.SUNNY, Weather.RAIN});

        builder.appendSeparator("Parâmetros da busca: ");
        JLabel lbl = builder.append("Consumo:", fuelComsumption);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        configureLabel(lbl, bold);
        lbl = builder.append("Risco:", riskMin);
        configureLabel(lbl, bold);
        lbl = builder.append("à", riskMax);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Desgaste:", tyreWear);
        configureLabel(lbl, bold);
        lbl = builder.append("Temperatura:", tempMin);
        configureLabel(lbl, bold);
        lbl = builder.append("à", tempMax);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Composto:", tyre);
        configureLabel(lbl, bold);
        lbl = builder.append("Humidade:", humMin);
        configureLabel(lbl, bold);
        lbl = builder.append("à", humMax);
        configureLabel(lbl, bold);

        JButton updateGrid = new JButton("Atualizar Lista");
        updateGrid.setIcon(UIUtils.createImageIcon("/icons/reload_16.png"));
        updateGrid.setToolTipText("Atualiza a pesquisa de acordo com os campos preenchidos");
        updateGrid.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                updateGrid();
            }
        });
        builder.append(updateGrid);
        builder.nextLine();

        builder.appendSeparator("Resultados: ");

        Object[][] data = new Object[][]{{null, null, null, null, null},};
        this.columns = loadColumns();

        this.gridRaces = new JTable(new ReadOnlyTableModel(data, this.columns));
        //this.gridRaces.setPreferredScrollableViewportSize(new Dimension((int) gridRaces.getPreferredScrollableViewportSize().getWidth(), 340));
        this.gridRaces.setPreferredScrollableViewportSize( new Dimension( 1400, 330 ) ); 
        JScrollPane scrollPane = new JScrollPane(gridRaces, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        add(builder.build(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buildStatusPanel(), BorderLayout.SOUTH);

        updateGrid();
    }

    private void configureLabel(JLabel lbl, Font bold) {
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private JPanel buildStatusPanel() {
        FormLayout layout = new FormLayout("404dlu",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        status = new JLabel();
        status.setText("Nenhum stint encontrado!");
        status.setFont(status.getFont().deriveFont(Font.ITALIC));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        builder.append(status);
        JPanel panel = builder.getPanel();
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private String[] loadColumns() {
        return new String[]{"Pista", "Corrida", "Temp", "Risco", "Stint", "Qtd. Voltas", "Razão do Pit", "Tempo do Pit", "Composto",
                "Pneu Utilizado", "Pneu sem bad", "Pneu restante", "Combustível Inicial", "Combustível Restante", "Temperatura",
                "Humidade"};
    }

    private void updateGrid() {
        updateSpecifications();
        List<Object[][]> races = new ArrayList<Object[][]>();
        int stintsCount = 0;
        for (Race race : this.races) {
            Object[][] raceStints = setGridLine(race);
            if ((raceStints != null) && (raceStints.length > 0)) {
                stintsCount += raceStints.length;
                races.add(raceStints);
            }
        }

        Object[][] dataVector = new Object[stintsCount][columnsCount];
        int addTo = 0;
        for (int i = 0; i < races.size(); i++) {
            Object[][] race = races.get(i);
            for (int j = 0; j < (race.length); j++) {
                dataVector[addTo] = race[j];
                addTo++;
            }
        }
        createGrid(dataVector);
        status.setText(stintsCount > 0 ? stintsCount+" stints encontrados!" : "Nenhum stint encontrado!");
    }

    private Object[][] setGridLine(final Race race) {
        int fuel = race.getStartingFuel();
        List<Stint> validStints = this.specifications.getValidStints(race);
        Object[][] stints = null;
        if (validStints != null) {
            stints = new Object[validStints.size()][columnsCount];
            for (int i = 0; i < validStints.size(); i++) {
                Stint stint = validStints.get(i);
                stints[i][0] = race.getTrack().getName();
                stints[i][1] = race.getNumber();
                stints[i][2] = race.getSeasonNumber();
                stints[i][3] = race.getRiskClear();
                stints[i][4] = stint.getNumber();
                stints[i][5] = stint.getLapsCount();
                stints[i][6] = stint.getPitReason();
                stints[i][7] = stint.getPitTime();
                stints[i][8] = stint.getTyre();
                stints[i][9] = stint.getTyreUsed();
                stints[i][10] = stint.getTyreNoBad();
                stints[i][11] = stint.getTyreLeft();
                stints[i][12] = fuel;
                stints[i][13] = stint.getFuelLeft();
                stints[i][14] = stint.getAvgTemp();
                stints[i][15] = stint.getAvgHum();
                fuel = stint.getRefueledTo() != null ? stint.getRefueledTo() : 0;
            }
        }
        return stints;
    }

    private void updateSpecifications() {
        Integer minRiskClear = (Integer) this.riskMin.getValue();
        Integer maxRiskClear = (Integer) this.riskMax.getValue();
        Forecast forecast = new Forecast();
        forecast.setHumidityMin((Integer) this.humMin.getValue());
        forecast.setHumidityMax((Integer) this.humMax.getValue());
        forecast.setTempMin((Integer) this.tempMin.getValue());
        forecast.setTempMax((Integer) this.tempMax.getValue());
        this.specifications = new RaceSpecifications(minRiskClear, maxRiskClear,
                FuelConsumption.fromString((String) this.fuelComsumption.getSelectedItem()),
                TyreWear.fromString((String) this.tyreWear.getSelectedItem()),
                Tyre.determineTyre((String) this.tyre.getSelectedItem()),
                forecast);
    }

    private void createGrid(final Object[][] dataVector) {
        this.gridRaces.setModel(new ReadOnlyTableModel(dataVector, this.columns));
    }

    @Override
    public void update() {
        loadRaces();
        updateGrid();
    }

    private void loadRaces() {
        List<Integer> seasonsForTelemetry = this.db.getSeasonsForTelemetry();
        for (Integer season : seasonsForTelemetry) {
            List<Race> racesForTelemetry = this.db.getRacesForTelemetry(season);
            for (Race race : racesForTelemetry) {
                this.races.add(race);
            }
        }
    }

    @Override
    public String getTitle() {
        return "Pesquisar Telemetrias ";
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
        return "Pesquisa de telemetrias";
    }

    @Override
    public Category getCategory() {
        return Category.REPORT;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_P;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
    
    public static class ReadOnlyTableModel extends DefaultTableModel {
        public ReadOnlyTableModel(Object[][] data, String[] columns) {
            super( data, columns );
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
