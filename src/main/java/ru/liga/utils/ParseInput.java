package ru.liga.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ParseInput {

    private static final Logger logger = LoggerFactory.getLogger(ParseCSV.class);

    public static Map<String, String> parse(String inputString) throws ArgumentException {

        logger.info("Parsing input string");

        Map<String, String> args = new HashMap<>();
        String[] commands = inputString.split(" ");
        if (commands.length % 2 != 0) {
            logger.info("Not enough arguments or invalid request");
            throw new ArgumentException("Недостаточно параметров или неверно задан запрос.");
        }

        if(commands[0].toLowerCase(Locale.ROOT).equals("rate")){
            args.put(KeysCommand.COMMAND.getKey(), commands[0].toLowerCase(Locale.ROOT));
            args.put(KeysCommand.CURRENCY.getKey(), commands[1].toLowerCase(Locale.ROOT));
        } else {
            logger.info("Unknown command: {}", commands[0]);
            throw new ArgumentException("Непонятная комманда: " + commands[0]);
        }

        try {
            for (int i = 2; i < commands.length; i++) {
                if (commands[i].startsWith("-")) {
                    args.put(commands[i].toLowerCase(Locale.ROOT), commands[i + 1].toLowerCase(Locale.ROOT));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            logger.info("Not enough arguments or invalid request");
            throw new ArgumentException("Недостаточно параметров или неверно задан запрос.");
        }
        return args;
    }
}
