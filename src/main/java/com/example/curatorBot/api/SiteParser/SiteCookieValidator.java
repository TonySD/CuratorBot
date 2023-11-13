package com.example.curatorBot.api.SiteParser;

import com.example.curatorBot.api.CookieValidator;
import com.example.curatorBot.api.dto.ParsedHomework;
import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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
            WebDriver driver = new ChromeDriver();
            driver.get(configParser.getProperty("site.valid_cookies"));
            for (String cookie : cookies.keySet()) {
                driver.manage().addCookie(new Cookie(cookie, cookies.get(cookie)));
            }

            JavascriptExecutor executor = (JavascriptExecutor) driver;
            String html = executor.executeScript("return document.getElementsByTagName('html')[0].innerHTML").toString();
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