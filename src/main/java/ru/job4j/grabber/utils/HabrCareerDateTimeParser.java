package ru.job4j.grabber.utils;

import ru.job4j.grabber.HabrCareerParse;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        if (!parse.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException();
        }
        String[] dateTimeArray = parse.split("T");
        String[] dateArray = dateTimeArray[0].split("-");
        String[] timeArray = dateTimeArray[1].split("[:\\+]");
        return LocalDateTime.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]),
                Integer.parseInt(dateArray[2]), Integer.parseInt(timeArray[0]),
                Integer.parseInt(timeArray[1]), Integer.parseInt(timeArray[2]));
    }
}
