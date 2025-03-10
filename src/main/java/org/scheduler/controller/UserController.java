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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserResponse> userResponses = users.map(userMapper::toResponse);
        return ResponseEntity.ok(userResponses);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<UserResponse> getUserByTelegram(String telegram) {
        User user = userService.getUserByTelegram(telegram);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(createdUser));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(Long id, UserRequest request) {
        User existingUser = userService.getUserById(id);
        userMapper.updateEntity(existingUser, request);
        User updatedUser = userService.updateUser(id, existingUser);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Set<String>> getUserRoles(Long id) {
        Set<Role> roles = userService.getUserRoles(id);
        Set<String> roleNames = roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        return ResponseEntity.ok(roleNames);
    }

    @Override
    public ResponseEntity<UserResponse> addRoleToUser(Long id, Role.RoleName roleName) {
        User user = userService.addRoleToUser(id, roleName);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<UserResponse> removeRoleFromUser(Long id, Role.RoleName roleName) {
        User user = userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
