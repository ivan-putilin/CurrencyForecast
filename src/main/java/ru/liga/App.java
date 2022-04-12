package ru.liga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.liga.telegram.Bot;
import ru.liga.telegram.BotSettings;

import java.io.*;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final BotSettings botSettings = BotSettings.getInstance();

    public static void main(String[] args) {
        String botName = botSettings.getBotName();
        String botToken = botSettings.getBotToken();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(botName, botToken));
            logger.info("Bot started!");
            logger.debug("Bot Name:{} Bot Token:{}", botName, botToken);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
