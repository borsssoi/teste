package org.gproman.ui.table;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.gproman.calc.CarWearPlanner;
import org.gproman.calc.CarWearPlanner.StepAction;
import org.gproman.model.car.Car;

public class WearPlanTableModel extends AbstractTableModel {
    private static final long serialVersionUID   = 5503929276563600355L;
    
    public static final int  PARTS_FIRST_COLUMN = 5;
    
    
    private String[][]        columnNames;
    private CarWearPlanner    planner;

    public WearPlanTableModel() {
    	
    	columnNames = new String[28][];
        columnNames[0] = new String[]{"", "#"};
        columnNames[1] = new String[]{"", "Corrida"};
        columnNames[2] = new String[]{"", "Risco"};
        columnNames[3] = new String[]{"", "T1"};
        columnNames[4] = new String[]{"", "T2"};
        
        
        for ( int i = PARTS_FIRST_COLUMN; i < columnNames.length - 1; i += 2 ) {
            columnNames[i] = new String[2];
            columnNames[i + 1] = new String[2];
            columnNames[i][0] = Car.PARTS[convertToPartIndex( i )];
            columnNames[i][1] = "Ação";
            columnNames[i + 1][0] = "";
            columnNames[i + 1][1] = "Fim";
        }
        columnNames[columnNames.length - 1] = new String[]{"", "Custo"};
    }

    private int convertToPartIndex(int i) {
        return (i - PARTS_FIRST_COLUMN) / 2;
    }

    public List<StepAction> getActionsFor(int row,
                                          int column) {
        return this.planner.getActionsFor( row, convertToPartIndex( column ) );
    }

    public void setPlanner(CarWearPlanner planner) {
        this.planner = planner;
    }

    public int getRowCount() {
        return planner != null ? planner.getSteps().size() + 1 : 0;
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column][1];
    }

    public Object getValueAt(int row,
                             int col) {
        boolean isLevelCol = (col > 4) && (col < columnNames.length - 1) && ((col - PARTS_FIRST_COLUMN) % 2 == 0);
        if ( row == planner.getSteps().size() ) {
            // bottom cost
            if ( isLevelCol ) {
                // part cost
                return planner.getCostForPart( convertToPartIndex( col ) );
            } else if ( col == columnNames.length - 1 ) {
                // total cost
                return planner.getTotalCost();
            } else {
                return null;
            }
        }
        switch ( col ) {
            case 0 :
                return planner.getStep( row ).getRace().getNumber();
            case 1 :
                return planner.getStep( row ).getTrack().getName();
            case 2 :
                return planner.getStep( row ).getRisk();
            case 3: 
            	return planner.getStep( row ).getTestLapsBefore();
            case 4:
            	return planner.getStep( row ).getTestLapsAfter();
                
            default :
                if ( isLevelCol ) {
                    // level/action
                    return planner.getStep( row ).getAction( convertToPartIndex( col ) );
                } else if ( col < columnNames.length - 1 ) {
                    // wear
                    return planner.getStep( row ).getFinishCar().getParts()[convertToPartIndex( col )].getWear();
                } else {
                    // last column = cost
                    return planner.getStep( row ).getCost();
                }
        }
    }

    @Override
    public boolean isCellEditable(int row,
                                  int col) {
        if( row == 0 && planner.getStartCar( 0 ).getParts()[0].getOptions().isEmpty() && col >= PARTS_FIRST_COLUMN ) {
            // update already done for this race, so no longer editable
            return false;
        }
        
        boolean isLevelCol = (col > 2) && (col < columnNames.length - 1) && ((col - PARTS_FIRST_COLUMN) % 2 == 0);
        boolean isRiskCol = col == 2;
        boolean isTestCol = col ==3 || col == 4;
        
        // it is an action column or a risk column and it is not the last row
        boolean result = (isLevelCol || isRiskCol || isTestCol) && (row < planner.getSteps().size());
        
        return result;
    }

    @Override
    public void setValueAt(Object aValue,
                           int rowIndex,
                           int columnIndex) {
    	
    	if ( columnIndex == 2 ) {
            // risk
            int newRisk = ((Number) aValue).intValue();
            if ( planner.getRisk( rowIndex ).intValue() != newRisk ) {
                planner.setRisk( rowIndex, new BigDecimal( newRisk ) );
                planner.projectWear( rowIndex );
                fireTableRowsUpdated( rowIndex, planner.getSteps().size() );
            }
    	}else if (columnIndex == 3){ //T1
            int newTestLapsBefore = ((Number) aValue).intValue();
            if ( planner.getStep( rowIndex ).getTestLapsBefore() != newTestLapsBefore ) {
                planner.getStep( rowIndex ).setTestLapsBefore(newTestLapsBefore );
                planner.projectWear( rowIndex );
                fireTableRowsUpdated( rowIndex, planner.getSteps().size() );
            }
    	}else if (columnIndex == 4){//T2
            int newTestLapsAfter = ((Number) aValue).intValue();
            if ( planner.getStep( rowIndex ).getTestLapsAfter() != newTestLapsAfter ) {
                planner.getStep( rowIndex ).setTestLapsAfter(newTestLapsAfter );
                planner.projectWear( rowIndex );
                fireTableRowsUpdated( rowIndex, planner.getSteps().size() );
            }
    	} else if (columnIndex == -1){
    		planner.projectWear( rowIndex);
    		fireTableRowsUpdated( rowIndex, planner.getSteps().size() );
    	} else {
            StepAction action = (StepAction) aValue;
            StepAction oldAction = planner.getAction( rowIndex, action.getPartIndex() );
            if ( !action.equals( oldAction ) ) {
                planner.setAction( rowIndex, action );
                planner.projectWear( rowIndex );
                fireTableRowsUpdated( rowIndex, planner.getSteps().size() );
            }
        }
    }

    public void reset() {
        this.planner.reset();
        fireTableDataChanged();
    }

}