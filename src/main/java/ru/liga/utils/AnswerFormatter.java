package ru.liga.utils;

import ru.liga.model.Rate;

import java.util.List;

public class AnswerFormatter {
    public static String printDayRate(Rate rate) {
        return String.format("%s - %s", rate.getDate().format(DateTimeUtil.PRINT_FORMATTER), String.format("%.2f", rate.getRate()));
    }
}
