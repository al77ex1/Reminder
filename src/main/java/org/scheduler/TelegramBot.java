package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.exception.ApplicationRuntimeException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final Properties properties;
    
    public TelegramBot() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new ApplicationRuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Error loading application.properties", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String username = update.getMessage().getFrom().getUserName();
            if (username == null) {
                String firstName = update.getMessage().getFrom().getFirstName();
                String lastName = update.getMessage().getFrom().getLastName();
                username = firstName + 
                          (lastName != null ? " " + lastName : "");
                username = username.trim();
                if (username.isEmpty()) username = "Unknown";
            }
            log.info("Message received: CHAT_ID: {} от @{} : {}", update.getMessage().getChatId(), username, messageText);
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
