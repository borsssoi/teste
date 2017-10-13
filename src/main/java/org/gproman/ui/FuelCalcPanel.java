package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.FuelCalculator;
import org.gproman.db.DataService;
import org.gproman.model.race.Weather;
import org.gproman.model.track.Track;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class FuelCalcPanel extends UIPluginBase {

    private static final long serialVersionUID = 210232127277861273L;

    private JSpinner          engine;
    private JSpinner          elect;
    private JComboBox         track;

    private JLabel            fuelDry          = new JLabel();
    private JLabel            fuelWet          = new JLabel();
    private JLabel            fuelPerLapDry    = new JLabel();
    private JLabel            fuelPerLapWet    = new JLabel();

    private Date              start;

    private boolean           updating;

    public FuelCalcPanel(GPROManFrame gproManFrame,
            DataService dataService) {
        super(gproManFrame,
                dataService);
        setLayout(new BorderLayout());

        // Building the second column
        FormLayout layout = new FormLayout("80dlu, 4dlu, 80dlu", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        this.engine = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
        this.engine.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updateFuel();
            }
        });
        this.elect = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
        this.elect.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updateFuel();
            }
        });
        this.track = new JComboBox();
        this.track.setRenderer(new TrackRenderer());
        this.track.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateFuel();
            }
        });

        builder.appendSeparator("Parâmetros:");
        JLabel lbl = builder.append("Pista: ", track);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Motor: ", engine);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Eletrônicos: ", elect);
        configureLabel(lbl, bold);
        builder.nextLine();

        builder.appendSeparator("Consumo:");
        lbl = builder.append("Seco: ", fuelDry);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Chuva: ", fuelWet);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Volta seco: ", fuelPerLapDry);
        configureLabel(lbl, bold);
        builder.nextLine();

        lbl = builder.append("Volta chuva: ", fuelPerLapWet);
        configureLabel(lbl, bold);
        builder.nextLine();
        builder.append("");
        builder.nextLine();
        builder.appendSeparator("Aviso:");

        JLabel msg = new JLabel();
        msg.setText("<html>Esta calculadora somente leva em conta<br/>"
                + "o nível de motor e eletrônicos, e portanto <br/>"
                + "possui uma precisão baixa. Use margens de <br/>"
                + "segurança.</html>");
        builder.append(msg, 3);

        JPanel panel = builder.getPanel();
        add(panel, BorderLayout.WEST);
    }

    private void configureLabel(JLabel lbl, Font bold) {
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void updateFuel() {
        Track t = (Track) track.getSelectedItem();
        if (!this.updating && t != null) {
            int eng = ((Number) engine.getValue()).intValue();
            int ele = ((Number) elect.getValue()).intValue();
            double raceDryConsumption = 0;
            double raceWetConsumption = 0;
            BigDecimal dryConsumption = FuelCalculator.predictConsumption(Weather.SUNNY,
                    eng,
                    ele,
                    t);
            raceDryConsumption = dryConsumption.doubleValue() * t.getDistance();
            BigDecimal wetConsumption = FuelCalculator.predictConsumption(Weather.RAIN,
                    eng,
                    ele,
                    t);
            raceWetConsumption = wetConsumption.doubleValue() * t.getDistance();

            double lapDryConsumption = raceDryConsumption / t.getLaps();
            double lapWetConsumption = raceWetConsumption / t.getLaps();
            fuelDry.setText(String.format("%6.2f lts", raceDryConsumption));
            fuelPerLapDry.setText(String.format("%4.2f lts/volta", lapDryConsumption));
            fuelWet.setText(String.format("%6.2f lts", raceWetConsumption));
            fuelPerLapWet.setText(String.format("%4.2f lts/volta", lapWetConsumption));
        }
    }

    @Override
    public void update() {
        if( this.track.getItemCount() == 0 ) {
            this.updating = true; // to avoid the recalculation during updates
            this.track.removeAll();
            for (Track t : db.getAllTracks()) {
                this.track.addItem(t);
            }
            this.updating = false;
        }
        setDirty(false);
        updateFuel();
    }

    @Override
    public String getTitle() {
        return "Consumo de combustível ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/fuel_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/fuel_16.png");
    }

    @Override
    public String getDescription() {
        return "Calculadora de consumo de combustível";
    }

    @Override
    public Category getCategory() {
        return Category.CALC;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_B;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
