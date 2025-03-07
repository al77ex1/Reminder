package org.scheduler.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    @Schema(description = "Уникальный идентификатор уведомления", example = "1")
    private Integer id;
    
    @Schema(description = "Название работы, связанной с этим уведомлением", example = "ChoirRehearsal")
    private String jobName;
    
    @Schema(description = "Имя триггера для этого уведомления", example = "WeeklyReminder")
    private String triggerName;
    
    @Schema(description = "Cron выражение для расписания уведомления", example = "0 0 18 ? * SAT")
    private String cronSchedule;
    
    @Schema(description = "Сообщение уведомления", example = "Reminder: Choir rehearsal tomorrow at 10:00 AM")
    private String message;
    
    @Schema(description = "Временная метка создания уведомления", example = "2025-03-07T10:15:30")
    private LocalDateTime createdAt;
    
    @Schema(description = "Является ли уведомление неактивным", example = "false")
    private Boolean noActive;
}
