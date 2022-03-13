package ru.liga.service;

import ru.liga.model.Currency;
import ru.liga.model.Rate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ForecastService {

    Rate tomorrowForecast(Currency currency);

    List<Rate> weekForecast(Currency currency);

}
