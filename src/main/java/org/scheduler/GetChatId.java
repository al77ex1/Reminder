package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.exception.ApplicationRuntimeException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class GetChatId {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
            log.info("Bot started! Send a message to the group to get CHAT_ID");
        } catch (TelegramApiException e) {
            throw new ApplicationRuntimeException("Error starting application {}", e);
        }
    }
}
