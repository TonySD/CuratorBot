package com.example.curatorBot.api;

import com.example.curatorBot.configParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class ApiParser {
    private static int homeworks = 0;
    private static Map<String, String> cookies;

    static {
        updateCookie();
    }

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect(configParser.getProperty("api.homeworks_url"))
                .cookies(cookies)
                .get();
        System.out.println();
    }
    public static void parseHW() {

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

    private static boolean checkHWUpdate() {
        Document doc;
        try {
            doc = Jsoup.connect(configParser.getProperty("api.homeworks_url"))
                    .cookies(cookies)
                    .get();
            doc.select("#example2_info");
        } catch (IOException exception) {
            updateCookie();
        }

        return false;
    }
}
