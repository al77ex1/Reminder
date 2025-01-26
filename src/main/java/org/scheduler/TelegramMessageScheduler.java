package org.scheduler;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.Scheduler;

public class TelegramMessageScheduler {
    public static void main(String[] args) {
        try {
            // Создаем Job
            JobDetail job = JobBuilder.newJob(TelegramBotJob.class)
                    .withIdentity("weeklyMessageJob", "group1")
                    .build();

            // Создаем Trigger с cron-выражением для запуска каждую субботу в 16:00
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("weeklyTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 16 ? * SAT"))
                    .build();

            // Получаем Scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
