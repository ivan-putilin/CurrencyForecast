package ru.liga.utils;

import ru.liga.model.Currency;
import ru.liga.model.Rate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ParseCSV {
    public static List<Rate> parse(int lineNumber, List<Rate> data, String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ParseCSV.class.getResourceAsStream(fileName)))) {
            int count = 0;
            String[] line;

            if (reader.ready()) {
                reader.readLine();
            }
            while (reader.ready() && count < lineNumber) {
                line = reader.readLine().split(";");
                Rate rate = new Rate(
                        LocalDate.parse(line[0], DateTimeUtil.PARSE_FORMATTER),
                        BigDecimal.valueOf(Double.parseDouble(line[1].replace(',', '.'))),
                        getCurrency(line[2])
                );
                data.add(rate);
                count++;
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
            default:
                currency = null;
        }
        return currency;
    }
}
