package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.Locale;
import java.util.Map;

public class CommandValidator implements Validator{
    @Override
    public void validate(Map<String, String> args) throws ArgumentException {
        if(!args.get(KeysCommand.COMMAND.getKey()).toLowerCase(Locale.ROOT).equals("rate")){
              throw new ArgumentException("Неизвестная команда, либо команда задана неверно!");
        }
    }
}
