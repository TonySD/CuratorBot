package com.example.curatorBot.API.parsers;

import com.example.curatorBot.API.validators.SiteAuthValidator;
import com.example.curatorBot.API.dto.lessons.LessonDTO;
import com.example.curatorBot.API.dto.lessons.LevelMarks;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import com.example.curatorBot.configParser;
import org.openqa.selenium.NoSuchElementException;


import java.io.IOException;
import java.util.*;

@Log4j2
public class LessonParser {
    private SiteAuthValidator siteAuthValidator;

    public LessonParser() {
        siteAuthValidator = new SiteAuthValidator();
    }

    private int getHomeworkMaxPoint(int id) {
        String url = String.format(configParser.getProperty("site.homework_info"), id);
        Document doc = null;
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
        try {
            String url = String.format(configParser.getProperty("site.lesson_info"), id);
            System.out.println(url);
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
        System.out.println(jsonObject);
        String name = jsonObject.getString("title");

        JSONArray difficulties = jsonObject.getJSONObject("homeworks").getJSONArray("difficulties");

        System.out.println(name);
        return null;
    }

    private List<WebElement> getHomeworkLevels() {
        return null;
//        List<WebElement> levels = driver.findElements(By.xpath("//*[@id=\"root\"]/div[1]/div/div/div[2]/div[2]/div[3]/child::*"));
//        if (levels.isEmpty()) levels = driver.findElements(By.xpath("//*[@id=\"root\"]/div[1]/div/div/div[2]/div[3]/div[3]/child::*"));
//        if (levels.isEmpty()) levels =  driver.findElements(By.xpath("//*[@id=\"root\"]/div[1]/div/div/div[2]/div[3]/div[3]/div/child::*"));
//        return levels;
    }

    /* JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight)");*/
    public LessonDTO getLessonByUrl(String url) throws InterruptedException {
//        driver.get(url);
//        Sleeper.sleepRandTime(5000);
//
//        int id = Integer.parseInt(url.split("lesson/")[1]);
//        String html = driver.getPageSource();
//        Document parsedHTML = Jsoup.parse(html);
//        Element nameOfLesson = parsedHTML.selectXpath("//*[@id=\"root\"]/div[1]/div/div/div[1]/div/h1").first();
//
//        List<WebElement> levels = getHomeworkLevels();
//        List<LevelMarks> levelInfo = new ArrayList<>();
//        int currentLevel, amountLevels = levels.size();
//
//        for (currentLevel = 0; currentLevel < amountLevels; ++currentLevel) {
//            levelInfo.add(
//                    getLessonLevelInfo(levels.get(currentLevel), currentLevel)
//            );
//            levels = getHomeworkLevels();
//        }
//
//        assert nameOfLesson != null;
//        String name = nameOfLesson.text();
//
//        return new LessonDTO(id, url, name, levelInfo);
        return null;
    }
}
