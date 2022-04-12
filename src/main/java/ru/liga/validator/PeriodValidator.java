package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;
import ru.liga.utils.AnswerFormatter;
import ru.liga.utils.DateTimeUtil;

import java.time.LocalDate;
import java.util.Map;

public class PeriodValidator implements Validator {
    @Override
    public void validate(Map<String, String> args) throws ArgumentException {

        if (!args.containsKey(KeysCommand.PERIOD.getKey()) && !args.containsKey(KeysCommand.DATE.getKey())) {
            throw new ArgumentException("Не указан период или дата прогноза!");
        }

        if (args.containsKey(KeysCommand.DATE.getKey())) {
            String date = args.get(KeysCommand.DATE.getKey());
            if (!date.equals("tomorrow")) {
                try {
                    LocalDate.parse(date, DateTimeUtil.PARSE_FORMATTER);
                } catch (Exception e) {
                    throw new ArgumentException("Неверный формат даты!");
                }
            }
        }

        if (args.containsKey((KeysCommand.PERIOD.getKey()))) {
            String period = args.get((KeysCommand.PERIOD.getKey()));
            if (!period.equals("week") && !period.equals("month")) {
                throw new ArgumentException("Некорректно указан период,либо данный период не поддерживается!");
            }
        }
    }
}
