package org.scheduler.jobs;

import lombok.AllArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobExecutor {
    private final Scheduler scheduler;

    public void scheduleJob(Class<? extends Job> jobClass, String jobName, String triggerName, String cronExpression, String message) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, "group1")
                .build();

        job.getJobDataMap().put("message", message);

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
