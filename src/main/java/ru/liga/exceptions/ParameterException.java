package ru.liga.exceptions;

public class ParameterException extends Exception{
    public ParameterException(String message) {
        super(message + "\n" +
                "Пример запроса для прогноза на день(на завтра или на дату): \"rate TRY -date tomorrow -alg moon\"\n" +
                "Пример запроса прогноза на период(неделя или месяц), с указанием формата вывода: \"rate USD -period week -alg moon -output list\"\n" +
                "Пример запроса прогноза на период(неделя или месяц), для нескольких валют с выводом графика : \"rate USD,TRY -period month -alg moon -output graph\"\n" +
                "Доступные валюты:     \n" +
                "    TRY,\n" +
                "    USD,\n" +
                "    EUR,\n" +
                "    BGN,\n" +
                "    AMD\n" +
                "Доступные алгоритмы:     \n" +
                "    moon,\n" +
                "    actual,\n" +
                "    linear");
    }
}
