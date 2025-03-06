package org.scheduler.jobs;

import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.scheduler.entity.Notification;
import org.scheduler.repository.NotificationRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JobSchedulerConfig {
    private final JobExecutor jobExecutor;
    private final NotificationRepository notificationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void scheduleJobs() throws SchedulerException {
        List<Notification> notifications = notificationRepository.findAll();

        for (Notification notification : notifications) {
            if (Boolean.TRUE.equals(notification.getNoActive())) {
                continue;
            }
            String jobName = notification.getJobName();
            String triggerName = notification.getTriggerName();
            String cronExpression = notification.getCronSchedule();
            String message = notification.getMessage();

            jobExecutor.scheduleJob(TelegramMessageJob.class, jobName, triggerName, cronExpression, message);
        }
    }
}
