package org.gproman.miner;

import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

public interface TelemetryMinerParser {
    public ParsingResult parse( String managerName, HtmlTableCell postCell, String tool, String url );
}