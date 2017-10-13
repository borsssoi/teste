package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gproman.db.DataService;
import org.gproman.model.race.Race;
import org.gproman.model.staff.Facilities;
import org.gproman.ui.comp.ScaleGraph;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class FacilitiesPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private final MessageFormat salFormatter     = new MessageFormat("{0,number,currency}");
    private static final Color  DARK_GREEN       = new Color( 0, 255, 0 );
    private static final Color  DARK_RED         = new Color( 180, 100, 0 );

    private JLabel              overall          = new JLabel();
    private JLabel              experience       = new JLabel();
    private JLabel              motivation       = new JLabel();
    private JLabel              technical        = new JLabel();
    private JLabel              stress           = new JLabel();
    private JLabel              concentration    = new JLabel();
    private JLabel              efficiency       = new JLabel();
    private JLabel              windtunnel       = new JLabel();
    private JLabel              pitstop          = new JLabel();
    private JLabel              workshop         = new JLabel();
    private JLabel              design           = new JLabel();
    private JLabel              engineering      = new JLabel();
    private JLabel              alloy            = new JLabel();
    private JLabel              commercial       = new JLabel();
    private JLabel              salary           = new JLabel();
    private JLabel              maintenance      = new JLabel();
    private JLabel              mlt              = new JLabel();

    private ScaleGraph              goverall          = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gexperience       = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gmotivation       = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gtechnical        = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gstress           = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gconcentration    = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gefficiency       = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gwindtunnel       = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gpitstop          = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gworkshop         = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gdesign           = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gengineering      = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              galloy            = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gcommercial       = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);
    private ScaleGraph              gmlt              = new ScaleGraph(0, 0, 100, 10, DARK_RED, DARK_GREEN);

    public FacilitiesPanel(GPROManFrame gproManFrame,
            DataService dataService) {
        super(gproManFrame,
                dataService);
        FormLayout layout = new FormLayout("right:200dlu, 10dlu, 200dlu", "TOP:PREF:G");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        builder.append(buildLeftColumn(), buildRightColumn());

        setLayout(new BorderLayout());
        add(builder.getPanel(), BorderLayout.CENTER);
    }

    private JPanel buildLeftColumn() { 
        FormLayout layout = new FormLayout("right:120dlu, 4dlu, 10dlu, 4dlu, 40dlu", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        
        builder.appendSeparator("Pessoal & Instalações: ");

        addRow(builder, "Total: ", overall, goverall);
        addRow(builder, "Salário: ", salary, null);
        addRow(builder, "Manutenção: ", maintenance, null);

        builder.appendSeparator("Pessoal: ");
        addRow(builder, "Experiência: ", experience, gexperience);
        addRow(builder, "Motivação: ", motivation, gmotivation);
        addRow(builder, "Habilidade Técnica: ", technical, gtechnical);
        addRow(builder, "Tolerância à Pressão: ", stress, gstress);
        addRow(builder, "Concentração: ", concentration, gconcentration);
        addRow(builder, "Eficiência: ", efficiency, gefficiency);
        
        return builder.build();
    }

    private JPanel buildRightColumn() { 
        FormLayout layout = new FormLayout("right:120dlu, 4dlu, 10dlu, 4dlu, 40dlu", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);
        
        builder.appendSeparator("Instalações: ");

        addRow(builder, "Túnel de Vento: ", windtunnel, gwindtunnel);
        addRow(builder, "Centro de Pits: ", pitstop, gpitstop);
        addRow(builder, "Oficina de P&D: ", workshop, gworkshop);
        addRow(builder, "Centro de Concepção de P&D: ", design, gdesign);
        addRow(builder, "Oficina de Engenharia: ", engineering, gengineering);
        addRow(builder, "Laboratório Químico: ", alloy, galloy);
        addRow(builder, "Comercial: ", commercial, gcommercial);
        
        builder.appendSeparator("Treino de Pessoal: ");
        addRow(builder, "Nível máximo de treino: ", mlt, gmlt);
        
        return builder.build();
    }

    private Font bold = null;
    private void addRow(DefaultFormBuilder builder, String text, JComponent c1, JComponent c2) {
        JLabel lbl = null;
        if( c2 == null ) {
            lbl = builder.append(text);
            builder.append(c1, 3);
        } else {
            lbl = builder.append(text, c1, c2);
        }
        if( bold == null ) {
            bold = lbl.getFont().deriveFont(Font.BOLD);
        }
        lbl.setFont(bold);
        builder.nextLine();
    }

    public void updateFacilities(Facilities f) {
        if (f != null) {
            salary.setText(salFormatter.format(new Object[]{f.getSalary()}));
            maintenance.setText(salFormatter.format(new Object[]{f.getMaintenance()}));
            
            overall.setText(String.valueOf(f.getOverall()));
            experience.setText(String.valueOf(f.getExperience()));
            motivation.setText(String.valueOf(f.getMotivation()));
            technical.setText(String.valueOf(f.getTechnical()));
            stress.setText(String.valueOf(f.getStress()));
            concentration.setText(String.valueOf(f.getConcentration()));
            efficiency.setText(String.valueOf(f.getEfficiency()));
            windtunnel.setText(String.valueOf(f.getWindtunnel()));
            pitstop.setText(String.valueOf(f.getPitstop()));
            workshop.setText(String.valueOf(f.getWorkshop()));
            design.setText(String.valueOf(f.getDesign()));
            engineering.setText(String.valueOf(f.getEngineering()));
            alloy.setText(String.valueOf(f.getAlloy()));
            commercial.setText(String.valueOf(f.getCommercial()));
            mlt.setText(String.valueOf(f.getMlt()));
            
            goverall.setValue(f.getOverall());
            gexperience.setValue(f.getExperience());
            gmotivation.setValue(f.getMotivation());
            gtechnical.setValue(f.getTechnical());
            gstress.setValue(f.getStress());
            gconcentration.setValue(f.getConcentration());
            gefficiency.setValue(f.getEfficiency());
            gwindtunnel.setValue(f.getWindtunnel());
            gpitstop.setValue(f.getPitstop());
            gworkshop.setValue(f.getWorkshop());
            gdesign.setValue(f.getDesign());
            gengineering.setValue(f.getEngineering());
            galloy.setValue(f.getAlloy());
            gcommercial.setValue(f.getCommercial());
            gmlt.setValue(f.getMlt());
            
        } else {
            salary.setText("");
            maintenance.setText("");
            
            overall.setText("");
            experience.setText("");
            motivation.setText("");
            technical.setText("");
            stress.setText("");
            concentration.setText("");
            efficiency.setText("");
            windtunnel.setText("");
            pitstop.setText("");
            workshop.setText("");
            design.setText("");
            engineering.setText("");
            alloy.setText("");
            commercial.setText("");
            mlt.setText("");
            
            goverall.setValue(0);
            gexperience.setValue(0);
            gmotivation.setValue(0);
            gtechnical.setValue(0);
            gstress.setValue(0);
            gconcentration.setValue(0);
            gefficiency.setValue(0);
            gwindtunnel.setValue(0);
            gpitstop.setValue(0);
            gworkshop.setValue(0);
            gdesign.setValue(0);
            gengineering.setValue(0);
            galloy.setValue(0);
            gcommercial.setValue(0);
            gmlt.setValue(0);
        }
    }

    @Override
    public void update() {
        if (isDirty()) {
            Race nextRace = db.getNextRace();
            if (nextRace != null) {
                updateFacilities(nextRace.getFacilities());
            }
            setDirty(false);
        }
    }

    @Override
    public String getTitle() {
        return "Pessoal & Instalações ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/lab_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/lab_16.png");
    }

    @Override
    public String getDescription() {
        return "Informação sobre Pessoal & Instalações";
    }

    @Override
    public Category getCategory() {
        return Category.TEAM;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_P;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
