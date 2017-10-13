package org.gproman.scrapper;

import java.io.IOException;

import org.gproman.model.UserConfiguration;
import org.gproman.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class GPROUtil {

    private static final Logger   logger    = LoggerFactory.getLogger(GPROUtil.class);
    private static final String   GPRO_MAIN_SUFFIX = "/gb/gpro.asp";

    private final UserCredentials credentials;
    private UserConfiguration      conf;
    private WebConnection          webClient;
    private WebConnection          internal;
    private boolean               loggedIn;

    public GPROUtil(final UserCredentials credentials, UserConfiguration configuration) {
        this.credentials = credentials;
        this.conf = configuration;
        this.loggedIn = false;
    }

    public synchronized WebConnection getWebConnection() {
        return webClient;
    }

    public synchronized HtmlPage login() throws IOException {
        logger.info("Logging in into GPRO");
        this.webClient = new WebConnection(conf);
        final HtmlPage page = webClient.getPage(conf.getGproUrl() + GPRO_MAIN_SUFFIX);

        logger.info("Retrieving login form...");
        final HtmlForm form = page.getHtmlElementById("Form1");
        final HtmlSubmitInput button = form.getInputByName("LogonFake");
        final HtmlTextInput login = form.getInputByName("textLogin");
        final HtmlPasswordInput password = form.getInputByName("textPassword");

        // Change the value of the text field
        logger.info("Setting login credentials...");
        login.setValueAttribute(credentials.getGproUser());
        password.setValueAttribute(credentials.getGproPassword());
        logger.info("Submitting...");
        // Now submit the form by clicking the button and get back the second page.
        HtmlPage office = button.click();

        if (office.asText().contains("Invalid credentials! Please try again.")) {
            // login failed
            this.loggedIn = false;
            return null;
        }
        if (!office.getUrl().getPath().startsWith("/gb")) {
            logger.info("Switching to english...");
            HtmlAnchor english = office.getAnchorByHref("/gb/gpro.asp");
            office = english.click();
        }
        this.loggedIn = true;
        this.internal = webClient.clone();
        return office;
    }

    public synchronized boolean isLoggedIn() {
        return this.loggedIn;
    }

    public synchronized void logout() throws IOException {
        this.loggedIn = false;
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
}
