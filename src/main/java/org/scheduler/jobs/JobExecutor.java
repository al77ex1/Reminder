package org.scheduler.jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class JobExecutor {
    private final Scheduler scheduler;

    public JobExecutor() throws SchedulerException {
        this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        this.scheduler.start();
    }

    public void scheduleJob(Class<? extends Job> jobClass, String jobName, String triggerName, String cronExpression) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, "group1")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    public void shutdown() throws SchedulerException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
