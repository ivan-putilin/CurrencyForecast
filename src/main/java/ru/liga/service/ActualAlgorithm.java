package ru.liga.service;

import ru.liga.exceptions.DataException;
import ru.liga.model.Currency;
import ru.liga.model.Period;
import ru.liga.model.Rate;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.utils.FillInEmptyDate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ActualAlgorithm implements ForecastService {

    private InMemoryRatesRepository repository;

    public ActualAlgorithm(InMemoryRatesRepository repository) {
        this.repository = repository;
    }

    private void supplementRates(Currency currency, LocalDate dateFrom, List<Rate> rates) throws DataException {
        while (!Objects.equals(rates.get(rates.size() - 1).getDate(), dateFrom.minusDays(1))) {
            rates.add(getDayRateFromRates(currency, rates, dateFrom));
        }
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates, LocalDate date) throws DataException {

        BigDecimal rate;
        try {
            rate = new BigDecimal(0);
            BigDecimal rateTwoYearsAgo = rates.stream()
                    .filter(x -> x.getDate().isEqual(date.minusYears(2)))
                    .findFirst()
                    .map(Rate::getRate)
                    .get();
            BigDecimal rateThreeYearsAgo = rates.stream()
                    .filter(x -> x.getDate().isEqual(date.minusYears(3)))
                    .findFirst()
                    .map(Rate::getRate)
                    .get();
            rate = rate.add(rateTwoYearsAgo).add(rateThreeYearsAgo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataException("Не хватает данных для вычисления по данному алгоритму");
        }


        return new Rate(rates.get(rates.size() - 1).getDate().plusDays(1),
                rate,
                currency);
    }

    @Override
    public Rate oneDayForecast(Currency currency, LocalDate date) throws DataException {
        List<Rate> rates;
        try {
            rates = FillInEmptyDate.fill(currency, repository.getAllData(currency));
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        supplementRates(currency, date, rates);
        return getDayRateFromRates(currency, rates, date);

    }

    @Override
    public List<Rate> periodForecast(Currency currency, Period period) throws DataException {
        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates = null;

        try {
            rates = FillInEmptyDate.fill(currency, repository.getAllData(currency));
        } catch (IOException e) {
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        supplementRates(currency, LocalDate.now().plusDays(1), rates);

        if (period.equals(Period.WEEK)) {
            for (int i = 0; i < 7; i++) {

                Rate ratesDay = getDayRateFromRates(currency, rates, LocalDate.now().plusDays(i + 1));

                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        } else if (period.equals(Period.MONTH)) {
            for (int i = 0; i < 30; i++) {

                Rate ratesDay = getDayRateFromRates(currency, rates, LocalDate.now().plusDays(i + 1));
                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        }
        return forecasts;
    }
}
