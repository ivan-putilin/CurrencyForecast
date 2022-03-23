package ru.liga.repository;

import ru.liga.model.Currency;
import ru.liga.model.Rate;
import ru.liga.utils.ParseCSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryRatesRepository {

    private String fileName;

    public List<Rate> getData(int lineNumber, Currency currency) throws IOException {
        List<Rate> data = new ArrayList<>();

        switch (currency) {
            case EUR:
                fileName = "/EUR_F01_02_2005_T05_03_2022.csv";
                break;
            case TRY:
                fileName = "/TRY_F01_02_2005_T05_03_2022.csv";
                break;
            case USD:
                fileName = "/USD_F01_02_2005_T05_03_2022.csv";
                break;
            case BGN:
                fileName = "/BGN_F01_02_2005_T05_03_2022.csv";
                break;
            case AMD:
                fileName = "/AMD_F01_02_2005_T05_03_2022.csv";
                break;
            default:
                fileName = "/EUR_F01_02_2002_T01_02_2022.csv";
        }

        List<Rate> resultData = reverseData(ParseCSV.parse(lineNumber, data, fileName));

        return reverseData(resultData);
    }

    public List<Rate> getAllData(Currency currency) throws IOException {
        List<Rate> data = new ArrayList<>();

        switch (currency) {
            case EUR:
                fileName = "/EUR_F01_02_2005_T05_03_2022.csv";
                break;
            case TRY:
                fileName = "/TRY_F01_02_2005_T05_03_2022.csv";
                break;
            case USD:
                fileName = "/USD_F01_02_2005_T05_03_2022.csv";
                break;
            case BGN:
                fileName = "/BGN_F01_02_2005_T05_03_2022.csv";
                break;
            case AMD:
                fileName = "/AMD_F01_02_2005_T05_03_2022.csv";
                break;
            default:
                fileName = "/EUR_F01_02_2002_T01_02_2022.csv";
        }

        List<Rate> resultData = reverseData(ParseCSV.parseAll(data, fileName));

        return reverseData(resultData);
    }



    private List<Rate> reverseData(List<Rate> data) {
        List<Rate> reverseData = new ArrayList<>();
        reverseData.addAll(data);
        Collections.sort(reverseData, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        return reverseData;
    }
}
