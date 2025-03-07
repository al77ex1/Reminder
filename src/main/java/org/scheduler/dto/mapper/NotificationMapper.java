package org.scheduler.dto.mapper;

import org.mapstruct.*;
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
    
    class NotificationMapperUtil {
        private NotificationMapperUtil() {
            // Private constructor to prevent instantiation
        }
        
        private static final NotificationMapper INSTANCE = 
            org.mapstruct.factory.Mappers.getMapper(NotificationMapper.class);
            
        public static void updateEntityStatic(Notification notification, NotificationRequest request) {
            INSTANCE.updateEntity(notification, request);
        }
        
        public static Notification toEntityStatic(NotificationRequest request) {
            return INSTANCE.toEntity(request);
        }
        
        public static NotificationResponse toResponseStatic(Notification notification) {
            return INSTANCE.toResponse(notification);
        }
        
        public static List<NotificationResponse> toResponseListStatic(List<Notification> notifications) {
            return notifications.stream()
                .map(INSTANCE::toResponse)
                .toList();
        }
    }
}
