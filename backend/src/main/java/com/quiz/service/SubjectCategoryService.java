package com.quiz.service;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.entity.SubjectCategory;
import com.quiz.repository.SubjectCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectCategoryService {
    
    private final SubjectCategoryRepository categoryRepository;
    
    public List<SettingItemResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<SettingItemResponse> getActiveCategories() {
        return categoryRepository.findByStatus(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SettingItemResponse getCategoryById(Integer id) {
        SubjectCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return toResponse(category);
    }
    
    @Transactional
    public SettingItemResponse createCategory(SettingItemRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Category already exists with name: " + request.getName());
        }
        
        SubjectCategory category = new SubjectCategory();
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        
        SubjectCategory savedCategory = categoryRepository.save(category);
        return toResponse(savedCategory);
    }
    
    @Transactional
    public SettingItemResponse updateCategory(Integer id, SettingItemRequest request) {
        SubjectCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        categoryRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Category already exists with name: " + request.getName());
            }
        });
        
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        
        SubjectCategory updatedCategory = categoryRepository.save(category);
        return toResponse(updatedCategory);
    }
    
    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    @Transactional
    public SettingItemResponse toggleCategoryStatus(Integer id) {
        SubjectCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        category.setStatus(!category.getStatus());
        SubjectCategory updated = categoryRepository.save(category);
        return toResponse(updated);
    }
    
    private SettingItemResponse toResponse(SubjectCategory category) {
        return new SettingItemResponse(
                category.getId(),
                category.getName(),
                category.getStatus()
        );
    }
}
