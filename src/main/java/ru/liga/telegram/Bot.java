package ru.liga.telegram;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.exceptions.DataException;
import ru.liga.exceptions.ArgumentException;
import ru.liga.exceptions.TelegramException;
import ru.liga.model.Command;
import ru.liga.model.Period;
import ru.liga.model.Rate;
import ru.liga.repository.InMemoryRatesRepository;
import ru.liga.service.*;
import ru.liga.utils.AnswerFormatter;
import ru.liga.utils.ParseCommand;
import ru.liga.utils.ParseInput;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingCommandBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramLongPollingCommandBot.class);

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
        logger.info("Message processing, get update");
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = update.getMessage().getChatId();

            try {
                execCommand(chatId, ParseCommand.parseCommand(ParseInput.parse(message.getText())));
            } catch (ArgumentException | TelegramException | DataException e) {
                e.printStackTrace();
                sendMessage(chatId, e.getMessage());
                logger.error(e.getMessage(), e);
            }

        }
    }

    private void execCommand(Long chatId, Command command) throws ArgumentException, TelegramException, DataException {

        logger.debug("Execute command: {}", command.toString());

        repository = new InMemoryRatesRepository();
        setService(command);

        switch (command.getOutput()) {
            case "line":
                    sendMessage(chatId, AnswerFormatter.printDayRate(service.oneDayForecast(command.getCurrencies().get(0), command.getPeriodDate())));
                break;
            case "list":
                StringBuilder answer = new StringBuilder();
                List<Rate> rates = service.periodForecast(command.getCurrencies().get(0), command.getPeriod());

                for (Rate rate : rates) {
                    answer.append(AnswerFormatter.printDayRate(rate)).append("\n");
                }
                sendMessage(chatId, answer.toString());
                break;
            case "graph":
                List<List<Rate>> ratesListOfLists = new ArrayList<>();
                for (int i = 0; i < command.getCurrencies().size(); i++) {
                    ratesListOfLists.add(service.periodForecast(command.getCurrencies().get(i), command.getPeriod()));
                }
                ForecastPlot forecastPlot = new ForecastPlot(ratesListOfLists);
                forecastPlot.createPlot();

                sendImage(chatId, "./forecast.png");

                break;
            default:
                throw new ArgumentException("Не задан формат ответа");
        }

        if (command.getPeriod().equals(Period.DAY)) {
            service.oneDayForecast(command.getCurrencies().get(0), command.getPeriodDate());
        } else {
            service.periodForecast(command.getCurrencies().get(0), command.getPeriod());
        }

    }

    private void setService(Command command) throws ArgumentException {
        logger.debug("Set service for alg: {}", command.getAlgorithm());
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
                throw new ArgumentException("Не задан алгоритм, либо ошибка при вводе. Доступные алгоритмы: actual, moon, linear");

        }
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
            e.printStackTrace();
            logger.error(String.format("Sending message error: %s", e.getMessage()));
        }
    }

    public void sendImage(Long chatId, String path) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setPhoto(new InputFile(new File(path)));
            photo.setChatId(chatId.toString());
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error(String.format("Sending image error: %s", e.getMessage()));
        }
    }

}
