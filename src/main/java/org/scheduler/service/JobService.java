package org.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.scheduler.dto.response.JobResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final Scheduler scheduler;

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
