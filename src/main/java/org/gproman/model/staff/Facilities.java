package org.gproman.model.staff;

import java.io.Serializable;

import org.gproman.model.PersistentEntity;

public class Facilities extends PersistentEntity implements Serializable {

    private static final long serialVersionUID = 8283918806203199072L;

    private Integer           overall;
    private Integer           experience;
    private Integer           motivation;
    private Integer           technical;
    private Integer           stress;
    private Integer           concentration;
    private Integer           efficiency;
    private Integer           windtunnel;
    private Integer           pitstop;
    private Integer           workshop;
    private Integer           design;
    private Integer           engineering;
    private Integer           alloy;
    private Integer           commercial;
    private Integer           mlt;
    private Integer           salary;
    private Integer           maintenance;

    public Facilities() {
        super();
    }

    public Facilities(Integer id) {
        super(id);
    }

    public Integer getOverall() {
        return overall;
    }

    public void setOverall(Integer overall) {
        this.overall = overall;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getMotivation() {
        return motivation;
    }

    public void setMotivation(Integer motivation) {
        this.motivation = motivation;
    }

    public Integer getTechnical() {
        return technical;
    }

    public void setTechnical(Integer technical) {
        this.technical = technical;
    }

    public Integer getStress() {
        return stress;
    }

    public void setStress(Integer stress) {
        this.stress = stress;
    }

    public Integer getConcentration() {
        return concentration;
    }

    public void setConcentration(Integer concentration) {
        this.concentration = concentration;
    }

    public Integer getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Integer efficiency) {
        this.efficiency = efficiency;
    }

    public Integer getWindtunnel() {
        return windtunnel;
    }

    public void setWindtunnel(Integer windtunnel) {
        this.windtunnel = windtunnel;
    }

    public Integer getPitstop() {
        return pitstop;
    }

    public void setPitstop(Integer pitstop) {
        this.pitstop = pitstop;
    }

    public Integer getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Integer workshop) {
        this.workshop = workshop;
    }

    public Integer getDesign() {
        return design;
    }

    public void setDesign(Integer design) {
        this.design = design;
    }

    public Integer getEngineering() {
        return engineering;
    }

    public void setEngineering(Integer engineering) {
        this.engineering = engineering;
    }

    public Integer getAlloy() {
        return alloy;
    }

    public void setAlloy(Integer alloy) {
        this.alloy = alloy;
    }

    public Integer getCommercial() {
        return commercial;
    }

    public void setCommercial(Integer commercial) {
        this.commercial = commercial;
    }

    public Integer getMlt() {
        return mlt;
    }

    public void setMlt(Integer mlt) {
        this.mlt = mlt;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Integer maintenance) {
        this.maintenance = maintenance;
    }

    @Override
    public String toString() {
        return "Facilities [overall=" + overall + ", experience=" + experience + ", motivation=" + motivation + ", technical=" + technical + ", stress=" + stress + ", concentration=" + concentration + ", efficiency=" + efficiency + ", windtunnel=" + windtunnel + ", pitstop=" + pitstop + ", workshop=" + workshop + ", design=" + design + ", engineering=" + engineering + ", alloy=" + alloy + ", commercial=" + commercial + ", mlt=" + mlt + ", salary=" + salary + ", maintenance=" + maintenance + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alloy == null) ? 0 : alloy.hashCode());
        result = prime * result + ((commercial == null) ? 0 : commercial.hashCode());
        result = prime * result + ((concentration == null) ? 0 : concentration.hashCode());
        result = prime * result + ((design == null) ? 0 : design.hashCode());
        result = prime * result + ((efficiency == null) ? 0 : efficiency.hashCode());
        result = prime * result + ((engineering == null) ? 0 : engineering.hashCode());
        result = prime * result + ((experience == null) ? 0 : experience.hashCode());
        result = prime * result + ((maintenance == null) ? 0 : maintenance.hashCode());
        result = prime * result + ((mlt == null) ? 0 : mlt.hashCode());
        result = prime * result + ((motivation == null) ? 0 : motivation.hashCode());
        result = prime * result + ((overall == null) ? 0 : overall.hashCode());
        result = prime * result + ((pitstop == null) ? 0 : pitstop.hashCode());
        result = prime * result + ((salary == null) ? 0 : salary.hashCode());
        result = prime * result + ((stress == null) ? 0 : stress.hashCode());
        result = prime * result + ((technical == null) ? 0 : technical.hashCode());
        result = prime * result + ((windtunnel == null) ? 0 : windtunnel.hashCode());
        result = prime * result + ((workshop == null) ? 0 : workshop.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Facilities other = (Facilities) obj;
        if (alloy == null) {
            if (other.alloy != null)
                return false;
        } else if (!alloy.equals(other.alloy))
            return false;
        if (commercial == null) {
            if (other.commercial != null)
                return false;
        } else if (!commercial.equals(other.commercial))
            return false;
        if (concentration == null) {
            if (other.concentration != null)
                return false;
        } else if (!concentration.equals(other.concentration))
            return false;
        if (design == null) {
            if (other.design != null)
                return false;
        } else if (!design.equals(other.design))
            return false;
        if (efficiency == null) {
            if (other.efficiency != null)
                return false;
        } else if (!efficiency.equals(other.efficiency))
            return false;
        if (engineering == null) {
            if (other.engineering != null)
                return false;
        } else if (!engineering.equals(other.engineering))
            return false;
        if (experience == null) {
            if (other.experience != null)
                return false;
        } else if (!experience.equals(other.experience))
            return false;
        if (maintenance == null) {
            if (other.maintenance != null)
                return false;
        } else if (!maintenance.equals(other.maintenance))
            return false;
        if (mlt == null) {
            if (other.mlt != null)
                return false;
        } else if (!mlt.equals(other.mlt))
            return false;
        if (motivation == null) {
            if (other.motivation != null)
                return false;
        } else if (!motivation.equals(other.motivation))
            return false;
        if (overall == null) {
            if (other.overall != null)
                return false;
        } else if (!overall.equals(other.overall))
            return false;
        if (pitstop == null) {
            if (other.pitstop != null)
                return false;
        } else if (!pitstop.equals(other.pitstop))
            return false;
        if (salary == null) {
            if (other.salary != null)
                return false;
        } else if (!salary.equals(other.salary))
            return false;
        if (stress == null) {
            if (other.stress != null)
                return false;
        } else if (!stress.equals(other.stress))
            return false;
        if (technical == null) {
            if (other.technical != null)
                return false;
        } else if (!technical.equals(other.technical))
            return false;
        if (windtunnel == null) {
            if (other.windtunnel != null)
                return false;
        } else if (!windtunnel.equals(other.windtunnel))
            return false;
        if (workshop == null) {
            if (other.workshop != null)
                return false;
        } else if (!workshop.equals(other.workshop))
            return false;
        return true;
    }

}
