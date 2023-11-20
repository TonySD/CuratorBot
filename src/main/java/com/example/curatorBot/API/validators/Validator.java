package com.example.curatorBot.API.validators;

import java.util.Map;

public abstract class Validator {
    protected Map<String, String> auth_data;

    protected abstract void checkValidity();
    protected abstract void updateAuth();
    public Map<String, String> getAuth() {
        checkValidity();
        return auth_data;
    }
}
