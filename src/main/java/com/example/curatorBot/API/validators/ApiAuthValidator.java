package com.example.curatorBot.API.validators;

import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

@Log4j2
public class ApiAuthValidator extends Validator {
    public ApiAuthValidator() {
        updateAuth();
    }

    public void checkValidity() {
        try {
            Connection.Response res = Jsoup
                    .connect(configParser.getProperty("api.index_url"))
                    .cookies(auth_data)
                    .followRedirects(true)
                    .execute();
            if (res.url().toString().equals(configParser.getProperty("api.login_url"))) {
                log.info("Cookies was invalid - updating");
                updateAuth(); // If redirects to login page - cookie invalidated
            } else log.trace("Cookies are valid");
        } catch (IOException ignored) {}
    }


    protected void updateAuth() {
        Connection.Response res;
        try {
            res = Jsoup
                    .connect(configParser.getProperty("api.login_url"))
                    .data("email", configParser.getProperty("api.email"), "password", configParser.getProperty("api.password"))
                    .method(Connection.Method.POST)
                    .execute();
            auth_data = res.cookies();
            log.info("Cookies was updated");
        } catch (IOException exception) {
            log.warn(exception.getMessage());
        }
    }
}
