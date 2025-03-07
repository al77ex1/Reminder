package org.scheduler.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for User operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    
    @Schema(description = "Имя пользователя", 
            example = "Иван", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "Фамилия пользователя", 
            example = "Иванов")
    private String lastName;
    
    @Schema(description = "Имя пользователя в Telegram", 
            example = "ivan_ivanov")
    private String telegram;
    
    @Schema(description = "Статус активности пользователя (true - неактивен, false - активен)", 
            example = "false")
    private Boolean noActive;
}
