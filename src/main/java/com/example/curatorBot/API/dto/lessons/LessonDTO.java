package com.example.curatorBot.API.dto.lessons;

import java.util.List;

public record LessonDTO(int id, String url, String name, List<LevelMarks> levels) {

}
