package ru.liga.service;

import ru.liga.exceptions.DataException;
import ru.liga.model.Currency;
import ru.liga.model.Period;
import ru.liga.model.Rate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ForecastService {

    Rate oneDayForecast(Currency currency, LocalDate date) throws DataException;

    List<Rate> periodForecast(Currency currency, Period period) throws DataException;

}
