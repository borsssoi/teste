package org.gproman.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gproman.db.DataService;
import org.gproman.model.Manager;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.ui.comp.ScaleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class TyreSuppliersPanel extends UIPluginBase {

    private static final long   serialVersionUID = 210232127277861273L;

    private static final Logger logger           = LoggerFactory.getLogger(TyreSuppliersPanel.class);

    private static final Color  DARK_GREEN       = new Color(100, 210, 0);
    private static final Color  DARK_RED         = new Color(210, 70, 50);

    private static final int    SUPPLIERS_COUNT  = 9;

    private JPanel[]            panel            = new JPanel[SUPPLIERS_COUNT];
    private JLabel[]            icon             = new JLabel[SUPPLIERS_COUNT];
    private ScaleGraph[]        dry              = new ScaleGraph[SUPPLIERS_COUNT];
    private ScaleGraph[]        wet              = new ScaleGraph[SUPPLIERS_COUNT];
    private JLabel[]            peak             = new JLabel[SUPPLIERS_COUNT];
    private ScaleGraph[]        durability       = new ScaleGraph[SUPPLIERS_COUNT];
    private ScaleGraph[]        warmup           = new ScaleGraph[SUPPLIERS_COUNT];
    private JLabel[]            cost             = new JLabel[SUPPLIERS_COUNT];


    public TyreSuppliersPanel(GPROManFrame frame,
            DataService db) {
        super(frame, db);

        setLayout(new GridLayout(3, 3));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        for (int i = 0; i < SUPPLIERS_COUNT; i++) {
            FormLayout layout = new FormLayout("60dlu, 4dlu, 40dlu, 20dlu ",
                    "");
            // add rows dynamically
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);

            icon[i] = new JLabel();
            dry[i] = new ScaleGraph(0, 0, 8, 8, DARK_RED, DARK_GREEN );
            wet[i] = new ScaleGraph(0, 0, 8, 8, DARK_RED, DARK_GREEN );
            peak[i] = new JLabel();
            durability[i] = new ScaleGraph(0, 0, 8, 8, DARK_RED, DARK_GREEN );
            warmup[i] = new ScaleGraph(0, 0, 8, 8, DARK_GREEN, DARK_RED );
            cost[i] = new JLabel();

            Font bold = icon[i].getFont().deriveFont(Font.BOLD);
            icon[i].setIcon( TyreSupplier.values()[i].getIcon() );
            icon[i].setHorizontalAlignment( SwingConstants.CENTER );

            builder.append(icon[i], 4);
            builder.nextLine();

            addSupplierAttribute("Seco: ", dry[i], builder, bold);
            addSupplierAttribute("Molhado: ", wet[i], builder, bold);
            addSupplierAttribute("Pico: ", peak[i], builder, bold);
            addSupplierAttribute("Durabilidade: ", durability[i], builder, bold);
            addSupplierAttribute("Aquecimento: ", warmup[i], builder, bold);
            JLabel lbl = builder.append("Custo: ");
            lbl.setFont(bold);
            lbl.setHorizontalAlignment(SwingConstants.RIGHT);
            builder.append(cost[i],2);
            builder.nextLine();

            panel[i] = builder.build();
            panel[i].setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            add(panel[i]);
        }

    }

    private void addSupplierAttribute(String label, JComponent component, DefaultFormBuilder builder, Font bold) {
        JLabel lbl = builder.append(label, component);
        lbl.setFont(bold);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        builder.nextLine();
    }

    @Override
    public void update() {
        if (isDirty()) {
            Manager manager = db.getManager();
            if( manager != null ) {
                Season season = db.getCurrentSeason(manager.getName());
                if (season != null) {
                    logger.info("Updating tyre suppliers plugin with data for season '" + season.getNumber() + "'");
                    List<TyreSupplierAttrs> suppliers = db.getTyreSuppliersForSeason(season.getNumber());
                    for( TyreSupplierAttrs supplier : suppliers ) {
                        int i = supplier.getSupplier().ordinal();
                        dry[i].setValue(supplier.getDry());
                        wet[i].setValue(supplier.getWet());
                        peak[i].setText(supplier.getPeak().toString()+"°");;
                        durability[i].setValue(supplier.getDurability());
                        warmup[i].setValue(supplier.getWarmup());
                        cost[i].setText( formatCurrency( supplier.getCost() ) );
                        
                        if( season.getSupplier() != null && season.getSupplier().equals( supplier.getSupplier() ) ) {
                            panel[i].setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(DARK_GREEN, 2),
                                    BorderFactory.createEmptyBorder(2, 2, 2, 2) ) );
                        } else {
                            panel[i].setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4) );
                        }
                        panel[i].repaint();
                    }
                }
            }
            setDirty(false);
        }
    }

    private String formatCurrency(long amount) {
        return String.format( "$%,d", amount );
    }

    @Override
    public String getTitle() {
        return "Fornecedores ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon("/icons/tyres_32.png");
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon("/icons/tyres_16.png");
    }

    @Override
    public String getDescription() {
        return "Fornecedores de Pneus";
    }

    @Override
    public Category getCategory() {
        return Category.SEASON;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_F;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
