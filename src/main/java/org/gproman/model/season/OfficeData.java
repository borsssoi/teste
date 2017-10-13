package org.gproman.model.season;

public class OfficeData {
    
    private int season;
    private int nextRace;
    
    
    public int getSeason() {
        return season;
    }
    public void setSeason(int season) {
        this.season = season;
    }
    public int getNextRace() {
        return nextRace;
    }
    public void setNextRace(int nextRace) {
        this.nextRace = nextRace;
    }

    @Override
    public String toString() {
        return "OfficeData [season=" + season + ", nextRace=" + nextRace + "]";
    }

}
