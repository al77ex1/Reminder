package org.scheduler.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.scheduler.exception.ApplicationRuntimeException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationRuntimeException.class)
    public ResponseEntity<ErrorMessage> handleApplicationException(ApplicationRuntimeException ex, WebRequest request) {
        log.info("Application exception: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.info("Access denied: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.info("Authentication error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
    
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorMessage> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) {
        log.info("Invalid property reference: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGlobalException(Exception ex, WebRequest request) {
        log.info("Unexpected error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    
    private ResponseEntity<ErrorMessage> createErrorResponse(HttpStatus status, String message) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
        
        return new ResponseEntity<>(errorMessage, status);
    }
}
