package org.gproman.model.season;

import org.gproman.model.PersistentEntity;

public class TyreSupplierAttrs extends PersistentEntity {

    private TyreSupplier supplier;
    private Integer      seasonNumber;
    private Integer      dry;
    private Integer      wet;
    private Integer      peak;
    private Integer      durability;
    private Integer      warmup;
    private Integer      cost;

    public TyreSupplierAttrs() {
        super();
    }

    public TyreSupplierAttrs(Integer id) {
        super(id);
    }

    public TyreSupplierAttrs(Integer id,
            TyreSupplier supplier,
            Integer seasonId,
            Integer dry,
            Integer wet,
            Integer peak,
            Integer durability,
            Integer warmup,
            Integer cost) {
        super(id);
        this.supplier = supplier;
        this.seasonNumber = seasonNumber;
        this.dry = dry;
        this.wet = wet;
        this.peak = peak;
        this.durability = durability;
        this.warmup = warmup;
        this.cost = cost;
    }

    public TyreSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(TyreSupplier supplier) {
        this.supplier = supplier;
    }
    
    public Integer getSeasonNumber() {
        return seasonNumber;
    }
    
    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getDry() {
        return dry;
    }

    public void setDry(Integer dry) {
        this.dry = dry;
    }

    public Integer getWet() {
        return wet;
    }

    public void setWet(Integer wet) {
        this.wet = wet;
    }

    public Integer getPeak() {
        return peak;
    }

    public void setPeak(Integer peak) {
        this.peak = peak;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Integer getWarmup() {
        return warmup;
    }

    public void setWarmup(Integer warmup) {
        this.warmup = warmup;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "TyreSupplierAttrs [supplier=" + supplier + ", seasonNumber=" + seasonNumber + ", dry=" + dry + ", wet=" + wet + ", peak=" + peak + ", durability=" + durability + ", warmup=" + warmup + ", cost=" + cost + ", getId()=" + getId() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((seasonNumber == null) ? 0 : seasonNumber.hashCode());
        result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
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
        TyreSupplierAttrs other = (TyreSupplierAttrs) obj;
        if (seasonNumber == null) {
            if (other.seasonNumber != null)
                return false;
        } else if (!seasonNumber.equals(other.seasonNumber))
            return false;
        if (supplier != other.supplier)
            return false;
        return true;
    }
    
}
