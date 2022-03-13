package ru.liga.utils;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
        public static final DateTimeFormatter PARSE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        public static final DateTimeFormatter PRINT_FORMATTER = DateTimeFormatter.ofPattern("E dd.MM.yyyy");
}
