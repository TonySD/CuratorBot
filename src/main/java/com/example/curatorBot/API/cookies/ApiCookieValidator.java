package com.example.curatorBot.API.cookies;

import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

@Log4j2
public class ApiCookieValidator extends CookieValidator {
    public ApiCookieValidator() {
        updateCookie();
    }

    public void checkCookieForUpdate() {
        try {
            Connection.Response res = Jsoup
                    .connect(configParser.getProperty("api.index_url"))
                    .cookies(cookies)
                    .followRedirects(true)
                    .execute();
            if (res.url().toString().equals(configParser.getProperty("api.login_url"))) {
                log.info("Cookies was invalid - updating");
                updateCookie(); // If redirects to login page - cookie invalidated
            } else log.trace("Cookies are valid");
        } catch (IOException ignored) {}
    }


    protected void updateCookie() {
        Connection.Response res;
        try {
            res = Jsoup
                    .connect(configParser.getProperty("api.login_url"))
                    .data("email", configParser.getProperty("api.email"), "password", configParser.getProperty("api.password"))
                    .method(Connection.Method.POST)
                    .execute();
            cookies = res.cookies();
            log.info("Cookies was updated");
        } catch (IOException exception) {
            log.warn(exception.getMessage());
        }
    }
}
