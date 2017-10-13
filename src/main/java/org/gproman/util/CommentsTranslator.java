package org.gproman.util;

import static org.gproman.model.race.Comment.Part.BRA;
import static org.gproman.model.race.Comment.Part.ENG;
import static org.gproman.model.race.Comment.Part.GEA;
import static org.gproman.model.race.Comment.Part.SUS;
import static org.gproman.model.race.Comment.Part.WNG;
import static org.gproman.model.race.Comment.Satisfaction.D;
import static org.gproman.model.race.Comment.Satisfaction.DD;
import static org.gproman.model.race.Comment.Satisfaction.DDD;
import static org.gproman.model.race.Comment.Satisfaction.I;
import static org.gproman.model.race.Comment.Satisfaction.II;
import static org.gproman.model.race.Comment.Satisfaction.III;

import java.util.HashMap;
import java.util.Map;

import org.gproman.model.race.Comment;

public class CommentsTranslator {
    private final static Map<String, Comment> TRANSLATIONS = new HashMap<String, Comment>();
    
    static {
        TRANSLATIONS.put( "Asas: Falta ao carro muita velocidade nas retas", new Comment(WNG,DDD) );
        TRANSLATIONS.put( "Asas: O carro está perdendo alguma velocidade nas retas", new Comment(WNG,DD) );
        TRANSLATIONS.put( "Asas: O carro poderia ter um pouco mais de velocidade nas retas", new Comment(WNG,D) );
        TRANSLATIONS.put( "Asas: Estou perdendo um pouco de aderência nas curvas", new Comment(WNG,I) );
        TRANSLATIONS.put( "Asas: O carro é muito instável em muitas curvas", new Comment(WNG,II) );
        TRANSLATIONS.put( "Asas: Não posso dirigir o carro, ele não tem aderência", new Comment(WNG,III) );
        
        TRANSLATIONS.put( "Motor: Não, não, não!!! Favoreça muito mais as baixas rotações!", new Comment(ENG,DDD) );
        TRANSLATIONS.put( "Motor: As rotações estão muito altas", new Comment(ENG,DD) );
        TRANSLATIONS.put( "Motor: Tente favorecer um pouco mais as baixas rotações", new Comment(ENG,D) );
        TRANSLATIONS.put( "Motor: Eu sinto que não tenho força suficiente no motor durante as retas", new Comment(ENG,I) );
        TRANSLATIONS.put( "Motor: A força do motor nas retas não é suficiente", new Comment(ENG,II) );
        TRANSLATIONS.put( "Motor: Você deve tentar favorecer muito mais as altas rotações", new Comment(ENG,III) );

        TRANSLATIONS.put( "Freios: Por favor, coloque o balanço dos freios muito mais para trás", new Comment(BRA,DDD) );
        TRANSLATIONS.put( "Freios: Eu penso que a eficácia dos freios pode ser maior se movermos o balanço para trás", new Comment(BRA,DD) );
        TRANSLATIONS.put( "Freios: Coloque o balanço um pouco mais para trás", new Comment(BRA,D) );
        TRANSLATIONS.put( "Freios: Eu gostaria de ter o balanço um pouco mais para frente", new Comment(BRA,I) );
        TRANSLATIONS.put( "Freios: Eu penso que a eficácia dos freios pode ser maior se movermos o balanço para frente", new Comment(BRA,II) );
        TRANSLATIONS.put( "Freios: Eu me sentiria muito mais confortável se movêssemos o balanço para a frente", new Comment(BRA,III) );

        TRANSLATIONS.put( "Câmbio: Por favor, coloque um pouco menor o intervalo entre as marchas.", new Comment(GEA,DDD) );
        TRANSLATIONS.put( "Câmbio: A relação do câmbio é muito longa", new Comment(GEA,DD) );
        TRANSLATIONS.put( "Câmbio: Eu não posso tirar vantagem da força do motor. Coloque a relação do câmbio um pouco menor", new Comment(GEA,D) );
        TRANSLATIONS.put( "Câmbio: Estou muito frequentemente no vermelho. Coloque a relação do câmbio um pouco mais alta", new Comment(GEA,I) );
        TRANSLATIONS.put( "Câmbio: O intervalo entre marchas está muito curto", new Comment(GEA,II) );
        TRANSLATIONS.put( "Câmbio: Eu sinto que o motor vai explodir. Coloque o intervalo de marchas bem maior.", new Comment(GEA,III) );

        TRANSLATIONS.put( "Suspensão: O carro está rígido demais. Diminua muito mais a rigidez", new Comment(SUS,DDD) );
        TRANSLATIONS.put( "Suspensão: A rigidez da suspensão está muito alta", new Comment(SUS,DD) );
        TRANSLATIONS.put( "Suspensão: O carro está muito rígido. Diminua um pouco a rigidez", new Comment(SUS,D) );
        TRANSLATIONS.put( "Suspensão: Eu penso que com uma suspensão um pouco mais rígida eu poderei ir mais rápido", new Comment(SUS,I) );
        TRANSLATIONS.put( "Suspensão: A rigidez da suspensão está muito baixa", new Comment(SUS,II) );
        TRANSLATIONS.put( "Suspensão: A rigidez da suspensão deve ser muito maior", new Comment(SUS,III) );
        
        TRANSLATIONS.put( "Wings: I am really missing a lot of speed in straights", new Comment(WNG,DDD) );
        TRANSLATIONS.put( "Wings: The car is lacking some speed in the straights", new Comment(WNG,DD) );
        TRANSLATIONS.put( "Wings: The car could have a bit more speed in the straights", new Comment(WNG,D) );
        TRANSLATIONS.put( "Wings: I am missing a bit of grip in the curves", new Comment(WNG,I) );
        TRANSLATIONS.put( "Wings: The car is very unstable in many corners", new Comment(WNG,II) );
        TRANSLATIONS.put( "Wings: I cannot drive the car, there's no grip on it", new Comment(WNG,III) );

        TRANSLATIONS.put( "Engine: No, no, no!!! Favor a lot more the low revs!", new Comment(ENG,DDD) );
        TRANSLATIONS.put( "Engine: The engine revs are too high", new Comment(ENG,DD) );
        TRANSLATIONS.put( "Engine: Try to favor a bit more the low revs", new Comment(ENG,D) );
        TRANSLATIONS.put( "Engine: I feel that I do not have enough engine power in the straights", new Comment(ENG,I) );
        TRANSLATIONS.put( "Engine: The engine power on the straights is not sufficient", new Comment(ENG,II) );
        TRANSLATIONS.put( "Engine: You should try to favor a lot more the high revs", new Comment(ENG,III) );
                           
        TRANSLATIONS.put( "Brakes: Please move the balance a lot more to the back", new Comment(BRA,DDD) );
        TRANSLATIONS.put( "Brakes: Please, move the balance a lot more to the back", new Comment(BRA,DDD) );
        TRANSLATIONS.put( "Brakes: I think the brakes effectiveness could be higher if we move the balance to the back", new Comment(BRA,DD) );
        TRANSLATIONS.put( "Brakes: Put the balance a bit more to the back", new Comment(BRA,D) );
        TRANSLATIONS.put( "Brakes: I would like to have the balance a bit more to the front", new Comment(BRA,I) );
        TRANSLATIONS.put( "Brakes: I think the brakes effectiveness could be higher if we move the balance to the front", new Comment(BRA,II) );
        TRANSLATIONS.put( "Brakes: I would feel a lot more comfortable to move the balance to the front", new Comment(BRA,III) );

        TRANSLATIONS.put( "Gear: Please, put a lot lower ratio between gears", new Comment(GEA,DDD) );
        TRANSLATIONS.put( "Gear: Please put a lot lower ratio between the gears", new Comment(GEA,DDD) );
        TRANSLATIONS.put( "Gear: The gear ratio is too high", new Comment(GEA,DD) );
        TRANSLATIONS.put( "Gear: I cannot take advantage of the power of the engine. Put the gear ratio a bit lower", new Comment(GEA,D) );
        TRANSLATIONS.put( "Gear: I am very often in the red. Put the gear ratio a bit higher", new Comment(GEA,I) );
        TRANSLATIONS.put( "Gear: The gear ratio is too low", new Comment(GEA,II) );
        TRANSLATIONS.put( "Gear: It feels like the engine is going to explode. Put a lot higher ratio between gears", new Comment(GEA,III) );
        TRANSLATIONS.put( "Gear: It feels like the engine is going to explode. Put a lot higher ratio between the gears", new Comment(GEA,III) );
                           
        TRANSLATIONS.put( "Suspension: The car is far too rigid. Lower a lot the rigidit", new Comment(SUS,DDD) );
        TRANSLATIONS.put( "Suspension: The suspension rigidity is too high", new Comment(SUS,DD) );
        TRANSLATIONS.put( "Suspension: The car is too rigid. Lower a bit the rigidity", new Comment(SUS,D) );
        TRANSLATIONS.put( "Suspension: I think with a bit more rigid suspension I will be able to go faster", new Comment(SUS,I) );
        TRANSLATIONS.put( "Suspension: The suspension rigidity is too low", new Comment(SUS,II) );
        TRANSLATIONS.put( "Suspension: The suspension rigidity should be a lot higher", new Comment(SUS,III) );
    }
    
    public static Comment getTranslation( String comment ) {
        return TRANSLATIONS.get( comment.trim().replaceAll( "<.*?>", "" ).replaceAll( "\\s+", " " ) );
    }

}
