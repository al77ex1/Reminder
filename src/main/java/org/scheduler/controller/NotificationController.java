package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.NotificationApi;
import org.scheduler.entity.Notification;
import org.scheduler.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController implements NotificationApi {
    private final NotificationService notificationService;

    @GetMapping("/")
    public Page<Notification> getNotificationByJobName(Pageable params) {
        return notificationService.getAllNotifications(params);
    }
}
