package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.CarPHACalculator;
import org.gproman.calc.CarWearCalculator;
import org.gproman.db.DataService;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.CarPartCost;
import org.gproman.model.car.PHA;
import org.gproman.model.car.PartOption;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.track.WearCoefs;

import sun.swing.DefaultLookup;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * WARNING: the code of this plugins is officially shit... :(
 * as it was the first plugin I implemented, it is pretty bad... for 
 * examples of how to code the plugins, look at the newer plugins.
 * 
 * Eventually I will fix this.
 */
public class CarPanel extends UIPluginBase {

    private static final long     serialVersionUID        = 210232127277861273L;
    public static final int       CHASSIS                 = 0;
    public static final int       ENGINE                  = 1;
    public static final int       FRONT_WING              = 2;
    public static final int       REAR_WING               = 3;
    public static final int       UNDERBODY               = 4;
    public static final int       SIDEPODS                = 5;
    public static final int       COOLING                 = 6;
    public static final int       GEARBOX                 = 7;
    public static final int       BRAKES                  = 8;
    public static final int       SUSPENSION              = 9;
    public static final int       ELECTRONICS             = 10;

    private static final String[] headers2                = new String[]{"", "Nível", "%", "T1", "Simulação da Ação", "T2", "Proj.", "Final", "Peças", "Desgaste"};

    private JLabel[]              power                   = new JLabel[3];
    private JLabel[]              handling                = new JLabel[3];
    private JLabel[]              accel                   = new JLabel[3];
    private JLabel[]              levels                  = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              wear                    = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              testWearBefore          = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              testWearAfter           = new JLabel[Car.PARTS_COUNT];
    private JComboBox[]           action                  = new JComboBox[Car.PARTS_COUNT];
    private JLabel[]              wearProj                = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              wearEnd                 = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              cost                    = new JLabel[Car.PARTS_COUNT];
    private JLabel[]              wearCost                = new JLabel[Car.PARTS_COUNT];
    private JLabel                totalCost               = new JLabel();
    private JLabel                totalWearCost           = new JLabel();
    private JLabel                nextRace                = new JLabel();
    private JSpinner              risk                    = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
    private JSpinner              lapsBefore              = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
    private JSpinner              lapsAfter               = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );

    private final Object[]        fp                      = new Object[1];
    private Font                  bold;
    private Font                  normal;

    private CarPanelModel         cpmodel;

    private boolean               isWearProjectionEnabled = false;

    public CarPanel(GPROManFrame gproManFrame,
                    DataService db) {
        super( gproManFrame,
               db );
        this.cpmodel = new CarPanelModel();

        setLayout( new BorderLayout() );

        add( buildTopPanel(), BorderLayout.NORTH );
        add( buildBottomPanel(), BorderLayout.CENTER );
    }

    private JPanel buildTopPanel() {
        // Top panel with car character
        FormLayout layout = new FormLayout( "60dlu, 4dlu, 30dlu, 4dlu, 30dlu, 4dlu, 54dlu, 11dlu, 120dlu, 4dlu, 83dlu", // 1st major column
        "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );

        for ( int i = 0; i < power.length; i++ ) {
            power[i] = new JLabel();
            handling[i] = new JLabel();
            accel[i] = new JLabel();
        }
        JLabel lbl = new JLabel( "Característica atual do carro" );
        normal = lbl.getFont();
        bold = lbl.getFont().deriveFont( Font.BOLD );

        UIUtils.createColumnTitle( builder, lbl, 7, Color.BLACK, Color.WHITE, bold );
        UIUtils.createColumnTitle( builder, new JLabel( "Voltas de Teste" ), 3, Color.BLACK, Color.WHITE, bold );
        builder.nextLine();

        lapsBefore.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateCarWearProjection( BigDecimal.valueOf( ((Number) risk.getValue()).intValue() ) );
            }
        } );

        lbl = builder.append( "Potência: ", power[0], power[1], power[2] );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );

        lbl = builder.append( "T1. Voltas antes da troca: ", lapsBefore );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.nextLine();

        lapsAfter.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateCarWearProjection( BigDecimal.valueOf( ((Number) risk.getValue()).intValue() ) );
            }
        } );

        lbl = builder.append( "Estabilidade: ", handling[0], handling[1], handling[2] );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        lbl = builder.append( "T2. Voltas após a troca: ", lapsAfter );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        builder.nextLine();

        lbl = builder.append( "Aceleração: ", accel[0], accel[1], accel[2] );
        lbl.setFont( bold );
        lbl.setHorizontalAlignment( SwingConstants.RIGHT );
        lbl = new JLabel("<html><center>IMPORTANTE: considere erro de até 3% na estimativa de desgaste das voltas de teste.</center></html>");
        lbl.setFont( bold.deriveFont( Font.ITALIC ) );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setForeground( Color.red );
        builder.append( lbl, 3 );
        builder.nextLine();
        return builder.build();
    }

    private JPanel buildBottomPanel() {
        // Center panel with car parts
        FormLayout layout = new FormLayout( "right:25dlu, 4dlu, c:20dlu, 4dlu, c:20dlu, 4dlu, c:20dlu, 4dlu, fill:102dlu, 4dlu, c:20dlu, 4dlu, c:30dlu, 4dlu, c:30dlu, 4dlu, c:50dlu, 4dlu, c:50dlu",
                                            "" );

        // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );
        UIUtils.createColumnTitle( builder, new JLabel( "Peças do carro" ), 19, Color.BLACK, Color.WHITE, bold );
        builder.nextLine();

        nextRace.setFont( bold );
        nextRace.setOpaque( true );
        nextRace.setHorizontalAlignment( SwingConstants.CENTER );
        nextRace.setBackground( Color.DARK_GRAY );
        nextRace.setForeground( Color.WHITE );

        JLabel lbl = new JLabel( "Custo" );
        lbl.setFont( bold );
        lbl.setOpaque( true );
        lbl.setHorizontalAlignment( SwingConstants.CENTER );
        lbl.setBackground( Color.DARK_GRAY );
        lbl.setForeground( Color.WHITE );

        builder.append( "" );
        builder.add( nextRace,
                     CC.xyw( 13, builder.getRow(), 3, "fill, fill" ) );
        builder.add( lbl,
                     CC.xyw( 17, builder.getRow(), 3, "fill, fill" ) );
        builder.nextLine();
        nextRace.setText( "Próxima Corrida" );

        headers = new JLabel[headers2.length];
        for ( int i = 0; i < headers2.length; i++ ) {
            headers[i] = new JLabel( headers2[i] );
            headers[i].setFont( bold );
            headers[i].setHorizontalAlignment( SwingConstants.CENTER );
            builder.append( headers[i] );
        }
        builder.nextLine();

        ActionComboboxRedender cbrenderer = new ActionComboboxRedender();
        for ( int i = 0; i < levels.length; i++ ) {
            levels[i] = new JLabel();
            wear[i] = new JLabel();
            testWearBefore[i] = new JLabel();
            action[i] = new JComboBox();
            testWearAfter[i] = new JLabel();
            wearProj[i] = new JLabel();
            wearEnd[i] = new JLabel();
            cost[i] = new JLabel();
            wearCost[i] = new JLabel();

            final int index = i;
            action[i].setRenderer( cbrenderer );
            action[i].addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateWearPred( index );
                }
            } );

            lbl = builder.append( Car.MNEM_PTBR[i], levels[i], wear[i], testWearBefore[i], action[i] );
            builder.append( testWearAfter[i], wearProj[i], wearEnd[i] );
            builder.append( cost[i], wearCost[i] );
            lbl.setFont( bold );
            builder.nextLine();
        }

        builder.append( "" );
        reset = new JButton( "Recomeçar" );
        reset.setIcon( UIUtils.createImageIcon( "/icons/trash_16.png" ) );
        reset.setMnemonic( KeyEvent.VK_M );
        reset.setToolTipText( "Recomeça a simulação de trocas de peças." );
        reset.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        } );
        builder.add( reset,
                     CC.xy( 9, builder.getRow() ) );

        riskLabel = new JLabel( "Risco: " );
        builder.add( riskLabel,
                     CC.xy( 13, builder.getRow() ) );
        builder.add( risk,
                     CC.xy( 15, builder.getRow(), "fill,fill" ) );
        builder.add( totalCost,
                     CC.xy( 17, builder.getRow(), "fill,fill" ) );
        builder.add( totalWearCost,
                     CC.xy( 19, builder.getRow(), "fill,fill" ) );
        builder.nextLine();
        totalCost.setHorizontalAlignment( SwingConstants.CENTER );
        totalCost.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        totalWearCost.setHorizontalAlignment( SwingConstants.CENTER );
        totalWearCost.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );

        risk.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner mySpinner = (JSpinner) (e.getSource());
                updateCarWearProjection( BigDecimal.valueOf( ((Number) mySpinner.getValue()).intValue() ) );
            }
        } );
        return builder.build();
    }

    private void resetSimulation() {
        for ( int i = 0; i < action.length; i++ ) {
            if ( action[i].getSelectedIndex() != 0 ) {
                action[i].setSelectedIndex( 0 );
            }
        }
    }

    public void updateCar(DriverWearWeight dww,
                          WearCoefs coefs,
                          Driver driver,
                          Car car,
                          Race race) {
        this.cpmodel.dww = dww;
        this.cpmodel.coefs = coefs;
        this.cpmodel.driver = driver;
        this.cpmodel.car = car;
        this.cpmodel.race = race;

        power[0].setText( String.valueOf( car.getPower() ) );
        handling[0].setText( String.valueOf( car.getHandling() ) );
        accel[0].setText( String.valueOf( car.getAcceleration() ) );
        if ( race != null ) {
            nextRace.setText( race.getTrack().getName() );
        }
        if ( car != null ) {
            CarPart[] carParts = this.cpmodel.car.getParts();
            this.cpmodel.base = new CarPart[carParts.length];
            // has to clone all parts before updating them
            for ( int i = 0; i < Car.PARTS_PTBR.length; i++ ) {
                cpmodel.base[i] = carParts[i].clone();
            }
            for ( int i = 0; i < Car.PARTS_PTBR.length; i++ ) {
                updatePart( i );
            }
        }
        if ( isWearDataAvailable() ) {
            this.cpmodel.calc = new CarWearCalculator( this.cpmodel.dww,
                                                       this.cpmodel.coefs,
                                                       this.cpmodel.driver,
                                                       this.cpmodel.race.getTrack(),
                                                       this.cpmodel.car );
            if ( this.cpmodel.race.getTestSession() != null ) {
                this.cpmodel.testCalc = new CarWearCalculator( this.cpmodel.dww,
                                                               this.cpmodel.coefs,
                                                               this.cpmodel.driver,
                                                               this.cpmodel.race.getTestSession().getTrack(),
                                                               this.cpmodel.car );
            } else {
                this.cpmodel.testCalc = null;
            }
        }
        updateCarWearProjection( BigDecimal.valueOf( ((Number) risk.getValue()).intValue() ) );
    }

    private boolean isWearDataAvailable() {
        return this.cpmodel.dww != null &&
               this.cpmodel.coefs != null &&
               this.cpmodel.driver != null &&
               this.cpmodel.car != null &&
               this.cpmodel.race != null &&
               this.cpmodel.race.getTrack() != null &&
               this.cpmodel.race.getTrack().getWearFactors().getBrakesWF() != null;
    }

    public void updateCarWearProjection(BigDecimal risk) {
        this.cpmodel.risk = risk;
        if ( this.cpmodel.calc != null ) {
            enableWearProjection( true );
            this.cpmodel.proj = this.cpmodel.calc.predictWear( risk );

            if ( this.cpmodel.testCalc != null ) {
                int lapsB = lapsBefore.isEnabled() ? ((Number) lapsBefore.getValue()).intValue() : 0;
                int lapsA = lapsAfter.isEnabled() ? ((Number) lapsAfter.getValue()).intValue() : 0;
                this.cpmodel.testBefore = this.cpmodel.testCalc.predictWearForTestLaps( BigDecimal.ZERO, lapsB );
                this.cpmodel.testAfter = this.cpmodel.testCalc.predictWearForTestLaps( BigDecimal.ZERO, lapsA );
            }
            for ( int i = 0; i < Car.PARTS_PTBR.length; i++ ) {
                updateWearPred( i );
            }
        } else {
            enableWearProjection( false );
        }
    }

    private void enableWearProjection(boolean enable) {
        if ( isWearProjectionEnabled != enable ) {
            for ( int i = 0; i < wearProj.length; i++ ) {
                nextRace.setEnabled( enable );
                headers[4].setEnabled( enable );
                headers[5].setEnabled( enable );
                wearProj[i].setEnabled( enable );
                wearEnd[i].setEnabled( enable );
                riskLabel.setEnabled( enable );
                this.risk.setEnabled( enable );
            }
            isWearProjectionEnabled = enable;
        }
        boolean canTestBefore = cpmodel.base != null && action[0].isEnabled();
        if ( canTestBefore ) {
            for ( int i = 0; canTestBefore && i < cpmodel.base.length; i++ ) {
                canTestBefore = cpmodel.base[i].getWear() < 91;
            }
        }
        lapsBefore.setEnabled( enable && canTestBefore && this.cpmodel.testCalc != null );
        if( !lapsBefore.isEnabled() ) {
        	lapsBefore.setValue(0);
        }
        lapsAfter.setEnabled( enable && this.cpmodel.testCalc != null );
    }

    private void updatePart(int i) {
        CarPart part;
        if ( cpmodel.car != null && (part = cpmodel.base[i]) != null ) {
            levels[i].setText( String.valueOf( part.getLevel() ) );

            DefaultComboBoxModel model = (DefaultComboBoxModel) action[i].getModel();
            model.removeAllElements();
            model.addElement( "" );
            for ( PartOption po : part.getOptions() ) {
                model.addElement( po );
            }
            action[i].setEnabled( model.getSize() > 1 );
            reset.setEnabled( action[i].isEnabled() );

            updateWearPred( i );
        }
    }

    private void updateWearPred(int i) {
        Object select = action[i].getSelectedItem();
        updateCost( i, select );
        updateWear( i, select );
        updatePHA();
    }

    private void updateWear(int i,
                            Object select) {
        double testWearB = cpmodel.testBefore != null ? cpmodel.testBefore[i].getWear() : 0;
        if ( cpmodel.car != null && cpmodel.base[i] != null ) {
            updateField( wear[i], cpmodel.base[i].getWear() );
            updateField( testWearBefore[i], testWearB );
        }
        if ( cpmodel.proj != null ) {
            double baseWear;
            double projWear;
            int partLevel;
            if ( select instanceof PartOption ) {
                PartOption partOption = (PartOption) select;
                baseWear = partOption.getAction().equals(PartOption.Action.BUY_NEW) ? 0 : partOption.getNewWear() + testWearB;
                projWear = cpmodel.calc.predictWearForLevel( i, cpmodel.risk, partOption.getToLevel() ).getWear();
                partLevel = partOption.getToLevel();
            } else {
                baseWear = cpmodel.base[i].getWear() + testWearB;
                projWear = cpmodel.proj[i].getWear();
                partLevel = cpmodel.proj[i].getLevel();
            }
            double testWearA = cpmodel.testAfter != null ? cpmodel.testAfter[i].getWear() : 0;
            cpmodel.wearCost[i] = Math.round( CarPartCost.getCost( i, partLevel ) * (projWear + testWearA + testWearB) / 100 );
            updateField( testWearAfter[i], testWearA );
            updateField( wearProj[i], projWear );
            updateField( wearEnd[i], baseWear + projWear + testWearA );
            wearCost[i].setText( (formatCurrency( cpmodel.wearCost[i] )) );
            totalWearCost.setText( formatCurrency( calculateTotalWearCost() ) );
        }
    }

    private long calculateTotalWearCost() {
        long result = 0;
        for ( int i = 0; i < cpmodel.wearCost.length; i++ ) {
            result += cpmodel.wearCost[i];
        }
        return result;
    }

    private void updateCost(int i,
                            Object select) {
        if ( select instanceof PartOption ) {
            PartOption partOption = (PartOption) select;
            cpmodel.partsCost[i] = partOption.getCost();
            cpmodel.base[i].setLevel( partOption.getToLevel() );
        } else {
            cpmodel.partsCost[i] = 0;
            cpmodel.base[i].setLevel( cpmodel.car.getParts()[i].getLevel() );
        }
        cost[i].setText( formatCurrency( cpmodel.partsCost[i] ) );
        totalCost.setText( formatCurrency( calculateTotalCost() ) );
    }

    private void updatePHA() {
        PHA pha = CarPHACalculator.calculateBasePHA( cpmodel.base );
        int tp = pha.getP() + cpmodel.car.getBonusPHA().getP();
        updatePHAFields( power, tp - cpmodel.car.getPower(), tp );

        int th = pha.getH() + cpmodel.car.getBonusPHA().getH();
        updatePHAFields( handling, th - cpmodel.car.getHandling(), th );

        int ta = pha.getA() + cpmodel.car.getBonusPHA().getA();
        updatePHAFields( accel, ta - cpmodel.car.getAcceleration(), ta );
    }

    private static final Color DARK_GREEN = new Color( 0, 128, 0 );
    private JLabel[]           headers;
    private JLabel             riskLabel;
    private JButton            reset;

    private void updatePHAFields(JLabel[] lbl,
                                 int delta,
                                 int tp) {
        if ( delta != 0 ) {
            if ( delta > 0 ) {
                lbl[1].setForeground( DARK_GREEN );
                lbl[2].setForeground( DARK_GREEN );
            } else {
                lbl[1].setForeground( Color.RED );
                lbl[2].setForeground( Color.RED );
            }
            lbl[1].setText( String.format( "%+d =", delta ) );
            lbl[2].setText( String.format( "%d", tp ) );
        } else {
            lbl[1].setForeground( Color.BLACK );
            lbl[2].setForeground( Color.BLACK );
            lbl[1].setText( "" );
            lbl[2].setText( "" );
        }
    }

    private int calculateTotalCost() {
        int result = 0;
        for ( int i = 0; i < cpmodel.partsCost.length; i++ ) {
            result += cpmodel.partsCost[i];
        }
        return result;
    }

    private String formatCurrency(long amount) {
        return String.format( "$%,d", amount );
    }

    private void updateField(JLabel lbl,
                             double val) {
        fp[0] = val;
        if ( val >= 90 ) {
            lbl.setForeground( Color.RED );
            lbl.setFont( bold );
        } else {
            lbl.setForeground( Color.BLACK );
            lbl.setFont( normal );
        }
        lbl.setText( String.format( "%2.0f%%", fp ) );
    }

    private static class CarPanelModel {
        public DriverWearWeight  dww;
        public WearCoefs         coefs;
        public Driver            driver;
        public Car               car;
        public Race              race;
        public CarPart[]         base;
        public CarPart[]         proj;
        public CarPart[]         testBefore;
        public CarPart[]         testAfter;
        public BigDecimal        risk      = BigDecimal.ZERO;
        public CarWearCalculator calc;
        public CarWearCalculator testCalc;
        public long[]            partsCost = new long[Car.PARTS_COUNT];
        public long[]            wearCost  = new long[Car.PARTS_COUNT];
    }

    @Override
    public void update() {
        if ( isDirty() ) {
            DriverWearWeight dww = db.getDriverAttributesWearWeight();
            WearCoefs coefs = db.getWearCoefs();

            Race race = db.getNextRace();
            if ( race != null ) {
                Car car = race.getCarStart();
                Driver driver = race.getDriverStart();
                if ( car != null && driver != null ) {
                    updateCar( dww,
                               coefs,
                               driver,
                               car,
                               race );
                }
            }
            setDirty( false );
        }
    }

    @Override
    public String getTitle() {
        return "Carro ";
    }

    @Override
    public ImageIcon getIcon() {
        return UIUtils.createImageIcon( "/icons/Car_32.png" );
    }

    @Override
    public ImageIcon getSmallIcon() {
        return UIUtils.createImageIcon( "/icons/Car_16.png" );
    }

    @Override
    public String getDescription() {
        return "Informações sobre o carro";
    }

    @Override
    public Category getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getMnemonic() {
        return KeyEvent.VK_C;
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    public static class ActionComboboxRedender extends DefaultListCellRenderer {
        private static final long     serialVersionUID = -5799069863086293253L;
        public static final ImageIcon KEEP             = UIUtils.createImageIcon( "/icons/Keep_16.png" );
        public static final ImageIcon REPLACE          = UIUtils.createImageIcon( "/icons/dollar_16.png" );
        public static final ImageIcon DOWNGRADE        = UIUtils.createImageIcon( "/icons/Downgrade_16.png" );

        public ActionComboboxRedender() {
            setHorizontalAlignment( CENTER );
            setVerticalAlignment( CENTER );
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            setComponentOrientation( list.getComponentOrientation() );

            Color bg = null;
            Color fg = null;

            JList.DropLocation dropLocation = list.getDropLocation();
            if ( dropLocation != null
                 && !dropLocation.isInsert()
                 && dropLocation.getIndex() == index ) {

                bg = DefaultLookup.getColor( this, ui, "List.dropCellBackground" );
                fg = DefaultLookup.getColor( this, ui, "List.dropCellForeground" );

                isSelected = true;
            }

            if ( isSelected ) {
                setBackground( bg == null ? list.getSelectionBackground() : bg );
                setForeground( fg == null ? list.getSelectionForeground() : fg );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            if ( value != null && value instanceof PartOption ) {
                PartOption action = (PartOption) value;
                //Set the icon and text.  If icon was null, say so.
                switch ( action.getAction() ) {
                    case BUY_NEW :
                        setIcon( REPLACE );
                        break;
                    case DOWNGRADE :
                        setIcon( DOWNGRADE );
                        break;
                }
                setText( action.toString() );
            } else {
                setIcon( KEEP );
                setText( "" );
            }
            setEnabled( list.isEnabled() );
            setFont( list.getFont() );

            Border border = null;
            if ( cellHasFocus ) {
                if ( isSelected ) {
                    border = DefaultLookup.getBorder( this, ui, "List.focusSelectedCellHighlightBorder" );
                }
                if ( border == null ) {
                    border = DefaultLookup.getBorder( this, ui, "List.focusCellHighlightBorder" );
                }
            } else {
                border = getNoFocusBorder();
            }
            setBorder( border );
            return this;
        }

        private Border getNoFocusBorder() {
            Border border = DefaultLookup.getBorder( this, ui, "List.cellNoFocusBorder" );
            if ( System.getSecurityManager() != null ) {
                if ( border != null ) return border;
                return noFocusBorder;
            } else {
                return noFocusBorder;
            }
        }
    }

}
