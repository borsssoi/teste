package org.gproman.scrapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.gproman.db.DataService;
import org.gproman.model.ApplicationStatus;
import org.gproman.model.Manager;
import org.gproman.model.SeasonHistory;
import org.gproman.model.car.Car;
import org.gproman.model.driver.Driver;
import org.gproman.model.race.CarSettings;
import org.gproman.model.race.Forecast;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Qualify;
import org.gproman.model.race.Race;
import org.gproman.model.race.RaceReport;
import org.gproman.model.race.StartingRisk;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.Tyre;
import org.gproman.model.season.OfficeData;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.staff.Facilities;
import org.gproman.model.staff.TechDirector;
import org.gproman.report.TelemetryReportGenerator;
import org.gproman.report.TelemetryReportGenerator.BBReportGenerator;
import org.gproman.scrapper.GPROBrUtil.AuthorizationResult;
import org.gproman.ui.GPROManFrame;
import org.gproman.ui.ReportPanel.TelemetryReportPanel;
import org.gproman.util.CredentialsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class DataWorker
        implements
        Runnable {

    private static final String   SUPPLIERS_URL_SUFFIX     = "/gb/Suppliers.asp";

    private static final Logger   logger                   = LoggerFactory.getLogger(DataWorker.class);

    private static final String   Q2_URL_SUFFIX            = "/gb/Qualify2.asp";
    private static final String   SETUP_URL_SUFFIX         = "/gb/RaceSetup.asp";
    private static final String   PAST_URL_SUFFIX          = "/gb/PastSetups.asp";
    private static final String   RACE_ANALYSIS_URL_SUFFIX = "/gb/RaceAnalysis.asp";

    private final DataService     db;
    private final ExecutorService executor;
    private final GPROManFrame    frame;

    private final GPROUtil        gpro;
    private final GPROBrUtil      gproBr;

    private WebConnection         webClient;

    private ProgressMonitor       monitor;

    public DataWorker(GPROManFrame frame,
            DataService db,
            ExecutorService executor,
            GPROUtil gpro,
            GPROBrUtil gproBr) {
        this.frame = frame;
        this.db = db;
        this.executor = executor;
        this.gpro = gpro;
        this.gproBr = gproBr;
        this.monitor = new ProgressMonitor(frame.getFrame(),
                "Buscando dados no GPRO...                                  ",
                "",
                0,
                100);
        this.monitor.setMillisToDecideToPopup(1);
        this.monitor.setMillisToPopup(1);

    }

    private boolean updateProgress(int perc,
            String note) {
        if (this.monitor.isCanceled()) {
            return false;
        }
        this.monitor.setNote(note);
        this.monitor.setProgress(perc);
        return true;
    }

    private void cancelDownload() {
        JOptionPane.showMessageDialog(frame.getFrame(), "Download cancelado...", "Download cancelado", JOptionPane.INFORMATION_MESSAGE);
        monitor.close();
    }

    @Override
    public void run() {
        Race publishTelemetry = null;
        try {
            if (!updateProgress(0, "Autorizando usuário no GPRO Brasil...")) {
                cancelDownload();
                return;
            }
            AuthorizationResult authorize = gproBr.authorize(false);
            if (authorize == AuthorizationResult.SUCCESS) {
                CredentialsManager.saveCredentials(frame.getCredentials());
            } else if (authorize == AuthorizationResult.LOGIN_FAILED) {
                logger.error("User not authorized. Login failed.");
                monitor.close();
                JOptionPane.showMessageDialog(frame.getFrame(),
                        "O login no fórum GPRO Brasil falhou.\n" +
                                "Verifique seu login e senha no menu:\n" +
                                "GMT -> Configurações -> Segurança\n",
                        "Erro de autenticação",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else if (authorize == AuthorizationResult.GROUP_NOT_AUTHORIZED) {
                logger.error("User not authorized. Group not authorized.");
                monitor.close();
                JOptionPane.showMessageDialog(frame.getFrame(),
                        "Você não é um membro ativo do fórum GPRO Brasil.\n" +
                                "Somente membros ativos do fórum estão\n" +
                                "autorizados a usar o GMT. Entre em contato\n" +
                                "com os administradores do fórum para regularizar\n" +
                                "sua situação. Assim que seu usuário no fórum\n" +
                                "estiver ativo, o GMT irá passar a funcionar automaticamente.",
                        "Erro de autorização",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!updateProgress(5, "Logando no GPRO...")) {
                return;
            }
            ;
            final HtmlPage office = gpro.login();
            if (office == null) {
                logger.error("Invalid GPRO user and/or password.");
                monitor.close();
                JOptionPane.showMessageDialog(frame.getFrame(),
                        "O login no GPRO falhou.\n" +
                                "Verifique seus dados de login e senha\n" +
                                "no menu GMT -> Configurações -> Segurança.",
                        "Erro de autenticação",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.webClient = this.gpro.getWebConnection();

            boolean isNewOffice = office.getFirstByXPath("//link[contains( @href, 'styles/office_new.css')]") != null;

            if (!updateProgress(10, "Checando status da aplicação...")) {
                cancelDownload();
                return;
            }
            ApplicationStatus status = db.getApplicationStatus();

            Manager manager = db.getManager();
            if (manager == null) {
                updateProgress(12, "Buscando dados do gerente...");
                logger.info("Manager data not found in the local database. Fetching it.");
                ManagerWorker managerW = new ManagerWorker(office);
                manager = managerW.call();
                db.store(manager);
            } else {
                logger.info("Manager data found in the local database: " + manager);
                HtmlAnchor group = office.getFirstByXPath("//a[starts-with(@href,'Standings.asp?Group=')]");
                if (group != null) {
                    String g = group.getTextContent().trim();
                    if (manager.getGroup() == null || !manager.getGroup().equals(g)) {
                        manager.setGroup(g);
                        logger.info("Updating manager's group to: " + manager);
                        db.store(manager);
                    }
                }
            }

            if (!updateProgress(14, "Lendo dados da página principal...")) {
                cancelDownload();
                return;
            }
            OfficeWorker officeW = new OfficeWorker(db,
                    office);
            Future<OfficeData> officeF = executor.submit(officeW);
            OfficeData officeData = officeF.get();

            HtmlPage raceAnalysisPage = null;
            if (officeData.getSeason() == -1) {
                // might be end of the season, so look at the past race page
                raceAnalysisPage = loadRaceAnalysisPage(webClient.clone());
                parseTitle(raceAnalysisPage,
                        officeData);
            }

            if (!updateProgress(15, "Atualizando dados da próxima corrida... ")) {
                cancelDownload();
                return;
            }
            status.setNextRace(officeData.getNextRace());
            status.setCurrentSeason(officeData.getSeason());
            status.setLastDownload(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().getTime()));
            db.store(status);

            Season season = db.getSeason(manager.getName(), officeData.getSeason());
            // load calendar if it is a new season
            boolean reloadCalendar = season == null;
            if (season != null) {
                // or if the season is already loaded, check if all tracks are set
                for (Race race : season.getRaces()) {
                    if (race.getTrack() == null) {
                        reloadCalendar = true;
                        break;
                    }
                }
            }
            if (reloadCalendar) {
                if (!updateProgress(16, "Carregando dados da temporada... ")) {
                    cancelDownload();
                    return;
                }
                CalendarWorker calendarW = new CalendarWorker(db,
                        manager,
                        office,
                        status.getCurrentSeason(),
                        monitor);
                Future<Season> calendarF = executor.submit(calendarW);
                if (season == null) {
                    // new season, so save the whole season
                    season = calendarF.get();
                    season.setGroupName(manager.getGroup());
                    season.setManagerName(manager.getName());
                    db.store(season);
                } else {
                    // update missing track references
                    Season calendar = calendarF.get();
                    for (int i = 0; i < season.getRaces().size(); i++) {
                        Race race = season.getRaces().get(i);
                        if (race.getTrack() == null) {
                            Race calRace = calendar.getRaces().get(i);
                            if (calRace != null && calRace.getTrack() != null) {
                                race.setTrack(calRace.getTrack());
                                db.store(manager.getName(), race);
                            }
                        }
                    }
                }

            }
            if (season.getGroupName() == null || season.getManagerName() == null) {
                season.setGroupName(manager.getGroup());
                season.setManagerName(manager.getName());
                db.store(season);
            }

            // check for old season groups that were not set
            List<Season> nullGroups = db.getSeasonsWithNullGroups();
            if (!nullGroups.isEmpty()) {
                logger.info(nullGroups.size() + " seasons without a group name found. Trying to update them.");
                HtmlAnchor managerLink = office.getFirstByXPath("//a[@class='managername']");
                if (managerLink != null) {
                    HtmlPage managerPage = managerLink.click();
                    if (managerPage != null) {
                        ManagerProfileWorker mpw = new ManagerProfileWorker(managerPage);
                        Manager manhistory = mpw.call();
                        for (Season s : nullGroups) {
                            for (SeasonHistory h : manhistory.getSeasonHistory()) {
                                if (s.getNumber().intValue() == h.getSeasonNumber().intValue()) {
                                    s.setGroupName(h.getGroupName());
                                    logger.info("Updating group name on old season record: Season " + s.getNumber() + " -> " + s.getGroupName());
                                    db.store(s);
                                    break;
                                }
                            }
                        }
                    } else {
                        logger.error("Error loading manager's page.");
                    }
                } else {
                    logger.error("Manager's link not found.");
                }
            }

            if (!updateProgress(49, "Checando fornecedor de pneus... ")) {
                cancelDownload();
                return;
            }
            PageLoader loader = new PageLoader(webClient.clone(), webClient.getConf().getGproUrl() + SUPPLIERS_URL_SUFFIX);
            Future<HtmlPage> supPage = executor.submit(loader);
            TyreSupplierWorker ts = new TyreSupplierWorker(supPage);
            Future<TyreSupplierWorkerResult> fts = executor.submit(ts);
            TyreSupplierWorkerResult supplier = fts.get();
            if (supplier != null) {
                if (reloadCalendar || (supplier.signed != null && season.getSupplier() != supplier.signed) || db.getTyreSuppliersForSeason(season.getNumber()).isEmpty()) {
                    for (TyreSupplierAttrs attrs : supplier.suppliers) {
                        attrs.setSeasonNumber(status.getCurrentSeason());
                        logger.info("Updating tyre supplier attributes: " + attrs);
                        db.store(attrs);
                    }
                    if (supplier.signed != null && season.getSupplier() != supplier.signed) {
                        logger.info("New supplier detected: " + supplier + ". Updating season info.");
                        season.setSupplier(supplier.signed);
                        db.store(season);
                    }
                }
            }
            if (!updateProgress(50, "Dados da temporada carregados. ")) {
                cancelDownload();
                return;
            }

            if (!updateProgress(52, "Carregando página do piloto... ")) {
                cancelDownload();
                return;
            }
            Future<Driver> driverF = null;
            try {
                HtmlAnchor driverLink = office.getFirstByXPath("//a[starts-with(@href,'DriverProfile.asp?ID=')]");
                if (driverLink != null) {
                    HtmlPage driverPage = driverLink.click();
                    DriverWorker driverW = new DriverWorker(db, driverPage);
                    driverF = executor.submit(driverW);
                } else {
                    logger.info("No Driver found. Probably not hired.");
                }
            } catch (Exception e) {
                logger.error("Error finding driver profile page.", e);
            }

            Driver driver = driverF != null ? driverF.get() : null;

            if (!updateProgress(54, "Carregando página do diretor técnico... ")) {
                cancelDownload();
                return;
            }
            Future<TechDirector> tdF = null;
            try {
                HtmlAnchor tdLink = office.getFirstByXPath("//a[contains(@href,'TechDProfile.asp?ID=')]");
                if (tdLink != null) {
                    HtmlPage tdPage = tdLink.click();
                    TechDirectorWorker tdW = new TechDirectorWorker(tdPage);
                    tdF = executor.submit(tdW);
                } else {
                    logger.info("Technical Director profile page not found.");
                }
            } catch (Exception e) {
                logger.info("Technical Director profile page not found.");
            }

            TechDirector td = tdF != null ? tdF.get() : null;

            if (!updateProgress(57, "Carregando página do carro... ")) {
                cancelDownload();
                return;
            }
            CarWorker carW = new CarWorker(webClient.clone());
            Future<Car> carF = executor.submit(carW);
            Car car = carF.get();

            if (!updateProgress(58, "Carregando dados de staff&facilities... ")) {
                cancelDownload();
                return;
            }
            Facilities facilities = null;
            try {
                loader = new PageLoader(webClient.clone(), "http://gpro.net/gb/StaffAndFacilities.asp");
                Future<HtmlPage> facilitiesPageF = executor.submit(loader);
                HtmlPage facilitiesPage = facilitiesPageF.get();
                if (facilitiesPage != null) {
                    FacilitiesWorker facilitiesW = new FacilitiesWorker(facilitiesPage);
                    Future<Facilities> facilitiesF = executor.submit(facilitiesW);
                    facilities = facilitiesF.get();
                }
            } catch (Exception e1) {
                logger.error("Error loading and parsing facilities page", e1);
            }

            if (status.getNextRace() != -1) {
                if (!updateProgress(58, "Carregando página de testes... ")) {
                    cancelDownload();
                    return;
                }
                TestSession testSession = null;
                try {
                    Future<HtmlPage> tsPageF = executor.submit(new PageLoader(webClient.clone(), webClient.getConf().getGproUrl() + TestSessionWorker.TEST_SESSION_URL_SUFFIX));
                    HtmlPage tsPage = tsPageF.get();
                    TestSessionWorker tsW = new TestSessionWorker(tsPage, db);
                    Future<TestSession> tsF = executor.submit(tsW);
                    testSession = tsF.get();
                } catch (Exception e) {
                    logger.error("Unable to load test session page...", e);
                }

                Race race = db.getRace(status.getCurrentSeason(),
                        status.getNextRace());

                if (driver != null) {
                    frame.setStatusMT("Dados do piloto obtidos.");
                    race.setDriverStart(driver);
                }
                if (car != null) {
                    frame.setStatusMT("Dados do carro obtidos.");
                    race.setCarStart(car);
                }
                if (td != null) {
                    frame.setStatusMT("Dados do diretor técnico obtidos.");
                    race.setTDStart(td);
                } else {
                    race.setTDStart(null);
                }
                if (testSession != null) {
                    frame.setStatusMT("Dados da sessão de tests obtidos.");
                    // requires merge
                    if (race.getTestSession() != null) {
                        race.getTestSession().merge(testSession);
                    } else {
                        race.setTestSession(testSession);
                    }
                } else {
                    race.setTestSession(null);
                }

                if (facilities != null) {
                    race.setFacilities(facilities);
                } else {
                    race.setFacilities(null);
                }

                Future<HtmlPage> practicePageF = null;
                Future<HtmlPage> q2PageF = null;
                Future<HtmlPage> setupPageF = null;
                if (isNewOffice) {
                    HtmlTable table = office.getFirstByXPath("//a[contains(text(),'Practice')]/ancestor::table");

                    practicePageF = executor.submit(new PageLoader(webClient.clone(), webClient.getConf().getGproUrl() + PracticeWorker.PRACTICE_URL_SUFFIX));
                    if ("Complete".equals(table.getRow(2).getCell(1).getTextContent().trim())) {
                        q2PageF = executor.submit(new PageLoader(webClient.clone(), frame.getConfiguration().getGproUrl() + Q2_URL_SUFFIX));
                    }
                    if ("Complete".equals(table.getRow(3).getCell(1).getTextContent().trim())) {
                        setupPageF = executor.submit(new PageLoader(webClient.clone(), frame.getConfiguration().getGproUrl() + SETUP_URL_SUFFIX));
                    }
                } else {
                    boolean isStrategyDone = office.getFirstByXPath("//img[@title='Race strategy confirmed!']") != null;
                    practicePageF = executor.submit(new PageLoader(webClient.clone(), webClient.getConf().getGproUrl() + PracticeWorker.PRACTICE_URL_SUFFIX));
                    if (isStrategyDone) {
                        q2PageF = executor.submit(new PageLoader(webClient.clone(), frame.getConfiguration().getGproUrl() + Q2_URL_SUFFIX));
                        setupPageF = executor.submit(new PageLoader(webClient.clone(), frame.getConfiguration().getGproUrl() + SETUP_URL_SUFFIX));
                    }
                }
                Future<Forecast[]> forecastF = null;
                Future<Qualify> q1F = null;
                Future<Practice> practiceF = null;
                if (practicePageF != null) {
                    if (!updateProgress(60, "Carregando página de treinos/Q1... ")) {
                        cancelDownload();
                        return;
                    }
                    HtmlPage practicePage = practicePageF.get();

                    if (practicePage != null) {
                        ForecastWorker forecastWorker = new ForecastWorker(practicePage);
                        forecastF = executor.submit(forecastWorker);
                        PracticeWorker practiceW = new PracticeWorker(practicePage);
                        practiceF = executor.submit(practiceW);
                        Q1Worker q1W = new Q1Worker(practicePage);
                        q1F = executor.submit(q1W);
                    }
                }

                Future<Qualify> q2F = null;
                if (q2PageF != null) {
                    if (!updateProgress(62, "Carregando página de Q2... ")) {
                        cancelDownload();
                        return;
                    }
                    HtmlPage q2Page = q2PageF.get();
                    Q2Worker q2W = new Q2Worker(q2Page);
                    q2F = executor.submit(q2W);
                }

                Future<SetupWorker.SetupData> setupF = null;
                if (setupPageF != null) {
                    if (!updateProgress(65, "Carregando página de estratégia da corrida... ")) {
                        cancelDownload();
                        return;
                    }
                    HtmlPage setupPage = setupPageF.get();
                    SetupWorker setupW = new SetupWorker(setupPage);
                    setupF = executor.submit(setupW);
                }
                if (forecastF != null) {
                    if (!updateProgress(68, "Lendo dados da previsão do tempo... ")) {
                        cancelDownload();
                        return;
                    }
                    Forecast[] forecast = forecastF.get();
                    if (forecast != null) {
                        frame.setStatusMT("Previsão do tempo obtida.");
                        race.setForecast(forecast);
                    }
                }
                if (practiceF != null) {
                    if (!updateProgress(70, "Lendo dados dos treinos... ")) {
                        cancelDownload();
                        return;
                    }
                    Practice practice = practiceF.get();
                    if (practice != null) {
                        frame.setStatusMT("Dados dos treinos obtidos.");
                        race.setPractice(practice);
                        race.getStatus().setPractice(true);
                    }
                }
                if (q1F != null) {
                    if (!updateProgress(75, "Lendo dados do Q1... ")) {
                        cancelDownload();
                        return;
                    }
                    Qualify q1 = q1F.get();
                    if (q1 != null) {
                        frame.setStatusMT("Dados do Q1 obtidos.");
                        race.setQualify1(q1);
                        race.getStatus().setQualify1(true);
                    }
                }
                if (q2F != null) {
                    if (!updateProgress(80, "Lendo dados do Q2... ")) {
                        cancelDownload();
                        return;
                    }
                    Qualify q2 = q2F.get();
                    if (q2 != null) {
                        frame.setStatusMT("Dados do Q2 obtidos.");
                        race.setQualify2(q2);
                        race.getStatus().setQualify2(true);
                    }
                }
                if (setupF != null) {
                    if (!updateProgress(85, "Lendo dados da estratégia de corrida... ")) {
                        cancelDownload();
                        return;
                    }
                    SetupWorker.SetupData setup = setupF.get();
                    if (setup != null) {
                        frame.setStatusMT("Dados do setup obtidos.");
                        race.setRaceSettings(new CarSettings());
                        race.getRaceSettings().setFrontWing(setup.fwing);
                        race.getRaceSettings().setRearWing(setup.rwing);
                        race.getRaceSettings().setEngine(setup.engine);
                        race.getRaceSettings().setBrakes(setup.brakes);
                        race.getRaceSettings().setGear(setup.gear);
                        race.getRaceSettings().setSuspension(setup.suspension);
                        race.setStartingFuel(setup.startingFuel);
                        race.setFuelStrategy(setup.fuelStrategy);
                        race.setRiskOvertake(setup.overtake);
                        race.setRiskDefend(setup.defend);
                        race.setRiskClear(setup.clear);
                        race.setRiskClearWet(setup.clearWet);
                        race.setRiskMalfunction(setup.malfunc);
                        race.setRiskStarting(setup.startRisk != null ? StartingRisk.determineRisk(setup.startRisk) : null);
                        race.setTyreAtStart(setup.tyreAtStart != null ? Tyre.determineTyre(setup.tyreAtStart) : null);
                        race.setTyreWhenWet(setup.tyreWhenWet != null ? Tyre.determineTyre(setup.tyreWhenWet) : null);
                        race.setTyreWhenDry(setup.tyreWhenDry != null ? Tyre.determineTyre(setup.tyreWhenDry) : null);
                        race.setWaitPitWet(setup.waitWhenWet);
                        race.setWaitPitDry(setup.waitWhenDry);
                        race.getStatus().setSetup(true);
                    }
                }
                db.store(manager.getName(), race);
            }

            Race previousRace = null;
            Future<RaceReport> pastF = null;
            int pRace = status.getNextRace() > 1 ? status.getNextRace() - 1 : 17;
            int pSeason = status.getNextRace() > 1 ? status.getCurrentSeason() :
                    (status.getNextRace() == -1 ? status.getCurrentSeason() : status.getCurrentSeason() - 1);
            previousRace = db.getRace(pSeason,
                    pRace);
            if (previousRace != null && !previousRace.getStatus().isTelemetry()) {
                if (!updateProgress(90, "Carregando página da corrida anterior... ")) {
                    cancelDownload();
                    return;
                }
                logger.info("Retrieving previous race telemetry.");
                final HtmlPage pastPage = loadPastSetupPage(webClient.clone());
                PastSetupWorker past = new PastSetupWorker(pastPage);
                pastF = executor.submit(past);
            }

            if (pastF != null) {
                if (!updateProgress(95, "Lendo dados da corrida anterior... ")) {
                    cancelDownload();
                    return;
                }
                RaceReport raceReport = pastF.get();
                if (previousRace != null && raceReport != null) {
                    logger.info("Saving previous race telemetry.");
                    previousRace.populateFromReport(raceReport);
                    if (previousRace.getDriverFinish() != null && previousRace.getDriverStart() != null) {
                        // this is a quick hack... need to be fixed when driver attributes are normalized
                        previousRace.getDriverFinish().setFavoriteTracks(previousRace.getDriverStart().getFavoriteTracks());
                    }
                    frame.setStatusMT("Dados de telemetria da corrida anterior obtidos.");
                    db.store(manager.getName(), previousRace);
                    publishTelemetry = previousRace;
                } else if (raceReport == null) {
                    // past race report worker failed. Trying past race analysis instead.
                    logger.info("Retrieving previous race analysis...");
                    if (raceAnalysisPage == null) {
                        raceAnalysisPage = loadRaceAnalysisPage(webClient.clone());
                    }
                    if (raceAnalysisPage != null) {
                        RaceAnalysisWorker past = new RaceAnalysisWorker(raceAnalysisPage);
                        pastF = executor.submit(past);
                        RaceReport report = pastF.get();
                        report.setDriver(driver);
                        report.setCarFinish(car);
                        previousRace.populateFromReport(report);
                        frame.setStatusMT("Dados de análise da ultima corrida obtidos.");
                        db.store(manager.getName(), previousRace);
                        publishTelemetry = previousRace;
                    }
                }
            }
            if (!updateProgress(100, "Dados carregados com sucesso. ")) {
                cancelDownload();
                return;
            }
            monitor.close();
            frame.setStatusMT("Dados obtidos com sucesso.");

        } catch (Exception e) {
            logger.error("Error fetching data.", e);
            frame.setStatusMT("Erro obtendo dados. Verifique o arquivo gmt.log para detalhes.");
            monitor.close();
            JOptionPane.showMessageDialog(frame.getFrame(),
                    "Erro fazendo download dos dados.\nCheque o log para maiores detalhes e/ou\ntente novamente mais tarde.",
                    "Erro no download",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            executor.shutdown();
            final Race toPublish = publishTelemetry;
            if (toPublish != null) {
                publishTelemetry(toPublish);
            } else {
                // just update
                frame.updateData(false);
                frame.setStatus("Dados atualizados.");
            }
        }
    }

    private HtmlPage loadPastSetupPage(WebConnection webClient) {
        try {
            HtmlPage pastPage = webClient.getPage(frame.getConfiguration().getGproUrl() + PAST_URL_SUFFIX);
            return pastPage;
        } catch (IOException e) {
            logger.error("Error retrieving past setups page. ", e);
        }

        return null;
    }

    private HtmlPage loadRaceAnalysisPage(WebConnection webClient) {
        try {
            HtmlPage pastPage = webClient.getPage(frame.getConfiguration().getGproUrl() + RACE_ANALYSIS_URL_SUFFIX);
            return pastPage;
        } catch (IOException e) {
            logger.error("Error retrieving race analysis page. ", e);
        }

        return null;
    }

    private void parseTitle(HtmlPage page,
            OfficeData data) {
        // Parse season and race info
        String title = page.getTitleText();
        Matcher tm = Pattern.compile(".*Season (\\d+) - Race \\d+ .*").matcher(title);
        if (tm.matches()) {
            Integer seasonNumber = Integer.valueOf(tm.group(1));
            logger.info("Season " + seasonNumber + " reset detected.");
            data.setSeason(seasonNumber);
            data.setNextRace(-1);
        } else {
            logger.error("Failed to find season number parsing title '" + title + "'");
        }
    }

    public void publishTelemetry(final Race toPublish) {
        int n = JOptionPane.showConfirmDialog(
                frame.getFrame(),
                "Deseja publicar o relatório da corrida " + toPublish.getTrack().getName() + " no Forum GPROBrasil?",
                "Confirmação de Publicação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (n == JOptionPane.YES_OPTION) {
            BBReportGenerator rg = new TelemetryReportGenerator.BBReportGenerator();
            Season season = frame.getApplication().getDataService().getSeason(frame.getApplication().getDataService().getManager().getName(), toPublish.getSeasonNumber());
            String report = rg.generate(frame.getApplication().getDataService().getManager(),
                    season,
                    toPublish,
                    frame.getApplication().getDataService().getTyreSupplier(toPublish.getSeasonNumber(), season.getSupplier().toString()));
            final ReportPublisher publisher = new ReportPublisher(ReportPublisher.ReportType.TELEMETRY,
                    frame.getFrame(),
                    TelemetryReportPanel.TELEMETRY_FORUM_URL,
                    frame.getCredentials(),
                    frame.getConfiguration(),
                    toPublish,
                    report);
            // because of the monitor, we need to star a separate thread
            SwingWorker<Boolean, Boolean> worker = new SwingWorker<Boolean, Boolean>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    publisher.run();
                    return Boolean.TRUE;
                }

                @Override
                protected void done() {
                    frame.getApplication().getDataService().store(frame.getApplication().getDataService().getManager().getName(), toPublish);
                    frame.updateData(false);
                    frame.setStatus("Dados atualizados.");
                }
            };
            worker.execute();
        } else {
            frame.updateData(false);
            frame.setStatus("Dados atualizados.");
        }
    }

}