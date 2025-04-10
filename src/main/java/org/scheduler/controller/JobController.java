package org.scheduler.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.JobApi;
import org.scheduler.dto.response.JobResponse;
import org.scheduler.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController implements JobApi {

    private final JobService jobService;
    private static final String JOB_NOT_FOUND_MESSAGE = "Задание не найдено: ";
    private static final String GROUP_NOT_FOUND_MESSAGE = "Группа заданий не найдена: ";

    @Override
    @PreAuthorize("hasAuthority('VIEW_NOTIFICATION') or hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_NOTIFICATION') or hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<List<JobResponse>> getJobsByGroup(String groupName) {
        List<JobResponse> jobs = jobService.getJobsByGroup(groupName);
        if (jobs.isEmpty()) {
            throw new EntityNotFoundException(GROUP_NOT_FOUND_MESSAGE + groupName);
        }
        return ResponseEntity.ok(jobs);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_NOTIFICATION') or hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<JobResponse> getJobByName(String jobName) {
        JobResponse job = jobService.getJobByName(jobName)
                .orElseThrow(() -> new EntityNotFoundException(JOB_NOT_FOUND_MESSAGE + jobName));
        return ResponseEntity.ok(job);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_NOTIFICATION') or hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<List<JobResponse>> getRunningJobs() {
        List<JobResponse> jobs = jobService.getRunningJobs();
        return ResponseEntity.ok(jobs);
    }
    
    @Override
    @PreAuthorize("hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<JobResponse> pauseJob(String jobName, String groupName) {
        JobResponse job = jobService.pauseJob(jobName, groupName);
        return ResponseEntity.ok(job);
    }
    
    @Override
    @PreAuthorize("hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<JobResponse> resumeJob(String jobName, String groupName) {
        JobResponse job = jobService.resumeJob(jobName, groupName);
        return ResponseEntity.ok(job);
    }
    
    @Override
    @PreAuthorize("hasAuthority('MANAGE_NOTIFICATION')")
    public ResponseEntity<JobResponse> triggerJob(String jobName, String groupName) {
        JobResponse job = jobService.triggerJob(jobName, groupName);
        return ResponseEntity.ok(job);
    }
}
