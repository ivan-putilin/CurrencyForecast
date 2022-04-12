package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.Map;

public class OutputValidator implements Validator {
    @Override
    public void validate(Map<String, String> args) throws ArgumentException {
        if (args.containsKey(KeysCommand.OUTPUT.getKey())) {
            if (!args.get(KeysCommand.OUTPUT.getKey()).equals("graph")
                    && !args.get(KeysCommand.OUTPUT.getKey()).equals("list")) {
                throw new ArgumentException("Неверно задан формат вывода!");
            }
        }

        if(args.containsKey(KeysCommand.PERIOD.getKey()) && !args.containsKey(KeysCommand.OUTPUT.getKey())){
            throw new ArgumentException("Для прогноза периода необходимо указать формат вывода!");
        }
    }
}
