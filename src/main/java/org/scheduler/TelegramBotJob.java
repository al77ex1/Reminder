package org.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TelegramBotJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TelegramBot bot = new TelegramBot();
        bot.sendWeeklyMessage();
    }
}
