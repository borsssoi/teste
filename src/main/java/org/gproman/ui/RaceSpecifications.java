package org.gproman.ui;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.race.Forecast;
import org.gproman.model.race.Race;
import org.gproman.model.race.Race.Stint;
import org.gproman.model.race.Tyre;
import org.gproman.model.track.FuelConsumption;
import org.gproman.model.track.TyreWear;

public class RaceSpecifications {
	
	private Integer					minRiskClear	= 0;
	private Integer					maxRiskClear	= 100;
	private FuelConsumption	        fuelConsumption;
	private TyreWear				tyreWear;
	private Tyre					tyres;
	private Forecast				forecast;
	
	public RaceSpecifications(final Integer minRiskClear, final Integer maxRiskClear, final FuelConsumption fuelConsumption,
			final TyreWear tyreWear, final Tyre tyres, final Forecast forecast) {
		super();
		this.minRiskClear = minRiskClear;
		this.maxRiskClear = maxRiskClear;
		this.fuelConsumption = fuelConsumption;
		this.tyreWear = tyreWear;
		this.tyres = tyres;
		this.forecast = forecast;
	}
	
	public List<Stint> getValidStints(final Race race) {
		List<Stint> stints = new ArrayList<Stint>();
		boolean isValid = true;
		isValid &= (race.getRiskClear() >= getMinRiskClear()) && (race.getRiskClear() <= getMaxRiskClear());
		isValid &= (getFuelConsumption() == null) || (race.getTrack().getFuelConsumption() == getFuelConsumption());
		isValid &= (getTyreWear() == null) || (race.getTrack().getTyreWear() == getTyreWear());
		if (isValid) {
			for (Stint stint : race.getStints()) {
				if (isvalidStint(stint)) {
					stints.add(stint);
				}
			}
			return stints;
		} else {
			return null;
		}
	}
	
	private boolean isvalidStint(final Stint stint) {
		boolean isValid = (getTyres() == null) || (stint.getTyre().equals(getTyres()));
		isValid &= (stint.getAvgTemp() >= getForecast().getTempMin()) && (stint.getAvgTemp() <= getForecast().getTempMax());
		isValid &= (stint.getAvgHum() >= getForecast().getHumidityMin()) && (stint.getAvgHum() <= getForecast().getHumidityMax());
		return isValid;
	}
	
	/**
	 * @return the minriskClear
	 */
	public Integer getMinRiskClear() {
		return this.minRiskClear;
	}
	
	/**
	 * @param minRiskClear
	 *          the minriskClear to set
	 */
	public void setMinRiskClear(final Integer minRiskClear) {
		this.minRiskClear = minRiskClear;
	}
	
	/**
	 * @return the maxriskClear
	 */
	public Integer getMaxRiskClear() {
		return this.maxRiskClear;
	}
	
	/**
	 * @param maxRiskClear
	 *          the maxriskClear to set
	 */
	public void setMaxRiskClear(final Integer maxRiskClear) {
		this.maxRiskClear = maxRiskClear;
	}
	
	/**
	 * @return the tyres
	 */
	public Tyre getTyres() {
		return this.tyres;
	}
	
	/**
	 * @param tyres
	 *          the tyres to set
	 */
	public void setTyres(final Tyre tyres) {
		this.tyres = tyres;
	}
	
	/**
	 * @return the fuelConsumption
	 */
	public FuelConsumption getFuelConsumption() {
		return this.fuelConsumption;
	}
	
	/**
	 * @param fuelConsumption
	 *          the fuelConsumption to set
	 */
	public void setFuelConsumption(final FuelConsumption fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}
	
	/**
	 * @return the tyreWear
	 */
	public TyreWear getTyreWear() {
		return this.tyreWear;
	}
	
	/**
	 * @param tyreWear
	 *          the tyreWear to set
	 */
	public void setTyreWear(final TyreWear tyreWear) {
		this.tyreWear = tyreWear;
	}
	
	/**
	 * @return the forecast
	 */
	public Forecast getForecast() {
		return this.forecast;
	}
	
	/**
	 * @param forecast
	 *          the forecast to set
	 */
	public void setForecast(final Forecast forecast) {
		this.forecast = forecast;
	}
}
