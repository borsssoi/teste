package org.gproman.model.race;

import org.gproman.model.PersistentEntity;

public class Forecast extends PersistentEntity {

    private Weather weather;
    private Integer tempMin;
    private Integer tempMax;
    private Integer humidityMin;
    private Integer humidityMax;
    private Integer rainMin;
    private Integer rainMax;
    
    public Forecast() {
    }
    
    public Forecast(Integer id) {
        super( id );
    }

    public Forecast(Weather weather,
                    Integer tempMin,
                    Integer tempMax,
                    Integer humidityMin,
                    Integer humidityMax,
                    Integer rainMin,
                    Integer rainMax) {
        super();
        this.weather = weather;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.rainMin = rainMin;
        this.rainMax = rainMax;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Integer getTempMin() {
        return tempMin;
    }

    public void setTempMin(Integer tempMin) {
        this.tempMin = tempMin;
    }

    public Integer getTempMax() {
        return tempMax;
    }

    public void setTempMax(Integer tempMax) {
        this.tempMax = tempMax;
    }

    public Integer getHumidityMin() {
        return humidityMin;
    }

    public void setHumidityMin(Integer humidityMin) {
        this.humidityMin = humidityMin;
    }

    public Integer getHumidityMax() {
        return humidityMax;
    }

    public void setHumidityMax(Integer humidityMax) {
        this.humidityMax = humidityMax;
    }

    public Integer getRainMin() {
        return rainMin;
    }

    public void setRainMin(Integer rainMin) {
        this.rainMin = rainMin;
    }

    public Integer getRainMax() {
        return rainMax;
    }

    public void setRainMax(Integer rainMax) {
        this.rainMax = rainMax;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((humidityMax == null) ? 0 : humidityMax.hashCode());
        result = prime * result + ((humidityMin == null) ? 0 : humidityMin.hashCode());
        result = prime * result + ((rainMax == null) ? 0 : rainMax.hashCode());
        result = prime * result + ((rainMin == null) ? 0 : rainMin.hashCode());
        result = prime * result + ((tempMax == null) ? 0 : tempMax.hashCode());
        result = prime * result + ((tempMin == null) ? 0 : tempMin.hashCode());
        result = prime * result + ((weather == null) ? 0 : weather.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Forecast other = (Forecast) obj;
        if ( humidityMax == null ) {
            if ( other.humidityMax != null ) return false;
        } else if ( !humidityMax.equals( other.humidityMax ) ) return false;
        if ( humidityMin == null ) {
            if ( other.humidityMin != null ) return false;
        } else if ( !humidityMin.equals( other.humidityMin ) ) return false;
        if ( rainMax == null ) {
            if ( other.rainMax != null ) return false;
        } else if ( !rainMax.equals( other.rainMax ) ) return false;
        if ( rainMin == null ) {
            if ( other.rainMin != null ) return false;
        } else if ( !rainMin.equals( other.rainMin ) ) return false;
        if ( tempMax == null ) {
            if ( other.tempMax != null ) return false;
        } else if ( !tempMax.equals( other.tempMax ) ) return false;
        if ( tempMin == null ) {
            if ( other.tempMin != null ) return false;
        } else if ( !tempMin.equals( other.tempMin ) ) return false;
        if ( weather != other.weather ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Forecast [weather=" + weather + 
                ", temp=" + tempMin + "C-" + tempMax + 
                "C, humidity=" + humidityMin + "%-" + humidityMax + 
                "%, rain=" + rainMin + "%-" + rainMax + "%]";
    }
    
    

    
}
