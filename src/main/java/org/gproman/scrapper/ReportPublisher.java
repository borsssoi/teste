package org.gproman.scrapper;

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.race.Race;

public class ReportPublisher
        implements
        Runnable {

    public static enum ReportType {
        TEST, SETUP, TELEMETRY
    }

    private final String            url;
    private final UserCredentials   credentials;
    private final Race              race;
    private final String            report;
    private final JFrame            frame;
    private final ProgressMonitor   monitor;
    private final ReportType        type;
    private final UserConfiguration conf;

    public ReportPublisher(ReportType type,
            JFrame frame,
            String url,
            UserCredentials credentials,
            UserConfiguration conf,
            Race race,
            String report) {
        this.type = type;
        this.frame = frame;
        this.url = url;
        this.credentials = credentials;
        this.conf = conf;
        this.race = race;
        this.report = report;
        this.monitor = new ProgressMonitor(frame,
                "Publicando relatório no GPRO Brasil...                                  ",
                "",
                0,
                100);
        this.monitor.setMillisToDecideToPopup(1);
    }

    @Override
    public void run() {
        GPROBrUtil util = new GPROBrUtil(credentials, conf);
        boolean result = false;
        if (ReportType.TEST.equals(type)) {
            result = util.publishTestReport(url, report, race.getSeasonNumber(), monitor);
        } else {
            result = util.publishReport(url, report, race.getSeasonNumber(), race.getNumber(), monitor);
        }
        if (result) {
            if (ReportType.TEST.equals(type)) {
                race.getStatus().setTestsPublished(new Timestamp(new Date().getTime()));;
            } else if (ReportType.SETUP.equals(type)) {
                race.getStatus().setSetupPublished(new Timestamp(new Date().getTime()));;
            } else {
                race.getStatus().setTelemetryPublished(new Timestamp(new Date().getTime()));;
            }
            monitor.close();
            JOptionPane.showMessageDialog(frame,
                    "Relatório publicado com sucesso!",
                    "Relatório Publicado",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            monitor.close();
            JOptionPane.showMessageDialog(frame,
                    "Erro publicando o relatório. Verifique o log para maiores detalhes.",
                    "Relatório Não Publicado",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
