package com.example.curatorBot.API.dto.homeworks;

public record ParsedHomework(String student_name, String lesson, int student_mark, int max_mark) {
    @Override
    public String toString() {
        return "PassedHW{" +
                "student_name='" + student_name + '\'' +
                ", lesson='" + lesson + '\'' +
                ", student_mark=" + student_mark +
                ", max_mark=" + max_mark +
                '}';
    }
}
