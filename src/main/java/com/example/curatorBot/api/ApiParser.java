package com.example.curatorBot.api;

import com.example.curatorBot.api.dto.HwPages;
import com.example.curatorBot.api.dto.PassedHW;
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

    public static Optional<PassedHW> getSelectedHW(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .cookies(cookies)
                    .get();
            String student_name = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(1) > input")
                    .first()
                    .attr("value");
            String lesson_name = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(2) > div:nth-child(1) > small > b")
                    .first().text();
            String[] mark = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(6) > div:nth-child(1)")
                    .first().text()
                    .split(": ")[1]
                    .split("/");
            int student_mark = Integer.parseInt(mark[0]);
            int max_mark = Integer.parseInt(mark[1]);

            return Optional.of(new PassedHW(
                    student_name,
                    lesson_name,
                    student_mark,
                    max_mark
            ));
        } catch (IOException ignored) {}

        return Optional.empty();
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
