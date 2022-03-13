package ru.liga;

import ru.liga.controller.Controller;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.service.ForecastAverage7DaysService;
import ru.liga.service.ForecastService;
import ru.liga.utils.ControllerSelection;
import ru.liga.view.Console;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        InMemoryRatesRepository repository = new InMemoryRatesRepository();
        ForecastService service = new ForecastAverage7DaysService(repository);
        Console console = new Console();

        while (true) {
            String command = console.insertCommand();
            Controller controller = ControllerSelection.getController(command, service, console);
            controller.operate();
        }
    }
}
