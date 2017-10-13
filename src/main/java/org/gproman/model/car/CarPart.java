package org.gproman.model.car;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gproman.model.PersistentEntity;

public class CarPart extends PersistentEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = -6944031603704075856L;
    
    private String           name;
    private int              level;
    private double           wear;
    private List<PartOption> options = new ArrayList<PartOption>( 0 );

    public CarPart() {
    }

    public CarPart(String name,
                   int level,
                   double wear) {
        super();
        this.name = name;
        this.level = level;
        this.wear = wear;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getWear() {
        return wear;
    }

    public void setWear(double wear) {
        this.wear = wear;
    }

    public List<PartOption> getOptions() {
        return this.options;
    }
    
    public void setOptions( List<PartOption> options ) {
        this.options = options;
    }

    public void addOption(PartOption po) {
        this.options.add( po );
    }

    @Override
    public String toString() {
        return name + "(" + level + ", " + wear + "%)";
    }
    
    @Override
    public CarPart clone() {
        CarPart carPart = new CarPart( name, level, wear );
        carPart.setOptions( options );
        return carPart;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + level;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits( wear );
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        CarPart other = (CarPart) obj;
        if ( level != other.level ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( Double.doubleToLongBits( wear ) != Double.doubleToLongBits( other.wear ) ) return false;
        return true;
    }

}
