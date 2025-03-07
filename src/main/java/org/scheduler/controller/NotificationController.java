package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.NotificationApi;
import org.scheduler.dto.mapper.NotificationMapper;
import org.scheduler.dto.request.NotificationRequest;
import org.scheduler.dto.response.NotificationResponse;
import org.scheduler.entity.Notification;
import org.scheduler.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<Page<NotificationResponse>> getNotificationByJobName(Pageable params) {
        Page<Notification> notificationsPage = notificationService.getAllNotifications(params);
        
        Page<NotificationResponse> responsePage = new PageImpl<>(
            notificationsPage.getContent().stream()
                .map(NotificationMapper::toResponseStatic)
                .toList(),
            notificationsPage.getPageable(),
            notificationsPage.getTotalElements()
        );
        
        return ResponseEntity.ok(responsePage);
    }

    @Override
    public ResponseEntity<NotificationResponse> getNotificationById(Integer id) {
        Notification notification = notificationService.getNotificationById(id.longValue());
        return ResponseEntity.ok(NotificationMapper.toResponseStatic(notification));
    }

    @Override
    public ResponseEntity<NotificationResponse> createNotification(NotificationRequest request) {
        Notification notification = NotificationMapper.toEntityStatic(request);
        Notification savedNotification = notificationService.createNotification(notification);
        NotificationResponse response = NotificationMapper.toResponseStatic(savedNotification);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<NotificationResponse> updateNotification(Integer id, NotificationRequest request) {
        Notification existingNotification = notificationService.getNotificationById(id.longValue());
        NotificationMapper.updateEntityStatic(existingNotification, request);
        Notification updatedNotification = notificationService.updateNotification(id.longValue(), existingNotification);
        NotificationResponse response = NotificationMapper.toResponseStatic(updatedNotification);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteNotification(Integer id) {
        notificationService.deleteNotification(id.longValue());
        return ResponseEntity.noContent().build();
    }
}
