package com.example.curatorBot.API.dto.homeworks;

public record HomeworkPages(int amount_of_HWS, int HWs_on_page) {
    @Override
    public String toString() {
        return "HwPages{" +
                "amount_of_HWS=" + amount_of_HWS +
                ", HWs_on_page=" + HWs_on_page +
                '}';
    }
}
