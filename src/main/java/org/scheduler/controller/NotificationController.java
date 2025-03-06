package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.NotificationApi;
import org.scheduler.entity.Notification;
import org.scheduler.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final NotificationService notificationService;

    @Override
    public Page<Notification> getNotificationByJobName(Pageable params) {
        return notificationService.getAllNotifications(params);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationService.getNotificationById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public Notification createNotification(Notification notification) {
        return notificationService.createNotification(notification);
    }

    @Override
    public Notification updateNotification(Long id, Notification notification) {
        return notificationService.updateNotification(id, notification);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(Long id) {
        notificationService.deleteNotification(id);
    }
}
