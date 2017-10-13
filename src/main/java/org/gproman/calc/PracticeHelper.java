package org.gproman.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Comment;
import org.gproman.model.race.Comment.Part;
import org.gproman.model.race.Comment.Satisfaction;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.staff.TechDirector;
import org.gproman.model.track.Track;

public class PracticeHelper {

    private Forecast[]            forecast;
    private Track                 track;
    private Practice              practice;
    private Driver                driver;
    private TechDirector          td;
    private Car                   car;
    
    private int[]                 sz            = new int[6];
    private int[]                 init          = new int[6];
    private PartSetup[]           ps            = new PartSetup[6];
    private int                   lastLap;
    private Map<Integer, Integer> wingSplit     = new HashMap<Integer, Integer>();
    private int                   bestWingSplit = 0;
    
    private int                   trackWingSplit = 0;

    private boolean               dirty         = true;

    public PracticeHelper() {
    }

    public PracticeHelper(Track track,
                          Forecast[] forecast,
                          Practice practice,
                          Driver driver,
                          TechDirector td,
                          Car car) {
        this.track = track;
        this.forecast = forecast;
        this.practice = practice;
        this.driver = driver;
        this.td = td;
        this.car = car;
        this.trackWingSplit = track.getWingSplit() != null ? track.getWingSplit().intValue() : 0;
    }

    private void updateSZ(int[] sz,
                          Driver driver,
                          TechDirector td) {
        if ( driver != null && td == null ) {
            for ( int i = 0; i < sz.length; i++ ) {
                sz[i] = (int) driver.getSatisfactionZone();
            }
        } else if ( driver != null && td != null ) {
            sz[0] = driver.getSatisfactionZone() + td.getWingsSZ();
            sz[1] = driver.getSatisfactionZone() + td.getWingsSZ();
            sz[2] = driver.getSatisfactionZone() + td.getEngineSZ();
            sz[3] = driver.getSatisfactionZone() + td.getBrakesSZ();
            sz[4] = driver.getSatisfactionZone() + td.getGearboxSZ();
            sz[5] = driver.getSatisfactionZone() + td.getSuspensionSZ();
        } else {
            for ( int i = 0; i < sz.length; i++ ) {
                sz[i] = -1;
            }
        }
        int dsz = driver != null ? driver.getSatisfactionZone() : 0;
        ps[0] = new PartSetup( this.td != null, dsz, (sz[0] + sz[1]) / 2 );
        ps[1] = new PartSetup( this.td != null, dsz, (sz[0] + sz[1]) / 2 );
        for ( int i = 2; i < ps.length; i++ ) {
            ps[i] = new PartSetup( this.td != null, dsz, sz[i] );
        }
        this.dirty = false;
    }

    public int[] getEffectiveSZ() {
        if ( dirty ) {
            updateItself();
        }
        return sz;
    }

    public int[] getInitialSetup() {
        if ( dirty ) {
            updateItself();
        }
        return init;
    }

    private void updateItself() {
        updateSZ( sz, getDriver(), getTd() );
        updateInitialSetup();
    }


    private void updateInitialSetup() {
        if( track.getSetupWings() != null && forecast[0] != null && driver != null ) {
            init[0] = SetupCalculator.calculateWings( track, forecast[0].getTempMax(), forecast[0].getWeather(), car, driver );
            init[1] = init[0];
            init[2] = SetupCalculator.calculateEngine( track, forecast[0].getTempMax(), forecast[0].getWeather(), car, driver );
            init[3] = SetupCalculator.calculateBrakes( track, forecast[0].getTempMax(), forecast[0].getWeather(), car, driver );
            init[4] = SetupCalculator.calculateGear( track, forecast[0].getTempMax(), forecast[0].getWeather(), car, driver );
            init[5] = SetupCalculator.calculateSuspension( track, forecast[0].getTempMax(), forecast[0].getWeather(), car, driver );
        } else {
            for( int i = 0; i < init.length; i++ ) {
                init[i] = 500;
            }
        }
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
        lastLap = 0;
        getEffectiveSZ(); // to initialize PSs
        for ( int i = 0; i < ps.length; i++ ) {
            if ( ps[i] != null ) {
                ps[i].reset();
            }
        }
        calculateWingSplit();
    }

    public void calculateWingSplit() {
        List<List<Lap>> lapGroups = practice.getWingSplitLapGroups();
        for ( List<Lap> laps : lapGroups ) {
            if ( laps.size() > 1 ) {
                if ( wingSplit.isEmpty() ) {
                    // create a new baseline
                    createBaseline( laps );
                } else {
                    // has to find an existing baseline
                    Lap baseline = null;
                    int ws = 0;
                    for ( Lap lap : laps ) {
                        ws = (lap.getSettings().getFrontWing() - lap.getSettings().getRearWing()) / 2;
                        if ( wingSplit.containsKey( ws ) ) {
                            baseline = lap;
                            break;
                        }
                    }
                    if ( baseline != null ) {
                        // if there is a baseline, we can join both sets
                        int offset = wingSplit.get( ws );
                        for ( Lap lap : laps ) {
                            if ( lap != baseline ) {
                                int nws = (lap.getSettings().getFrontWing() - lap.getSettings().getRearWing()) / 2;
                                int ntime = lap.getNetTime() - baseline.getNetTime() + offset;
                                wingSplit.put( nws, ntime );
                            }
                        }
                    } else {
                        // otherwise, we will take the larger set
                        if ( laps.size() > wingSplit.size() ) {
                            wingSplit.clear();
                            createBaseline( laps );
                        }
                    }
                }
            }
        }

        if ( !wingSplit.isEmpty() ) {
            boolean found = false;
            if ( wingSplit.size() == 3 ) {
                // we can try to interpolate
                int[] ws = new int[3];
                int[] to = new int[3];
                int i = 0;
                for ( Map.Entry<Integer, Integer> e : wingSplit.entrySet() ) {
                    ws[i] = e.getKey();
                    to[i] = e.getValue();
                    i++;
                }
                int result = WingSplitInterpolator.interpolate( ws, to );
                if ( Math.abs( result ) < Math.abs( 2.5 * (trackWingSplit != 0 ? trackWingSplit : 70) ) ) {
                    bestWingSplit = result;
                    found = true;
                }
            }
            if ( !found ) {
                Map.Entry<Integer, Integer> best = wingSplit.entrySet().iterator().next();
                for ( Map.Entry<Integer, Integer> e : wingSplit.entrySet() ) {
                    if ( e.getValue() < best.getValue() ) {
                        best = e;
                    }
                }
                bestWingSplit = best.getKey();
            }
            adjustWingSplit();
        }
    }

    private void adjustWingSplit() {
        int[] wings = calculateWings( ps[0].getRawIdealAdjustment(),
                                      ps[1].getRawIdealAdjustment(),
                                      bestWingSplit );
        ps[0].setOffset( wings[0] - ps[0].getRawIdealAdjustment() );
        ps[1].setOffset( wings[1] - ps[1].getRawIdealAdjustment() );
    }

    private void createBaseline(List<Lap> laps) {
        Lap baseline = laps.get( 0 );
        int ws = (baseline.getSettings().getFrontWing() - baseline.getSettings().getRearWing()) / 2;
        int baseTime = baseline.getNetTime();
        wingSplit.put( ws, 0 );
        for ( int i = 1; i < laps.size(); i++ ) {
            Lap delta = laps.get( i );
            int nws = (delta.getSettings().getFrontWing() - delta.getSettings().getRearWing()) / 2;
            int ntime = delta.getNetTime() - baseTime;
            wingSplit.put( nws, ntime );
        }
    }

    public void update() {
        if ( dirty ) {
            updateSZ( sz, getDriver(), getTd() );
            updateInitialSetup();
        }
        while ( lastLap < practice.getLaps().size() ) {
            Lap lap = practice.getLaps().get( lastLap );
            Map<Part, Comment> comments = lap.getParsedComments();
            ps[0].addHint( (lap.getSettings().getFrontWing() + lap.getSettings().getRearWing()) / 2,
                           comments.containsKey( Part.WNG ) ? comments.get( Part.WNG ) : new Comment( Part.WNG, Satisfaction.OK ) );
            ps[1].addHint( (lap.getSettings().getFrontWing() + lap.getSettings().getRearWing()) / 2,
                           comments.containsKey( Part.WNG ) ? comments.get( Part.WNG ) : new Comment( Part.WNG, Satisfaction.OK ) );
            ps[2].addHint( lap.getSettings().getEngine(),
                           comments.containsKey( Part.ENG ) ? comments.get( Part.ENG ) : new Comment( Part.ENG, Satisfaction.OK ) );
            ps[3].addHint( lap.getSettings().getBrakes(),
                           comments.containsKey( Part.BRA ) ? comments.get( Part.BRA ) : new Comment( Part.BRA, Satisfaction.OK ) );
            ps[4].addHint( lap.getSettings().getGear(),
                           comments.containsKey( Part.GEA ) ? comments.get( Part.GEA ) : new Comment( Part.GEA, Satisfaction.OK ) );
            ps[5].addHint( lap.getSettings().getSuspension(),
                           comments.containsKey( Part.SUS ) ? comments.get( Part.SUS ) : new Comment( Part.SUS, Satisfaction.OK ) );
            lastLap++;
        }
        adjustWingSplit();
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.dirty = true;
        this.driver = driver;
    }

    public TechDirector getTd() {
        return td;
    }

    public void setTd(TechDirector td) {
        this.dirty = true;
        this.td = td;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.dirty = true;
        this.car = car;
    }

    public Forecast[] getForecast() {
        return forecast;
    }

    public void setForecast(Forecast[] forecast) {
        this.dirty = true;
        this.forecast = forecast;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.dirty = true;
        this.track = track;
        this.trackWingSplit = track.getWingSplit() != null ? track.getWingSplit().intValue() : 0;
    }

    public PartSetup[] getPartSetup() {
        return ps;
    }

    public static class PartSetup {
        // interval for the ideal adjustment
        private int[]                 ia    = new int[2];
        // hints and comments
        private Map<Integer, Comment> hints = new HashMap<Integer, Comment>();
        // the base SZ for the given part (driver only) 
        private int                   sz;
        // the effective SZ for the given part (takes DT into account)
        private int                   szDT;
        // half SZ, just as a cache
        private double                hsz;
        // the offset for the ideal adjustment (used for wings with wing split != 0)
        private int                   offset;
        // true if it should use the method to calculate from PRO (i.e. using DT)
        private boolean               proCalc;

        public PartSetup(boolean proCalc,
                         int sz,
                         int szDT) {
            this.proCalc = proCalc;
            this.sz = sz;
            this.szDT = szDT;
            this.hsz = proCalc ? ((double) szDT - 1) * 0.5 : ((double) sz - 1) * 0.5;
            ia[0] = 0;
            ia[1] = 999;
        }

        public void reset() {
            hints.clear();
            ia[0] = 0;
            ia[1] = 999;
            offset = 0;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getSz() {
            return sz;
        }

        public int[] getIARange() {
            return ia;
        }

        public boolean foundIdealAdjustment() {
            return ia[1] - ia[0] <= 1;
        }

        public int getRawIdealAdjustment() {
            return (ia[0] + ia[1]) / 2;
        }

        public int getIdealAdjustment() {
            return (ia[0] + ia[1]) / 2 + offset;
        }

        public int getError() {
            return (int) Math.ceil( (ia[1] - ia[0]) / 2.0 );
        }

        public int getNextSetupValue() {
            double lia = (ia[0] + ia[1]) / 2.0;
            double guess = lia > 200 ? lia - hsz : lia + hsz;
            return hints.containsKey( (int) Math.ceil( guess ) ) ? (int) Math.floor( guess ) : (int) Math.ceil( guess );
        }

        public void addHint(Integer point,
                            Comment comment) {
            hints.put( point, comment );
            Satisfaction sat = comment.getSat();
            if ( !proCalc ) {
                double lk = sat.getFactor() - 0.5;
                int lb = sat.getFactor() == -3 ? 0 : (lk < 0 ? (int) Math.ceil( point.doubleValue() + lk * sz + 0.5 ) : (int) Math.floor( point.doubleValue() + lk * sz + 0.5 ));
                double uk = sat.getFactor() + 0.5;
                int ub = sat.getFactor() == +3 ? 999 : (uk < 0 ? (int) Math.ceil( point.doubleValue() + uk * sz - 0.5 ) : (int) Math.floor( point.doubleValue() + uk * sz - 0.5 ));
                int[] nia = new int[]{lb, ub};
                ia = intersect( ia, nia );
            } else {
                int lb = 0, ub = 0;
                switch ( sat.getFactor() ) {
                    case -3 :
                        lb = 0;
                        ub = (int) Math.ceil( point.doubleValue() - 0.5 * szDT - sz * 3 - 0.5 );
                        break;
                    case -2 :
                        lb = (int) Math.ceil( point.doubleValue() - 0.5 * szDT - 3 * sz + 0.5 );
                        ub = (int) Math.ceil( point.doubleValue() - 0.5 * szDT - sz - 0.5 );
                        break;
                    case -1 :
                        lb = (int) Math.ceil( point.doubleValue() - 0.5 * szDT - sz + 0.5 );
                        ub = (int) Math.ceil( point.doubleValue() - 0.5 * szDT - 0.5 );
                        break;
                    case 0 :
                        lb = (int) Math.ceil( point.doubleValue() - 0.5 * szDT + 0.5 );
                        ub = (int) Math.floor( point.doubleValue() + 0.5 * szDT - 0.5 );
                        break;
                    case 1 :
                        lb = (int) Math.floor( point.doubleValue() + 0.5 * szDT + 0.5 );
                        ub = (int) Math.floor( point.doubleValue() + 0.5 * szDT + sz - 0.5 );
                        break;
                    case 2 :
                        lb = (int) Math.floor( point.doubleValue() + 0.5 * szDT + sz + 0.5 );
                        ub = (int) Math.floor( point.doubleValue() + 0.5 * szDT + 3 * sz - 0.5 );
                        break;
                    case 3 :
                        lb = (int) Math.floor( point.doubleValue() + 0.5 * szDT + 3 * sz + 0.5 );
                        ub = 999;
                        break;
                }
                int[] nia = new int[]{lb, ub};
                ia = intersect( ia, nia );
            }
            //System.out.println(String.format("%d. %3d (%+3d [%3d, %3d]) -> %s", hints.size(), point, getError(), ia[0], ia[1], comment.toString()) );
        }

        private int[] intersect(int[] ia,
                                int[] nia) {
            ia[0] = Math.max( ia[0], nia[0] );
            ia[1] = Math.min( ia[1], nia[1] );
            return ia;
        }

        public Map<Integer, Comment> getHints() {
            return hints;
        }
    }

    public CarSettings getWingSplitPracticeLap() {
        List<Lap> laps = null;
        for ( List<Lap> group : practice.getWingSplitLapGroups() ) {
            if ( laps == null || group.size() > laps.size() ) {
                laps = group;
            } else if ( group.size() == laps.size() && group.get( 0 ).getNetTime() < laps.get( 0 ).getNetTime() ) {
                laps = group;
            }
        }
        int referenceWingSplit = trackWingSplit != 0 ? trackWingSplit : 70;
        if ( laps != null ) {
            boolean found = false;
            CarSettings settings = null;
            int count = 0;
            while( ! found && count < 5 ) {
                settings = calculateCarSettingsForWingSplit(laps, referenceWingSplit);
                count++;
                found = false;
                // checks if these settings were already used
                for( Lap lap : practice.getLaps() ) {
                    if( lap.getSettings().isSameSetting( settings ) ) {
                        found = false;
                        referenceWingSplit *= -1.5;
                        settings = null;
                        break;
                    }
                }
            }
            return settings;
        } else {
            int[] initialSetup = getInitialSetup();
            int[] wings = calculateWings( initialSetup[0],
                                          initialSetup[1],
                                          trackWingSplit );
            return new CarSettings( null, wings[0], wings[1], initialSetup[2], initialSetup[3], initialSetup[4], initialSetup[5], null );
        }
    }

    private CarSettings calculateCarSettingsForWingSplit(List<Lap> laps, int referenceWingSplit) {
        int[] wings = null;
        Lap baseLap = null;
        if ( laps.size() == 1 ) {
            baseLap = laps.get( 0 );
            wings = calculateWings( baseLap.getSettings().getFrontWing(),
                                    baseLap.getSettings().getRearWing(),
                                    referenceWingSplit );
        } else if ( laps.size() == 2 ) {
            baseLap = laps.get( 0 );
            wings = calculateWings( baseLap.getSettings().getFrontWing(),
                                    baseLap.getSettings().getRearWing(),
                                    referenceWingSplit * 2 );
            if ( laps.get( 1 ).getSettings().getFrontWing() == wings[0] &&
                 laps.get( 1 ).getSettings().getRearWing() == wings[1] ) {
                baseLap = laps.get( 1 );
                wings = calculateWings( baseLap.getSettings().getFrontWing(),
                                        baseLap.getSettings().getRearWing(),
                                        referenceWingSplit * 2 );
            }
        } else if ( laps.size() == 3 ) {
            baseLap = laps.get( 0 );
            wings = calculateWings( baseLap.getSettings().getFrontWing(),
                                    baseLap.getSettings().getRearWing(),
                                    bestWingSplit );
        }
        CarSettings clone = baseLap.getSettings().clone();
        clone.setFrontWing( wings[0] );
        clone.setRearWing( wings[1] );
        return clone;
    }

    public int[] calculateWings(int frontWing,
                                 int rearWing,
                                 int ws) {
        int[] wings = new int[2];
        wings[0] = frontWing + ws;
        wings[1] = rearWing - ws;
        if ( wings[0] < 0 ) {
            wings[1] += wings[0];
            wings[0] = 0;
        } else if ( wings[1] > 999 ) {
            wings[0] += wings[1] - 999;
            wings[1] = 999;
        } else if ( wings[1] < 0 ) {
            wings[0] += wings[1];
            wings[1] = 0;
        } else if ( wings[0] > 999 ) {
            wings[1] += wings[0] - 999;
            wings[0] = 999;
        }
        return wings;
    }

    public Map<Integer, Integer> getWingSplit() {
        return wingSplit;
    }

    public int getBestWingSplit() {
        return bestWingSplit;
    }
}
