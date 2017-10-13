package org.gproman.calc;

import org.gproman.model.race.Tyre;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.TyreWear;

public class TyreDurabilityCalculator {

    /**
     * Calculo baseado na planilha da Brasil II. Não calcula durabilidade do pneu de chuva.
     * 
     * @param base durabilidade do XS no seco
     * @param compound composto a ser calculado
     * @param risk risco em CT
     * @return durabilidade estimada do composto para o risco
     */
    public static double predictDurability(double base,
                                           Tyre compound,
                                           int risk) {
        double val = base * (1.0 - (((double) risk) / 500.0));
        switch ( compound ) {
            case XSOFT :
                return val;
            case SOFT :
                return val * 1.32 /* * (1.0 - ((double)risk/500.0)) */;
            case MEDIUM :
                return val * 1.73 /* * (1.0 - ((double)risk/500.0)) */;
            case HARD :
                return val * 2.28 /* * (1.0 - ((double)risk/500.0)) */;
            case RAIN :
                return val * 2.99; // Ainda a confirmar - Baseado em que diferença entre o Medium e o Soft é de 131%.
        }
        return 0;
    }

    /**
     * Cálculo baseado nos parâmetros, para qualquer tipo de pneu, mas usando o desgaste de risco da Brasil II
     * 
     * @param temp temperatura média
     * @param hum humidade média
     * @param wear nível de desgaste da pista
     * @param supplier fornecedor
     * @param compound composto do pneu
     * @param risk risco desejado
     * 
     * @return
     */
    public static double predictDurability(int temp,
                                           int hum,
                                           TyreWear wear,
                                           TyreSupplier supplier,
                                           Tyre compound,
                                           int risk) {
        double tempFactor = Math.pow( 0.985221675, temp );
        double humFactor = Math.pow( 1.002, hum );
        double base = tempFactor * humFactor * wear.getWearFactor() * supplier.getDurabilityFactor( compound );
        //double dur = base * (1.0 - (((double) risk) / 500.0));
        double dur = base - ( base * compound.riskFactor * risk / 100);
        return dur;
    }

}
