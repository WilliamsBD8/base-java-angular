package com.usco.convocatoria.app.categories.interfaces;

import com.usco.convocatoria.app.convocations.domain.services.ConvocationService;
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

import com.usco.convocatoria.app.categories.application.request.CategoryCreate;
import com.usco.convocatoria.app.categories.application.request.CategoryUpdate;
import com.usco.convocatoria.app.categories.domain.service.CategoryService;
import com.usco.convocatoria.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor

public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CategoryCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                HttpStatus.CREATED.value() + "",
                "Categoría creada exitosamente",
                categoryService.fromEntity(categoryService.createCategory(request))
            )
        );
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Categorías obtenidas exitosamente",
                categoryService.findAllCategories(page, size)
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Categoría obtenida exitosamente",
                categoryService.findCategory(id)
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdate request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value() + "",
                "Categoría actualizada exitosamente",
                categoryService.fromEntity(categoryService.updateCategory(id, request))
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value() + "", "Categoría eliminada exitosamente"));
    }
}
