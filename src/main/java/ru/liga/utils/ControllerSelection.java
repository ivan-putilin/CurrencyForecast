package ru.liga.utils;

import ru.liga.controller.Controller;
import ru.liga.controller.DefaultController;
import ru.liga.controller.RateController;
import ru.liga.controller.SystemController;
import ru.liga.service.ForecastService;
import ru.liga.view.Console;

import java.util.Locale;

public class ControllerSelection {

    public static Controller getController(String command, ForecastService service, Console console) {
        String[] commands = command.split(" ");
        Controller controller;

        switch (commands[0].toLowerCase(Locale.ROOT)) {
            case "exit":
            case "help":
                controller = new SystemController(commands[0], console);
                break;
            case "rate":
                controller = new RateController(commands, console, service);
                break;
            default:
                controller = new DefaultController(command, console);
        }
        return controller;
    }
}
