package org.scheduler.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @Schema(description = "Название работы, связанной с этим уведомлением", example = "ChoirRehearsal")
    @NotBlank(message = "Название работы обязательно")
    private String jobName;
    
    @Schema(description = "Имя триггера для этого уведомления", example = "WeeklyReminder")
    @NotBlank(message = "Название триггера обязательно")
    private String triggerName;
    
    @Schema(description = "Cron выражение для расписания уведомления", example = "0 0 18 ? * SAT")
    @NotBlank(message = "Cron выражение обязательно")
    private String cronSchedule;
    
    @Schema(description = "Сообщение уведомления", example = "Reminder: Choir rehearsal tomorrow at 10:00 AM")
    @NotBlank(message = "Сообщение обязательно")
    private String message;
    
    @Schema(description = "Является ли уведомление неактивным", example = "false", defaultValue = "false")
    private Boolean noActive = false;
}
