package ru.liga.telegram;

import lombok.Data;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
public class BotSettings {

    public static final String FILE_NAME = "config.properties";
    private static BotSettings instance;
    private Properties properties;
    private String botToken;
    private String botName;
    private TelegramBotsApi telegramBotsApi;

    public static BotSettings getInstance() {
        if (instance == null)
            instance = new BotSettings();
        return instance;
    }

    {
        try {
            properties = new Properties();
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new IOException(String.format("Error loading properties file '%s'", FILE_NAME));
            }
            botToken = properties.getProperty("botToken");
            if (botToken == null) {
                throw new RuntimeException("Token value is null");
            }
            botName = properties.getProperty("botName");
            if (botName == null) {
                throw new RuntimeException("UserName value is null");
            }
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("Bot initialization error: " + e.getMessage());
        }

    }
}