package org.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.scheduler.entity.Notification;
import org.scheduler.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Notification getNotificationByJobName(String jobName) {
        return notificationRepository.findByJobName(jobName);
    }
}
