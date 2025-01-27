package org.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.scheduler.TelegramBot;

public class SatMessageBotJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TelegramBot bot = new TelegramBot();
        String saturdayMessage = """
                Дорогие проповедники!

                Если у кого есть готовая проповедь на ближайшее воскресенье.

                То будьте добры - поделитесь драгоценными ссылками из Библии для вашей проповеди в этой группе.

                Все что будет в телеграме, то и будет на экране.""";
        bot.sendMessage(saturdayMessage);
    }
}
