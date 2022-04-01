package ru.liga.telegram;


import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.exceptions.DataException;
import ru.liga.exceptions.ParameterException;
import ru.liga.exceptions.TelegramException;
import ru.liga.model.Command;
import ru.liga.model.Currency;
import ru.liga.model.Period;
import ru.liga.model.Rate;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.service.*;
import ru.liga.utils.AnswerFormatter;
import ru.liga.utils.DateTimeUtil;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingCommandBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    private InMemoryRatesRepository repository;
    private ForecastService service;

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();

            try {
                execCommand(chatId, parseCommand(message.getText()));
            } catch (ParameterException | TelegramException | DataException e) {
                e.printStackTrace();
                sendMessage(chatId, e.getMessage());
            }

        }
    }

    private void execCommand(Long chatId, Command command) throws ParameterException, TelegramException, DataException {

        repository = new InMemoryRatesRepository();
        setService(command);

        switch (command.getOutput()) {
            case "line":
                if(!command.getPeriod().equals(Period.DAY)){
                    throw new ParameterException("Не задан формат вывода для периода");
                }
                if (command.getCurrencies().size() > 1){
                    throw new ParameterException("Прогноз на день доступен только для одной валюты, скорректируйте запрос");
                }
                    sendMessage(chatId, AnswerFormatter.printDayRate(service.oneDayForecast(command.getCurrencies().get(0), command.getPeriodDate().get(0))));
                break;
            case "list":
                if (command.getCurrencies().size() > 1){
                    throw new ParameterException("Прогноз с выводом листа доступен только для одной валюты, скорректируйте запрос");
                }
                if(command.getPeriod().equals(Period.DAY)){
                    throw new ParameterException("Не верно задан период или формат вывода");
                }
                StringBuilder answ = new StringBuilder("");
                List<Rate> rates = service.periodForecast(command.getCurrencies().get(0), command.getPeriod());

                for (Rate rate : rates) {
                    answ.append(AnswerFormatter.printDayRate(rate)).append("\n");
                }
                sendMessage(chatId, answ.toString());
                break;
            case "graph":
                if(command.getPeriod().equals(Period.DAY)){
                    throw new ParameterException("Не верно задан период или формат вывода");
                }
                List<List<Rate>> ratesListOfLists = new ArrayList<>();
                for (int i = 0; i < command.getCurrencies().size(); i++) {
                    ratesListOfLists.add(service.periodForecast(command.getCurrencies().get(i), command.getPeriod()));
                }
                ForecastPlot forecastPlot = new ForecastPlot(ratesListOfLists);
                forecastPlot.createPlot();

                sendImage(chatId, "C:/Users/Ivan/IdeaProjects/LigaIntership/CurrencyForecast/forecast.png");

                break;
            default:
                throw new ParameterException("Не задан формат ответа");
        }

        if (command.getPeriod().equals(Period.DAY)) {
            service.oneDayForecast(command.getCurrencies().get(0), command.getPeriodDate().get(0));
        } else {
            service.periodForecast(command.getCurrencies().get(0), command.getPeriod());
        }

    }

    private void setService(Command command) throws ParameterException {
        switch (command.getAlgorithm()) {
            case "actual":
                service = new ActualAlgorithm(repository);
                break;

            case "moon":
                service = new MysticalAlgorithm(repository);
                break;

            case "linear":
                service = new LinearRegression(repository);
                break;
            default:
                throw new ParameterException("Не задан алгоритм, либо ошибка при вводе. Доступные алгоритмы: actual, moon, linear");

        }
    }

    private Command parseCommand(String lineCommands) throws ParameterException {
        List<Currency> currencies = new ArrayList<>();
        Period period = null;
        List<LocalDate> periodLocaleDate = new ArrayList<>();
        String algorithm = null;
        String output = "line";

        String[] commands = lineCommands.split(" ");
        if (commands.length < 6 || commands.length % 2 != 0 || !commands[0].equals("rate")) {
            throw new ParameterException("Недостаточно параметров или неверно задан запрос.");
        }
        String[] currenciesArr = commands[1].split(",");

        for (String currency : currenciesArr) {
            switch (currency) {
                case "USD":
                    currencies.add(Currency.USD);
                    break;
                case "EUR":
                    currencies.add(Currency.EUR);
                    break;
                case "TRY":
                    currencies.add(Currency.TRY);
                    break;
                case "AMD":
                    currencies.add(Currency.AMD);
                    break;
                case "BGN":
                    currencies.add(Currency.BGN);
                    break;
                default:
                    throw new ParameterException("Данная валюта не поддерживается.");
            }
        }

        for (int i = 2; i < commands.length; i = i + 2) {

            switch (commands[i]) {
                case "-date":
                    try {
                        if (commands[i + 1].equals("tomorrow")) {
                            periodLocaleDate.add(LocalDate.now().plusDays(1));
                        } else {
                            periodLocaleDate.add(LocalDate.parse(commands[i + 1], DateTimeUtil.PARSE_FORMATTER));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ParameterException("Неверный формат даты, введите \"tomorrow\" или дату в формате dd.MM.yyyy");
                    }
                    period = Period.DAY;
                    break;
                case "-period":
                    switch (commands[i + 1]) {
                        case "week":
                            period = Period.WEEK;
                            break;
                        case "month":
                            period = Period.MONTH;
                            break;
                        default:
                            throw new ParameterException("Неверно задан период!");
                    }
                    break;
                case "-alg":
                    algorithm = commands[i + 1];
                    if(!algorithm.equals("moon") && !algorithm.equals("linear") && !algorithm.equals("actual")){
                        throw new ParameterException("Алгоритм введен некорректно!");
                    }
                    break;
                case "-output":
                    output = commands[i + 1];
                    if(!output.equals("list") && !output.equals("graph")){
                        throw new ParameterException("Неверно задан формат вывода!");
                    }
                    break;
                default:
                    throw new ParameterException("Неверно задан запрос");
            }
        }

        return new Command(currencies, period, periodLocaleDate, algorithm, output);
    }

    public void sendMessage(Long chatId, String message) {
        try {
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //log.error(String.format("Sending message error: %s", e.getMessage()));
        }
    }

    public void sendImage(Long chatId, String path) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setPhoto(new InputFile(new File(path)));
            photo.setChatId(chatId.toString());
            execute(photo);
        } catch (TelegramApiException e) {
            //log.error(String.format("Sending image error: %s", e.getMessage()));
            e.printStackTrace();
        }
    }

}
