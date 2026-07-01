package com.usco.convocatoria.app.reports.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usco.convocatoria.app.reports.domain.service.ReportService;
import com.usco.convocatoria.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/convocations-categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getConvocationsCategories() {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatorias por categorías obtenidas exitosamente",
                reportService.getConvocationCategories()
            )
        );
    }

    @GetMapping("/petitions-convocations")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getPetitionsConvocations() {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Peticiones por convocatorias obtenidas exitosamente",
                reportService.getPetitionConvocation()
            )
        );
    }

    @GetMapping("/petitions-states")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getPetitionsStates() {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Estados de peticiones obtenidos exitosamente",
                reportService.getPetitionStates()
            )
        );
    }
}
