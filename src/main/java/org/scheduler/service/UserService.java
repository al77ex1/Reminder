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
    public User getUserByTelegram(String telegram) {
        return userRepository.findByTelegram(telegram)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Transactional
    public User createUser(User user) {
        if (user.getTelegram() != null && userRepository.existsByTelegram(user.getTelegram())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram уже существует");
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
        
        // Проверяем, не занят ли новый Telegram другим пользователем
        if (user.getTelegram() != null && !user.getTelegram().equals(existingUser.getTelegram()) 
                && userRepository.existsByTelegram(user.getTelegram())) {
            throw new IllegalArgumentException("Пользователь с таким Telegram уже существует");
        }
        
        existingUser.setName(user.getName());
        existingUser.setLastName(user.getLastName());
        existingUser.setTelegram(user.getTelegram());
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
        
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRoleFromUser(Long userId, Role.RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Роль не найдена"));
        
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        
        return user.getRoles();
    }
}
