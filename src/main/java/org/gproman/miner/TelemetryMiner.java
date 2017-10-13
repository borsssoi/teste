package org.gproman.miner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ProgressMonitor;

import org.gproman.db.DataService;
import org.gproman.miner.parsers.GMTTelemetryParser;
import org.gproman.miner.parsers.GoBRTelemetryParser;
import org.gproman.model.everest.NormalizedRace;
import org.gproman.model.everest.NormalizedRace.RaceStatus;
import org.gproman.scrapper.GPROBrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlEmphasis;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeaderCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

/**
 * A class to aggregate the telemetry mining methods
 */
public class TelemetryMiner {

    private final GPROBrUtil  gprobr;
    private final DataService db;
    private static Logger     logger = LoggerFactory.getLogger(TelemetryMinerWorker.class);

    public TelemetryMiner(DataService db, GPROBrUtil gprobr ) {
        this.db = db;
        this.gprobr = gprobr;
    }

    public MiningResult mine(String forumURL, ProgressMonitor monitor, int current, int percent, boolean parseContent ) {
        logger.info("Mining " + forumURL);
        if (!updateProgress(monitor, current, "Buscando página inicial...") ) {
            return new MiningResult();
        }
        HtmlPage page1 = gprobr.getPage(forumURL + "1"); // fetches the first page

        // checks if there are multiple pages
        int lastPage = parsePageCount(page1);
        logger.info("Found " + lastPage + " pages for " + forumURL);

        List<HtmlPage> pages = new ArrayList<HtmlPage>();
        pages.add(page1);
        for (int i = 2; i <= lastPage; i++) {
            if (!updateProgress(monitor, current, "Buscando página "+i+" de "+lastPage) ) {
                return new MiningResult();
            }
            pages.add(gprobr.getPage(forumURL + i));
        }
        TelemetryMinerWorker miner = new TelemetryMinerWorker(db, pages, monitor, current, percent, parseContent );
        MiningResult result = miner.call();

        return result;
    }

    public int parsePageCount(HtmlPage page1) {
        int ret = 1;
        HtmlUnorderedList pages = page1.getFirstByXPath("//li[contains(text(),'Pages:')]/ancestor::ul");
        if (pages != null) {
            String[] p = pages.asText().split("\\s");
            ret = Integer.parseInt(p[p.length - 1].trim());
        }
        return ret;
    }
    
    private boolean updateProgress(ProgressMonitor monitor,
            int perc,
            String note) {
        if( monitor != null ) {
            if (monitor.isCanceled()) {
                return false;
            }
            monitor.setNote(note);
            monitor.setProgress(perc);
        }
        return true;
    }
    

    public static class TelemetryMinerWorker implements Callable<MiningResult> {

        private static final String  GMT    = "GMT ";
        private static final String  GO_BR  = "GoBR";
        private static Logger        logger = LoggerFactory.getLogger(TelemetryMinerWorker.class);
        private final Pattern        gmtP   = Pattern.compile(".*Telemetria gerada por GPRO Manager's Toolbox( v?(\\d\\.\\d\\.\\d\\.\\w*))?+.*", Pattern.DOTALL | Pattern.MULTILINE);
        private final Pattern        gobrP  = Pattern.compile("\\s*S\\d\\dR\\d\\d - .*?Risks Used.*", Pattern.DOTALL | Pattern.MULTILINE);

        private final DataService    db;
        private final List<HtmlPage> pages;
        private final ProgressMonitor monitor;
        private int current;
        private int percent;
        private final boolean parseContent;

        public TelemetryMinerWorker(DataService db, List<HtmlPage> pages, ProgressMonitor monitor, int current, int percent, boolean parseContent) {
            this.db = db;
            this.pages = pages;
            this.monitor = monitor;
            this.current = current;
            this.percent = percent;
            this.parseContent = parseContent;
        }

        @Override
        public MiningResult call() {
            return mine(pages, parseContent);
        }

        public MiningResult mine(List<HtmlPage> pages, boolean parseContent) {
            GMTTelemetryParser gmtParser = new GMTTelemetryParser(db);
            GoBRTelemetryParser gobrParser = new GoBRTelemetryParser(db);
            MiningResult result = new MiningResult();
            int[] topic = new int[2];
            int pageCount = pages.size();
            int totalCount = 0;

            for (HtmlPage page : pages) {
                try {
                    HtmlTable table = page.getFirstByXPath("//th[contains(text(), 'Replies:')]/ancestor::table");
                    int startIndex = 7;
                    if (table == null) {
                        // this is the first page
                        table = page.getFirstByXPath("//table[@id='topic_viewer']");
                        String header = ((HtmlTableHeaderCell) table.getFirstByXPath("./thead/tr/th")).asText();
                        result.setTopic(header);
                        logger.info("Mining = " + header);
                        topic = parseHeader(header);
                    } else {
                        // subsequent page
                        startIndex = 1;
                    }

                    int deletedUserCounter = 1;
                    List<HtmlTableRow> rows = table.getRows();
                    int count = 0;
                    for (int i = startIndex; i < rows.size() - 6; i += 5) {
                        HtmlTableRow nameRow = rows.get(i);
                        String name = nameRow.getCell(0).asText();
                        if ("Deleted User".equalsIgnoreCase(name)) {
                            // need to differentiate between deleted users or the telemetries will override each other
                            name += deletedUserCounter++;
                        }
                        if( parseContent ) {
                            String url = ((HtmlAnchor) nameRow.getFirstByXPath(".//a[contains( text(), 'Post #' )] ")).getHrefAttribute();
                            HtmlTableRow postRow = rows.get(i + 1);
                            HtmlTableCell postCell = postRow.getCell(1);

                            String tool = detectTool(postCell);
                            ParsingResult parse = null;
                            if (!updateProgress(current + (int)(((double)percent/(double)pageCount/50.0)*count++), "Processando telemetria #" + ++totalCount) ) {
                                return result;
                            }
                            if (tool.startsWith(GMT)) {
                                parse = gmtParser.parse(name, postCell, tool, url);
                            } else if (tool.equals(GO_BR)) {
                                parse = gobrParser.parse(name, postCell, tool, url);
                            } else {
                                parse = new ParsingResult();
                                parse.setContent(postCell.asText());
                                parse.addError("Parsing not supported for tool " + tool, null);
                            }
                            parse.setUrl(url);
                            result.addContent(tool, name, parse);
                            if ( parse.hasErrors() || parse.getRace().getRaceStatus() == RaceStatus.ERROR ) {
                                reportError(topic, tool, name, parse);
                            }
                        } else {
                            ParsingResult parse = new ParsingResult();
                            parse.setRace(new NormalizedRace());
                            parse.getRace().setRaceStatus(RaceStatus.UNKNOWN);
                            result.addContent("Unknown", name, parse);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error mining telemetries from page " + page.getUrl().toString(), e);
                }
                current += (percent/pageCount);
            }
            logger.info("Mined " + result.getReport());
            return result;
        }

        private void reportError(int[] topic, String tool, String name, ParsingResult parse) {
            PrintWriter fw = null;
            String folderName = "miner/" + topic[0] + "/" + topic[1] + "/" + tool.replaceAll("[\\s<>]+", "_");
            String fileName = folderName + "/" + name.replaceAll("\\s+", "_") + ".txt";
            try {
                File folder = new File(folderName);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                fw = new PrintWriter(fileName);
                fw.write("Error parsing content for tool: " + tool + "\n");
                fw.write("URL: " + parse.getUrl() + "\n");
                int index = 1;
                for (Map.Entry<String, Exception> entry : parse.getErrors().entrySet()) {
                    fw.write("    " + index + ". " + entry.getKey() + " \n");
                    if (entry.getValue() != null) {
                        entry.getValue().printStackTrace(fw);
                    }
                    index++;
                }
                fw.write("===================== CONTENT =======================\n");
                fw.write(parse.getContent());
                fw.close();
            } catch (IOException e) {
                logger.error("Error saving miner error report to file: " + fileName, e);
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (Exception e) {
                    }
                }
            }
        }

        private String detectTool(HtmlTableCell postCell) {
            HtmlEmphasis em = postCell.getFirstByXPath(".//em[contains( text(), 'Telemetria gerada por GPRO Manager' )]");
            String content = em != null ? em.asText() : postCell.asText();
            
            Matcher matcher = gmtP.matcher(content);
            if (matcher.matches()) {
                String version = matcher.group(2);
                return GMT + (version != null ? version : "1.0.0.GA");
            }
            matcher = gobrP.matcher(content);
            if (matcher.matches()) {
                return GO_BR;
            }
            return "<ferramenta desconhecida>";
        }

        private final Pattern seasonParser = Pattern.compile(".*Season (\\d+) - Race (\\d+).*");

        private int[] parseHeader(String theader) {
            int[] result = new int[2];
            try {
                Matcher m = seasonParser.matcher(theader);
                if (m.matches()) {
                    result[0] = Integer.valueOf(m.group(1));
                    result[1] = Integer.valueOf(m.group(2));
                }
            } catch (Exception e) {
                logger.error("Error parsing header '" + theader + "'", e);
            }
            return result;
        }
        
        private boolean updateProgress(int perc,
                String note) {
            if( this.monitor != null ) {
                if (this.monitor.isCanceled()) {
                    return false;
                }
                this.monitor.setNote(note);
                this.monitor.setProgress(perc);
            }
            return true;
        }
    }

}
