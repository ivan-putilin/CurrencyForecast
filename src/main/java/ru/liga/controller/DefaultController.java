package ru.liga.controller;

import ru.liga.view.Console;

public class DefaultController implements Controller{
    private final Console console;
    private final String command;

    public DefaultController(String command, Console console) {
        this.command = command;
        this.console = console;
    }

    @Override
    public void operate() {
        console.printMessage("Введена неверная команда: " + "\"" + command + "\"");
        console.printMessage("Для просмотра списка доступных команд введите \"help\"");
    }
}
