package com.usco.convocatoria.app.convocations.interfaces;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usco.convocatoria.app.convocations.application.request.ConvocationCreate;
import com.usco.convocatoria.app.convocations.application.request.ConvocationUpdate;
import com.usco.convocatoria.app.convocations.domain.model.enums.ConvocationsStates;
import com.usco.convocatoria.app.convocations.domain.services.ConvocationService;
import com.usco.convocatoria.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/convocations")
@RequiredArgsConstructor

public class ConvocationController {

    private final ConvocationService convocationService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> createConvocation(@Valid @RequestBody ConvocationCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                HttpStatus.CREATED.value() + "",
                "Convocatoria creada exitosamente",
                convocationService.fromEntity(convocationService.createConvocation(request))
            )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllConvocations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatorias obtenidas exitosamente",
                convocationService.findAllConvocations(page, size)
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getConvocationById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatoria obtenida exitosamente",
                convocationService.fromEntity(convocationService.findConvocationById(id))
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> updateConvocation(@PathVariable Long id, @Valid @RequestBody ConvocationUpdate request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatoria actualizada exitosamente",
                convocationService.fromEntity(convocationService.updateConvocation(id, request))
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteConvocation(@PathVariable Long id) {
        convocationService.deleteConvocation(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value() + "", "Convocatoria eliminada exitosamente"));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> publishConvocation(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatoria publicada exitosamente",
                convocationService.fromEntity(convocationService.updateConvocationState(id, ConvocationsStates.PUBLICADA))
            )
        );
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> closeConvocation(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Convocatoria cerrada exitosamente",
                convocationService.fromEntity(convocationService.updateConvocationState(id, ConvocationsStates.CERRADA))
            )
        );
    }
}
