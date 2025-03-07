package org.scheduler.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Response DTO for User operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    @Schema(description = "Уникальный идентификатор пользователя", 
            example = "1")
    private Long id;
    
    @Schema(description = "Имя пользователя", 
            example = "Иван")
    private String name;
    
    @Schema(description = "Фамилия пользователя", 
            example = "Иванов")
    private String lastName;
    
    @Schema(description = "Имя пользователя в Telegram", 
            example = "ivan_ivanov")
    private String telegram;
    
    @Schema(description = "Дата создания пользователя", 
            example = "2025-03-07T14:30:15")
    private LocalDateTime createdAt;
    
    @Schema(description = "Статус активности пользователя (true - неактивен, false - активен)", 
            example = "false")
    private Boolean noActive;
    
    @Schema(description = "Список ролей пользователя")
    private Set<String> roles = new HashSet<>();
}
