package org.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.config.EnvConfig;
import org.scheduler.exception.ApplicationRuntimeException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@SpringBootApplication
public class TelegramMessageScheduler {

    public static void main(String[] args) {
        EnvConfig.loadEnvVariables();
        SpringApplication.run(TelegramMessageScheduler.class, args);
    }
    
    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot();
    }
    
    @Bean
    public ApplicationListener<ApplicationReadyEvent> botRegistrar(ApplicationContext context) {
        return event -> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                TelegramBot bot = context.getBean(TelegramBot.class);
                botsApi.registerBot(bot);
                log.info("Telegram bot successfully registered and ready to receive messages");
            } catch (TelegramApiException e) {
                throw new ApplicationRuntimeException("Error registering Telegram bot", e);
            }
        };
    }
}
