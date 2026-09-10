package com.ayushcart.service;

import com.ayushcart.dto.CategoryRequest;
import com.ayushcart.dto.CategoryResponse;
import com.ayushcart.entity.Category;
import com.ayushcart.exception.BadRequestException;
import com.ayushcart.exception.ConflictException;
import com.ayushcart.exception.ResourceNotFoundException;
import com.ayushcart.repository.CategoryRepository;
import com.ayushcart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Category '" + name + "' already exists");
        }
        return CategoryResponse.from(categoryRepository.save(new Category(name, request.description())));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id);
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Category '" + name + "' already exists");
        }
        category.setName(name);
        category.setDescription(request.description());
        // No save() needed: changes to a managed entity are flushed when the transaction commits
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getEntity(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new BadRequestException("This category still has products. Move or remove them first.");
        }
        categoryRepository.delete(category);
    }

    Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
    }
}
