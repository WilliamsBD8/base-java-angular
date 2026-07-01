package com.usco.convocatoria.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.usco.convocatoria.common.response.ApiError;

import jakarta.annotation.PostConstruct;

@RestControllerAdvice
public class GlobalExceptionHandler {

        ObjectMapper objectMapper = new ObjectMapper();
        private final Map<String, String> CONSTRAINT_MESSAGES = new HashMap<>();

        private static class ConstraintMessages {
                private String code;
                private String message;

                public String getMessage() {
                return message;
                }

                public String getCode() {
                return code;
                }

                public void setMessage(String message) {
                this.message = message;
                }

                public void setCode(String code) {
                this.code = code;
                }
        }

        @PostConstruct
        public void loadConstraintMessages() {

                try {

                        Resource[] resources = new PathMatchingResourcePatternResolver()
                                .getResources("classpath:jsons/*.json");

                        for (Resource resource : resources) {

                                System.out.println("Procesando archivo: " + resource.getFilename());

                                var list = objectMapper.readValue(
                                                resource.getInputStream(),
                                                new TypeReference<java.util.List<ConstraintMessages>>() {
                                        });

                                for (ConstraintMessages item : list) {
                                        CONSTRAINT_MESSAGES.put(item.getCode(), item.getMessage());
                                }

                        }

                        System.out.println("✔ Mensajes de constraints cargados: " + CONSTRAINT_MESSAGES.size());

                } catch (Exception e) {
                        System.out.println("⚠ Error leyendo JSON: " + e.getMessage());
                        throw new RuntimeException("Error leyendo archivos JSON", e);
                }
        }
        
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

                Throwable root = ex.getRootCause();

                String rootMessage = root != null
                        ? root.getMessage()
                        : ex.getMessage();

                // Logger.error("Database error", ex);

                Optional<String> errorMessage = CONSTRAINT_MESSAGES.entrySet()
                        .stream()
                        .filter(entry -> rootMessage != null && rootMessage.contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst();

                System.out.println("Database error: " + rootMessage);

                if (errorMessage.isPresent()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.error("400", errorMessage.get(), null));
                }
                

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
