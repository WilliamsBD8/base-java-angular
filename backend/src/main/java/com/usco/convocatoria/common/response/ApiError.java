package com.usco.convocatoria.common.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private boolean success;
    private String code;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private Map<String, Object> errors;

    public static ApiError error(String code, String message) {
        return error(code, message, null);
    }

    public static ApiError error(
            String code,
            String message,
            Map<String, Object> errors
    ) {
        return ApiError.builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
