package org.scheduler.jobs;

import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobSchedulerConfig {
    private final JobExecutor jobExecutor;

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleJobs() throws SchedulerException {
        jobExecutor.scheduleJob(
                SatMessageBotJob.class,
                "weeklyMessageJob",
                "weeklyTrigger",
                "0 0 16 ? * SAT"
        );
    }
}
