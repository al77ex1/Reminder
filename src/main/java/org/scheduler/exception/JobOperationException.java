package org.scheduler.exception;

/**
 * Исключение, которое выбрасывается при ошибках операций с заданиями планировщика
 */
public class JobOperationException extends RuntimeException {

    public JobOperationException(String message) {
        super(message);
    }

    public JobOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
