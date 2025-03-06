package org.scheduler.interceptor;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder(access = lombok.AccessLevel.PUBLIC)
public class ErrorMessage {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public static ErrorMessageBuilder builder() {
        return new ErrorMessageBuilder()
                .timestamp(LocalDateTime.now());
    }

    public static ErrorMessage fromException(Exception ex, HttpStatus status, String error) {
        return ErrorMessage.builder()
                .status(status.value())
                .error(error)
                .message(ex.getMessage() + " (" + ex.getClass().getSimpleName() + ")")
                .build();
    }
}
