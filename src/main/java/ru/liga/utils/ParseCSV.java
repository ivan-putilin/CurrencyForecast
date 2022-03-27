package ru.liga.utils;

import ru.liga.model.Currency;
import ru.liga.model.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ParseCSV {
    public static List<Rate> parse(int lineNumber, List<Rate> data, String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ParseCSV.class.getResourceAsStream(fileName)), "windows-1251"))) {
            int count = 0;
            String[] line;

            if (reader.ready()) {
                reader.readLine();
            }
            while (reader.ready() && count < lineNumber) {
                line = reader.readLine().split(";");

                Rate rate = new Rate(
                        LocalDate.parse(line[1], DateTimeUtil.PARSE_FORMATTER),
                        new BigDecimal(line[2].replace(',', '.').replace("\"", "")),
                        getCurrency(line[3])
                );
                data.add(rate);

                count++;
            }
        }
        return data;
    }

    public static List<LocalDate> parseDatesOfMoon(List<LocalDate> data, String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ParseCSV.class.getResourceAsStream(fileName)), "windows-1251"))) {
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                data.add(LocalDate.parse(line, DateTimeUtil.PARSE_FORMATTER));
            }
        }
        return data;
    }

    public static List<Rate> parseAll(List<Rate> data, String fileName) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ParseCSV.class.getResourceAsStream(fileName)), "windows-1251"))) {
                String[] line;

                if (reader.ready()) {
                    reader.readLine();
                }
                while (reader.ready()) {
                    line = reader.readLine().split(";");

                    Rate rate = new Rate(
                            LocalDate.parse(line[1], DateTimeUtil.PARSE_FORMATTER),
                            new BigDecimal(line[2].replace(',', '.').replace("\"", "")),
                            getCurrency(line[3])
                    );
                    data.add(rate);
                }
            }
            return data;
    }

    private static Currency getCurrency(String currencyTitle) {
        Currency currency;
        switch (currencyTitle) {
            case "Доллар США":
                currency = Currency.USD;
                break;
            case "ЕВРО":
                currency = Currency.EUR;
                break;
            case "Турецкая лира":
                currency = Currency.TRY;
                break;
            case "Армянский драм":
                currency = Currency.AMD;
                break;
            case "Болгарский лев":
                currency = Currency.BGN;
                break;
            default:
                currency = null;
        }
        return currency;
    }
}
