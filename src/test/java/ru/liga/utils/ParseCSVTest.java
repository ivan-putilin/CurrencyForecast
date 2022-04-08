package ru.liga.utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.liga.model.Currency;
import ru.liga.model.Rate;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ParseCSVTest {

    private static File dir;

    @BeforeClass
    public static void beforeClass() throws IOException {
        dir = File.createTempFile("test", ".csv", new File("src/main/resources"));
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(dir),"windows-1251")) {
            writer.write("nominal;data;curs;cdx\n");
            writer.write("1;05.03.2022;\"116,5312\";ЕВРО\n");
            writer.write("1;06.03.2022;\"119,5312\";ЕВРО\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseFileTest() throws IOException {
        List<Rate> rates = new ArrayList<>();
        String fileName = "/" + dir.getName();
        List<Rate> ratesActual = ParseCSV.parse(1, rates, fileName);
        assertEquals(new Rate(LocalDate.parse("05.03.2022", DateTimeUtil.PARSE_FORMATTER), new BigDecimal("116.5312"), Currency.EUR),
                ratesActual.get(0));
    }

    @AfterClass
    public static void afterClass() {
        dir.deleteOnExit();
    }
}