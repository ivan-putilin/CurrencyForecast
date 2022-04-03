package ru.liga.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rate {

    private final LocalDate date;
    private final BigDecimal rate;
    private final Currency currency;

    public Rate(LocalDate date, BigDecimal rate, Currency currency) {
        this.date = date;
        this.rate = rate;
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "date=" + date +
                ", rate=" + rate +
                ", currency=" + currency +
                '}';
    }
}
