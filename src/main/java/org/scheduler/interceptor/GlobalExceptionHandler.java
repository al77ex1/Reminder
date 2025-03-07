package org.scheduler.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.exception.ApplicationRuntimeException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationRuntimeException.class)
    public ResponseEntity<ErrorMessage> handleApplicationRuntimeException(ApplicationRuntimeException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorMessage> handlePropertyReferenceException(PropertyReferenceException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Property Reference")
                .message(String.format("Property '%s' does not exist in entity '%s'", 
                        ex.getPropertyName(), ex.getType().getType().getSimpleName()))
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllUncaughtException(Exception ex) {
        ErrorMessage errorMessage = ErrorMessage.fromException(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
