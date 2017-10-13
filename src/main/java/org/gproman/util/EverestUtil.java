package org.gproman.util;

import java.util.List;

import javax.swing.ProgressMonitor;

import org.gproman.db.EverestService;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.gproman.scrapper.GPROBrUtil;
import org.gproman.ui.GPROManFrame;
import org.gproman.ui.ReportPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EverestUtil {
    private static final Logger logger = LoggerFactory.getLogger(EverestUtil.class);
    
    public static void rebuildIndex(final GPROManFrame frame, final GPROBrUtil browser, final EverestService db) {
        ProgressMonitor monitor = new ProgressMonitor(frame.getFrame(),
                "Criando o índice de tópicos do GPRO Brasil...         ",
                "",
                0,
                100);
        try {
            monitor.setMillisToDecideToPopup(1);
            logger.info("Rebuilding index for telemetry topics.");
            List<ForumTopic> topics = browser.loadTopics(TopicType.TELEMETRY, 
                    frame.getConfiguration().getProperty(ReportPanel.PROP_REPORT_TELEMETRY), 
                    monitor,
                    0,
                    50);
            logger.info(topics.size() + " topics loaded. Saving database...");
            for (ForumTopic topic : topics) {
                db.store(topic);
            }
            logger.info(topics.size() + " Telemetry topics saved.");
            logger.info("Rebuilding index for setup topics.");
            topics = browser.loadTopics(TopicType.SETUP, 
                    frame.getConfiguration().getProperty(ReportPanel.PROP_REPORT_SETUP), 
                    monitor,
                    51,
                    100);
            logger.info(topics.size() + " topics loaded. Saving database...");
            for (ForumTopic topic : topics) {
                db.store(topic);
            }
            logger.info(topics.size() + " Setup topics saved.");
        } finally { 
            monitor.close();
        }
    }
}
