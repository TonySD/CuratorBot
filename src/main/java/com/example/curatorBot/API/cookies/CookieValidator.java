package com.example.curatorBot.API.cookies;

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
