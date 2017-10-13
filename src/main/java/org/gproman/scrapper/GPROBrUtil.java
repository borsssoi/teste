package org.gproman.scrapper;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDefinitionDescription;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.ProgressMonitor;
import org.gproman.GproManager;
import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.gproman.model.everest.ForumTopic;
import org.gproman.model.everest.ForumTopic.TopicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPROBrUtil {

    private static final String ADMINISTRADORES = "Administradores";
    public static final String GPROBrasil_URL = "http://s3.zetaboards.com/Grand_Prix_RO/";
    public static final String GPROBrasil = "http://s3.zetaboards.com/Grand_Prix_RO/login";

    public static final String GPROBrasil_JOIN_GMT_USERS_URL = "http://s3.zetaboards.com/Grand_Prix_RO/home/?c=23&id=613";
    public static final String GPROBrasil_GMT_TIMESTAMP = "http://s3.zetaboards.com/Grand_Prix_RO/home/?c=6";
    public static final String GPROBrasil_MEMBERS = "http://s3.zetaboards.com/Grand_Prix_RO/members/";

    private static final Logger logger = LoggerFactory.getLogger(GPROBrUtil.class);

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("dd/MM/yyyy");

    private UserCredentials credentials;
    private UserConfiguration conf;
    private WebConnection webClient;
    private WebConnection internal;

    private boolean isLoggedIn;

    public GPROBrUtil(UserCredentials credentials, UserConfiguration configuration) {
        this(credentials, configuration, new WebConnection(configuration));
    }

    public GPROBrUtil(UserCredentials credentials,
            UserConfiguration configuration,
            WebConnection webClient) {
        this.credentials = credentials;
        this.conf = configuration;
        this.webClient = webClient;
        this.internal = webClient.clone();
        this.isLoggedIn = false;
    }

    public synchronized boolean isLoggedIn() {
        return isLoggedIn;
    }

    public synchronized WebConnection getWebConnection() {
        return webClient;
    }

    public synchronized HtmlPage login() throws IOException {
        logger.info("Logging in into GPROBrasil");
        final HtmlPage page = webClient.getPage(GPROBrasil);
        if (page == null) {
            logger.error("Error retrieving login page. Got a null response.");
            return null;
        }

        try {
            if (page.asText().contains("You are logged in already.")) {
                logger.info("We are already logged in. Retrieving main page.");
                return webClient.getPage(GPROBrasil_URL);
            } else {
                logger.info("Retrieving login form...");
                HtmlForm form = page.getFirstByXPath("//form[@action='http://s3.zetaboards.com/Grand_Prix_RO/login/log_in/']");

                final HtmlTextInput login = form.getInputByName("uname");
                final HtmlPasswordInput pwd = form.getInputByName("pw");
                final HtmlButton button = (HtmlButton) form.getElementsByTagName("button").get(0);

                logger.info("Setting login credentials...");
                login.setValueAttribute(credentials.getGproBrUser());
                pwd.setValueAttribute(credentials.getGproBrPassword());

                logger.info("Submitting...");
                HtmlPage mainPage = button.click();
                if (validateLogin(mainPage)) {
                    isLoggedIn = true;
                    this.internal = webClient.clone();
                    return mainPage;
                }
            }
        } catch (Exception e) {
            logger.error("Error trying to login. Login failed.", e);
        }
        return null;
    }

    public synchronized AuthorizationResult authorize(final boolean force) throws ElementNotFoundException, IOException {
        if (force || requiresAuthorization()) {
            HtmlPage page = login();
            if (page == null) {
                return AuthorizationResult.LOGIN_FAILED;
            }
            page = getProfilePage(page);
            String group = extractGroup(page);
            if (!isAuthenticated(group)) {
                logger.info("Authentication on GPROBrasil failed. User group = " + group);
                return AuthorizationResult.GROUP_NOT_AUTHORIZED;
            }

            if (!joinedGMTUsers(page)) {
                logger.info("Not yet a member of the GMT Users group. Joining...");
                // join GMT Users group
                try {
                    HtmlPage joinPage = webClient.getPage(GPROBrasil_JOIN_GMT_USERS_URL);
                    HtmlButton confirm = joinPage.getFirstByXPath("//button[contains(text(),'Confirm')]");
                    if (confirm != null) {
                        confirm.click();
                    } else {
                        logger.error("Error trying to join GMT Users group. Confirm button not found.");
                    }
                } catch (Exception e) {
                    logger.error("Error trying to join GMT Users group.", e);
                }
            }

            if (ADMINISTRADORES.equals(group)) {
                logger.info("Admin user. Upgrading credentials...");
                credentials.setRole(UserCredentials.UserRole.ADMIN);
            } else if (joinedEverest(page)) {
                logger.info("Everest member. Upgrading credentials...");
                credentials.setRole(UserCredentials.UserRole.ADVANCED);
            } else {
                logger.info("Standard user authenticated.");
                credentials.setRole(UserCredentials.UserRole.ADVANCED);
            }

            try {
                logger.info("Posting GMT timestamp.");
                HtmlPage profile = webClient.getPage(GPROBrasil_GMT_TIMESTAMP);
                HtmlInput input = profile.getFirstByXPath("//input[@name='choice_1045']");
                if (input != null) {
                    input.setValueAttribute(GproManager.getVersionString() + new SimpleDateFormat(" dd/MMM/yyyy HH:mm:ss z").format(Calendar.getInstance().getTime()));
                    HtmlButton confirm = profile.getFirstByXPath("//button[contains(text(),'Save Profile Changes')]");
                    if (confirm != null) {
                        confirm.click();
                    } else {
                        logger.error("Error posting GMT timestamp. Confirm button not found.");
                    }
                } else {
                    logger.error("Error posting GMT timestamp. Input field not found.");
                }
            } catch (Exception e) {
                logger.error("Error posting GMT timestamp.", e);
            }

            credentials.setLastAuthentication(Calendar.getInstance());
            logger.info("Authentication on GPROBrasil successful.");
        } else {
            logger.info(String.format("User was last authenticated on %tc", credentials.getLastAuthentication()));
        }
        return AuthorizationResult.SUCCESS;
    }

    public synchronized boolean publishReport(String forumUrl, String report, Integer season, Integer race, ProgressMonitor monitor) {
        try {
            monitor.setProgress(0);
            monitor.setNote("Logando no GPRO Brasil...");
            HtmlPage mainPage = login();
            if (mainPage != null) {
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Buscando o forum...");
                monitor.setProgress(20);
                logger.info("Fetching forum page: " + forumUrl);
                HtmlPage forumPage = webClient.getPage(forumUrl);
                HtmlAnchor link = forumPage.getFirstByXPath(String.format("//div[contains(text(), 'Season %d - Race %d')]/preceding-sibling::a", season, race));
                logger.info("Fetching race page: " + link.getHrefAttribute());
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Buscando o tópico...");
                monitor.setProgress(40);
                HtmlPage racePage = link.click();
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }

                logger.info("Race page retrieved: " + racePage.getTitleText());
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Postando o relatório na página: " + racePage.getTitleText());
                monitor.setProgress(70);
                HtmlForm form = racePage.getFirstByXPath("//dt[contains(text(), 'Fast Reply')]/ancestor::form");
                HtmlTextArea ta = form.getTextAreaByName("post");
                ta.setText(report);
                HtmlButton button = form.getButtonByName("sd");
                logger.info("Posting report...");
                button.click();
                monitor.setNote("Relatório publicado com sucesso.");
                monitor.setProgress(100);
                logger.info("Report successfully posted.");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error posting report to URL=" + forumUrl + " for season" + season + " race " + race, e);
        }
        return false;
    }

    public synchronized boolean publishTestReport(String forumUrl, String report, Integer season, ProgressMonitor monitor) {
        try {
            monitor.setProgress(0);
            monitor.setNote("Logando no GPRO Brasil...");
            HtmlPage mainPage = login();
            if (mainPage != null) {
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Buscando o forum...");
                monitor.setProgress(20);
                logger.info("Fetching forum page: " + forumUrl);
                HtmlPage forumPage = webClient.getPage(forumUrl);
                HtmlAnchor link = forumPage.getFirstByXPath(String.format("//div[contains(text(), 'Season %d Test Reports')]/preceding-sibling::a", season));
                logger.info("Fetching test reports page: " + link.getHrefAttribute());
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Buscando o tópico...");
                monitor.setProgress(40);
                HtmlPage racePage = link.click();
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }

                logger.info("Test reports page retrieved: " + racePage.getTitleText());
                if (monitor.isCanceled()) {
                    logger.info("Publish task cancelled by the user.");
                    return false;
                }
                monitor.setNote("Postando o relatório na página: " + racePage.getTitleText());
                monitor.setProgress(70);
                HtmlForm form = racePage.getFirstByXPath("//dt[contains(text(), 'Fast Reply')]/ancestor::form");
                HtmlTextArea ta = form.getTextAreaByName("post");
                ta.setText(report);
                HtmlButton button = form.getButtonByName("sd");
                logger.info("Posting test report...");
                button.click();
                monitor.setNote("Relatório publicado com sucesso.");
                monitor.setProgress(100);
                logger.info("Report successfully posted.");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error posting test report to URL=" + forumUrl + " for season" + season, e);
        }
        return false;
    }

    public synchronized List<ForumTopic> loadTopics(TopicType type, String forumUrl, ProgressMonitor monitor, int startProgress, int finalProgress) {
        try {
            List<ForumTopic> topics = new ArrayList<ForumTopic>();
            double total = finalProgress - startProgress;
            monitor.setProgress(startProgress);
            monitor.setNote("Logando no GPRO Brasil...");
            HtmlPage mainPage = login();
            if (mainPage != null) {
                if (monitor.isCanceled()) {
                    logger.info("Load topics task cancelled by the user.");
                    return topics;
                }
                monitor.setNote("Buscando o forum...");
                monitor.setProgress(startProgress + 1);
                logger.info("Fetching forum page: " + forumUrl);
                HtmlPage forumPage = webClient.getPage(forumUrl);
                int pageCount = parsePageCount(forumPage);
                logger.info("Found " + pageCount + " pages.");

                boolean cont = parseTopicsInThePage(type, monitor, topics, forumPage);
                for (int i = 2; i <= pageCount && cont; i++) {
                    monitor.setProgress(startProgress + (int) (((total / pageCount) * i)));
                    monitor.setNote("Carregando " + type + " página " + i);
                    logger.info("Fetching forum page: " + forumUrl + i);
                    forumPage = webClient.getPage(forumUrl + i);
                    cont = parseTopicsInThePage(type, monitor, topics, forumPage);
                    if (monitor.isCanceled()) {
                        logger.info("Load topics task cancelled by the user.");
                        return topics;
                    }
                }

                monitor.setNote("Índice criado com sucesso.");
                monitor.setProgress(finalProgress);
                logger.info("Topics successfully loaded.");
                return topics;
            }
        } catch (Exception e) {
            logger.error("Error loading topics from URL=" + forumUrl, e);
        }
        return Collections.emptyList();
    }

    private boolean parseTopicsInThePage(TopicType type, ProgressMonitor monitor, List<ForumTopic> topics, HtmlPage forumPage) {
        List<?> divs = forumPage.getByXPath("//div[starts-with(text(), 'Season ')]");
        for (Object o : divs) {
            try {
                ForumTopic topic = new ForumTopic();
                topic.setType(type);
                HtmlDivision div = (HtmlDivision) o;
                String[] parts = div.getTextContent().trim().split("\\s+");
                topic.setSeason(Integer.valueOf(parts[1]));
                topic.setRace(Integer.valueOf(parts[4]));
                HtmlAnchor anchor = div.getFirstByXPath("./preceding-sibling::a");
                topic.setUrl(anchor.getHrefAttribute());
                logger.info("Topic found: " + topic);
                topics.add(topic);
            } catch (Exception e) {
                logger.error("Error parsing topic " + o.toString());
            }
            if (monitor.isCanceled()) {
                logger.info("Index creation cancelled by the user.");
                return false;
            }
        }
        return true;
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

    private boolean isAuthenticated(String group) {
        if (group == null) {
            return false;
        }
        //if the groups allowed to use this tool were changed, it's needed just change the groups allowed in this array
        final String[] GroupsAuthenticateds = {ADMINISTRADORES, "Moderadores", "Membros", "Membros Portugueses", "Férias", "Membros Brasileiros"};

        for (String groupsAuthenticated : GroupsAuthenticateds) {
            if (group.equals(groupsAuthenticated)) {
                return true;
            }
        }
        return false;
    }

    private boolean requiresAuthorization() {
//        return true;
        Calendar leaseLimit = Calendar.getInstance();
        leaseLimit.add(Calendar.DAY_OF_MONTH, -3);
        return credentials.getLastAuthentication() == null
                || leaseLimit.after(credentials.getLastAuthentication());
    }

    public synchronized boolean validateLogin(final HtmlPage pag) {
        HtmlDefinitionDescription dd = pag.getFirstByXPath("//dt[contains(text(), 'Notice')]/following-sibling::dd");
        try {
            return dd.getFirstChild().getTextContent().equals("Thank you for logging in.");
        } catch (Exception e) {
            logger.error("GPRO Brasil login validation failed.");
            return false;
        }
    }

    public synchronized HtmlPage getProfilePage(HtmlPage pag) throws ElementNotFoundException,
            IOException {
        if (pag != null) {
            HtmlAnchor anchor = pag.getFirstByXPath("//div[contains( text(), 'Welcome' )]/strong/a");
            if (anchor != null) {
                pag = anchor.click();
                return pag;
            }
        }
        return null;
    }

    public synchronized String extractGroup(HtmlPage pag) {
        HtmlDefinitionDescription dd = pag.getFirstByXPath("//dt[contains(text(), 'Group')]/following-sibling::dd");
        return dd != null ? dd.getTextContent().trim() : "Desconhecido";
    }

    public synchronized boolean joinedGMTUsers(HtmlPage pag) {
        HtmlTableDataCell td = pag.getFirstByXPath("//td[contains(text(), 'Groups Joined')]/following-sibling::td");
        return td != null && td.asText().contains("GMT User");
    }

    public synchronized boolean joinedEverest(HtmlPage pag) {
        HtmlTableDataCell td = pag.getFirstByXPath("//td[contains(text(), 'Groups Joined')]/following-sibling::td");
        return td != null && td.asText().contains("Evereste");
    }

    public synchronized HtmlPage getPage(String url) {
        try {
            if (!isLoggedIn()) {
                logger.info("Not logged in... doing it now...");
                login();
            }
            HtmlPage page = internal.getPage(url);
            if (page.getTitleText().contains("Sign in")) {
                logger.info("It seems GPRO site timed out... trying to login again...");
                login();
                page = internal.getPage(url);
            }
            if (page.getTitleText().contains("Sign in")) {
                logger.info("Unable to retrieve requested page " + url + " . Got this one instead: " + page.getUrl());
                return null;
            }
            return page;
        } catch (IOException e) {
            logger.error("Error trying to retrieve page " + url, e);
        }
        return null;
    }

    public synchronized HtmlPage postNewTopic(String forumUrl, String title, String desc, String tags, String content) {
        try {
            if (!isLoggedIn()) {
                logger.info("Not logged in... doing it now...");
                login();
            }
            String newTopicUrl = "http://s3.zetaboards.com/Grand_Prix_RO/post/?type=1&mode=1&f=" + forumUrl.substring(forumUrl.indexOf("forum/") + 6).replaceAll("/", "");
            HtmlPage page = internal.getPage(newTopicUrl);
            if (page.getTitleText().contains("Sign in")) {
                logger.info("It seems GPRO site timed out... trying to login again...");
                login();
                page = internal.getPage(newTopicUrl);
            }
            if (page.getTitleText().contains("Sign in")) {
                logger.info("Unable to post a new topic to the requested forum " + forumUrl + " . Got this page instead: " + page.getUrl());
                return null;
            }
            HtmlForm post = page.getFormByName("posting");

            HtmlInput ptitle = post.getInputByName("title");
            ptitle.setValueAttribute(title);

            HtmlInput pdesc = post.getInputByName("description");
            pdesc.setValueAttribute(desc);

            HtmlInput ptags = post.getInputByName("tags");
            ptags.setValueAttribute(tags);

            HtmlTextArea pcont = post.getTextAreaByName("post");
            pcont.setTextContent(content);

            HtmlButton submit = post.getButtonByName("post_submit");
            HtmlPage newTopic = submit.click();

            return newTopic;
        } catch (IOException e) {
            logger.error("Error trying to post a new topic to forum " + forumUrl, e);
        }
        return null;
    }

    public synchronized String findEditPostUrl(HtmlPage page) {
        //"http://gprobrasil.com/post/?mode=3
        HtmlAnchor link = page.getFirstByXPath("//a[starts-with(@href, 'http://s3.zetaboards.com/Grand_Prix_RO/?mode=3')]");
        return link != null ? link.getHrefAttribute() : null;
    }

    public synchronized HtmlPage editTopic(String topicUrl, String title, String desc, String tags, String content) {
        try {
            if (!isLoggedIn()) {
                logger.info("Not logged in... doing it now...");
                login();
            }
            HtmlPage page = internal.getPage(topicUrl);
            if (page.getTitleText().contains("Sign in")) {
                logger.info("It seems GPRO site timed out... trying to login again...");
                login();
                page = internal.getPage(topicUrl);
            }
            if (page.getTitleText().contains("Sign in")) {
                logger.info("Unable to edit topic " + topicUrl + " . Got this page instead: " + page.getUrl());
                return null;
            }
            HtmlForm post = page.getFormByName("posting");

            HtmlInput ptitle = post.getInputByName("title");
            ptitle.setValueAttribute(title);

            HtmlInput pdesc = post.getInputByName("description");
            pdesc.setValueAttribute(desc);

            HtmlInput ptags = post.getInputByName("tags");
            ptags.setValueAttribute(tags);

            HtmlTextArea pcont = post.getTextAreaByName("post");
            pcont.setTextContent(content);

            HtmlButton submit = post.getButtonByName("post_submit");
            HtmlPage newTopic = submit.click();

            return newTopic;
        } catch (IOException e) {
            logger.error("Error trying to edit topic " + topicUrl, e);
        }
        return null;
    }

    public static enum AuthorizationResult {
        SUCCESS, LOGIN_FAILED, GROUP_NOT_AUTHORIZED
    }

    public List<GproBrMember> fetchListOfMembers(ProgressMonitor monitor, int startProgress, int finalProgress) {
        double total = finalProgress - startProgress;
        monitor.setProgress(startProgress);
        monitor.setNote("Carregando lista de usuários, página 1");

        logger.info("Fetching members page: " + GPROBrasil_MEMBERS);
        HtmlPage forumPage = getPage(GPROBrasil_MEMBERS);
        int pageCount = parsePageCount(forumPage);
        logger.info("Found " + pageCount + " pages.");

        List<GproBrMember> members = new ArrayList<GproBrMember>();

        boolean cont = parseMembersInThePage(monitor, members, forumPage);
        for (int i = 2; i <= pageCount && cont; i++) {
            monitor.setProgress(startProgress + (int) (((total / pageCount) * i)));
            monitor.setNote("Carregando lista de usuários, página " + i);
            logger.info("Fetching forum page: " + GPROBrasil_MEMBERS + i);
            forumPage = getPage(GPROBrasil_MEMBERS + i);
            cont = parseMembersInThePage(monitor, members, forumPage);
            if (monitor.isCanceled()) {
                logger.info("Load members task cancelled by the user.");
                return members;
            }
        }

        monitor.setNote("Usuários carregados.");
        monitor.setProgress(finalProgress);
        logger.info(members.size() + " members successfully loaded.");
        return members;
    }

    private boolean parseMembersInThePage(ProgressMonitor monitor, List<GproBrMember> members, HtmlPage forumPage) {
        HtmlTable table = forumPage.getHtmlElementById("member_list_full");
        for (int i = 3; i < table.getRowCount() - 1; i++) {
            try {
                HtmlTableRow row = table.getRow(i);
                GproBrMember member = new GproBrMember();
                member.name = row.getCell(0).asText().trim();
                member.group = row.getCell(1).asText().trim();
                member.joinDate = DATE_PARSER.parse(row.getCell(3).asText().trim());
                member.posts = Integer.parseInt(row.getCell(4).asText().trim().replaceAll("[\\.,]", ""));
                members.add(member);
            } catch (Exception e) {
                logger.error("Error parsing member " + table.getRow(i).asText());
            }
            if (monitor.isCanceled()) {
                logger.info("Index creation cancelled by the user.");
                return false;
            }
        }
        return true;
    }

    public static class GproBrMember {

        public String name;
        public String group;
        public Date joinDate;
        public int posts;
        public boolean gmtUser;
        public int telemetries;

        @Override
        public String toString() {
            return "GproBrMember[name=" + name + ", group=" + group + ", joinDate=" + joinDate + ", posts=" + posts + ", gmtUser=" + gmtUser + "]";
        }
    }
}
