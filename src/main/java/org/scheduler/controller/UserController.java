package org.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.scheduler.controller.interfaces.UserApi;
import org.scheduler.entity.User;
import org.scheduler.entity.Role;
import org.scheduler.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public User getUserByTelegram(String telegram) {
        return userService.getUserByTelegram(telegram);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return userService.updateUser(id, user);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    @Override
    public Set<Role> getUserRoles(Long id) {
        return userService.getUserRoles(id);
    }

    @Override
    public User addRoleToUser(Long id, Role.RoleName roleName) {
        return userService.addRoleToUser(id, roleName);
    }

    @Override
    public User removeRoleFromUser(Long id, Role.RoleName roleName) {
        return userService.removeRoleFromUser(id, roleName);
    }
}
