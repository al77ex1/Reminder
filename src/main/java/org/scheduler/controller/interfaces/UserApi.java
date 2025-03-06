package org.scheduler.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scheduler.entity.User;
import org.scheduler.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Users", description = "Операции связанные с пользователями")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @GetMapping
    @Operation(
        summary = "Получить список пользователей",
        description = "Предоставляет информацию о всех пользователях с поддержкой пагинации"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей")
    Page<User> getAllUsers(
        @Parameter(description = "Параметры пагинации") Pageable pageable
    );

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить пользователя по ID",
        description = "Предоставляет информацию о пользователе по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение пользователя")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    User getUserById(
        @Parameter(description = "ID пользователя") @PathVariable Long id
    );

    @GetMapping("/telegram/{telegram}")
    @Operation(
        summary = "Получить пользователя по имени в Telegram",
        description = "Предоставляет информацию о пользователе по его имени в Telegram"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение пользователя")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    User getUserByTelegram(
        @Parameter(description = "Имя пользователя в Telegram") @PathVariable String telegram
    );

    @PostMapping
    @Operation(
        summary = "Создать нового пользователя",
        description = "Создает нового пользователя с указанными параметрами"
    )
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    User createUser(
        @Parameter(description = "Данные нового пользователя") @RequestBody User user
    );

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить пользователя",
        description = "Обновляет существующего пользователя по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    User updateUser(
        @Parameter(description = "ID пользователя") @PathVariable Long id,
        @Parameter(description = "Обновленные данные пользователя") @RequestBody User user
    );

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Удалить пользователя",
        description = "Удаляет пользователя по его ID"
    )
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    void deleteUser(
        @Parameter(description = "ID пользователя") @PathVariable Long id
    );

    @GetMapping("/{id}/roles")
    @Operation(
        summary = "Получить роли пользователя",
        description = "Предоставляет информацию о ролях пользователя по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение ролей пользователя")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    Set<Role> getUserRoles(
        @Parameter(description = "ID пользователя") @PathVariable Long id
    );

    @PostMapping("/{id}/roles/{roleName}")
    @Operation(
        summary = "Добавить роль пользователю",
        description = "Добавляет роль пользователю по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Роль успешно добавлена пользователю")
    @ApiResponse(responseCode = "404", description = "Пользователь или роль не найдены")
    User addRoleToUser(
        @Parameter(description = "ID пользователя") @PathVariable Long id,
        @Parameter(description = "Имя роли") @PathVariable Role.RoleName roleName
    );

    @DeleteMapping("/{id}/roles/{roleName}")
    @Operation(
        summary = "Удалить роль у пользователя",
        description = "Удаляет роль у пользователя по его ID"
    )
    @ApiResponse(responseCode = "200", description = "Роль успешно удалена у пользователя")
    @ApiResponse(responseCode = "404", description = "Пользователь или роль не найдены")
    User removeRoleFromUser(
        @Parameter(description = "ID пользователя") @PathVariable Long id,
        @Parameter(description = "Имя роли") @PathVariable Role.RoleName roleName
    );
}
