package org.gproman.update;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.gproman.scrapper.GPROBrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class GMTUpdateManager {

    private static final Logger logger = LoggerFactory.getLogger(GMTUpdateManager.class);

    private String              updateInfoURL;
    private UpdateInfo          info;

    private GPROBrUtil browser;

    public GMTUpdateManager(GPROBrUtil browser, String updateInfoURL) {
        this.browser = browser;
        this.updateInfoURL = updateInfoURL;
    }

    public GMTUpdateManager checkLatestVersion() {
        try {
            logger.info("Checking latest version...");
            HtmlPage page = browser.getPage(updateInfoURL);
            
            if( page != null ) {
                HtmlTable table = page.getFirstByXPath("//table[@id='single_post']");
                if( table != null ) {
                    HtmlTableRow row = table.getRow(2);
                    HtmlTableCell cell = row.getCell(1);
                    String content = cell.asText().replaceAll("Edited by.*", "");
                    
                    Properties prop = new Properties();
                    prop.load(new ByteArrayInputStream(content.getBytes()));

                    info = new UpdateInfo(prop);
                    logger.info("Latest available version = " + info.getLatestVersion());
                }
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve latest version information", e);
            info = null;
        }
        return this;
    }

    public UpdateInfo getUpdateInfo() {
        return info;
    }

}
