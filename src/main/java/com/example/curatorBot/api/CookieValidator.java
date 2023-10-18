package com.example.curatorBot.api;

import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

@Log4j
public class CookieValidator {
    private static Map<String, String> cookies;

    static {
        updateCookie();
    }

    public static Map<String, String> getCookies() {
        checkCookieForUpdate();
        return cookies;
    }
    public static void checkCookieForUpdate() {
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


    private static void updateCookie() {
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
