package ru.liga.validator;

import org.junit.Test;
import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandValidateServiceTest {


    @Test(expected = ArgumentException.class)
    public void whenCommandContainsIncorrectPeriodThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "year");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandContainsIncorrectCurrencyThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EURO");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandContainsIncorrectOutputThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "telegram");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandContainsIncorrectAlgorithmThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "integral");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandContainsIncorrectCommandThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "forecast");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandNotContainsCurrencyThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "forecast");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandNotContainsPeriodThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandNotContainsOutputThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandNotContainsAlgorithmThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.PERIOD.getKey(), "week");
        args.put(KeysCommand.OUTPUT.getKey(), "list");

        CommandValidateService.validate(args);

    }

    @Test(expected = ArgumentException.class)
    public void whenCommandNotContainsDateThanValid() throws ArgumentException {
        Map<String, String> args = new HashMap<>();

        args.put(KeysCommand.COMMAND.getKey(), "rate");
        args.put(KeysCommand.CURRENCY.getKey(), "EUR");
        args.put(KeysCommand.OUTPUT.getKey(), "list");
        args.put(KeysCommand.ALGORITHM.getKey(), "moon");

        CommandValidateService.validate(args);

    }

}