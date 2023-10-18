package com.example.curatorBot.api;

import com.example.curatorBot.api.dto.HomeworkPages;
import com.example.curatorBot.api.dto.ParsedHomework;
import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Log4j
public class ApiParser {
    private static int homeworks;


    static {
        getHWInfo();
    }

    public static void main(String[] args) {
        System.out.println(getHWInfo().orElseThrow());
    }



    public static Optional<ParsedHomework> getSelectedHW(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .cookies(CookieValidator.getCookies())
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
            log.debug("Homework was parsed successfully");

            return Optional.of(new ParsedHomework(
                    student_name,
                    lesson_name,
                    student_mark,
                    max_mark
            ));
        } catch (IOException | NullPointerException exception) {
            log.error(exception.getMessage());
        }

        return Optional.empty();
    }

    private static Optional<HomeworkPages> getHWInfo() {
        Document doc;
        try {
            doc = Jsoup.connect(configParser.getProperty("api.homeworks_url"))
                    .cookies(CookieValidator.getCookies())
                    .get();
            Element data = doc.select("#example2_info").first();
            String[] result = data.select("div").text().split(" ");
            result[4] = result[4].substring(0, result[4].length() - 1);
            homeworks = Integer.parseInt(result[6]);
            return Optional.of(new HomeworkPages(
                    homeworks,
                    Integer.parseInt(result[4])
            ));
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();

    }

    private static boolean checkNewHWs() {
        Optional<HomeworkPages> result = getHWInfo();
        if (result.isEmpty()) return false;
        return result.get().amount_of_HWS() == homeworks;
    }
}
