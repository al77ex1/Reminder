package org.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramMessageScheduler {

    public static void main(String[] args) {
        SpringApplication.run(TelegramMessageScheduler.class, args);
    }
}
