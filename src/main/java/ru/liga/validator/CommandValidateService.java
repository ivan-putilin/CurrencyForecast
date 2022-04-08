package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;

import java.util.Map;

public class CommandValidateService {
    public static void validate(Map<String, String> commandArgs) throws ArgumentException {
        new CommandValidator().validate(commandArgs);
        new CurrencyValidator().validate(commandArgs);
        new PeriodValidator().validate(commandArgs);
        new AlgorithmValidator().validate(commandArgs);
        new OutputValidator().validate(commandArgs);
    }
}
