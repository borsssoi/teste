package org.gproman.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gproman.calc.CarWearPlanner;
import org.gproman.calc.CarWearPlanner.StepAction.Action;
import org.gproman.calc.CarWearPlanner.WearStep;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarWearDetail;
import org.gproman.ui.table.WearPlanTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class CustomWearDetailDialog extends JPanel {

	private static final long serialVersionUID = -6441691679618705628L;

	private static final Logger logger = LoggerFactory.getLogger( CustomWearDetailDialog.class );
	
	private CarWearPlanner planner;
	
	private int selectedRow;
	
	private WearPlanTableModel tmodel;
	
	private static final int TEST_BEFORE = 1;
	private static final int TEST_AFTER = 2;
	private static final int RACE = 3;
	
	private boolean customRace = false;
	private boolean customTestBefore = false;
	private boolean customTestAfter = false;
	
    public CustomWearDetailDialog() {
    }

    private void buildFields() {
        //Because de instance is the same for all tracks, clear the dialog before
    	removeAll();
    	setLayout( new BorderLayout() );
        FormLayout layout = new FormLayout( "right:70dlu, 10dlu, center:20dlu, 10dlu, center:20dlu, 10dlu, center:40dlu, 10dlu" +
        		", center:40dlu, 10dlu, center:40dlu, 10dlu, center:40dlu, 10dlu, center:20dlu, 10dlu",
                                            "" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
        builder = new DefaultFormBuilder( layout );
        builder.border( Borders.DIALOG );

        builder.appendSeparator( "Detalhes dos carro: " );
        
        if(planner != null){
        	
        		builder.append("");
        		builder.append("Nível");
        		builder.append("%");
        		builder.append("T1");
        		builder.append("Ação");
        		builder.append("T2");
        		builder.append("Proj");
         		builder.append("Final");
        		
	        for (int i = 0; i < Car.PARTS_PTBR.length; i++) {
	        	builder.append( Car.PARTS_PTBR[i] +": ");
	        	 
	        	final WearStep step = planner.getStep(selectedRow);
	        	final CarWearDetail detail = step.getCarWearDetail()[i];
	        	final CarWearDetail customCarWearDetail = step.getCustomCarWearDetail()[i];
	        	
	        	final CarWearDetail actualDetail;
	        	
	        	if(customCarWearDetail != null){
	        		actualDetail = customCarWearDetail;
	        	}else{
	        		actualDetail = detail;
	        	}
	        	
	        	Action action = planner.getAction(selectedRow, i).getAction();
	        	String actionStr = getFormattedAction(action, actualDetail); 
	        	
	        	builder.append( String.valueOf(step.getAction(i).getOriginalPart().getLevel()));
	        	builder.append( getFormattedPercentualLabel(actualDetail.getWearBase()));
	        	
	        	final JSpinner customTestBefore = new JSpinner( new SpinnerNumberModel( actualDetail.getWearTestBefore(), 0, 100, 1 ) );
	        	builder.append(customTestBefore);
	        	if(action.equals(Action.REPLACE)){
	        		customTestBefore.setEnabled(false);
	        	}
	        	customTestBefore.addChangeListener( new ChangeListener() {
	                @Override
	                public void stateChanged(ChangeEvent e) {
	                	setCustomDetail(actualDetail, customCarWearDetail, detail, customTestBefore, step, TEST_BEFORE);
	                }
	            } );
	        	
	        	builder.append( actionStr);
	        	
	        	final JSpinner customTestAfter = new JSpinner( new SpinnerNumberModel( actualDetail.getWearTestAfter(), 0, 100, 1 ) );
	        	builder.append(customTestAfter);
	        	
	        	customTestAfter.addChangeListener( new ChangeListener() {
	                @Override
	                public void stateChanged(ChangeEvent e) {
	                	setCustomDetail(actualDetail, customCarWearDetail, detail, customTestAfter, step, TEST_AFTER);
	                }
	            } );
	        	
	            final JSpinner customRaceWear = new JSpinner( new SpinnerNumberModel( actualDetail.getWearRace(), 0, 100, 1 ) );
	        	builder.append(customRaceWear);
	            
	        	customRaceWear.addChangeListener( new ChangeListener() {
	                @Override
	                public void stateChanged(ChangeEvent e) {
	                	setCustomDetail(actualDetail, customCarWearDetail, detail, customRaceWear, step, RACE);
	                }
	            } );
	        	
	        	builder.append( getFormattedPercentualLabel(actualDetail.getWearTotal()));
	        	
	        	builder.nextLine();
			}
	        
	        JButton reset = new JButton("Descartar modificações");
	        reset.setIcon(UIUtils.createImageIcon("/icons/trash_16.png"));
	        reset.setMnemonic(KeyEvent.VK_M);
	        reset.setToolTipText("Descarta as modificações...");
	        reset.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	planner.getStep(selectedRow).setCustomCarWearDetail(null);
	            	rebuild();
	            }
	        });

	        //FIXME
	        builder.append("");
	        builder.append("");
	        builder.append("");
	        builder.append("");
	        builder.append("");
	        builder.append(reset, 5);
	        builder.nextLine();

	        
        }else{
        	logger.error("Erro ao montar o PlanSelectedRaceDetailDialog: planner é nulo ");
        }
        
        add( builder.getPanel(), BorderLayout.CENTER );
    }

    public void generateDialog(){
    	buildFields();
    	
    }    
	public CarWearPlanner getPlanner() {
		return planner;
	}

	public void setPlanner(CarWearPlanner planner) {
		this.planner = planner;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	
	private JLabel createLabel(String text, Color color){
		JLabel lbl = new JLabel();
		lbl.setText(text);
		lbl.setForeground(color);
		
		return lbl;
	}
	
	private String getFormattedAction(Action action, CarWearDetail detail){
		String retorno = new String();
		
		switch (action) {
		case KEEP:
			retorno = "Manter";
			break;

		case DOWNGRADE:
			retorno = "Rebaixar("+ detail.getPartLevel() + ")" ;
			break;
			
		case REPLACE:
			retorno = "Trocar("+ detail.getPartLevel() + ")";
			detail.setWearTestBefore(0);			
			break;
		default:
			break;
		}
		
		return retorno;
	}
	
	private JLabel getFormattedPercentualLabel(double percentual){
		String formatted = String.format( "%3.0f%%",percentual);
		
		Color color = Color.BLACK;
		if(percentual > 95){
			color = Color.RED;
		}
		return createLabel(formatted, color);
		
	}

	public WearPlanTableModel getTmodel() {
		return tmodel;
	}

	public void setTmodel(WearPlanTableModel tmodel) {
		this.tmodel = tmodel;
	}
	
	private void setCustomDetail(CarWearDetail actualDetail, CarWearDetail customCarWearDetail, CarWearDetail detail, JSpinner spinner, WearStep step, int operation){
    	CarWearDetail customDetail = actualDetail;

    	
    	if(customCarWearDetail == null){ // Creates customDetail
    		customDetail = detail.clone();
    	}
		if(operation == RACE){
		
			customDetail.setWearRace(Double.valueOf((Double)spinner.getValue())); //Set's user input value
			
			customRace = detail.getWearRace() != customDetail.getWearRace() ? true : false; 
			
		}else if (operation == TEST_BEFORE){
			
			customDetail.setWearTestBefore(Double.valueOf((Double)spinner.getValue())); //Set's user input value
			
			customTestBefore = detail.getWearTestBefore() != customDetail.getWearTestBefore() ? true : false; 
			
		}else if (operation == TEST_AFTER){

			customDetail.setWearTestAfter(Double.valueOf((Double)spinner.getValue())); //Set's user input value
			
			customTestAfter = detail.getWearTestAfter() != customDetail.getWearTestAfter() ? true : false; 
		}
    	
		if(!this.customRace && !this.customTestBefore && !this.customTestAfter){ // Removes customDetail if it's equals default
			customDetail = null;
		}
		step.addCustomCarWearDetail(customDetail, detail.getPartIndex());
		
		rebuild();
		
	}

	private void rebuild() {
		tmodel.setValueAt(null, selectedRow, -1); //Rebuild Table
        buildFields(); // Rebuild modal dialog
	}

}
