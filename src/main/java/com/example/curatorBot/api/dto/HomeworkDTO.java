package com.example.curatorBot.api.dto;

import com.example.curatorBot.api.ApiParser.ApiParser;
import com.example.curatorBot.configParser;

public class HomeworkDTO {
    private final long id;
    private final String url;

    public HomeworkDTO(long id) {
        this.id = id;
        url = String.format(configParser.getProperty("api.selected_homework_url"), id);
    }

    public HomeworkDTO(String url) {
        this.url = url;
        int end_index = url.indexOf('?');
        int first_index = url.lastIndexOf('/');
        id = Integer.parseInt(url.substring(first_index + 1, end_index));
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
