package org.scheduler.jobs;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobExecutor {
    private final Scheduler scheduler;

    @Autowired
    public JobExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
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
