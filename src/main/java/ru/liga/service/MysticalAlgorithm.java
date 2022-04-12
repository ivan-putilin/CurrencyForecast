package ru.liga.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.exceptions.DataException;
import ru.liga.model.Currency;
import ru.liga.model.Period;
import ru.liga.model.Rate;
import ru.liga.repository.InMemoryDatesOfFullMoonRepository;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.utils.FillInEmptyDate;
import ru.liga.utils.ParseCSV;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class MysticalAlgorithm implements ForecastService {

    private static final Logger logger = LoggerFactory.getLogger(MysticalAlgorithm.class);

    private final InMemoryRatesRepository repositoryOfRates;
    private final InMemoryDatesOfFullMoonRepository repositoryOfFullMoonDates;
    private final String fileOfMoonDates = "/List_of_Full_Moon.csv";

    public MysticalAlgorithm(InMemoryRatesRepository repositoryOfRates) {
        this.repositoryOfRates = repositoryOfRates;
        this.repositoryOfFullMoonDates = new InMemoryDatesOfFullMoonRepository(fileOfMoonDates);
    }

    private void supplementRates(Currency currency, LocalDate dateFrom, List<Rate> rates, List<LocalDate> datesOfMoon) throws DataException {
        logger.debug("Rates calculated before the required date for currency: {}, for date: {}", currency, dateFrom.toString());

        if (rates.stream()
                .noneMatch(r -> r.getDate().equals(dateFrom))) {
            rates.add(getDayRateFromRates(currency, rates, rates.get(rates.size() - 1).getDate(), datesOfMoon));
        }

        while (rates.stream()
                .noneMatch(r -> r.getDate().equals(dateFrom))) {
            rates.add(randomRateFromBeforeRate(rates.get(rates.size() - 1).getDate(), rates, currency));
        }
        logger.info("Rates calculated before the required date");
    }

    private BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    private Rate getDayRateFromRates(Currency currency, List<Rate> rates, LocalDate date, List<LocalDate> datesOfMoon) throws DataException {
        logger.debug("Forecast for the day based on the list of forecasts: currency: {} date: {}", currency, date.toString());

        BigDecimal sum = new BigDecimal(0);
        BigDecimal firstMoonDate;
        BigDecimal secondMoonDate;
        BigDecimal thirdMoonDate;

        for (int i = 0; i < datesOfMoon.size(); i++) {
            LocalDate currentDate = datesOfMoon.get(i);
            if (currentDate.equals(date) || currentDate.isBefore(date)) {
                firstMoonDate = findRateFromListOfRatesByDate(currentDate, rates, currency, datesOfMoon);
                secondMoonDate = findRateFromListOfRatesByDate(datesOfMoon.get(i + 1), rates, currency, datesOfMoon);
                thirdMoonDate = findRateFromListOfRatesByDate(datesOfMoon.get(i + 2), rates, currency, datesOfMoon);

                sum = sum.add(firstMoonDate).add(secondMoonDate).add(thirdMoonDate);
                break;
            }
        }

        Rate rate = new Rate(date,
                sum.divide(new BigDecimal(3), RoundingMode.HALF_UP),
                currency);

        logger.debug("Forecast for the day based on the list of forecasts: {}", rate);
        return rate;

    }

    private BigDecimal findRateFromListOfRatesByDate(LocalDate date, List<Rate> rates, Currency currency, List<LocalDate> datesOfMoon) throws DataException {
        logger.debug("Find rate from list of rates by date: currency: {} date: {}", currency, date.toString());

        BigDecimal rate;

        if (rates.stream()
                .noneMatch(r -> r.getDate().equals(date))) {
            supplementRates(currency, date, rates, datesOfMoon);
        }

        try {
            rate = rates.stream()
                    .filter(r -> r.getDate().equals(date))
                    .findFirst()
                    .get()
                    .getRate();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Не хватает данных для вычисления по данному алгоритму");
        }
        logger.debug("Rate from list of rates by date: currency: {} date: {}", currency, rate);
        return rate;
    }

    @Override
    public Rate oneDayForecast(Currency currency, LocalDate date) throws DataException {
        logger.debug("Single date forecast: currency: {} date: {}", currency, date.toString());

        List<Rate> rates;
        List<LocalDate> datesOfMoon;
        try {
            rates = FillInEmptyDate.fill(currency, repositoryOfRates.getAllData(currency));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        try {
            datesOfMoon = ParseCSV.parseDatesOfMoon(repositoryOfFullMoonDates.getData(), fileOfMoonDates);
            datesOfMoon.sort(Comparator.comparing(LocalDate::getChronology));
            Collections.reverse(datesOfMoon);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения файла с датами полнолуний!");
        }

        Rate rate = getDayRateFromRates(currency, rates, date, datesOfMoon);
        logger.info("One day forecast successfully built");
        logger.debug("One day forecast:{}", rate);
        return rate;

    }

    @Override
    public List<Rate> periodForecast(Currency currency, Period period) throws DataException {
        logger.debug("Period forecast:{} {}", currency, period.toString());

        List<Rate> forecasts = new ArrayList<>();
        List<Rate> rates;
        List<LocalDate> datesOfMoon;

        try {
            rates = FillInEmptyDate.fill(currency, repositoryOfRates.getAllData(currency));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения данных из файла валюты " + currency + "!");
        }

        try {
            datesOfMoon = ParseCSV.parseDatesOfMoon(repositoryOfFullMoonDates.getData(), fileOfMoonDates);
            datesOfMoon.sort(Comparator.reverseOrder());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка чтения файла с датами полнолуний!");
        }

        if (period.equals(Period.WEEK)) {
            for (int i = 0; i < 7; i++) {
                Rate ratesDay;

                ratesDay = randomRateFromBeforeRate(LocalDate.now().plusDays(i), rates, currency);

                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        } else if (period.equals(Period.MONTH)) {
            for (int i = 0; i < 30; i++) {

                Rate ratesDay;

                ratesDay = randomRateFromBeforeRate(LocalDate.now().plusDays(i), rates, currency);

                rates.add(ratesDay);
                forecasts.add(ratesDay);
            }
        }
        logger.info("Period forecast successfully built");
        logger.debug("Period forecast:{}", forecasts);
        return forecasts;
    }

    private Rate randomRateFromBeforeRate(LocalDate date, List<Rate> rates, Currency currency) {
        BigDecimal rate = new BigDecimal(0);
        BigDecimal beforeRate = rates.get(rates.size() - 1).getRate();

        BigDecimal min = beforeRate.multiply(new BigDecimal("-0.1"));
        BigDecimal max = beforeRate.multiply(new BigDecimal("0.1"));
        rate = rate.add(beforeRate).add(generateRandomBigDecimalFromRange(min, max));

        return new Rate(date.plusDays(1), rate, currency);
    }
}
