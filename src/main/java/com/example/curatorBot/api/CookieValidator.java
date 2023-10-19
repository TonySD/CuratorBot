package com.example.curatorBot.api;

import com.example.curatorBot.configParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public abstract class CookieValidator {
    protected Map<String, String> cookies;

    protected abstract void checkCookieForUpdate();
    protected abstract void updateCookie();
    public Map<String, String> getCookies() {
        checkCookieForUpdate();
        return cookies;
    }
}
