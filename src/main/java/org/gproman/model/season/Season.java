package org.gproman.model.season;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;
import org.gproman.model.race.Race;

public class Season extends PersistentEntity {
    
    private Integer number;
    private List<Race> races;
    private TyreSupplier supplier;
    private String groupName;
    private String managerName;
    
    public Season() {
        races = new ArrayList<Race>();
        supplier = null;
    }
    
    public Season(Integer number,
                  List<Race> races,
                  TyreSupplier supplier,
                  String groupName,
                  String managerName ) {
        super();
        this.number = number;
        this.races = races;
        this.supplier = supplier;
        this.groupName = groupName;
        this.managerName = managerName;
    }
    
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<Race> getRaces() {
        return races;
    }

    public void setRaces(List<Race> races) {
        this.races = races;
    }
    
    public TyreSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(TyreSupplier supplier) {
        this.supplier = supplier;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "======== Season " )
               .append( number )
               .append( " / Supplier: " )
               .append( supplier )
               .append( " / Group: " )
               .append( groupName )
               .append( " / Manager: " )
               .append( managerName )
               .append( "========\n" );
        for( Race race : races ) {
            builder.append( race.toString() ).append( "\n" );
        }
        return builder.toString();
    }
}
