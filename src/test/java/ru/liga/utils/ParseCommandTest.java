package ru.liga.utils;

import org.junit.Test;
import ru.liga.exceptions.ArgumentException;
import ru.liga.model.Command;
import ru.liga.model.Currency;
import ru.liga.model.KeysCommand;
import ru.liga.model.Period;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ParseCommandTest {

    @Test
    public void whenOneCurrencyAndPeriodWeek() throws ArgumentException {
        Map<String, String> argsCommand = new HashMap<>();

        argsCommand.put(KeysCommand.COMMAND.getKey(), "rate");
        argsCommand.put(KeysCommand.CURRENCY.getKey(), "USD");
        argsCommand.put(KeysCommand.PERIOD.getKey(), "week");
        argsCommand.put(KeysCommand.ALGORITHM.getKey(), "moon");
        argsCommand.put(KeysCommand.OUTPUT.getKey(), "list");

        List<Currency> currencies = new ArrayList<>();
        currencies.add(Currency.USD);
        Command expectedCommand = new Command(currencies, Period.WEEK, LocalDate.parse("01.01.2022", DateTimeUtil.PARSE_FORMATTER), "moon", "list");

        Command actualCommand = ParseCommand.parseCommand(argsCommand);

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void whenMoreCurrencyAndPeriodWeek() throws ArgumentException {
        Map<String, String> argsCommand = new HashMap<>();

        argsCommand.put(KeysCommand.COMMAND.getKey(), "rate");
        argsCommand.put(KeysCommand.CURRENCY.getKey(), "USD,EUR,AMD");
        argsCommand.put(KeysCommand.PERIOD.getKey(), "week");
        argsCommand.put(KeysCommand.ALGORITHM.getKey(), "moon");
        argsCommand.put(KeysCommand.OUTPUT.getKey(), "list");

        List<Currency> currencies = new ArrayList<>();
        currencies.add(Currency.USD);
        currencies.add(Currency.EUR);
        currencies.add(Currency.AMD);

        Command expectedCommand = new Command(currencies, Period.WEEK, LocalDate.parse("01.01.2022", DateTimeUtil.PARSE_FORMATTER), "moon", "list");

        Command actualCommand = ParseCommand.parseCommand(argsCommand);

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void whenOneCurrencyAndPeriodDay() throws ArgumentException {
        Map<String, String> argsCommand = new HashMap<>();

        argsCommand.put(KeysCommand.COMMAND.getKey(), "rate");
        argsCommand.put(KeysCommand.CURRENCY.getKey(), "USD");
        argsCommand.put(KeysCommand.DATE.getKey(), "10.04.2022");
        argsCommand.put(KeysCommand.ALGORITHM.getKey(), "moon");

        List<Currency> currencies = new ArrayList<>();
        currencies.add(Currency.USD);
        Command expectedCommand = new Command(currencies, Period.DAY, LocalDate.parse("10.04.2022", DateTimeUtil.PARSE_FORMATTER), "moon", "line");

        Command actualCommand = ParseCommand.parseCommand(argsCommand);

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void whenOneCurrencyAndPeriodTomorrow() throws ArgumentException {
        Map<String, String> argsCommand = new HashMap<>();

        argsCommand.put(KeysCommand.COMMAND.getKey(), "rate");
        argsCommand.put(KeysCommand.CURRENCY.getKey(), "USD");
        argsCommand.put(KeysCommand.DATE.getKey(), LocalDate.now().plusDays(1).format(DateTimeUtil.PARSE_FORMATTER));
        argsCommand.put(KeysCommand.ALGORITHM.getKey(), "moon");

        List<Currency> currencies = new ArrayList<>();
        currencies.add(Currency.USD);
        Command expectedCommand = new Command(currencies, Period.DAY, LocalDate.parse(LocalDate.now().plusDays(1).format(DateTimeUtil.PARSE_FORMATTER), DateTimeUtil.PARSE_FORMATTER), "moon", "line");

        Command actualCommand = ParseCommand.parseCommand(argsCommand);

        assertEquals(expectedCommand, actualCommand);
    }

}