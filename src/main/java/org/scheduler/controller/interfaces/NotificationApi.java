package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Notifications", description = "Операции связанные с уведомлениями")
public interface NotificationApi {

    @Operation(
        summary = "Получить список уведомлений",
        description = "Предоставляет информацию о всех уведомлениях с поддержкой пагинации"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение списка уведомлений")
    Page<Notification> getNotificationByJobName(
        @Parameter(description = "Параметры пагинации") Pageable params
    );
}
