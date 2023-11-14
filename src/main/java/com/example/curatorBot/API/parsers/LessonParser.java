package com.example.curatorBot.API.parsers;

import com.example.curatorBot.API.cookies.SiteCookieValidator;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@Log4j2
public class LessonParser {
    private WebDriver driver;
    private SiteCookieValidator siteCookieValidator;


    public LessonParser() {
        driver = new ChromeDriver();
        siteCookieValidator = new SiteCookieValidator(driver);
    }
}
