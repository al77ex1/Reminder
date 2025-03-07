package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private AuthService authService;
    
    public TelegramBot() {
        // Constructor is now empty as we're using environment variables directly
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
            
            if (messageText.startsWith("/auth")) {
                handleAuthCommand(username, update.getMessage().getChatId());
            }
        }
    }
    
    private void handleAuthCommand(String username, Long chatId) {
        String result = authService.generateAuthLink(username);
        try {
            execute(new SendMessage(chatId.toString(), result));
        } catch (TelegramApiException e) {
            log.error("Error sending telegram message: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return System.getProperty("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getProperty("BOT_TOKEN");
    }

    public void sendMessage(String message) {
        try {
            execute(new SendMessage(System.getProperty("BOT_CHAT_ID"), message));
        } catch (TelegramApiException e) {
            log.error("Error sending telegram message: {}", e.getMessage(), e);
        }
    }
}
