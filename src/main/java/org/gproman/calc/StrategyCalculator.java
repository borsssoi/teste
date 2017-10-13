package org.gproman.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.gproman.model.race.Race;
import org.gproman.model.race.Tyre;
import org.gproman.model.race.Weather;
import org.gproman.model.season.TyreSupplier;
import org.gproman.model.track.Track;

public class StrategyCalculator {

    private Race                    race;
    private Set<RaceSegment>        segments;
    private List<StrategyCandidate> candidates;
    private double                  raceDryConsumption;
    private double                  lapDryConsumption;
    private int                     lapTime;           // milliseconds
    private int                     avgPitTime;        // milliseconds
    private TyreSupplier            supplier;
    private double                  temperature;
    private BigDecimal              compoundDiff;
    private double                  baseDurability;
    private int                     risk;

    /**
     * @param race race for which the strategy will be calculated  
     * @param engineLevel level of the engine
     * @param electronicsLevel level of the electronics
     * @param lapTime lap time in milliseconds
     * @param avgPitTime
     */
    public StrategyCalculator(Race race,
                              int engineLevel,
                              int electronicsLevel,
                              TyreSupplier supplier) {
        this.race = race;
        this.segments = new TreeSet<StrategyCalculator.RaceSegment>();
        this.candidates = new ArrayList<StrategyCalculator.StrategyCandidate>();
        this.avgPitTime = 25000; // just a default
        this.baseDurability = 80; // just a default
        this.risk = 0; // just a default
        this.supplier = supplier;
        setAverageTemperature( race.getAverageTemperature() );
        calculateConsumption( engineLevel,
                              electronicsLevel,
                              race );
        initSegments( candidates, segments, race, lapDryConsumption );
        setLapTime( 90000 ); // just a default
    }

    public Race getRace() {
        return race;
    }

    public int getLapTime() {
        return lapTime;
    }

    public StrategyCalculator setLapTime(int lapTime) {
        this.lapTime = lapTime;
        calculateTimeFactors( candidates,
                              segments,
                              lapTime );
        return this;
    }

    public int getAveragePitTime() {
        return this.avgPitTime;
    }

    public StrategyCalculator setAveragePitTime(int avgPitTime) {
        this.avgPitTime = avgPitTime;
        return this;
    }

    public double getRaceDryConsumption() {
        return raceDryConsumption;
    }

    public double getLapDryConsumption() {
        return lapDryConsumption;
    }

    public Set<RaceSegment> getSegments() {
        return segments;
    }

    public List<StrategyCandidate> getCandidates() {
        return candidates;
    }

    public StrategyCalculator setAverageTemperature(double temp) {
        this.temperature = temp;
        //calculateCompoundDiff( race.getTrack(), temp, supplier );
        return this;
    }

    public double getAverageTemperature() {
        return this.temperature;
    }

    public BigDecimal getCompoundDiff() {
        return this.compoundDiff;
    }

    public double getBaseDurability() {
        return baseDurability;
    }

    public StrategyCalculator setBaseDurability(double baseDurability) {
        this.baseDurability = baseDurability;
        return this;
    }

    public int getRisk() {
        return risk;
    }

    public StrategyCalculator setRisk(int risk) {
        this.risk = risk;
        return this;
    }

    public void setBaseCompoundDiff(int baseCompDiff) {
        compoundDiff = new BigDecimal( .001 * (double) baseCompDiff );
    }

//    private void calculateCompoundDiff(Track track,
//                                       double temperature,
//                                       TyreSupplier supplier) {
//        compoundDiff = CompoundDiffCalculator.predictDiff( track, temperature, supplier );
//    }

    private void calculateConsumption(int engineLevel,
                                      int electronicsLevel,
                                      Race race) {
        BigDecimal dryConsumption = FuelCalculator.predictConsumption( Weather.SUNNY,
                                                                       engineLevel,
                                                                       electronicsLevel,
                                                                       race.getTrack() );
        raceDryConsumption = dryConsumption.doubleValue() * race.getTrack().getDistance();
        lapDryConsumption = raceDryConsumption / race.getTrack().getLaps();
    }

    private void initSegments(List<StrategyCandidate> candidates,
                              Set<RaceSegment> segments,
                              Race race,
                              double lapDryConsumption) {
        int laps = race.getTrack().getLaps();
        Set<Integer> stops = new TreeSet<Integer>();
        for ( int i = 0; i < 4; i++ ) {
            // for each strategy from 1 to 4 pits
            StrategyCandidate cand = new StrategyCandidate( i + 1, race.getTrack().getTimeInOut() );

            int startLap = 0;
            int finishLap = 0;
            for ( int j = 0; j <= i; j++ ) {
                // for each stint
                finishLap = (int) Math.ceil( (j + 1) * (double) laps / (double) (i + 2) );
                stops.add( finishLap );
                cand.getStints()[j] = new Stint( startLap, finishLap, Math.ceil( (finishLap - startLap) * lapDryConsumption ), lapDryConsumption );
                startLap = finishLap;
            }
            cand.getStints()[i + 1] = new Stint( startLap, laps, Math.ceil( (laps - startLap) * lapDryConsumption ), lapDryConsumption );
            candidates.add( cand );
        }
        int startLap = 0;
        for ( Integer finishLap : stops ) {
            segments.add( new RaceSegment( startLap, finishLap ) );
            startLap = finishLap;
        }
        segments.add( new RaceSegment( startLap, laps ) );
    }

    private void calculateTimeFactors(List<StrategyCandidate> candidates,
                                      Set<RaceSegment> segments,
                                      int lapTime) {
        // reset calculation
        for( StrategyCandidate cand : candidates ) {
            cand.setFuelTimeLoss( BigDecimal.ZERO );
        }
        // all factors are calculated in comparison to the 4 pits strategy
        StrategyCandidate base = candidates.get( 3 );
        for ( RaceSegment seg : segments ) {
            Stint baseStint = base.getStintForLap( seg.getFinishLap() );
            double baseFuelAtStart = baseStint.getFuelAtLap( seg.getStartLap() );

            for ( int i = 0; i < 3; i++ ) {
                StrategyCandidate cand = candidates.get( i );
                Stint stint = cand.getStintForLap( seg.getFinishLap() );
                // factor is the fuel difference between the strategies during the laps of this segment
                double fuelDiff = stint.getFuelAtLap( seg.getStartLap() ) - baseFuelAtStart;
                BigDecimal diff = new BigDecimal( fuelDiff * seg.getLapCount() * 0.0002 * (lapTime / 1000) );
                cand.setFuelTimeLoss( cand.getFuelTimeLoss().add( diff ) );
            }
        }
    }

    public SortedSet<TopStrategy> getTopStrategies() {
        SortedSet<TopStrategy> strategies = new TreeSet<TopStrategy>();
        for ( Tyre compound : Tyre.values() ) {
            double durab = TyreDurabilityCalculator.predictDurability( baseDurability,
                                                                       compound,
                                                                       risk );
            int durLaps = (int) (durab / race.getTrack().getLapDistance());

            for ( StrategyCandidate cand : candidates ) {
                // we only consider viable strategies if we have at least 2 remaining laps per stint
                if ( ((cand.getPits() + 1) * (durLaps-1)) > race.getTrack().getLaps() ) {
                    // this is a viable strategy
                    if (!compound.equals(Tyre.RAIN)) {
                        TopStrategy top = new TopStrategy( compound,
                                                           cand,
                                                           avgPitTime,
                                                           compoundDiff,
                                                           race.getTrack(),
                                                           durab,
                                                           durLaps );
                        strategies.add( top );
                    }
                }
            }
        }
        return strategies;
    }

    public static class TopStrategy
            implements
            Comparable<TopStrategy> {
        private final Tyre              compound;
        private final StrategyCandidate strat;
        private final int               avgPitTime;
        private final BigDecimal        compoundDiff;
        private final Track             track;
        private final double            totalTimeLoss;
        private final double            durab;
        private final int               durLaps;

        public TopStrategy(Tyre compound,
                           StrategyCandidate strat,
                           int avgPitTime,
                           BigDecimal compoundDiff,
                           Track track,
                           double durab,
                           int durLaps) {
            this.compound = compound;
            this.strat = strat;
            this.avgPitTime = avgPitTime;
            this.compoundDiff = compoundDiff;
            this.track = track;
            this.durab = durab;
            this.durLaps = durLaps;
            this.totalTimeLoss = this.strat.getTotalTimeLoss( this.avgPitTime ).doubleValue() + track.getLaps() * compoundDiff.doubleValue() * compound.diffFactor;
        }

        public int getPits() {
            return strat.getPits();
        }

        public Tyre getCompound() {
            return this.compound;
        }
        
        public BigDecimal getCompoundDiff() {
            return this.compoundDiff;
        }

        public double getTotalTimeLoss() {
            return totalTimeLoss;
        }
        
        public double getDurability() {
            return durab;
        }
        
        public int getDurabilityLaps() {
            return durLaps;
        }
        
        public double getRemainingTyres() {
            return 1 - (strat.getStints()[0].getLapCount() * track.getLapDistance() ) / durab;
        }

        @Override
        public int compareTo(TopStrategy o) {
            return (int) ((this.totalTimeLoss - o.totalTimeLoss) * 1000);
        }
        
        @Override
        public String toString() {
            return String.format( "Pneu %s com %d pit: %7.3fs - %2.0f%%", 
                                  compound.toString(),
                                  strat.getPits(),
                                  this.totalTimeLoss,
                                  getRemainingTyres()*100 );
        }
    }

    public static class RaceSegment
            implements
            Comparable<RaceSegment> {
        private final int startLap;
        private final int finishLap;

        public RaceSegment(int startLap,
                           int finishLap) {
            this.startLap = startLap;
            this.finishLap = finishLap;
        }

        public int getLapCount() {
            return finishLap - startLap;
        }

        public int getStartLap() {
            return startLap;
        }

        public int getFinishLap() {
            return finishLap;
        }

        @Override
        public int compareTo(RaceSegment o) {
            return this.startLap - o.startLap;
        }

        @Override
        public String toString() {
            return "RaceSegment [startLap=" + startLap + ", finishLap=" + finishLap + "]";
        }
    }

    public static class StrategyCandidate {
        private int        pits;
        private int        timeInOut;
        private BigDecimal fuelTimeLoss;
        private Stint[]    stints;

        public StrategyCandidate() {
        }

        /**
         * 
         * @param pits number of pits
         * @param avgPitTime average pit time for the given manager in milliseconds
         * @param timeInOut time to enter and exit the pit in milliseconds
         */
        public StrategyCandidate(int pits,
                                 int timeInOut) {
            this.pits = pits;
            this.timeInOut = timeInOut;
            this.stints = new Stint[pits + 1];
            this.fuelTimeLoss = BigDecimal.ZERO;
        }

        private double weightedPitTime(double avgPitTime) {
            switch ( pits ) {
                case 1 :
                    return avgPitTime * 1.29 / 1000.0;
                case 2 :
                    return avgPitTime * 1.16 / 1000.0;
                case 3 :
                    return avgPitTime * 1.08 / 1000.0;
            }
            return avgPitTime / 1000.0;
        }

        public int getPits() {
            return pits;
        }

        public void setPits(int pits) {
            this.pits = pits;
        }

        public Stint[] getStints() {
            return stints;
        }

        public void setStints(Stint[] stints) {
            this.stints = stints;
        }

        public BigDecimal getFuelTimeLoss() {
            return fuelTimeLoss;
        }

        public void setFuelTimeLoss(BigDecimal timeFactor) {
            this.fuelTimeLoss = timeFactor;
        }

        public Stint getStintForLap(int lap) {
            for ( Stint stint : stints ) {
                if ( stint.getStartLap() < lap && stint.getFinishLap() >= lap ) {
                    return stint;
                }
            }
            // this should never happen
            return null;
        }

        public BigDecimal getPitTimeLoss(int avgPitTime) {
            return new BigDecimal( pits * (weightedPitTime( avgPitTime ) + (timeInOut / 1000.0)) );
        }

        public BigDecimal getTotalTimeLoss(int avgPitTime) {
            return getPitTimeLoss( avgPitTime ).add( this.fuelTimeLoss );
        }

        @Override
        public String toString() {
            return "StrategyCandidate [pits=" + pits + ", timeFactor=" + fuelTimeLoss + ", stints=" + Arrays.toString( stints ) + "]";
        }
    }

    public static class Stint {
        private final int    startLap;
        private final int    finishLap;
        private final double fuel;
        private final double fuelPerLap;

        public Stint(int startLap,
                     int finishLap,
                     double fuel,
                     double fuelPerLap) {
            super();
            this.startLap = startLap;
            this.finishLap = finishLap;
            this.fuel = fuel;
            this.fuelPerLap = fuelPerLap;
        }

        public int getLapCount() {
            return this.finishLap-this.startLap;
        }

        public double getFuelAtLap(int lap) {
            // returns the starting fuel - the fuel spent before the giving lap
            return fuel - ((lap - startLap) * fuelPerLap);
        }

        public int getStartLap() {
            return startLap;
        }

        public int getFinishLap() {
            return finishLap;
        }

        public double getFuel() {
            return fuel;
        }

        @Override
        public String toString() {
            return "Stint [startLap=" + startLap + ", finishLap=" + finishLap + ", fuel=" + fuel + "]";
        }

    }

}
