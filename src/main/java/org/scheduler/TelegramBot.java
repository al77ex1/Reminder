package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

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
            } else if (messageText.startsWith("/start")) {
                handleStartCommand(update.getMessage().getChatId());
            } else if (messageText.startsWith("/register")) {
                handleRegisterCommand(username, update.getMessage().getChatId());
            }
        }
    }
    
    private void handleAuthCommand(String username, Long chatId) {
        String authLink = authService.generateAuthLink(username);
        log.info(authLink);
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("<a href=\"" + authLink + "\">Авторизоваться</a>");
            message.setParseMode("HTML");
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending telegram message: {}", e.getMessage(), e);
        }
    }
    
    private void handleRegisterCommand(String username, Long chatId) {
        try {
            execute(new SendMessage(chatId.toString(), "Запрос на регистрацию принят. После модерации вы получите уведомление."));
        } catch (TelegramApiException e) {
            log.error("Error sending registration message: {}", e.getMessage(), e);
        }
    }
    
    private void handleStartCommand(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Добро пожаловать! Нажмите на кнопку ниже для аутентификации или регистрации.");
        
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        
        KeyboardButton authButton = new KeyboardButton("/auth");
        KeyboardButton registerButton = new KeyboardButton("/register");
        row.add(authButton);
        row.add(registerButton);
        
        keyboard.add(row);
        
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending start message: {}", e.getMessage(), e);
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
