package com.usco.convocatoria.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;
    private List<Map<String, String>> details;

    public BusinessException(HttpStatus httpStatus, String message, List<Map<String, String>> details) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = details;
    }

}
