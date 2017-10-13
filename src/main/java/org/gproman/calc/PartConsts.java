package org.gproman.calc;

import java.math.BigDecimal;

public enum PartConsts {
    WINGS(new BigDecimal("5.75"), new BigDecimal("0.3"), new BigDecimal("281.0"), // tempDry, tempWet, rainOffset
            new BigDecimal("-10"), BigDecimal.ZERO, new BigDecimal("15"), new BigDecimal("-8"), BigDecimal.ZERO, // chaLvl, engLvl, wngLvl, undLvl, sidLvl  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // cooLvl, geaLvl, braLvl, susLvl, eleLvl  
            new BigDecimal("25"), BigDecimal.ZERO, new BigDecimal("-28"), new BigDecimal("15"), BigDecimal.ZERO, // chaWear, engWear, wngWear, undWear, sidWear  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // cooWear, geaWear, braWear, susWear, eleWear  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // con, tal, agg
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO ), // exp, tei, wei

    ENGINE(new BigDecimal("-3.0"), new BigDecimal("0.8"), new BigDecimal("-193.5"), // tempDry, tempWet, rainOffset
            BigDecimal.ZERO, new BigDecimal("16"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaLvl, engLvl, wngLvl, undLvl, sidLvl  
            new BigDecimal("5"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("3"), // cooLvl, geaLvl, braLvl, susLvl, eleLvl  
            BigDecimal.ZERO, new BigDecimal("-50"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaWear, engWear, wngWear, undWear, sidWear  
            new BigDecimal("-7"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("-5"), // cooWear, geaWear, braWear, susWear, eleWear  
            BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.3"), // con, tal, agg
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO ), // exp, tei, wei

    BRAKES(new BigDecimal("6.0"), new BigDecimal("4.0"), new BigDecimal("105.5"), // tempDry, tempWet, rainOffset
            new BigDecimal("6"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaLvl, engLvl, wngLvl, undLvl, sidLvl  
            BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("-29"), BigDecimal.ZERO, new BigDecimal("6"), // cooLvl, geaLvl, braLvl, susLvl, eleLvl  
            new BigDecimal("-14"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaWear, engWear, wngWear, undWear, sidWear  
            BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("71"), BigDecimal.ZERO, new BigDecimal("-9"), // cooWear, geaWear, braWear, susWear, eleWear  
            BigDecimal.ZERO, new BigDecimal("-0.5"), BigDecimal.ZERO, // con, tal, agg
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO ), // exp, tei, wei

    GEAR(new BigDecimal("-4.0"), new BigDecimal("-8.0"), new BigDecimal("-4.5"), // tempDry, tempWet, rainOffset
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaLvl, engLvl, wngLvl, undLvl, sidLvl  
            BigDecimal.ZERO, new BigDecimal("-41"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("9"), // cooLvl, geaLvl, braLvl, susLvl, eleLvl  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // chaWear, engWear, wngWear, undWear, sidWear  
            BigDecimal.ZERO, new BigDecimal("108"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("-14"), // cooWear, geaWear, braWear, susWear, eleWear  
            new BigDecimal("0.5"), BigDecimal.ZERO, BigDecimal.ZERO, // con, tal, agg
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO ), // exp, tei, wei

    SUSPENSION(new BigDecimal("-6.0"), new BigDecimal("-1.0"), new BigDecimal("-258.0"), // tempDry, tempWet, rainOffset
            new BigDecimal("-14"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("-12"), new BigDecimal("6"), // chaLvl, engLvl, wngLvl, undLvl, sidLvl  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("31"), BigDecimal.ZERO, // cooLvl, geaLvl, braLvl, susLvl, eleLvl  
            new BigDecimal("36"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("22"), new BigDecimal("-11"), // chaWear, engWear, wngWear, undWear, sidWear  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("-69"), BigDecimal.ZERO, // cooWear, geaWear, braWear, susWear, eleWear  
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // con, tal, agg
            new BigDecimal("0.75"), new BigDecimal("0.11"), new BigDecimal("2") ); // exp, tei, wei

    public final BigDecimal tempDry;
    public final BigDecimal tempWet;
    public final BigDecimal rainOffset;

    public final BigDecimal chaLvl;
    public final BigDecimal engLvl;
    public final BigDecimal wngLvl;
    public final BigDecimal undLvl;
    public final BigDecimal sidLvl;
    public final BigDecimal cooLvl;
    public final BigDecimal geaLvl;
    public final BigDecimal braLvl;
    public final BigDecimal susLvl;
    public final BigDecimal eleLvl;

    public final BigDecimal chaWear;
    public final BigDecimal engWear;
    public final BigDecimal wngWear;
    public final BigDecimal undWear;
    public final BigDecimal sidWear;
    public final BigDecimal cooWear;
    public final BigDecimal geaWear;
    public final BigDecimal braWear;
    public final BigDecimal susWear;
    public final BigDecimal eleWear;

    public final BigDecimal con;
    public final BigDecimal tal;
    public final BigDecimal agg;
    public final BigDecimal exp;
    public final BigDecimal tei;
    public final BigDecimal wei;

    private PartConsts(BigDecimal tempDry, 
            BigDecimal tempWet, 
            BigDecimal rainOffset, 
            BigDecimal chaLvl, 
            BigDecimal engLvl, 
            BigDecimal wngLvl, 
            BigDecimal undLvl, 
            BigDecimal sidLvl, 
            BigDecimal cooLvl, 
            BigDecimal geaLvl, 
            BigDecimal braLvl, 
            BigDecimal susLvl, 
            BigDecimal eleLvl, 
            BigDecimal chaWear, 
            BigDecimal engWear, 
            BigDecimal wngWear, 
            BigDecimal undWear, 
            BigDecimal sidWear, 
            BigDecimal cooWear, 
            BigDecimal geaWear, 
            BigDecimal braWear, 
            BigDecimal susWear, 
            BigDecimal eleWear, 
            BigDecimal con, 
            BigDecimal tal, 
            BigDecimal agg, 
            BigDecimal exp, 
            BigDecimal tei, 
            BigDecimal wei) {
        this.tempDry = tempDry;
        this.tempWet = tempWet;
        this.rainOffset = rainOffset;
        this.chaLvl = chaLvl;
        this.engLvl = engLvl;
        this.wngLvl = wngLvl;
        this.undLvl = undLvl;
        this.sidLvl = sidLvl;
        this.cooLvl = cooLvl;
        this.geaLvl = geaLvl;
        this.braLvl = braLvl;
        this.susLvl = susLvl;
        this.eleLvl = eleLvl;
        this.chaWear = chaWear;
        this.engWear = engWear;
        this.wngWear = wngWear;
        this.undWear = undWear;
        this.sidWear = sidWear;
        this.cooWear = cooWear;
        this.geaWear = geaWear;
        this.braWear = braWear;
        this.susWear = susWear;
        this.eleWear = eleWear;
        this.con = con;
        this.tal = tal;
        this.agg = agg;
        this.exp = exp;
        this.tei = tei;
        this.wei = wei;
    }

    public static PartConsts getByIndex(int index) {
        switch (index) {
            case 0:
            case 1:
                return WINGS;
            case 2:
                return ENGINE;
            case 3:
                return BRAKES;
            case 4:
                return GEAR;
            case 5:
                return SUSPENSION;
        }
        return null;
    }
}