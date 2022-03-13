package ru.liga.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rate {

    private LocalDate date;
    private BigDecimal rate;
    private Currency currency;

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
}
