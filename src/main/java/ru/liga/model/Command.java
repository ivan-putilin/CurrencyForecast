package ru.liga.model;

import java.time.LocalDate;
import java.util.List;

public class Command {
    private List<Currency> currencies;
    private Period period;
    private List<LocalDate> periodDate;
    private String algorithm;
    private String output;

    public Command(List<Currency> currencies, Period period, List<LocalDate> periodDate, String algorithm, String output) {
        this.currencies = currencies;
        this.period = period;
        this.periodDate = periodDate;
        this.algorithm = algorithm;
        this.output = output;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public Period getPeriod() {
        return period;
    }

    public List<LocalDate> getPeriodDate() {
        return periodDate;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getOutput() {
        return output;
    }
}
