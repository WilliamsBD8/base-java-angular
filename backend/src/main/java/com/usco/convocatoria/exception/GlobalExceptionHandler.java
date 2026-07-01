package com.usco.convocatoria.exception;

import java.util.HashMap;
import java.util.Map;
// import java.util.logging.Logger;
import java.util.stream.Collectors;

// import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.usco.convocatoria.common.response.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex
    ) {

        Map<String, Object> errors = new HashMap<>();
        errors.put("details", ex.getDetails());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ApiError.error(
                        ex.getHttpStatus().value() + "",
                        ex.getMessage(),
                        errors
                )
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.error("401", "Credenciales incorrectas", null)
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.error("401", ex.getMessage(), null)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, Object> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                ApiError.error(
                        "400",
                        "Error de validación.",
                        errors
                )
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDatabase(
            DataAccessException ex
    ) {

        // Logger.error("Database error", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.error(
                        "500",
                        "Ha ocurrido un error al acceder a la base de datos.",
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(
            Exception ex
    ) {

        // log.error("Unexpected error", ex);

        System.out.println("Unexpected error: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.error(
                        "500",
                        "Ha ocurrido un error interno.",
                        null
                ));
    }
}
