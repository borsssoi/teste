package org.gproman.model.track;

import java.math.BigDecimal;

import org.gproman.model.PersistentEntity;

public class WearCoefs extends PersistentEntity {
    
    private BigDecimal[] coefs = new BigDecimal[9];

    public WearCoefs() {
        for( int i=0; i<coefs.length; i++ ) {
            coefs[i] = BigDecimal.ZERO;
        }
    }

    public WearCoefs(BigDecimal[] coefs) {
        this.coefs = coefs;
    }
    
    public BigDecimal[] getCoefs() {
        return coefs;
    }

    public void setCoefs(BigDecimal[] coefs) {
        this.coefs = coefs;
    }

}
