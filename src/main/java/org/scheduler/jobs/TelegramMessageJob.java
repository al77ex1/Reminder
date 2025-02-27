package org.scheduler.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scheduler.TelegramBot;

@Slf4j
public class TelegramMessageJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TelegramBot bot = new TelegramBot();
        String message = (String) context.getJobDetail().getJobDataMap().get("message");
        bot.sendMessage(message);
    }
}
