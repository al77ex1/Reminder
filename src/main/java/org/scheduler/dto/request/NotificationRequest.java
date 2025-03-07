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
    
    @Schema(description = "Name of the job associated with this notification", 
            example = "ChoirRehearsal")
    @NotBlank(message = "Job name is required")
    private String jobName;
    
    @Schema(description = "Name of the trigger for this notification", 
            example = "WeeklyReminder")
    @NotBlank(message = "Trigger name is required")
    private String triggerName;
    
    @Schema(description = "Cron schedule expression for the notification", 
            example = "0 0 18 ? * SAT")
    @NotBlank(message = "Cron schedule is required")
    private String cronSchedule;
    
    @Schema(description = "Message content of the notification", 
            example = "Reminder: Choir rehearsal tomorrow at 10:00 AM")
    @NotBlank(message = "Message is required")
    private String message;
    
    @Schema(description = "Whether the notification is inactive", 
            example = "false", 
            defaultValue = "false")
    private Boolean noActive = false;
}
