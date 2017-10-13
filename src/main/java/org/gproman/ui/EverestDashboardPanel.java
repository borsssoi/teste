package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gproman.db.DataService;
import org.gproman.db.EverestService;
import org.gproman.model.everest.EverestMetrics;
import org.gproman.model.everest.WeatherType;
import org.gproman.model.season.TyreSupplier;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class EverestDashboardPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger(EverestDashboardPanel.class);
    private static final Shape  CIRCLE           = new Ellipse2D.Double(-3, -3, 6, 6);

    private EverestService      everest;

    private JLabel              lbTelemetries;
    private JLabel              lbStints;
    private JLabel              lbSeasons;
    private JLabel              lbRaces;
    private JLabel              lbLatestSeason;

    private EverestMetrics      everestMetrics;

    private XYSeriesCollection  telemetriesPerSeasonDs;
    private XYSeriesCollection  telemetriesPerRaceDs;
    //private DefaultPieDataset   stintsPerWeatherDs;
    private DefaultPieDataset   telemetriesPerSupplierDs;

    public EverestDashboardPanel(final GPROManFrame frame,
            final DataService db) {
        super(frame, db);
        setLayout(new BorderLayout());
        everest = frame.getApplication().getEverestService();
        everestMetrics = everest.getEverestMetrics();

        FormLayout layout = new FormLayout(
                "200dlu, 4dlu, 200dlu ",
                "top:pref:grow, top:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        builder.append(createStatsPanel());
        builder.append(createTelemetriesPerSeason());
        builder.nextLine();
        builder.append(createTelemetriesPerRaceThisSeason());
        //builder.append(createStintsPerWeather());
        builder.append(createTelemetriesPerSupplier());

        add(builder.build(), BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        lbTelemetries = new JLabel();
        lbTelemetries.setHorizontalAlignment(SwingConstants.RIGHT);
        lbStints = new JLabel();
        lbStints.setHorizontalAlignment(SwingConstants.RIGHT);
        lbSeasons = new JLabel();
        lbSeasons.setHorizontalAlignment(SwingConstants.RIGHT);
        lbRaces = new JLabel();
        lbRaces.setHorizontalAlignment(SwingConstants.RIGHT);
        lbLatestSeason = new JLabel();
        lbLatestSeason.setHorizontalAlignment(SwingConstants.RIGHT);

        FormLayout layout = new FormLayout(
                "120dlu, 4dlu, 30dlu ",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.appendSeparator("Estatísticas: ");
        JLabel lbl = builder.append("Temporadas: ", lbSeasons);
        Font bold = lbl.getFont().deriveFont(Font.BOLD);
        configureLabel(lbl, bold);
        lbl = builder.append("Corridas: ", lbRaces);
        configureLabel(lbl, bold);
        lbl = builder.append("Telemetrias: ", lbTelemetries);
        configureLabel(lbl, bold);
        lbl = builder.append("Stints: ", lbStints);
        configureLabel(lbl, bold);
        lbl = builder.append("Temporada mais recente: ", lbLatestSeason);
        configureLabel(lbl, bold);

        return builder.build();
    }

    private void configureLabel(JLabel lbl, Font bold) {
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private ChartPanel createTelemetriesPerSeason() {
        final XYSeries series = new XYSeries("Telemetrias");
        telemetriesPerSeasonDs = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(null,
                "Temporada",
                "Telemetrias",
                telemetriesPerSeasonDs,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(this.getBackground());

        XYPlot cp = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) cp.getRenderer();
        renderer.setSeriesShape(0, CIRCLE);
        renderer.setSeriesOutlinePaint(0, Color.PINK);
        renderer.setSeriesVisible(0, true);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setBaseSeriesVisible(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setUseOutlinePaint(true);

        ValueAxis domainAxis = cp.getDomainAxis();
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(450, 250));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Telemetrias por temporada:"));
        return panel;
    }

    private ChartPanel createTelemetriesPerRaceThisSeason() {
        final XYSeries series = new XYSeries("Telemetrias");
        telemetriesPerRaceDs = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(null,
                "Corrida",
                "Telemetrias",
                telemetriesPerRaceDs,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(this.getBackground());

        XYPlot cp = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) cp.getRenderer();
        renderer.setSeriesShape(0, CIRCLE);
        renderer.setSeriesOutlinePaint(0, Color.PINK);
        renderer.setSeriesVisible(0, true);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setBaseSeriesVisible(true);
        renderer.setUseFillPaint(true);
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setUseOutlinePaint(true);
        
        ValueAxis domainAxis = cp.getDomainAxis();
        domainAxis.setRange(0, 17);
        domainAxis.setAutoRange(false);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(450, 250));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Telemetrias temporada mais recente:"));
        return panel;
    }

//    private ChartPanel createStintsPerWeather() {
//        stintsPerWeatherDs = new DefaultPieDataset();
//        JFreeChart chart = ChartFactory.createPieChart(null,
//                stintsPerWeatherDs,
//                false,
//                true,
//                false);
//        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(this.getBackground());
//
//        PiePlot plot = (PiePlot) chart.getPlot();
//        plot.setBackgroundPaint(this.getBackground());
//        plot.setOutlineVisible(false);
//
//        Font original = plot.getLabelFont();
//        plot.setLabelFont(original.deriveFont(original.getSize() * 0.85f));
//        plot.setLabelBackgroundPaint(this.getBackground());
//        plot.setLabelOutlinePaint(this.getBackground());
//        plot.setInteriorGap(0.02);
//        plot.setMaximumLabelWidth(0.20);
//        plot.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
//        plot.setLabelShadowPaint(getBackground());
//        plot.setStartAngle(1.28);
//
//        ChartPanel panel = new ChartPanel(chart);
//        panel.setPreferredSize(new Dimension(450, 250));
//        panel.setOpaque(false);
//        panel.setBorder(BorderFactory.createTitledBorder("Stints por Clima:"));
//        return panel;
//    }

    private ChartPanel createTelemetriesPerSupplier() {
        telemetriesPerSupplierDs = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(null,
                telemetriesPerSupplierDs,
                false,
                true,
                false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(this.getBackground());

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(this.getBackground());
        plot.setOutlineVisible(false);

        Font original = plot.getLabelFont();
        plot.setLabelFont(original.deriveFont(original.getSize() * 0.85f));
        plot.setLabelBackgroundPaint(this.getBackground());
        plot.setLabelOutlinePaint(this.getBackground());
        plot.setInteriorGap(0.02);
        plot.setMaximumLabelWidth(0.20);
        plot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
        plot.setLabelShadowPaint(getBackground());
        plot.setStartAngle(1.28);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(450, 250));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Telemetrias por provedor:"));
        return panel;
    }

    @Override
    public void update() {
        everestMetrics = everest.getEverestMetrics();
        lbSeasons.setText(String.format("%2d", everestMetrics.getSeasons().intValue()));
        lbRaces.setText(String.format("%3d", everestMetrics.getRaces()));
        lbTelemetries.setText(String.format("%,6d", everestMetrics.getTelemetries()));
        lbStints.setText(String.format("%,6d", everestMetrics.getStints()));
        if( everestMetrics.getLatestSeason() != null ) {
            lbLatestSeason.setText(String.format("%2d", everestMetrics.getLatestSeason().intValue()));
        }

        XYSeries series = telemetriesPerSeasonDs.getSeries(0);
        series.clear();
        for (Map.Entry<Integer, Integer> entry : everestMetrics.getTelemetriesPerSeason().entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        series = telemetriesPerRaceDs.getSeries(0);
        series.clear();
        for (Map.Entry<Integer, Integer> entry : everestMetrics.getTelemetriesPerRaceCurrentSeason().entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

//        stintsPerWeatherDs.clear();
//        for (Map.Entry<WeatherType, Integer> entry : everestMetrics.getStintsPerWeather().entrySet()) {
//            stintsPerWeatherDs.setValue(entry.getKey().portuguese, entry.getValue());
//        }

        telemetriesPerSupplierDs.clear();
        for (Map.Entry<TyreSupplier, Integer> entry : everestMetrics.getTelemetriesPerSupplier().entrySet()) {
            telemetriesPerSupplierDs.setValue(entry.getKey().name, entry.getValue());
        }
    }

    @Override
    public String getTitle() {
        return "Dashboard ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/chart_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/chart_16.png");
    }

    @Override
    public String getDescription() {
        return "Painel de visualização das métricas do Evereste";
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
        return KeyEvent.VK_H;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
