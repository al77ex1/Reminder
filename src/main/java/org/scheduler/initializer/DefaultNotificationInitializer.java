package org.scheduler.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.entity.Notification;
import org.scheduler.repository.NotificationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultNotificationInitializer {
    private final NotificationRepository notificationRepository;

    @PostConstruct
    @Transactional
    public void initialize() {
        if (notificationRepository.findByJobName("weeklyMessageJob") != null) return;
        Notification notification = new Notification();
        notification.setJobName("weeklyMessageJob");
        notification.setTriggerName("weeklyTrigger");
        notification.setCronSchedule("0 0 16 ? * SAT");
        notification.setMessage("""
                Дорогие проповедники!

                Если у кого есть готовая проповедь на ближайшее воскресенье.

                То будьте добры - поделитесь драгоценными ссылками из Библии для вашей проповеди в этой группе.

                Все что будет в телеграме, то и будет на экране.""");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
        log.info("Default notification created for weeklyMessageJob");
    }
}
