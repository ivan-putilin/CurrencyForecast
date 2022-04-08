package ru.liga.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import ru.liga.exceptions.ArgumentException;
import ru.liga.model.Command;
import ru.liga.model.Currency;
import ru.liga.model.KeysCommand;
import ru.liga.model.Period;
import ru.liga.validator.CommandValidateService;

import java.time.LocalDate;
import java.util.*;

public class ParseCommand {

    private static final Logger logger = LoggerFactory.getLogger(TelegramLongPollingCommandBot.class);

    public static Command parseCommand(Map<String, String> args) throws ArgumentException {
        logger.debug("Parsing command: {}", args.toString());
        Command command;
        CommandValidateService.validate(args);

        Period period = getPeriod(args);
        if (period.equals(Period.DAY)) {
            command = new Command(
                    getCurrency(args),
                    period,
                    getDateFromPeriod(args),
                    getAlgorithm(args),
                    getOutput(args)
            );
        } else {
            command = new Command(
                    getCurrency(args),
                    period,
                    null,
                    getAlgorithm(args),
                    getOutput(args)
            );
        }
        return command;
    }

    private static List<Currency> getCurrency(Map<String, String> args) throws ArgumentException {
        List<Currency> currencies = new ArrayList<>();
        String[] currenciesArr = args.get(KeysCommand.CURRENCY.getKey()).split(",");
        for (String currency : currenciesArr) {
            switch (currency.toUpperCase(Locale.ROOT)) {
                case "USD":
                    currencies.add(Currency.USD);
                    break;
                case "EUR":
                    currencies.add(Currency.EUR);
                    break;
                case "TRY":
                    currencies.add(Currency.TRY);
                    break;
                case "AMD":
                    currencies.add(Currency.AMD);
                    break;
                case "BGN":
                    currencies.add(Currency.BGN);
                    break;
                default:
                    throw new ArgumentException("Данная валюта не поддерживается.");
            }
        }
        return currencies;
    }

    private static LocalDate getDateFromPeriod(Map<String, String> args) throws ArgumentException {
        LocalDate periodDate;
        String date = args.get(KeysCommand.DATE.getKey());

        if (date.equals("tomorrow")) {
            periodDate = LocalDate.now().plusDays(1);
        } else {
            try {
                periodDate = LocalDate.parse(date, DateTimeUtil.PARSE_FORMATTER);
            } catch (Exception e) {
                throw new ArgumentException("Неверный формат даты!");
            }
        }
        return periodDate;
    }

    private static Period getPeriod(Map<String, String> args) throws ArgumentException {
        Period period;
        if (args.containsKey(KeysCommand.DATE.getKey())) {
            period = Period.DAY;

        } else {
            switch (args.get(KeysCommand.PERIOD.getKey())) {
                case "week":
                    period = Period.WEEK;
                    break;
                case "month":
                    period = Period.MONTH;
                    break;
                default:
                    throw new ArgumentException("Неверно задан период!");
            }
        }
        return period;
    }

    private static String getAlgorithm(Map<String, String> args) {
        return args.get(KeysCommand.ALGORITHM.getKey());
    }

    private static String getOutput(Map<String, String> args) {
        String output;

        if(args.containsKey(KeysCommand.PERIOD.getKey())){
            output = args.get(KeysCommand.OUTPUT.getKey());
        } else {
            output = "line";
        }
        return output;
    }
}
