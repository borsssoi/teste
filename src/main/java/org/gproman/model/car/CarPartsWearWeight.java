package org.gproman.model.car;

import java.math.BigDecimal;

public class CarPartsWearWeight {
    BigDecimal[] parts = new BigDecimal[ 11 ];

    public CarPartsWearWeight(BigDecimal[] parts) {
        this.parts = parts;
    }

    public CarPartsWearWeight() {
        for( int i = 0; i<parts.length; i++ ) {
            parts[i] = BigDecimal.ZERO;
        }
    }

    public BigDecimal[] getParts() {
        return parts;
    }

    public void setParts(BigDecimal[] parts) {
        this.parts = parts;
    }

}
