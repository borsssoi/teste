package org.gproman.model.season;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.gproman.model.race.Tyre;
import org.gproman.ui.UIUtils;

public enum TyreSupplier {

    PIPIRELLI("Pipirelli", new BigDecimal( "0" ), new double[] {143.7, 191.1, 254.2, 338, 388.7} ),
    AVONN("Avonn", new BigDecimal( "0.02" ), new double[] {0, 0, 0, 0, 0} ),
    YOKOMAMA("Yokomama", new BigDecimal( "0.05" ), new double[] {150.9, 200.7, 267, 354.9, 408.2} ),
    DUNNOLOP("Dunnolop", new BigDecimal( "0.07" ), new double[] {158.96, 211.42, 281.26, 373.88, 430.02} ),
    CONTIMENTAL("Contimental", new BigDecimal( "0.029" ), new double[] {0, 0, 0, 0, 0} ),
    BADYEAR("Badyear", new BigDecimal( "0.09" ), new double[] {175.5816, 233.42, 310.51, 412.88, 474.82} ),
    HANCOCK("Hancock", new BigDecimal( "0.11" ), new double[] {0, 0, 0, 0, 0} ),
    MICHELINI("Michelini", new BigDecimal( "0.075" ), new double[] {184, 244.7, 325.5, 432.9, 497.8} ),
    BRIDGEROCK("Bridgerock", new BigDecimal( "0.04" ), new double[] {167.235, 222.42, 295.885, 393.38, 452.42} );

    public final String     name;
    private ImageIcon  icon;
    private final BigDecimal diff;
    private final double[]   durabilityFactor;

    private TyreSupplier(String name,
                         BigDecimal diff,
                         double[] durabilityFactor ) {
        this.name = name;
        this.diff = diff;
        this.durabilityFactor = durabilityFactor;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getBBCode() {
        return name.substring( 0, 1 ) + " " + name.substring( 1 );
    }

    public ImageIcon getIcon() {
        if ( icon == null ) {
            icon = UIUtils.createImageIcon( "/icons/" + this.name.toLowerCase() + ".png" );
        }
        return icon;
    }

    private static Map<String, TyreSupplier> map = new HashMap<String, TyreSupplier>();
    static {
        map.put( PIPIRELLI.toString().toLowerCase(), PIPIRELLI );
        map.put( AVONN.toString().toLowerCase(), AVONN );
        map.put( YOKOMAMA.toString().toLowerCase(), YOKOMAMA );
        map.put( DUNNOLOP.toString().toLowerCase(), DUNNOLOP );
        map.put( CONTIMENTAL.toString().toLowerCase(), CONTIMENTAL);
        map.put( BADYEAR.toString().toLowerCase(), BADYEAR );
        map.put( HANCOCK.toString().toLowerCase(), HANCOCK);
        map.put( MICHELINI.toString().toLowerCase(), MICHELINI );
        map.put( BRIDGEROCK.toString().toLowerCase(), BRIDGEROCK );
    }

    public static TyreSupplier determineTyre(String string) {
        if( string != null ) {
            return map.get( string.toLowerCase() );
        }
        return null;
    }

    public BigDecimal getCompoundDiff() {
        return diff;
    }

    public double getDurabilityFactor(Tyre compound) {
        return durabilityFactor[ compound.diffFactor ];
    }
}
