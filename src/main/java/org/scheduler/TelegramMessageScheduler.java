package org.scheduler;

import org.scheduler.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramMessageScheduler {

    public static void main(String[] args) {
        EnvConfig.loadEnvVariables();
        SpringApplication.run(TelegramMessageScheduler.class, args);
    }
}
