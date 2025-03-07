package org.scheduler.dto.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.scheduler.dto.request.UserRequest;
import org.scheduler.dto.response.UserResponse;
import org.scheduler.entity.Permission;
import org.scheduler.entity.Role;
import org.scheduler.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "noActive", expression = "java(request.getNoActive() != null ? request.getNoActive() : false)")
    User toEntity(UserRequest request);
    
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    @Mapping(target = "permissions", expression = "java(mapPermissions(user.getRoles()))")
    UserResponse toResponse(User user);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(@MappingTarget User user, UserRequest request);
    
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
    
    default Set<String> mapPermissions(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
    
    static UserMapper getInstance() {
        return Mappers.getMapper(UserMapper.class);
    }
    
    static void updateEntityStatic(User user, UserRequest request) {
        getInstance().updateEntity(user, request);
    }
    
    static User toEntityStatic(UserRequest request) {
        return getInstance().toEntity(request);
    }
    
    static UserResponse toResponseStatic(User user) {
        return getInstance().toResponse(user);
    }
    
    static List<UserResponse> toResponseListStatic(List<User> users) {
        return users.stream()
            .map(getInstance()::toResponse)
            .toList();
    }
}
