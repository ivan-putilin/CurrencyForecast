package ru.liga.model;

import java.time.LocalDate;
import java.util.List;

public class Command {
    private final List<Currency> currencies;
    private final Period period;
    private final LocalDate periodDate;
    private final String algorithm;
    private final String output;

    public Command(List<Currency> currencies, Period period, LocalDate periodDate, String algorithm, String output) {
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

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "Command{" +
                "currencies=" + currencies +
                ", period=" + period +
                ", periodDate=" + periodDate +
                ", algorithm='" + algorithm + '\'' +
                ", output='" + output + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }



        Command anyCommand = (Command) obj;
        return currencies.equals(anyCommand.getCurrencies())
                && period.equals(anyCommand.getPeriod())
                && (periodDate.equals(anyCommand.getPeriodDate()))
                && algorithm.equals(anyCommand.getAlgorithm())
                && output.equals(anyCommand.getOutput());
    }
}
