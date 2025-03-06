package org.scheduler.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.scheduler.entity.Notification;
import org.scheduler.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String NOTIFICATION_NOT_FOUND = "Уведомление не найдено";
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOTIFICATION_NOT_FOUND));
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setId(null);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification updateNotification(Long id, Notification notification) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOTIFICATION_NOT_FOUND));
        
        existingNotification.setJobName(notification.getJobName());
        existingNotification.setTriggerName(notification.getTriggerName());
        existingNotification.setCronSchedule(notification.getCronSchedule());
        existingNotification.setMessage(notification.getMessage());
        existingNotification.setNoActive(notification.getNoActive());
        
        return notificationRepository.save(existingNotification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new EntityNotFoundException(NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(id);
    }
}
