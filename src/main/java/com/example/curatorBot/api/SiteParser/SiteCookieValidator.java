package com.example.curatorBot.api.SiteParser;

import com.example.curatorBot.api.CookieValidator;
import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.Set;

@Log4j2
public class SiteCookieValidator extends CookieValidator {
    public WebDriver driver;
    // Need for future serialization
    private Set<Cookie> driver_cookies;

    public SiteCookieValidator(WebDriver driver) {
        this.driver = driver;
        updateCookie();
    }

    @Override
    protected void checkCookieForUpdate() {
        try {
            driver.get(configParser.getProperty("site.valid_cookies"));
            if (driver.getCurrentUrl().equals(configParser.getProperty("site.index_url"))) {
                updateCookie();
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @Override
    protected void updateCookie() {
        driver.get(configParser.getProperty("site.index_url"));
        WebElement pop_login_page = driver.findElement(By.cssSelector("#root > main > header > section > button"));
        pop_login_page.click();
        WebElement email = driver.findElement(By.cssSelector("#email"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement submit = driver.findElement(By.xpath("//*[@id=\"popup-1\"]/div/section/form/div[3]/button[1]"));
        email.sendKeys(configParser.getProperty("site.email"));
        password.sendKeys(configParser.getProperty("site.password"));
        submit.click();
        driver_cookies = driver.manage().getCookies();
    }
}