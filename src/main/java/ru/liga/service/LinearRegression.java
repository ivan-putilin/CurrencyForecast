package ru.liga.service;

import ru.liga.model.Currency;
import ru.liga.model.Period;
import ru.liga.model.Rate;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.utils.FillInEmptyDate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LinearRegression implements ForecastService {

    private InMemoryRatesRepository repository;
    private LinearRegressionAlgorithm algorithm;

    public LinearRegression(InMemoryRatesRepository repository) {
        this.repository = repository;
    }

    private void supplementRates(Currency currency, LocalDate dateFrom, List<Rate> rates) {
        while (!Objects.equals(rates.get(rates.size() - 1).getDate(), dateFrom.minusDays(1))) {
            rates.add(getDayRateFromRates(currency, rates));
        }
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates) {
        List<BigDecimal> listRateFromData = rates
                .stream()
                .sorted(Comparator.comparing(Rate::getDate)
                        .reversed())
                .limit(30)
                .map(Rate::getRate)
                .collect(Collectors.toList());

        List<Double> listRateFromDataDouble = listRateFromData.stream().map(BigDecimal::doubleValue).collect(Collectors.toList());
        double[] arrRateFromDataDouble = listRateFromDataDouble.stream().mapToDouble(Double::doubleValue).toArray();
        double[] daysArr = new double[30];

        for (int i = 0; i < 30; i++) {
            daysArr[i] = i;
        }

        algorithm = new LinearRegressionAlgorithm(daysArr, arrRateFromDataDouble);


        return new Rate(rates.get(rates.size() - 1).getDate().plusDays(1),
                BigDecimal.valueOf(algorithm.predict(1.0)),
                currency);
    }

    @Override
    public Rate oneDayForecast(Currency currency, LocalDate date) {
        List<Rate> rates = null;
        try {
            rates = FillInEmptyDate.fill(currency, repository.getData(30, currency));
        } catch (IOException e) {
            System.out.println("Ошибка чтения данных из файла валюты " + currency + "!");
            e.printStackTrace();
        }

        supplementRates(currency, date, rates);
        return getDayRateFromRates(currency, rates);

    }

    @Override
    public List<Rate> periodForecast(Currency currency, Period period) {
        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates = null;

        try {
            rates = FillInEmptyDate.fill(currency, repository.getData(30, currency));
        } catch (IOException e) {
            System.out.println("Ошибка чтения данных из файла валюты " + currency + "!");
            e.printStackTrace();
        }

        supplementRates(currency, LocalDate.now().plusDays(1), rates);

        if (period.equals(Period.WEEK)) {
            for (int i = 0; i < 7; i++) {

                Rate ratesDay = getDayRateFromRates(currency, rates);

                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        } else if (period.equals(Period.MONTH)) {
            for (int i = 0; i < 30; i++) {

                Rate ratesDay = getDayRateFromRates(currency, rates);
                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        }
        return forecasts;
    }
}
