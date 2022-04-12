package ru.liga.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.model.Currency;
import ru.liga.model.Rate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для заполнения прогнозов в те даты, которые он не менялся
 */

public class FillInEmptyDate {

    private static final Logger logger = LoggerFactory.getLogger(FillInEmptyDate.class);

    public static List<Rate> fill(Currency currency, List<Rate> rates) {
        logger.debug("Filling days without rates for currency: {}", currency);
        List<Rate> ratesReversed = rates.stream()
                .sorted(Comparator
                        .comparing(Rate::getDate))
                .collect(Collectors
                        .toList());
        List<Rate> filled = new ArrayList<>();
        filled.add(ratesReversed.get(0));

        for (int i = 0; i < ratesReversed.size(); i++) {
            while (i != (ratesReversed.size() - 1) && !ratesReversed.get(i + 1).getDate().minusDays(1).equals(filled.get(filled.size() - 1).getDate())) {
                Rate rate = new Rate(
                        filled.get(filled.size() - 1).getDate().plusDays(1),
                        filled.get(filled.size() - 1).getRate(),
                        currency
                );
                filled.add(rate);
            }
            if(i != (ratesReversed.size() - 1)){
                filled.add(ratesReversed.get(i + 1));
            }
        }

        logger.info("Days without rates are filled");

        return filled;
    }
}
