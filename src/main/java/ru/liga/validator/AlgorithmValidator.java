package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.Map;

public class AlgorithmValidator implements Validator {
    public void validate(Map<String, String> args) throws ArgumentException {

        if (!args.containsKey(KeysCommand.ALGORITHM.getKey())) {
            throw new ArgumentException("Не задан алгоритм");
        } else {
            String alg = args.get(KeysCommand.ALGORITHM.getKey());
            if (!alg.equals("actual") && !alg.equals("moon") && !alg.equals("linear")) {
                throw new ArgumentException("Не задан алгоритм, либо ошибка при вводе. " +
                        "Доступные алгоритмы: actual, moon, linear");
            }
        }
    }
}
