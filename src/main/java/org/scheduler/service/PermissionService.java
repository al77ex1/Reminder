package org.scheduler.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.scheduler.entity.Permission;
import org.scheduler.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private static final String PERMISSION_NOT_FOUND = "Право не найдено";
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND));
    }

    @Transactional
    public Permission createPermission(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new IllegalArgumentException("Право с таким именем уже существует");
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission updatePermission(Long id, Permission permission) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND));
        
        // Проверяем, не занято ли новое имя другим правом
        if (!existingPermission.getName().equals(permission.getName()) && 
                permissionRepository.existsByName(permission.getName())) {
            throw new IllegalArgumentException("Право с таким именем уже существует");
        }
        
        existingPermission.setName(permission.getName());
        existingPermission.setDescription(permission.getDescription());
        
        return permissionRepository.save(existingPermission);
    }

    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new EntityNotFoundException(PERMISSION_NOT_FOUND);
        }
        permissionRepository.deleteById(id);
    }
}
