package org.gproman.model.car;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

public class CarWearDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4901382371575651827L;
	private double wearBase;
	private double wearTestBefore;
	private double wearTestAfter;
	
	private double wearBeforeRace;
	private double wearRace;
	private int partLevel;
	
	private int partIndex;
	
	public double getWearBase() {
		return wearBase;
	}
	public void setWearBase(double wearBase) {
		this.wearBase = wearBase;
	}

	public double getWearTestBefore() {
		return wearTestBefore;
	}
	public void setWearTestBefore(double wearTestBefore) {
		this.wearTestBefore = wearTestBefore;
	}
	public double getWearTestAfter() {
		return wearTestAfter;
	}
	public void setWearTestAfter(double wearTestAfter) {
		this.wearTestAfter = wearTestAfter;
	}
	public double getWearRace() {
		return wearRace;
	}
	public void setWearRace(double wearRace) {
		this.wearRace = wearRace;
	}
	public int getPartLevel() {
		return partLevel;
	}
	public void setPartLevel(int partLevel) {
		this.partLevel = partLevel;
	}
	
	public double getWearBeforeRace() {
		return wearBeforeRace;
	}
	public void setWearBeforeRace(double wearBeforeRace) {
		this.wearBeforeRace = wearBeforeRace;
	}
	public double getWearTotal(){
		return getWearBeforeRace() + getWearRace();
	}
	public int getPartIndex() {
		return partIndex;
	}
	public void setPartIndex(int partIndex) {
		this.partIndex = partIndex;
	}
	
	public CarWearDetail clone(){
		return SerializationUtils.clone(this);
		
	}
	
	
}
