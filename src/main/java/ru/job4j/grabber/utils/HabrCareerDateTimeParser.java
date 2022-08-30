package ru.job4j.grabber.utils;

import ru.job4j.grabber.HabrCareerParse;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        if (!parse.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException();
        }
        ZonedDateTime zdt = ZonedDateTime.parse(parse);
        return zdt.toLocalDateTime();
    }
}
