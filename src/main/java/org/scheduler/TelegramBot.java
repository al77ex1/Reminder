package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.service.AuthService;
import org.scheduler.service.UserService;
import org.scheduler.entity.User;
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
    
    @Autowired
    private UserService userService;
    
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
                handleAuthCommand(update);
            } else if (messageText.startsWith("/start")) {
                handleStartCommand(update.getMessage().getChatId());
            } else if (messageText.startsWith("/register")) {
                handleRegisterCommand(update);
            }
        }
    }
    
    private void handleAuthCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        
        try {
            String authLink = authService.generateAuthLinkByUserId(userId);
            
            if (authLink.startsWith("User not found")) {
                execute(new SendMessage(chatId.toString(), authLink));
                return;
            }
            
            execute(new SendMessage(chatId.toString(), "Ссылка для авторизации: " + authLink));
        } catch (TelegramApiException e) {
            log.info("Error sending telegram message: {}", e.getMessage());
        }
    }
    
    private void handleRegisterCommand(Update update) {
        String telegramUserName = update.getMessage().getFrom().getUserName();
        Long telegramUserId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String lastName = update.getMessage().getFrom().getLastName();

        log.info("Register command received for user: {}", update);

        try {
            // Проверяем, существует ли пользователь с таким telegramUserId
            if (userService.existsByTelegramUserId(telegramUserId)) {
                execute(new SendMessage(chatId.toString(), "Вы уже зарегистрированы!"));
                return;
            }
            
            // Создаем нового пользователя
            User user = User.builder()
                    .telegramUserId(telegramUserId)
                    .telegramUserName(telegramUserName)
                    .name(firstName)
                    .lastName(lastName)
                    .chatId(chatId)
                    .noActive(false)
                    .build();
            
            userService.createUser(user);
            
            execute(new SendMessage(chatId.toString(), "Регистрация успешно завершена!"));
        } catch (Exception e) {
            log.info("Error during registration: {}", e.getMessage());
            try {
                execute(new SendMessage(chatId.toString(), "Произошла ошибка при регистрации. Пожалуйста, попробуйте позже."));
            } catch (TelegramApiException ex) {
                log.info("Error sending error message: {}", ex.getMessage());
            }
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
            log.info("Error sending start message: {}", e.getMessage());
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
            log.info("Error sending telegram message: {}", e.getMessage());
        }
    }
}
