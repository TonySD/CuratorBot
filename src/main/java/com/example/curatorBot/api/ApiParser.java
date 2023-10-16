package com.example.curatorBot.api;

import com.example.curatorBot.api.dto.HwPages;
import com.example.curatorBot.configParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ApiParser {
    private static int homeworks = 0;
    private static Map<String, String> cookies;

    static {
        updateCookie();
    }

    public static void main(String[] args) {

    }
    public static void parseHW() {

    }

    public static void checkCookieForUpdate() {
        try {
            Connection.Response res = Jsoup
                    .connect(configParser.getProperty("api.index_url"))
                    .cookies(cookies)
                    .followRedirects(true)
                    .execute();
            if (res.url().toString().equals(configParser.getProperty("api.login_url"))) updateCookie(); // If redirects to login page - cookie invalidated
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
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static long getHWNumber() {
        checkCookieForUpdate();

        return 0;
    }

    private static Optional<HwPages> getHWInfo() {
        checkCookieForUpdate();
        Document doc;
        try {
            doc = Jsoup.connect(configParser.getProperty("api.homeworks_url"))
                    .cookies(cookies)
                    .get();
            Element data = doc.select("#example2_info").first();
            String[] result = data.select("div").text().split(" ");
            result[4] = result[4].substring(0, result[4].length() - 1);
            return Optional.of(new HwPages(
                    Integer.parseInt(result[6]),
                    Integer.parseInt(result[4])
            ));
        } catch (IOException ignored) {
            System.err.println("Something went wrong");
        }
        return Optional.empty();

    }
}
