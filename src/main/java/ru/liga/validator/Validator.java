package ru.liga.validator;

import ru.liga.exceptions.ArgumentException;

import java.util.Map;

public interface Validator {
    void validate(Map<String, String> args) throws ArgumentException;
}
