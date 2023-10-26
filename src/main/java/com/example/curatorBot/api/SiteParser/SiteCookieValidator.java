package com.example.curatorBot.api.SiteParser;

import com.example.curatorBot.api.CookieValidator;
import com.example.curatorBot.api.dto.ParsedHomework;
import com.example.curatorBot.configParser;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

@Log4j2
public class SiteCookieValidator extends CookieValidator {
    public SiteCookieValidator() {
        updateCookie();
    }

    @Override
    protected void checkCookieForUpdate() {
        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.setJavaScriptTimeout(15000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            HtmlPage myPage = webClient.getPage(configParser.getProperty("site.valid_cookies"));
            log.trace("Created webclient and got page");
            Document doc = Jsoup.parse(myPage.getPage().toString());
            System.out.println(doc.body());
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void updateCookie() {
        Connection.Response res;
        try {
            res = Jsoup
                    .connect(configParser.getProperty("site.login_url"))
                    .data("email", configParser.getProperty("site.email"), "password", configParser.getProperty("site.password"))
                    .method(Connection.Method.POST)
                    .execute();
            cookies = res.cookies();
            log.info("Cookies was updated");
        } catch (IOException exception) {
            log.warn(exception.getMessage());
        }
    }
}