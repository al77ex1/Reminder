package org.scheduler.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
            example = "Иван")
    @NotBlank(message = "Имя пользователя обязательно")
    private String name;
    
    @Schema(description = "Фамилия пользователя", 
            example = "Иванов")
    private String lastName;
    
    @Schema(description = "Имя пользователя в Telegram", 
            example = "ivan_ivanov")
    @NotBlank(message = "Имя пользователя в Telegram обязательно")
    private String telegramUserName;
    
    @Schema(description = "ID пользователя в Telegram", 
            example = "123456789")
    private Long telegramUserId;
    
    @Schema(description = "ID чата в Telegram", 
            example = "123456789")
    private Long chatId;
    
    @Schema(description = "Статус активности пользователя (true - неактивен, false - активен)", 
            example = "false")
    private Boolean noActive;
}
