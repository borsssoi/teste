package org.gproman.model.race;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public enum TestPriority {

    NONE("no special priority","nenhuma prioridade em especial", "Nenhuma", BigDecimal.ZERO), 
    TOP_SPEED("top speed","velocidade máxima", "Velocidade", BigDecimal.ZERO), 
    CORNERING("cornering","fazer curvas", "Curvas", BigDecimal.ZERO), 
    HAIRPINS("hairpins","cotovelos", "Cotovelos", BigDecimal.ZERO), 
    BRAKING("braking","frear", "Frenagem", BigDecimal.ZERO),
    OVERTAKING("overtaking","ultrapassagem", "Ultrapassagem", BigDecimal.ZERO), 
    CHICANES("chicanes","chicanes", "Chicanes", BigDecimal.ZERO), 
    LIMITS("test car limits","testar os limites do carro", "Limites", new BigDecimal( 100 ) ), 
    SETUP("setup tuning","afinação do ajuste", "Ajuste", BigDecimal.ZERO); 

    public final String english;
    public final String portuguese;
    public final String mnemPtBr;
    public final BigDecimal risk;
    
    private TestPriority( String english, String portuguese, String mnemPtBr, BigDecimal risk ) {
        this.english = english;
        this.portuguese = portuguese;
        this.mnemPtBr = mnemPtBr;
        this.risk = risk;
    }
    
    private static Map<String, TestPriority> map = new HashMap<String, TestPriority>();
    static {
        map.put( NONE.english, NONE );
        map.put( TOP_SPEED.english, TOP_SPEED );
        map.put( CORNERING.english, CORNERING );
        map.put( HAIRPINS.english, HAIRPINS );
        map.put( BRAKING.english, BRAKING );
        map.put( OVERTAKING.english, OVERTAKING );
        map.put( CHICANES.english, CHICANES );
        map.put( LIMITS.english, LIMITS );
        map.put( SETUP.english, SETUP );
        map.put( NONE.portuguese, NONE );
        map.put( TOP_SPEED.portuguese, TOP_SPEED );
        map.put( CORNERING.portuguese, CORNERING );
        map.put( HAIRPINS.portuguese, HAIRPINS );
        map.put( BRAKING.portuguese, BRAKING );
        map.put( OVERTAKING.portuguese, OVERTAKING );
        map.put( CHICANES.portuguese, CHICANES );
        map.put( LIMITS.portuguese, LIMITS );
        map.put( SETUP.portuguese, SETUP );
    }
    
    public static TestPriority determinePriority(String string) {
        return map.get( string.toLowerCase() );
    }
}
