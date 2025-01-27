package org.scheduler;
import org.quartz.SchedulerException;
import org.scheduler.jobs.SatMessageBotJob;
import org.scheduler.jobs.JobExecutor;

public class TelegramMessageScheduler {
    public static void main(String[] args) throws SchedulerException {
        JobExecutor executor = new JobExecutor();
        executor.scheduleJob(
                SatMessageBotJob.class,
                "weeklyMessageJob",
                "weeklyTrigger",
                "*/20 * * * * *"
        );
    }
}
