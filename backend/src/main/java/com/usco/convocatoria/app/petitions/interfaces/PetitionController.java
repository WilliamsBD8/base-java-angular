package com.usco.convocatoria.app.petitions.interfaces;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usco.convocatoria.app.petitions.application.request.PetitionCreate;
import com.usco.convocatoria.app.petitions.application.request.PetitionUpdate;
import com.usco.convocatoria.app.petitions.domain.service.PetitionService;
import com.usco.convocatoria.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/petitions")
@RequiredArgsConstructor

public class PetitionController {

    private final PetitionService petitionService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> createPetition(@Valid @RequestBody PetitionCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                HttpStatus.CREATED.value() + "",
                "Petición creada exitosamente",
                petitionService.fromEntity(petitionService.createPetition(request))
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> getPetitionById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Petición obtenida exitosamente",
                petitionService.findPetition(id)
            )
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> getAllPetitions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Peticiones obtenidas exitosamente",
                petitionService.findAllPetitions(page, size)
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> updatePetition(@PathVariable Long id, @Valid @RequestBody PetitionUpdate request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Petición actualizada exitosamente",
                petitionService.fromEntity(petitionService.updatePetition(id, request))
            )
        );
    }

}
