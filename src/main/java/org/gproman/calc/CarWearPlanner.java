package org.gproman.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.gproman.calc.CarWearPlanner.StepAction.Action;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.CarPartCost;
import org.gproman.model.car.CarWearDetail;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverWearWeight;
import org.gproman.model.race.Race;
import org.gproman.model.track.Track;
import org.gproman.model.track.WearCoefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarWearPlanner {
    private static Logger    logger = LoggerFactory.getLogger( CarWearPlanner.class );

    private List<WearStep>   steps  = new ArrayList<WearStep>( 17 );
    private DriverWearWeight dww;
    private WearCoefs        wpc;
    private Track            testTrack;

    public CarWearPlanner(DriverWearWeight dww,
                          WearCoefs wpc,
                          Track testTrack) {
        this.dww = dww;
        this.wpc = wpc;
        this.testTrack = testTrack;
    }

    public CarWearPlanner addRace(Race race) {
        WearStep step = new WearStep( race,
                                      BigDecimal.ZERO,
                                      new CarWearCalculator( dww,
                                                             wpc,
                                                             race.getTrack() ),
                                      new CarWearCalculator( dww,
                                                             wpc,
                                                             testTrack ),
                                      new CarWearCalculator( dww,
                                                             wpc,
                                                             testTrack ) );
        this.steps.add( step );
        return this;
    }

    public PPlan marshall() {
        if ( steps.isEmpty() ) {
            return null;
        }
        List<PStep> pss = new ArrayList<CarWearPlanner.PStep>();
        for ( WearStep ws : steps ) {
            pss.add( ws.marshall() );
        }
        return new PPlan( steps.get( 0 ).getRace().getSeasonNumber(), pss );
    }

    public void unmarshall(PPlan pp) {
        WearStep wearStep = steps.get( 0 );
        if ( steps.isEmpty() || wearStep.getRace().getSeasonNumber().intValue() != pp.season ) {
            logger.error( "Trying to deserialize the plan for the wrong season." );
            throw new IllegalArgumentException( "Impossible to load plan." );
        }
        int index = 0;
        for ( PStep ps : pp.steps ) {
            if ( index < steps.size() && steps.get( index ).getRace().getNumber() == ps.race ) {
                steps.get( index ).unmarshall( ps );
                index++;
            }
        }
        if ( wearStep.getStartCar().isUpdateDone() ) {
            CarPart[] parts = wearStep.getStartCar().getParts();
            for ( int i = 0; i < parts.length; i++ ) {
                wearStep.setAction( i, new StepAction( StepAction.Action.KEEP, parts[i].getLevel(), i, parts[i] ) );
            }
        }
    }

    public CarWearPlanner setDriver(Driver driver) {
        for ( WearStep step : steps ) {
            step.updateDriver( driver );
        }
        return this;
    }

    public List<Car> projectWear(int index) {
        List<Car> result = new ArrayList<Car>();
        Driver driver = steps.get(0).getRace().getDriverStart();
        
        Car current = steps.get( index ).getStartCar();
        for ( int i = index; i < steps.size(); i++ ) {
            WearStep step = steps.get( i );
            step.updateCar( current );
            step.setDriver(driver);
            current = step.calculateWear();
            
            CarWearDetail[] details = step.getCustomCarWearDetail();
            for (int j = 0; j < details.length; j++) {
            	CarWearDetail detail = details[j];
            	if(detail != null){
            		current.getParts()[detail.getPartIndex()].setWear(detail.getWearTotal());
            	}
			}
            result.add( current );
        }
        return result;
    }

    public List<WearStep> getSteps() {
        return steps;
    }

    public WearStep getStep(int index) {
        return steps.get( index );
    }

    public BigDecimal getRisk(int index) {
        return steps.get( index ).getRisk();
    }

    public CarWearPlanner setRisk(int index,
                                  BigDecimal risk) {
        steps.get( index ).setRisk( risk );
        return this;
    }

    public CarWearPlanner setCar(int index,
                                 Car car) {
        steps.get( index ).updateCar( car );
        return this;
    }

    public Car getStartCar(int index) {
        return steps.get( index ).getStartCar();
    }

    public Car getBaseCar(int index) {
        return steps.get( index ).getBaseCar();
    }

    public StepAction getAction(int index,
                                int partIndex) {
        return steps.get( index ).getAction( partIndex );
    }

    public CarWearPlanner setAction(int index,
                                    StepAction action) {
        WearStep previous = steps.get( index );
        previous.setAction( action.getPartIndex(), action );
        CarPart previousPart = previous.getFinishCar().getParts()[action.getPartIndex()];

        // changing an action at a given step will reset all the actions on subsequent steps
        for ( int i = index + 1; i < steps.size(); i++ ) {
            previousPart = steps.get( i ).updateStartCarPart( action.getPartIndex(), previousPart, true );
        }
        return this;
    }

    public List<StepAction> getActionsFor(int index,
                                          int part) {
        return steps.get( index ).getActionsForPart( part );
    }

    public long getTotalCost() {
        long result = 0;
        for ( WearStep step : steps ) {
            result += step.getCost();
        }
        return result;
    }

    public long getCost(int index) {
        return steps.get( index ).getCost();
    }

    public long getCostForPart(int partIndex) {
        long result = 0;
        for ( WearStep step : steps ) {
            result += step.getCostForPart( partIndex );
        }
        return result;
    }

    public void reset() {
        for ( WearStep step : steps ) {
            step.reset();
        }
        projectWear( 0 );
    }

    public static class WearStep {
        private Race              race;
        private BigDecimal        risk;
        private CarWearCalculator calculator;
        
        private CarWearCalculator testCalcBefore;
        private CarWearCalculator testCalcAfter;
        private int               testLapsBefore;
        private int               testLapsAfter;
        private Driver 			  driver;
        
        private Car               startCar;  // original unmodified car
        private Car               baseCar;   // car modified by actions and used as base to calculate wear
        private StepAction[]      action;
        private Car               finishCar;
        
        private CarWearDetail[]     carWearDetail;
        private CarWearDetail[]     customCarWearDetail;

        public WearStep(Race race,
                        BigDecimal risk,
                        CarWearCalculator calculator,
                        CarWearCalculator testCalcBefore,
                        CarWearCalculator testCalcAfter ) {
            this.race = race;
            this.risk = risk;
            this.calculator = calculator;
            this.testCalcBefore = testCalcBefore;
            this.testCalcAfter = testCalcAfter;
            this.action = new StepAction[Car.PARTS_COUNT];
        }

        public void reset() {
            this.risk = BigDecimal.ZERO;
            this.testLapsAfter = 0;
            this.testLapsBefore = 0;
            
            for ( int i = 0; i < action.length; i++ ) {
                action[i] = null;
            }
//            
            this.carWearDetail = null;
            this.customCarWearDetail = null;
        }

        public PStep marshall() {
            List<PAction> actions = new ArrayList<PAction>();
            for ( StepAction sa : action ) {
                actions.add( sa.marshall() );
            }
            return new PStep( race.getNumber(), risk.intValue(), testLapsBefore, testLapsAfter, actions, customCarWearDetail );
        }

        public void unmarshall(PStep ps) {
            if ( this.race.getNumber() != ps.race ) {
                logger.error( "Error unmarshalling plan. Trying to deserialize it to the wrong race." );
                throw new IllegalArgumentException( "Impossible to deserialize." );
            }
            this.risk = new BigDecimal( ps.risk );
            this.testLapsBefore = ps.testLapsBefore;
            this.testLapsAfter = ps.testLapsAfter;
            this.customCarWearDetail = ps.customDetail;
            
            for ( int i = 0; i < action.length; i++ ) {
                PAction pa = ps.actions.get( i );
                if ( pa != null ) {
                    if ( action[i] == null ) {
                        action[i] = new StepAction( Action.valueOf( pa.action ),
                                                    pa.level,
                                                    i,
                                                    this.startCar.getParts()[i] );
                    } else {
                        action[i].unmarshal( pa, i, this.startCar.getParts()[i] );
                    }
                } else {
                    action[i] = new StepAction( Action.KEEP,
                                                this.startCar.getParts()[i].getLevel(),
                                                i,
                                                this.startCar.getParts()[i] );
                }
            }
        }

        /**
         * Returns the list of possible actions given the current start car part
         * @param partIndex
         * @return
         */
        public List<StepAction> getActionsForPart(int partIndex) {
            CarPart part = startCar.getParts()[partIndex];
            List<StepAction> result = new ArrayList<CarWearPlanner.StepAction>();
            result.add( new StepAction( StepAction.Action.KEEP, part.getLevel(), partIndex, part ) );
            for ( int i = Math.min( part.getLevel() + 1, CarPartCost.getMaxLevel() ); i > 0; i-- ) {
                result.add( new StepAction( StepAction.Action.REPLACE, i, partIndex, part ) );
            }
            for ( int i = part.getLevel() - 1; i > 0; i-- ) {
                result.add( new StepAction( StepAction.Action.DOWNGRADE, i, partIndex, part ) );
            }
            return result;
        }

        /**
         * Updates the level and wear of the given start car part
         * optionally reseting the action to keep
         *  
         * @param partIndex the index of the part
         * @param newStart the state of the new start part
         * @param resetAction
         * @return the updated corresponding finish car part
         */
        public CarPart updateStartCarPart(int partIndex,
                                          CarPart newStart,
                                          boolean resetAction) {
            CarPart part = this.startCar.getParts()[partIndex];
            CarPart base = this.baseCar.getParts()[partIndex];

            part.setLevel( newStart.getLevel() );
            part.setWear( newStart.getWear() );

            if ( resetAction ) {
                action[partIndex] = new StepAction( StepAction.Action.KEEP, part.getLevel(), partIndex, part );
                base.setLevel( part.getLevel() );
                base.setWear( part.getWear() );
            } else {
                base = action[partIndex].getAdjustedPart();
                this.baseCar.getParts()[partIndex] = base;
            }
            return this.calculateWearForPart( partIndex );
        }

        /**
         * Updates the whole starting car
         * @param startCar
         */
        public void updateCar(Car startCar) {
            this.startCar = startCar;
            this.baseCar = new Car();
            for ( int i = 0; i < action.length; i++ ) {
                if ( action[i] != null ) {
                    action[i].setOriginalPart( this.startCar.getParts()[i] );
                    this.baseCar.getParts()[i] = action[i].getAdjustedPart();
                } else {
                    action[i] = new StepAction( StepAction.Action.KEEP, startCar.getParts()[i].getLevel(), i, startCar.getParts()[i] );
                }
                this.baseCar.getParts()[i] = action[i].getAdjustedPart();
            }
            this.baseCar.setPHA( this.startCar.getPHA() ); // this is not really correct but value is not being used at the moment
            this.calculator.updateCar( this.baseCar );
        }

        /**
         * Calculates the wear for a given part and returns the part
         * @param partIndex
         * @return
         */
        public CarPart calculateWearForPart(int partIndex) {
            CarPart wear = this.calculator.predictWear( this.risk, partIndex );
            
            CarPart finishPart = finishCar.getParts()[partIndex];
            finishPart.setLevel( wear.getLevel() );
            finishPart.setWear( baseCar.getParts()[partIndex].getWear() + wear.getWear() );
            return finishPart;
        }

        /**
         * Calculates the wear for the whole car and returns it
         * @return
         */
        public Car calculateWear() {
            CarPart[] wear = this.calculator.predictWear( this.risk );
        	
            this.carWearDetail = new CarWearDetail[wear.length];
            if(this.customCarWearDetail == null){
            	this.customCarWearDetail = new CarWearDetail[wear.length];	
            }
            
            this.testCalcBefore.updateCar(baseCar);
            this.testCalcAfter.updateCar(baseCar);
            this.testCalcBefore.updateDriver(getDriver());
            this.testCalcAfter.updateDriver(getDriver());
            
            
        	CarPart[] wearBeforeTest = this.testCalcBefore.predictWearForTestLaps(BigDecimal.ZERO, this.getTestLapsBefore());
        	CarPart[] wearAfterTest = this.testCalcAfter.predictWearForTestLaps(BigDecimal.ZERO, this.getTestLapsAfter());
        	
            finishCar = baseCar.clone();
            CarPart[] parts = finishCar.getParts();
            for ( int i = 0; i < wear.length; i++ ) {
            	CarWearDetail detail = new CarWearDetail();
            	this.carWearDetail[i] = detail;
            	
            	CarWearDetail customDetail = customCarWearDetail[i];
            	
            	detail.setPartIndex(i);

				double partWearBeforeTest = wearBeforeTest[i].getWear();
				double partWearAfterTest = wearAfterTest[i].getWear();
								
				if(customDetail != null){
					if(customDetail.getWearTestBefore() != detail.getWearTestBefore()){
						partWearBeforeTest = customDetail.getWearTestBefore();
					}
					
					if(customDetail.getWearTestAfter() != detail.getWearTestAfter()){
						partWearAfterTest = customDetail.getWearTestAfter();
					}
				}
            	
				switch (action[i].action) {
				case DOWNGRADE:
					
					double coef = 1 - 0.15 * (parts[i].getLevel() - action[i].level);

					double before = parts[i].getWear() + wearBeforeTest[i].getWear();
					detail.setWearBeforeRace((before * coef) + wearAfterTest[i].getWear());
					
					before = parts[i].getWear() + partWearBeforeTest;
					if(customDetail != null){
						customDetail.setWearBeforeRace((before * coef) + partWearAfterTest);
					}
					
					before = parts[i].getWear() +  wear[i].getWear() + partWearBeforeTest;
					parts[i].setWear((before * coef) + partWearAfterTest);

					break;

				case KEEP:
					detail.setWearBeforeRace( parts[i].getWear() + wearBeforeTest[i].getWear() + wearAfterTest[i].getWear());
					if(customDetail != null){
						customDetail.setWearBeforeRace( parts[i].getWear() + partWearBeforeTest + partWearAfterTest);
					}
					
					parts[i].setWear( parts[i].getWear() + wear[i].getWear() + partWearBeforeTest + partWearAfterTest);
					
					break;
					
				default:
					detail.setWearBeforeRace( parts[i].getWear() + wearAfterTest[i].getWear());
					if(customDetail != null){
						customDetail.setWearBeforeRace( parts[i].getWear() + partWearAfterTest);
					}
					
					parts[i].setWear( parts[i].getWear() + wear[i].getWear() + partWearAfterTest);
					
					break;
				}
				
				detail.setPartLevel(baseCar.getParts()[i].getLevel());
				
				detail.setWearBase(baseCar.getParts()[i].getWear());
				detail.setWearTestBefore(wearBeforeTest[i].getWear());
				detail.setWearTestAfter(wearAfterTest[i].getWear());
				
				detail.setWearRace(wear[i].getWear());
            	
				if(customDetail != null){
					CarWearDetail newCustomDetail = detail.clone();
					newCustomDetail.setWearTestBefore(customDetail.getWearTestBefore());
					newCustomDetail.setWearTestAfter(customDetail.getWearTestAfter());
					newCustomDetail.setWearRace(customDetail.getWearRace());
					newCustomDetail.setWearBeforeRace(customDetail.getWearBeforeRace());
					
					customCarWearDetail[i] = newCustomDetail;
				}
            	
            }
            return finishCar;
        }

        /**
         * Returns the finish car after applying wear
         * @return
         */
        public Car getFinishCar() {
            return finishCar;
        }

        /** 
         * Gets the starting car 
         */
        public Car getStartCar() {
            return startCar;
        }

        /**
         * Gets the base car after applying any part actions
         * @return
         */
        public Car getBaseCar() {
            return baseCar;
        }

        public void updateDriver(Driver driver) {
            this.calculator.updateDriver( driver );
        }

        public Track getTrack() {
            return race.getTrack();
        }

        public Race getRace() {
            return race;
        }

        public BigDecimal getRisk() {
            return risk;
        }

        public void setRisk(BigDecimal risk) {
            this.risk = risk;
        }
        public int getTestLapsBefore() {
            return testLapsBefore;
        }

        public void setTestLapsBefore(int testLapsBefore) {
            this.testLapsBefore = testLapsBefore;
        }

        public int getTestLapsAfter() {
            return testLapsAfter;
        }

        public void setTestLapsAfter(int testLapsAfter) {
            this.testLapsAfter = testLapsAfter;
        }

        public Driver getDriver() {
			return driver;
		}

		public void setDriver(Driver driver) {
			this.driver = driver;
		}

		public CarWearCalculator getCalculator() {
            return calculator;
        }

        public void setCalculator(CarWearCalculator calculator) {
            this.calculator = calculator;
        }
        
        public void setAction(int partIndex,
                              StepAction action) {
            this.action[partIndex] = action;
            updateStartCarPart( partIndex, this.startCar.getParts()[partIndex], false );
        }

        public StepAction getAction(int partIndex) {
            return this.action[partIndex];
        }

        public StepAction[] getActions() {
            return this.action;
        }

        public long getCost() {
            long result = 0;
            for ( int i = 0; i < action.length; i++ ) {
                result += action[i] != null ? action[i].getCost() : 0;
            }
            return result;
        }

        public long getCostForPart(int partIndex) {
            return action[partIndex] != null ? action[partIndex].getCost() : 0;
        }

		public CarWearDetail[] getCarWearDetail() {
			return carWearDetail;
		}

		public CarWearDetail[] getCustomCarWearDetail() {
			return customCarWearDetail;
		}

		public void setCustomCarWearDetail(CarWearDetail[] customCarWearDetail) {
			this.customCarWearDetail = customCarWearDetail;
		}
		
		public void addCustomCarWearDetail(CarWearDetail customDetail, int index){
			this.customCarWearDetail[index] = customDetail;
		}

    }

    public static class StepAction {
        public static enum Action {
            KEEP, REPLACE, DOWNGRADE;
        }

        private Action  action;
        private int     level;
        private int     partIndex;
        private CarPart originalPart;

        public StepAction(Action action,
                          int level,
                          int partIndex,
                          CarPart originalPart) {
            this.action = action;
            this.level = level;
            this.partIndex = partIndex;
            this.originalPart = originalPart;
        }

        public PAction marshall() {
            return new PAction( action.toString(), level );
        }

        public void unmarshal(PAction action,
                              int partIndex,
                              CarPart originalPart) {
            this.action = Action.valueOf( action.action );
            this.level = action.level;
            this.partIndex = partIndex;
            this.originalPart = originalPart;
        }

        public Action getAction() {
            return action;
        }

        public int getLevel() {
            return level;
        }

        public int getPartIndex() {
            return partIndex;
        }

        public void setOriginalPart(CarPart part) {
            this.originalPart = part;
        }
        
        public CarPart getOriginalPart() {
			return originalPart;
		}

		public double getWear() {
            if ( this.originalPart != null ) {
                return getWear( originalPart );
            }
            return 0;
        }

        public double getWear(CarPart part) {
            switch ( action ) {
                case KEEP :
                    return part.getWear();
                case REPLACE :
                    return 0;
                case DOWNGRADE :
                	//adjust wear if >99%
                	double wear = part.getWear();
                	if(!(wear < 100d)){ //not include 99.9999999999.....
                		wear = 99d;
                	}
                    // downgrades reduce the wear in 15% per level
                    return wear * (1 - 0.15 * (part.getLevel() - level));
            }
            return 0;
        }

        public CarPart getAdjustedPart() {
            CarPart clone = originalPart.clone();
            clone.setLevel( level );
            clone.setWear( getWear() );
            return clone;
        }

        public long getCost() {
            return (action.equals( Action.REPLACE )) ? CarPartCost.getCost( partIndex, level ) : 0;
        }

        @Override
        public String toString() {
            return "StepAction [action=" + action + ", level=" + level + ", partIndex=" + partIndex + ", originalPart=" + originalPart + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((action == null) ? 0 : action.hashCode());
            result = prime * result + level;
            result = prime * result + ((originalPart == null) ? 0 : originalPart.hashCode());
            result = prime * result + partIndex;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            StepAction other = (StepAction) obj;
            if ( action != other.action ) return false;
            if ( level != other.level ) return false;
            if ( originalPart == null ) {
                if ( other.originalPart != null ) return false;
            } else if ( !originalPart.equals( other.originalPart ) ) return false;
            if ( partIndex != other.partIndex ) return false;
            return true;
        }

    }

    public static class PPlan {
        public int         season;
        public List<PStep> steps;

        public PPlan() {
        }

        public PPlan(int season,
                     List<PStep> steps) {
            this.season = season;
            this.steps = steps;
        }
    }

    public static class PStep {
        public int           race;
        public int           risk;
        public List<PAction> actions;
		public int testLapsBefore;
		public int testLapsAfter;
		public CarWearDetail[] customDetail;

        public PStep() {
        }

        public PStep(int race,
                     int risk,
                     int testLapsBefore,
                     int testLapsAfter,
                     List<PAction> actions,
                     CarWearDetail[] customDetail) {
            super();
            this.race = race;
            this.risk = risk;
			this.testLapsBefore = testLapsBefore;
			this.testLapsAfter = testLapsAfter;
			
            this.actions = actions;
            this.customDetail = customDetail;
        }
    }

    public static class PAction {
        public String action;
        public int    level;

        public PAction() {
        }

        public PAction(String action,
                       int level) {
            super();
            this.action = action;
            this.level = level;
        }
    }

}
