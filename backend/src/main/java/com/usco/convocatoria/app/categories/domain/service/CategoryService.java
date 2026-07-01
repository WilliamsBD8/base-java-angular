package com.usco.convocatoria.app.categories.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.usco.convocatoria.app.categories.application.request.CategoryCreate;
import com.usco.convocatoria.app.categories.application.request.CategoryUpdate;
import com.usco.convocatoria.app.categories.application.response.CategoryResponse;
import com.usco.convocatoria.app.categories.domain.model.CategoriesEntity;
import com.usco.convocatoria.app.categories.domain.repository.CategoryRepository;
import com.usco.convocatoria.common.response.ApiPage;
import com.usco.convocatoria.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoriesEntity createCategory(CategoryCreate request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La categoría ya existe", null);
        }
        CategoriesEntity savedCategory = new CategoriesEntity();
        savedCategory.setName(request.getName());
        savedCategory.setDescription(request.getDescription());
        return categoryRepository.save(savedCategory);
    }

    public ApiPage<CategoryResponse> findAllCategories(int page, int size) {

        if (page < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El número de página no puede ser menor a 0", null);
        }
        if (size <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El tamaño de la página no puede ser menor o igual a 0", null);
        }
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "id")
        );
        Page<CategoriesEntity> categories = categoryRepository.findAll(pageable);
        return ApiPage.of(categories.map(this::fromEntity));
    }

    private CategoryResponse fromEntity(CategoriesEntity entity) {
        return CategoryResponse.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    @Transactional
    public CategoriesEntity findCategoryById(Long id) {
        if (id == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El ID de la categoría es requerido", null);
        }
        if (id <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El ID de la categoría no puede ser menor o igual a 0", null);
        }

        if (!categoryRepository.existsById(id)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Categoría no encontrada", null);
        }
        return categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Categoría no encontrada", null));
    }

    @Transactional
    public CategoryResponse findCategory(Long id) {
        CategoriesEntity entity = findCategoryById(id);
        return fromEntity(entity);
    }

    @Transactional
    public CategoriesEntity updateCategory(Long id, CategoryUpdate request) {
        CategoriesEntity category = findCategoryById(id);
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException(HttpStatus.CONFLICT, "La categoría ya existe", null);
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        CategoriesEntity category = findCategoryById(id);
        categoryRepository.delete(category);
    }

}
