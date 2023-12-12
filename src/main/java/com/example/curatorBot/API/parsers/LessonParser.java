package com.example.curatorBot.API.parsers;

import com.example.curatorBot.API.dto.lessons.LessonLevel;
import com.example.curatorBot.API.validators.SiteAuthValidator;
import com.example.curatorBot.API.dto.lessons.LessonDTO;
import com.example.curatorBot.API.dto.lessons.LevelMarks;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.example.curatorBot.configParser;
import org.openqa.selenium.NoSuchElementException;


import java.io.IOException;
import java.util.*;

@Log4j2
public class LessonParser {
    private final SiteAuthValidator siteAuthValidator;

    public LessonParser() {
        siteAuthValidator = new SiteAuthValidator();
    }

    private JSONArray getLessonPage(int page_id) {
        String pagination = String.format("{\"page\":%d}", page_id);
        Document response;
        JSONArray lessons = null;
        try {
            response = Jsoup
                    .connect(configParser.getProperty("site.api_lessons"))
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .referrer(configParser.getProperty("site.index_url"))
                    .headers(siteAuthValidator.getAuth())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .requestBody(pagination)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute()
                    .parse();
            lessons = new JSONObject(response.body().text()).getJSONArray("lessons");
            log.trace("Successfully got page number {}", page_id);
        } catch (IOException e) {
            log.error("Failed to parse level page with id: {}\n{}", page_id, e);
        }
        return lessons;
    }
    private LessonDTO getLessonByName(String name) {
        Document response;
        JSONArray current;
        JSONObject lesson;
        int last_page = 0;
        try {
            response = Jsoup
                    .connect(configParser.getProperty("site.api_lessons"))
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .referrer(configParser.getProperty("site.index_url"))
                    .headers(siteAuthValidator.getAuth())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true)
                    .method(Connection.Method.POST)
                    .execute()
                    .parse();
            last_page = new JSONObject(response.body().text()).getJSONObject("pagination").getInt("last_page");
        } catch (IOException e) {
            log.error("Failed to get pagination in levels\n{}", e.toString());
        }
        for (int i = 1; i <= last_page; ++i) {
            current = getLessonPage(i);
            for (Object o : current) {
                lesson = (JSONObject) o;
                if (lesson.getString("title").equals(name)) {
                    log.debug("Lesson \"{}\" was found", name);
                    return getLessonInfo(lesson.getInt("id"));
                }
            }
            log.trace("Lesson \"{}\" wasn't found on page {}", name, i);
        }
        throw new NoSuchElementException(
                String.format("Can't find lesson \"%s\"", name)
        );
    }

    private LessonLevel getHWLevel(String name) {
        return switch (name) {
            case "simple" -> LessonLevel.EASY;
            case "medium" -> LessonLevel.MEDIUM;
            case "hard" -> LessonLevel.HARD;
            case "sampler" -> LessonLevel.SAMPLER;
            default -> throw new NoSuchElementException(String.format("Level not found %s", name));
        };
    }

    private int getHomeworkMaxPoint(int id) {
        String url = String.format(configParser.getProperty("site.homework_info"), id);
        Document doc;
        int max_points = 0;
        try {
            doc = Jsoup
                    .connect(url)
                    .headers(siteAuthValidator.getAuth())
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true)
                    .get();
            JSONObject jsonObject = new JSONObject(doc.body().text());
            JSONArray questions = jsonObject.getJSONArray("questions");
            for (Object object : questions) {
                JSONObject question = (JSONObject) object;
                max_points += question.getInt("points");
            }

        } catch (Exception exception) {
            log.error("Error parse homework {} \n{}", url, exception.getMessage());
        }
        return max_points;
    }


    private LessonDTO getLessonInfo(int id) {
        Document doc = null;
        String url = String.format(configParser.getProperty("site.lesson_info"), id);
        try {
            doc = Jsoup
                    .connect(url)
                    .headers(siteAuthValidator.getAuth())
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .ignoreContentType(true)
                    .get();
        } catch (IOException exception) {
            log.error("Error getting lesson info");
        }
        if (doc == null) {
            throw new NoSuchElementException("Lesson was not found...");
        }
        JSONObject jsonObject = new JSONObject(doc.body().text());

        String name = jsonObject.getString("title");
        List<LevelMarks> levelMarks = new ArrayList<>();
        JSONArray difficulties = jsonObject.getJSONObject("homeworks").getJSONArray("difficulties");

        for (Object o : difficulties) {
            JSONObject current = (JSONObject) o;
            levelMarks.add(new LevelMarks(
                    getHWLevel(current.getString("difficulty")),
                    getHomeworkMaxPoint(current.getInt("id"))
            ));
        }

        return new LessonDTO(
                id,
                url,
                name,
                levelMarks
        );
    }
}
