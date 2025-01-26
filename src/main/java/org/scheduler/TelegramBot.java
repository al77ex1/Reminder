package org.scheduler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scheduler.exception.ConfigurationException;

public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private final Properties properties;
    
    public TelegramBot() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new ConfigurationException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigurationException("Error loading application.properties", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            log.info("CHAT_ID: {}", chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getProperty("bot.username");
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("bot.token");
    }

    public void sendMessage(String message) {
        try {
            execute(new SendMessage(properties.getProperty("bot.chat-id"), message));
        } catch (TelegramApiException e) {
            log.error("Error sending telegram message: {}", e.getMessage(), e);
        }
    }
}
