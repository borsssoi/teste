package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.gproman.model.car.WearPlan;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class PlanLoadSaveDialog extends JPanel {
    private static final long   serialVersionUID = -8818792328043232610L;

    private JTextField name;

    private JTable table;

    private WearPlan selected;
    private String   selectedName;
    
    private Map<String, WearPlan> plansMap;
    
    private WearPlanningPanel.Op operation;

    public PlanLoadSaveDialog(List<WearPlan> plans,
                              WearPlanningPanel.Op operation ) {
        this.operation = operation;
        plansMap = new HashMap<String, WearPlan>();
        buildMainPanel( plans );
    }

    private void buildMainPanel( List<WearPlan> plans ) {
        setLayout( new BorderLayout() );
        setPreferredSize( new Dimension( 380, 250 ) );
        FormLayout layout = new FormLayout( "right:70dlu, 4dlu, 120dlu",
                                            "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );

        builder.appendSeparator( "Planos: " );
        name = new JTextField();
        if( operation != WearPlanningPanel.Op.SAVE) {
            name.setEditable( false );
            name.setEnabled( false );
        }
        builder.append( "Nome do plano: ", name );
        builder.nextLine();
        
        String[] columnNames = {"Nome",
                                "Temporada",
                                "Corrida" };
        DefaultTableModel model = new DefaultTableModel( columnNames, 0 );
        for( WearPlan plan : plans ) {
            plansMap.put( plan.getName(), plan );
            model.addRow( new Object[] { plan.getName(), plan.getSeason(), plan.getRace() } );
        }
        table = new JTable( model );
        
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        builder.append( scrollPane, 3 );
        
        table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if( lsm.isSelectionEmpty() ) {
                    name.setText( "" );
                } else {
                    name.setText( (String) table.getValueAt( table.getSelectedRow(), 0 ) );
                }
            }
        });

        add( builder.getPanel(), BorderLayout.CENTER );
    }

    public boolean commit() {
        selectedName = name.getText();
        selected = plansMap.get( selectedName );
        return true;
    }

    public boolean rollback() {
        selected = null;
        selectedName = null;
        return true;
    }
    
    public WearPlan getSelectedPlan() {
        return selected;
    }
    
    public String getSelectedPlanName() {
        return selectedName;
    }

}
