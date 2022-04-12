package ru.liga.repository;

import ru.liga.model.Currency;
import ru.liga.model.Rate;
import ru.liga.utils.ParseCSV;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InMemoryDatesOfFullMoonRepository {
    private final String fileName;

    public InMemoryDatesOfFullMoonRepository(String fileName) {
        this.fileName = fileName;
    }

    public List<LocalDate> getData() throws IOException {
        List<LocalDate> datesOfMoon = new ArrayList<>();

        return ParseCSV.parseDatesOfMoon(datesOfMoon, fileName);
    }
}
