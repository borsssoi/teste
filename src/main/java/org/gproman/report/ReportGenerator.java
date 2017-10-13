package org.gproman.report;

import org.gproman.model.Manager;
import org.gproman.model.race.Race;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;

public interface ReportGenerator {

    public String generate(Manager manager,
                           Season season,
                           Race race,
                           TyreSupplierAttrs supplier);

}