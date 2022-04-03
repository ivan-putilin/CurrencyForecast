package ru.liga.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ActualAlgorithm.class);

    private final InMemoryRatesRepository repository;

    public ActualAlgorithm(InMemoryRatesRepository repository) {
        this.repository = repository;
    }

    private void supplementRates(Currency currency, LocalDate dateFrom, List<Rate> rates) throws DataException {
        logger.debug("Rates calculated before the required date for currency: {}, for date: {}", currency, dateFrom.toString());
        while (!Objects.equals(rates.get(rates.size() - 1).getDate(), dateFrom.minusDays(1))) {
            rates.add(getDayRateFromRates(currency, rates, dateFrom));
        }
        logger.info("Rates calculated before the required date");
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates, LocalDate date) throws DataException {
        logger.debug("Forecast for the day based on the list of forecasts: currency: {} date: {}", currency, date.toString());
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
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Не хватает данных для вычисления по данному алгоритму");
        }


        return new Rate(rates.get(rates.size() - 1).getDate().plusDays(1),
                rate,
                currency);
    }

    @Override
    public Rate oneDayForecast(Currency currency, LocalDate date) throws DataException {
        logger.debug("Single date forecast: currency: {} date: {}", currency, date.toString());

        List<Rate> rates;
        try {
            rates = FillInEmptyDate.fill(currency, repository.getAllData(currency));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        supplementRates(currency, date, rates);
        Rate rate = getDayRateFromRates(currency, rates, date);
        logger.info("One day forecast successfully built");
        logger.debug("One day forecast:{}", rate);
        return rate;

    }

    @Override
    public List<Rate> periodForecast(Currency currency, Period period) throws DataException {
        logger.debug("Period forecast:{} {}", currency, period.toString());

        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates;

        try {
            rates = FillInEmptyDate.fill(currency, repository.getAllData(currency));
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
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

        logger.info("Period forecast successfully built");
        logger.debug("Period forecast:{}", forecasts);
        return forecasts;
    }
}
