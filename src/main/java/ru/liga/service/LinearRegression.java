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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LinearRegression implements ForecastService {

    private static final Logger logger = LoggerFactory.getLogger(LinearRegression.class);

    private final InMemoryRatesRepository repository;

    public LinearRegression(InMemoryRatesRepository repository) {
        this.repository = repository;
    }

    private void supplementRates(Currency currency, LocalDate dateFrom, List<Rate> rates) {
        logger.debug("Rates calculated before the required date for currency: {}, for date: {}", currency, dateFrom.toString());
        while (!Objects.equals(rates.get(rates.size() - 1).getDate(), dateFrom.minusDays(1))) {
            rates.add(getDayRateFromRates(currency, rates));
        }
        logger.info("Rates calculated before the required date");
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates) {
        logger.debug("Forecasting for the day based on the list of forecasts: currency: {}", currency);

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

        LinearRegressionAlgorithm algorithm = new LinearRegressionAlgorithm(daysArr, arrRateFromDataDouble);

        Rate rate = new Rate(rates.get(rates.size() - 1).getDate().plusDays(1),
                BigDecimal.valueOf(algorithm.predict(1.0)),
                currency);


        logger.debug("Forecast for the day based on the list of forecasts: {}", rate);
        return rate;
    }

    @Override
    public Rate oneDayForecast(Currency currency, LocalDate date) throws DataException {
        logger.debug("Single date forecast: currency: {} date: {}", currency, date.toString());

        List<Rate> rates;
        try {
            rates = FillInEmptyDate.fill(currency, repository.getData(30, currency));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        supplementRates(currency, date, rates);

        Rate rate = getDayRateFromRates(currency, rates);
        logger.info("One day forecast successfully built");
        logger.debug("One day forecast:{}", rate);
        return rate;

    }

    @Override
    public List<Rate> periodForecast(Currency currency, Period period) throws DataException {
        logger.debug("Period forecasting:{} {}", currency, period.toString());

        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates;

        try {
            rates = FillInEmptyDate.fill(currency, repository.getData(30, currency));
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
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

        logger.info("Period forecast successfully built");
        logger.debug("Period forecast:{}", forecasts);
        return forecasts;
    }
}
