package org.scheduler.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.scheduler.entity.Role;
import org.scheduler.entity.Permission;
import org.scheduler.repository.RoleRepository;
import org.scheduler.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private static final String ROLE_NOT_FOUND = "Роль не найдена";
    private static final String PERMISSION_NOT_FOUND = "Право не найдено";
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(Role.RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
    }

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Роль с таким именем уже существует");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, Role role) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
        
        // Проверяем, не занято ли новое имя другой ролью
        if (role.getName() != existingRole.getName() && 
                roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Роль с таким именем уже существует");
        }
        
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException(ROLE_NOT_FOUND);
        }
        roleRepository.deleteById(id);
    }

    @Transactional
    public Role addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND));
        
        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
        
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND));
        
        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public Set<Permission> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND));
        
        return role.getPermissions();
    }
}
