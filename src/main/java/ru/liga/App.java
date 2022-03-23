package ru.liga;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.liga.telegram.Bot;
import ru.liga.telegram.BotSettings;


public class App {
    private static final BotSettings botSettings = BotSettings.getInstance();

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(botSettings.getBotName(), botSettings.getBotToken()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
