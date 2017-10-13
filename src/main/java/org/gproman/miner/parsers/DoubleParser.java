package org.gproman.miner.parsers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class DoubleParser {

    private final char from;
    private final char to;
    private final DecimalFormat df;

    public DoubleParser() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
        final char decimalSeparator = dfs.getDecimalSeparator();
        to = decimalSeparator;
        from = to == '.' ? ',' : '.';
        df = new DecimalFormat();
    }

    public Double parse(String value) throws ParseException {
        return Double.parseDouble(value.replace(',', '.'));
    }
}