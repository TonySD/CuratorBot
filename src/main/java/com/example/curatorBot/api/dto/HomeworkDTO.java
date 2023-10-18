package com.example.curatorBot.api.dto;

import com.example.curatorBot.api.ApiParser;
import com.example.curatorBot.configParser;

public class HomeworkDTO {
    private ParsedHomework hw_info = null;
    private final long id;
    private final String url;

    public HomeworkDTO(long id) {
        this.id = id;
        url = String.format(configParser.getProperty("api.selected_homework_url"), id);
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public ParsedHomework getHw_info() {
        if (hw_info == null) hw_info = ApiParser.getSelectedHW(url).orElseThrow();
        return hw_info;
    }
}
