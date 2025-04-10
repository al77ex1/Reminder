package org.scheduler.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.scheduler.entity.User;
import org.scheduler.entity.Role;
import org.scheduler.repository.UserRepository;
import org.scheduler.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "Пользователь не найден";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getUserByTelegramUserName(String telegramUserName) {
        return userRepository.findByTelegramUserName(telegramUserName)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }
    
    @Transactional(readOnly = true)
    public User getUserByTelegramUserId(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean existsByTelegramUserId(Long telegramUserId) {
        return userRepository.existsByTelegramUserId(telegramUserId);
    }

    @Transactional
    public User createUser(User user) {
        // Проверяем уникальность telegramUserName
        if (user.getTelegramUserName() != null && userRepository.existsByTelegramUserName(user.getTelegramUserName())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram username уже существует");
        }
        
        // Проверяем уникальность telegramUserId
        if (user.getTelegramUserId() != null && userRepository.existsByTelegramUserId(user.getTelegramUserId())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram ID уже существует");
        }
        
        user.setId(null);
        user.setCreatedAt(LocalDateTime.now());
        
        // Если роли не указаны, устанавливаем пустой набор
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        // Проверяем, не занят ли новый telegramUserName другим пользователем
        if (user.getTelegramUserName() != null && !user.getTelegramUserName().equals(existingUser.getTelegramUserName()) 
                && userRepository.existsByTelegramUserName(user.getTelegramUserName())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram username уже существует");
        }
        
        // Проверяем, не занят ли новый telegramUserId другим пользователем
        if (user.getTelegramUserId() != null && !user.getTelegramUserId().equals(existingUser.getTelegramUserId()) 
                && userRepository.existsByTelegramUserId(user.getTelegramUserId())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram ID уже существует");
        }
        
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setTelegramUserName(user.getTelegramUserName());
        existingUser.setTelegramUserId(user.getTelegramUserId());
        existingUser.setNoActive(user.getNoActive());
        
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public User addRoleToUser(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Роль не найдена"));
        
        boolean roleAlreadyAssigned = false;
        for (Role existingRole : user.getRoles()) {
            if (existingRole.getId().equals(role.getId())) {
                roleAlreadyAssigned = true;
                break;
            }
        }
        
        if (!roleAlreadyAssigned) {
            Set<Role> updatedRoles = new HashSet<>(user.getRoles());
            updatedRoles.add(role);
            user.setRoles(updatedRoles);
            return userRepository.save(user);
        }
        
        return user;
    }

    @Transactional
    public User removeRoleFromUser(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        if (!roleRepository.findByName(roleName).isPresent()) {
            throw new EntityNotFoundException("Роль не найдена");
        }
        
        Set<Role> updatedRoles = new HashSet<>();
        for (Role existingRole : user.getRoles()) {
            if (!existingRole.getName().equals(roleName)) {
                updatedRoles.add(existingRole);
            }
        }
        
        user.setRoles(updatedRoles);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        return user.getRoles();
    }
}
