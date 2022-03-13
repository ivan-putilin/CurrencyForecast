package ru.liga.controller;

import ru.liga.model.Currency;
import ru.liga.service.ForecastService;
import ru.liga.view.Console;

import java.util.Locale;

public class RateController implements Controller {
    private final String[] commands;
    private final Console console;
    private final ForecastService service;

    public RateController(String[] commands, Console console, ForecastService service) {
        this.commands = commands;
        this.console = console;
        this.service = service;
    }

    @Override
    public void operate() {
        Currency currency;
        if (commands.length < 3) {
            console.printMessage("После команды \"rate\" необходимо вводить наименование валюты и период прогноза.\n" + "Для просмотра списка доступных команд введите \"help\"");
            return;
        }

        try {
            currency = Currency.valueOf(commands[1].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            console.printMessage("После команды \"rate\" необходимо вводить сокращенное наименование волюты (USD, EUR, TRY)");
            return;
        }

        switch (commands[2].toLowerCase(Locale.ROOT)) {
            case "tomorrow":
                console.printDayRate(service.tomorrowForecast(currency));
                break;
            case "week":
                console.printWeekRate(service.weekForecast(currency));
                break;
            default:
                console.printMessage("После команды \"rate\" и наименования валюты необходимо вводить период прогноза (tomorrow, week");

        }

    }
}
