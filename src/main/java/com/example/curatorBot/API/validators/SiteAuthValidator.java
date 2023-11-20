package com.example.curatorBot.API.validators;

import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;

@Log4j2
public class SiteAuthValidator extends Validator {
    public SiteAuthValidator() {
        auth_data = new HashMap<>();
        updateAuth();
    }

    @Override
    protected void checkValidity() {
        Connection.Response response;
        try {
            response = Jsoup.connect(configParser.getProperty("site.valid_cookies"))
                    .headers(auth_data)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(1000)
                    .execute();
            if (response.statusCode() != 200) updateAuth();
        } catch (Exception exception) {
            log.error("Error checking authorization \n{}", exception.getMessage());
        }
    }

    private String formatToken(String token) {
        return String.format("Bearer %s", token);
    }

    @Override
    protected void updateAuth() {
        String wtf = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", configParser.getProperty("site.email"), configParser.getProperty("site.password")), token;
        Connection.Response response;
        try {
            response = Jsoup
                    .connect(configParser.getProperty("site.login_url"))
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .referrer(configParser.getProperty("site.index_url"))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .requestBody(wtf)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute();
            token = response.body().split("\":\"")[1].split("\"}")[0];
            String final_token = formatToken(token);
            auth_data.put("Authorization", final_token);
            log.debug("Authorization updated successfully");
        } catch (Exception exception) {
            log.error("Error updating authorization \n{}", exception.getMessage());
        }
    }
}