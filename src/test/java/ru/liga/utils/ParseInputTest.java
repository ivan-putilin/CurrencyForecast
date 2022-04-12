package ru.liga.utils;

import org.junit.Test;
import ru.liga.exceptions.ArgumentException;
import ru.liga.model.KeysCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

public class ParseInputTest {

    @Test
    public void whenInputDataEqualsExpectDataThenValid() throws ArgumentException {
        Map<String, String> expected = new HashMap<>();

        expected.put(KeysCommand.COMMAND.getKey(), "rate");
        expected.put(KeysCommand.CURRENCY.getKey(), "usd");
        expected.put(KeysCommand.PERIOD.getKey(), "week");
        expected.put(KeysCommand.ALGORITHM.getKey(), "moon");
        expected.put(KeysCommand.OUTPUT.getKey(), "list");

        Map<String, String> inputLine = ParseInput.parse("rate USD -period week -alg moon -output list");
        assertEquals(expected, inputLine);
    }

    @Test(expected = ArgumentException.class)
    public void whenTheNumberIsOddThrownArgumentExceptionThenValid() throws ArgumentException {
        Map<String, String> expected = new HashMap<>();

        expected.put(KeysCommand.COMMAND.getKey(), "rate");
        expected.put(KeysCommand.CURRENCY.getKey(), "usd");
        expected.put(KeysCommand.PERIOD.getKey(), "week");
        expected.put(KeysCommand.ALGORITHM.getKey(), "moon");
        expected.put(KeysCommand.OUTPUT.getKey(), "list");

        Map<String, String> inputLine = ParseInput.parse("rate USD -period week -alg moon -output");
        assertEquals(expected, inputLine);
    }
}