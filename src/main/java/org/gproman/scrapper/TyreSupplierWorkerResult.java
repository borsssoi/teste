package org.gproman.scrapper;

import java.util.ArrayList;
import java.util.List;

import org.gproman.model.season.TyreSupplier;
import org.gproman.model.season.TyreSupplierAttrs;


public class TyreSupplierWorkerResult {
    public TyreSupplier signed;
    public List<TyreSupplierAttrs> suppliers = new ArrayList<TyreSupplierAttrs>(9);
    
    @Override
    public String toString() {
        return "TyreSupplierWorkerResult [signed=" + signed + ", suppliers=" + suppliers + "]";
    }
}
