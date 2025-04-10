package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.UserApi;
import org.scheduler.dto.mapper.UserMapper;
import org.scheduler.dto.request.UserRequest;
import org.scheduler.dto.response.UserResponse;
import org.scheduler.entity.Role;
import org.scheduler.entity.User;
import org.scheduler.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @PreAuthorize("hasAuthority('VIEW_USERS') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserResponse> userResponses = users.map(userMapper::toResponse);
        return ResponseEntity.ok(userResponses);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_USERS') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> getUserById(Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_USERS') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> getUserByTelegramUserName(String telegramUserName) {
        User user = userService.getUserByTelegramUserName(telegramUserName);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    
    @Override
    @PreAuthorize("hasAuthority('VIEW_USERS') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> getUserByTelegramUserId(Long telegramUserId) {
        User user = userService.getUserByTelegramUserId(telegramUserId);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> updateUser(Long id, UserRequest request) {
        User existingUser = userService.getUserById(id);
        userMapper.updateEntity(existingUser, request);
        User updatedUser = userService.updateUser(id, existingUser);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_USERS') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Set<String>> getUserRoles(Long id) {
        Set<Role> roles = userService.getUserRoles(id);
        Set<String> roleNames = roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(roleNames);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> addRoleToUser(Long id, Role.RoleName roleName) {
        User user = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> removeRoleFromUser(Long id, Role.RoleName roleName) {
        User user = userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
