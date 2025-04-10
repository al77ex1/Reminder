package org.scheduler.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.scheduler.dto.response.JobResponse;
import org.scheduler.entity.Notification;
import org.scheduler.exception.JobOperationException;
import org.scheduler.jobs.TelegramMessageJob;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final Scheduler scheduler;
    private static final String DEFAULT_GROUP = "DEFAULT";
    private static final String JOB_NOT_FOUND_MESSAGE = "Задание не найдено: ";
    private static final String PAUSE_ERROR_MESSAGE = "Ошибка при приостановке задания: ";
    private static final String RESUME_ERROR_MESSAGE = "Ошибка при возобновлении задания: ";
    private static final String TRIGGER_ERROR_MESSAGE = "Ошибка при запуске задания: ";
    private static final String DELETE_ERROR_MESSAGE = "Ошибка при удалении задания: ";
    private static final String CREATE_ERROR_MESSAGE = "Ошибка при создании задания из уведомления: ";

    public List<JobResponse> getAllJobs() {
        try {
            List<JobResponse> jobResponses = new ArrayList<>();
            
            // Получаем все группы заданий
            for (String groupName : scheduler.getJobGroupNames()) {
                // Получаем все задания в группе
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    JobResponse jobResponse = getJobResponseByKey(jobKey);
                    if (jobResponse != null) {
                        jobResponses.add(jobResponse);
                    }
                }
            }
            
            return jobResponses;
        } catch (SchedulerException e) {
            log.info("Error getting all jobs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<JobResponse> getJobsByGroup(String groupName) {
        try {
            List<JobResponse> jobResponses = new ArrayList<>();
            
            // Получаем все задания в группе
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                JobResponse jobResponse = getJobResponseByKey(jobKey);
                if (jobResponse != null) {
                    jobResponses.add(jobResponse);
                }
            }
            
            return jobResponses;
        } catch (SchedulerException e) {
            log.info("Error getting jobs by group {}: {}", groupName, e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<JobResponse> getJobByName(String jobName) {
        try {
            // Получаем все группы заданий
            for (String groupName : scheduler.getJobGroupNames()) {
                JobKey jobKey = new JobKey(jobName, groupName);
                if (scheduler.checkExists(jobKey)) {
                    JobResponse jobResponse = getJobResponseByKey(jobKey);
                    return Optional.ofNullable(jobResponse);
                }
            }
            
            return Optional.empty();
        } catch (SchedulerException e) {
            log.info("Error getting job by name {}: {}", jobName, e.getMessage());
            return Optional.empty();
        }
    }

    public List<JobResponse> getRunningJobs() {
        try {
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
            
            return currentlyExecutingJobs.stream()
                    .map(context -> getJobResponseByKey(context.getJobDetail().getKey()))
                    .filter(Objects::nonNull)
                    .toList();
        } catch (SchedulerException e) {
            log.info("Error getting running jobs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public JobResponse pauseJob(String jobName, String groupName) {
        try {
            JobKey jobKey = getJobKey(jobName, groupName);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new EntityNotFoundException(JOB_NOT_FOUND_MESSAGE + jobName);
            }
            
            scheduler.pauseJob(jobKey);
            log.info("Job paused: {}", jobKey);
            
            return getJobResponseByKey(jobKey);
        } catch (SchedulerException e) {
            log.info("Error pausing job {}: {}", jobName, e.getMessage());
            throw new JobOperationException(PAUSE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }
    
    public JobResponse resumeJob(String jobName, String groupName) {
        try {
            JobKey jobKey = getJobKey(jobName, groupName);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new EntityNotFoundException(JOB_NOT_FOUND_MESSAGE + jobName);
            }
            
            scheduler.resumeJob(jobKey);
            log.info("Job resumed: {}", jobKey);
            
            return getJobResponseByKey(jobKey);
        } catch (SchedulerException e) {
            log.info("Error resuming job {}: {}", jobName, e.getMessage());
            throw new JobOperationException(RESUME_ERROR_MESSAGE + e.getMessage(), e);
        }
    }
    
    public JobResponse triggerJob(String jobName, String groupName) {
        try {
            JobKey jobKey = getJobKey(jobName, groupName);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new EntityNotFoundException(JOB_NOT_FOUND_MESSAGE + jobName);
            }
            
            scheduler.triggerJob(jobKey);
            log.info("Job triggered: {}", jobKey);
            
            return getJobResponseByKey(jobKey);
        } catch (SchedulerException e) {
            log.info("Error triggering job {}: {}", jobName, e.getMessage());
            throw new JobOperationException(TRIGGER_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    public boolean deleteJob(String jobName, String groupName) {
        try {
            JobKey jobKey = getJobKey(jobName, groupName);
            
            if (!scheduler.checkExists(jobKey)) {
                throw new EntityNotFoundException(JOB_NOT_FOUND_MESSAGE + jobName);
            }
            
            boolean result = scheduler.deleteJob(jobKey);
            log.info("Job deleted: {}, result: {}", jobKey, result);
            
            return result;
        } catch (SchedulerException e) {
            log.info("Error deleting job {}: {}", jobName, e.getMessage());
            throw new JobOperationException(DELETE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }
    
    public JobResponse createJobFromNotification(Notification notification) {
        try {
            // Создаем JobDetail
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("notificationId", notification.getId());
            jobDataMap.put("message", notification.getMessage());
            
            JobDetail jobDetail = JobBuilder.newJob(TelegramMessageJob.class)
                    .withIdentity(notification.getJobName(), DEFAULT_GROUP)
                    .withDescription("Задание для уведомления #" + notification.getId())
                    .usingJobData(jobDataMap)
                    .build();
            
            // Создаем CronTrigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(notification.getTriggerName(), DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(notification.getCronSchedule()))
                    .build();
            
            // Добавляем задание в планировщик
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Job created from notification: {}", notification.getId());
            
            return getJobResponseByKey(jobDetail.getKey());
        } catch (SchedulerException e) {
            log.info("Error creating job from notification {}: {}", notification.getId(), e.getMessage());
            throw new JobOperationException(CREATE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    private JobKey getJobKey(String jobName, String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            try {
                // Если группа не указана, ищем задание во всех группах
                for (String group : scheduler.getJobGroupNames()) {
                    JobKey key = new JobKey(jobName, group);
                    if (scheduler.checkExists(key)) {
                        return key;
                    }
                }
                // Если задание не найдено, используем группу по умолчанию
                return new JobKey(jobName, DEFAULT_GROUP);
            } catch (SchedulerException e) {
                log.info("Error finding job key: {}", e.getMessage());
                return new JobKey(jobName, DEFAULT_GROUP);
            }
        }
        return new JobKey(jobName, groupName);
    }

    private JobResponse getJobResponseByKey(JobKey jobKey) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            
            if (triggers.isEmpty()) {
                return null;
            }
            
            Trigger trigger = triggers.get(0);
            String cronExpression = "";
            
            if (trigger instanceof CronTrigger cronTrigger) {
                cronExpression = cronTrigger.getCronExpression();
            }
            
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            Map<String, Object> jobData = new HashMap<>();
            
            for (String key : jobDataMap.getKeys()) {
                jobData.put(key, jobDataMap.get(key));
            }
            
            return JobResponse.builder()
                    .jobName(jobKey.getName())
                    .jobGroup(jobKey.getGroup())
                    .description(jobDetail.getDescription())
                    .jobClass(jobDetail.getJobClass().getName())
                    .triggerName(trigger.getKey().getName())
                    .triggerGroup(trigger.getKey().getGroup())
                    .cronExpression(cronExpression)
                    .nextFireTime(trigger.getNextFireTime())
                    .previousFireTime(trigger.getPreviousFireTime())
                    .state(triggerState.name())
                    .jobData(jobData)
                    .build();
        } catch (SchedulerException e) {
            log.info("Error getting job details for {}: {}", jobKey, e.getMessage());
            return null;
        }
    }
}
