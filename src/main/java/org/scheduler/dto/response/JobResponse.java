package org.scheduler.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * Response DTO for Job information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    
    @Schema(description = "Имя задания", 
            example = "weeklyMessageJob")
    private String jobName;
    
    @Schema(description = "Группа задания", 
            example = "DEFAULT")
    private String jobGroup;
    
    @Schema(description = "Описание задания", 
            example = "Отправка еженедельного сообщения")
    private String description;
    
    @Schema(description = "Класс задания", 
            example = "org.scheduler.job.WeeklyMessageJob")
    private String jobClass;
    
    @Schema(description = "Имя триггера", 
            example = "weeklyTrigger")
    private String triggerName;
    
    @Schema(description = "Группа триггера", 
            example = "DEFAULT")
    private String triggerGroup;
    
    @Schema(description = "Выражение cron", 
            example = "0 0 16 ? * SAT")
    private String cronExpression;
    
    @Schema(description = "Следующее время запуска", 
            example = "2025-04-13T16:00:00")
    private Date nextFireTime;
    
    @Schema(description = "Предыдущее время запуска", 
            example = "2025-04-06T16:00:00")
    private Date previousFireTime;
    
    @Schema(description = "Состояние задания", 
            example = "NORMAL")
    private String state;
    
    @Schema(description = "Данные задания")
    private Map<String, Object> jobData;
}
