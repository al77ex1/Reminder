package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.dto.request.UserRequest;
import org.scheduler.dto.response.UserResponse;
import org.scheduler.entity.Role;
import org.scheduler.interceptor.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@Tag(name = "Users", description = "Операции связанные с пользователями")
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public interface UserApi {

    @GetMapping
    @Operation(
        summary = "Получить список пользователей",
        description = "Предоставляет информацию о всех пользователях с поддержкой пагинации"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение списка пользователей",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    ResponseEntity<Page<UserResponse>> getAllUsers(
        @Parameter(description = "Параметры пагинации") Pageable pageable
    );

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить пользователя по ID",
        description = "Предоставляет информацию о пользователе по его ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение пользователя",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "ID пользователя") @PathVariable UUID id
    );

    @GetMapping("/telegram-username/{telegramUserName}")
    @Operation(
        summary = "Получить пользователя по имени пользователя в Telegram",
        description = "Предоставляет информацию о пользователе по его имени пользователя в Telegram"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение пользователя",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> getUserByTelegramUserName(
        @Parameter(description = "Имя пользователя в Telegram") @PathVariable String telegramUserName
    );

    @GetMapping("/telegram-id/{telegramUserId}")
    @Operation(
        summary = "Получить пользователя по ID в Telegram",
        description = "Предоставляет информацию о пользователе по его ID в Telegram"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение пользователя",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> getUserByTelegramUserId(
        @Parameter(description = "ID пользователя в Telegram") @PathVariable Long telegramUserId
    );

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить пользователя",
        description = "Обновляет существующего пользователя по его ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Пользователь успешно обновлен",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Некорректные данные запроса",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> updateUser(
        @Parameter(description = "ID пользователя") @PathVariable UUID id,
        @Parameter(description = "Обновленные данные пользователя") @RequestBody UserRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удалить пользователя",
        description = "Удаляет пользователя по его ID"
    )
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID пользователя") @PathVariable UUID id
    );

    @GetMapping("/{id}/roles")
    @Operation(
        summary = "Получить роли пользователя",
        description = "Предоставляет информацию о ролях пользователя по его ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение ролей пользователя"
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь не найден",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<Set<String>> getUserRoles(
        @Parameter(description = "ID пользователя") @PathVariable UUID id
    );

    @PostMapping("/{id}/roles/{roleName}")
    @Operation(
        summary = "Добавить роль пользователю",
        description = "Добавляет роль пользователю по его ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Роль успешно добавлена пользователю",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь или роль не найдены",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Некорректные данные запроса",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> addRoleToUser(
        @Parameter(description = "ID пользователя") @PathVariable UUID id,
        @Parameter(description = "Имя роли") @PathVariable Role.RoleName roleName
    );

    @DeleteMapping("/{id}/roles/{roleName}")
    @Operation(
        summary = "Удалить роль у пользователя",
        description = "Удаляет роль у пользователя по его ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Роль успешно удалена у пользователя",
        content = @Content(schema = @Schema(implementation = UserResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Пользователь или роль не найдены",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Некорректные данные запроса",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<UserResponse> removeRoleFromUser(
        @Parameter(description = "ID пользователя") @PathVariable UUID id,
        @Parameter(description = "Имя роли") @PathVariable Role.RoleName roleName
    );
}
