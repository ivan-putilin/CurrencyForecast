package ru.liga.service;

import ru.liga.model.Rate;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.model.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ForecastAverage7DaysService implements ForecastService {

    private InMemoryRatesRepository repository;

    public ForecastAverage7DaysService(InMemoryRatesRepository repository) {
        this.repository = repository;
    }

    private BigDecimal average(List<BigDecimal> bigDecimals, RoundingMode roundingMode) {
        BigDecimal sum = bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(bigDecimals.size()), roundingMode);
    }

    private void supplementRates(Currency currency, List<Rate> rates) {
        while (!Objects.equals(rates.get(rates.size() - 1).getDate(), LocalDate.now())) {
            rates.add(getDayRateFromRates(currency, rates));
        }
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates) {
        List<BigDecimal> listRateFromData = rates
                .stream()
                .sorted(Comparator.comparing(Rate::getDate)
                        .reversed())
                .limit(7)
                .map(Rate::getRate)
                .collect(Collectors.toList());
        return new Rate(rates.get(rates.size() - 1).getDate().plusDays(1),
                average(listRateFromData, RoundingMode.FLOOR),
                currency);
    }


    @Override
    public Rate tomorrowForecast(Currency currency) {

        List<Rate> rates = null;
        try {
            rates = repository.getData(7, currency);
        } catch (IOException e) {
            System.out.println("Ошибка чтения данных из файла валюты " + currency + "!");
            e.printStackTrace();
        }

        supplementRates(currency, rates);
        return getDayRateFromRates(currency, rates);
    }

    @Override
    public List<Rate> weekForecast(Currency currency) {
        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates = null;
        try {
            rates = repository.getData(7, currency);
        } catch (IOException e) {
            System.out.println("Ошибка чтения данных из файла валюты " + currency + "!");
            e.printStackTrace();
        }

        supplementRates(currency,rates);

        for (int i = 0; i < 7; i++) {

            Rate ratesDay = getDayRateFromRates(currency, rates);

            rates.add(ratesDay);
            forecasts.add(ratesDay);
        }

        return forecasts;
    }
}
