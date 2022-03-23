package ru.liga.service;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import ru.liga.model.Rate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ForecastPlot {

    private final List<List<Rate>> rates;
    private Double maxRate;

    public ForecastPlot(List<List<Rate>> rates) {
        this.rates = rates;
        this.maxRate = rates.stream()
                .flatMap(r -> r.stream())
                .max(Comparator.comparing(Rate::getRate))
                .map(r -> r.getRate())
                .get()
                .doubleValue();
    }

    public void createPlot() {

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
            title.append("(" + colors.get(i) + ")").append(rate.get(0).getCurrency().toString()).append(", ");
            i++;
        }

        plt.title(title.toString());
        plt.xlabel("Days:");
        plt.ylabel("Rates:");
        plt.savefig("C:/Users/Ivan/IdeaProjects/LigaIntership/CurrencyForecast/forecast.png").dpi(200);
        try {
            plt.executeSilently();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PythonExecutionException e) {
            System.out.println("Проблема с Питоном");
            e.printStackTrace();
        }
    }
}
