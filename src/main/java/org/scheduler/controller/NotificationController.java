package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.entity.Notification;
import org.scheduler.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notification")
    public Notification getNotificationByJobName(@RequestParam String jobName) {
        return notificationService.getNotificationByJobName(jobName);
    }
}
