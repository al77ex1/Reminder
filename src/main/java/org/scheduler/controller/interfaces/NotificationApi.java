package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notifications", description = "Операции связанные с уведомлениями")
@RequestMapping("/api/v1/notification")
public interface NotificationApi {

    @GetMapping
    @Operation(
        summary = "Получить список уведомлений",
        description = "Предоставляет информацию о всех уведомлениях с поддержкой пагинации"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение списка уведомлений")
    Page<Notification> getNotificationByJobName(
        @Parameter(description = "Параметры пагинации") Pageable params
    );

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить уведомление",
        description = "Предоставляет информацию об уведомлении по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение уведомления")
    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
    Notification getNotificationById(
        @Parameter(description = "ID уведомления") @PathVariable Long id
    );

    @PostMapping
    @Operation(
        summary = "Создать новое уведомление",
        description = "Создает новое уведомление с указанными параметрами"
    )
    @ApiResponse(responseCode = "201", description = "Уведомление успешно создано")
    Notification createNotification(
        @Parameter(description = "Данные нового уведомления") @RequestBody Notification notification
    );

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить уведомление",
        description = "Обновляет существующее уведомление по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Уведомление успешно обновлено")
    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
    Notification updateNotification(
        @Parameter(description = "ID уведомления") @PathVariable Long id,
        @Parameter(description = "Обновленные данные уведомления") @RequestBody Notification notification
    );

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удалить уведомление",
        description = "Удаляет уведомление по его ID"
    )
    @ApiResponse(responseCode = "204", description = "Уведомление успешно удалено")
    @ApiResponse(responseCode = "404", description = "Уведомление не найдено")
    void deleteNotification(
        @Parameter(description = "ID уведомления") @PathVariable Long id
    );
}
