package org.scheduler.dto.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.scheduler.dto.request.NotificationRequest;
import org.scheduler.dto.response.NotificationResponse;
import org.scheduler.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface NotificationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "noActive", expression = "java(request.getNoActive() != null ? request.getNoActive() : false)")
    Notification toEntity(NotificationRequest request);
    
    NotificationResponse toResponse(Notification notification);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget Notification notification, NotificationRequest request);
    
    static NotificationMapper getInstance() {
        return Mappers.getMapper(NotificationMapper.class);
    }
    
    static void updateEntityStatic(Notification notification, NotificationRequest request) {
        getInstance().updateEntity(notification, request);
    }
    
    static Notification toEntityStatic(NotificationRequest request) {
        return getInstance().toEntity(request);
    }
    
    static NotificationResponse toResponseStatic(Notification notification) {
        return getInstance().toResponse(notification);
    }
    
    static List<NotificationResponse> toResponseListStatic(List<Notification> notifications) {
        return notifications.stream()
            .map(getInstance()::toResponse)
            .toList();
    }
}
