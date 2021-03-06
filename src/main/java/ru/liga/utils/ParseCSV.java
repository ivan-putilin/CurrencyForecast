package ru.liga.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.model.Currency;
import ru.liga.model.Rate;
import ru.liga.telegram.BotSettings;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ParseCSV {

    private static final Logger logger = LoggerFactory.getLogger(ParseCSV.class);

    public static List<Rate> parse(int lineNumber, List<Rate> data, String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream("src/main/resources" + fileName)), "windows-1251"))) {
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
        logger.info("Files of rates was parsed");
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
        logger.info("Files dates of full moons was parsed");
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
        logger.info("Files of rates was parsed");
        return data;
    }

    private static Currency getCurrency(String currencyTitle) {
        Currency currency;
        switch (currencyTitle) {
            case "???????????? ??????":
                currency = Currency.USD;
                break;
            case "????????":
                currency = Currency.EUR;
                break;
            case "???????????????? ????????":
                currency = Currency.TRY;
                break;
            case "?????????????????? ????????":
                currency = Currency.AMD;
                break;
            case "???????????????????? ??????":
                currency = Currency.BGN;
                break;
            default:
                currency = null;
        }
        return currency;
    }
}
