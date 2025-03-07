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
    
    @Schema(description = "Unique identifier of the notification", example = "1")
    private Integer id;
    
    @Schema(description = "Name of the job associated with this notification", example = "ChoirRehearsal")
    private String jobName;
    
    @Schema(description = "Name of the trigger for this notification", example = "WeeklyReminder")
    private String triggerName;
    
    @Schema(description = "Cron schedule expression for the notification", example = "0 0 18 ? * SAT")
    private String cronSchedule;
    
    @Schema(description = "Message content of the notification", example = "Reminder: Choir rehearsal tomorrow at 10:00 AM")
    private String message;
    
    @Schema(description = "Timestamp when the notification was created", example = "2025-03-07T10:15:30")
    private LocalDateTime createdAt;
    
    @Schema(description = "Whether the notification is inactive", example = "false")
    private Boolean noActive;
}
