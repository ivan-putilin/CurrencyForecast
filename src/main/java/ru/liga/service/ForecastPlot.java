package ru.liga.service;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.exceptions.DataException;
import ru.liga.model.Rate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ForecastPlot {

    private static final Logger logger = LoggerFactory.getLogger(ForecastPlot.class);

    private final List<List<Rate>> rates;

    public ForecastPlot(List<List<Rate>> rates) {
        this.rates = rates;
    }

    public void createPlot() throws DataException {

        Plot plt = Plot.create();
        StringBuilder title = new StringBuilder("Forecast for ");

        List<Double> x = new ArrayList<>();

        for (int i = 1; i < rates.get(0).size() + 1; i++) {
            x.add((double) i);
        }

        List<String> colors = new ArrayList<>(Arrays.asList("red", "green", "blue", "yellow", "orange"));

        int i = 0;
        for (List<Rate> rate : rates) {
            List<Double> y = rate.stream()
                    .map(r -> r.getRate().doubleValue())
                    .collect(Collectors.toList());
            plt.plot().add(x, y).color(colors.get(i));
            title.append("(").append(colors.get(i)).append(")").append(rate.get(0).getCurrency().toString()).append(", ");
            i++;
        }

        plt.title(title.toString());
        plt.xlabel("Days:");
        plt.ylabel("Rates:");
        plt.savefig("./forecast.png").dpi(200);
        try {
            plt.executeSilently();
            logger.info("Plot successfully built and saved");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new DataException("Ошибка сохранения файла с графиком");
        } catch (PythonExecutionException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new RuntimeException("Проблема с Питоном");
        }
    }
}
