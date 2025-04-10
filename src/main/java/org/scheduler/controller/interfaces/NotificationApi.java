package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.dto.request.NotificationRequest;
import org.scheduler.dto.response.NotificationResponse;
import org.scheduler.interceptor.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notifications", description = "Операции связанные с уведомлениями")
@RequestMapping("/api/v1/notification")
public interface NotificationApi {

    @GetMapping
    @Operation(
        summary = "Получить список уведомлений",
        description = "Предоставляет информацию о всех уведомлениях с поддержкой пагинации",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение списка уведомлений",
        content = @Content(schema = @Schema(implementation = NotificationResponse.class))
    )
    ResponseEntity<Page<NotificationResponse>> getNotificationByJobName(
        @Parameter(description = "Параметры пагинации") Pageable params
    );

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить уведомление",
        description = "Предоставляет информацию об уведомлении по его ID",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Успешное получение уведомления",
        content = @Content(schema = @Schema(implementation = NotificationResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Уведомление не найдено",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<NotificationResponse> getNotificationById(
        @Parameter(description = "ID уведомления") @PathVariable Integer id
    );

    @PostMapping
    @Operation(
        summary = "Создать новое уведомление",
        description = "Создает новое уведомление с указанными параметрами",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
        responseCode = "201", 
        description = "Уведомление успешно создано",
        content = @Content(schema = @Schema(implementation = NotificationResponse.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Некорректные данные запроса",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<NotificationResponse> createNotification(
        @Parameter(description = "Данные нового уведомления") @RequestBody NotificationRequest request
    );

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить уведомление",
        description = "Обновляет существующее уведомление по его ID",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Уведомление успешно обновлено",
        content = @Content(schema = @Schema(implementation = NotificationResponse.class))
    )
    @ApiResponse(
        responseCode = "404", 
        description = "Уведомление не найдено",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    @ApiResponse(
        responseCode = "400", 
        description = "Некорректные данные запроса",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<NotificationResponse> updateNotification(
        @Parameter(description = "ID уведомления") @PathVariable Integer id,
        @Parameter(description = "Обновленные данные уведомления") @RequestBody NotificationRequest request
    );

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удалить уведомление",
        description = "Удаляет уведомление по его ID",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponse(responseCode = "204", description = "Уведомление успешно удалено")
    @ApiResponse(
        responseCode = "404", 
        description = "Уведомление не найдено",
        content = @Content(schema = @Schema(implementation = ErrorMessage.class))
    )
    ResponseEntity<Void> deleteNotification(
        @Parameter(description = "ID уведомления") @PathVariable Integer id
    );
}
