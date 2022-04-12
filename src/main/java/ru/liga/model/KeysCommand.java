package ru.liga.model;

public enum KeysCommand {
    COMMAND("rate"),
    CURRENCY("currency"),
    DATE("-date"),
    PERIOD("-period"),
    ALGORITHM("-alg"),
    OUTPUT("-output");

    private final String key;

    KeysCommand(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
