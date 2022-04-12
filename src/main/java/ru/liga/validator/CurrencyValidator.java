package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;
import ru.liga.model.Currency;
import ru.liga.model.KeysCommand;

import java.util.Locale;
import java.util.Map;

public class CurrencyValidator implements Validator {
    @Override
    public void validate(Map<String, String> args) throws ArgumentException {
        if (!args.containsKey(KeysCommand.CURRENCY.getKey())) {
            throw new ArgumentException("Не задана валюта!");
        } else {
            String[] currencies = args.get(KeysCommand.CURRENCY.getKey())
                    .toUpperCase(Locale.ROOT)
                    .split(",");
            for (String currency : currencies){
                if (!currency.equals(Currency.AMD.toString())
                        && !currency.equals(Currency.BGN.toString())
                        && !currency.equals(Currency.EUR.toString())
                        && !currency.equals(Currency.TRY.toString())
                        && !currency.equals(Currency.USD.toString())) {
                    throw new ArgumentException("Код валюты указан некорректно, либо валюта не поддерживается!");
                }
            }
        }
    }
}
