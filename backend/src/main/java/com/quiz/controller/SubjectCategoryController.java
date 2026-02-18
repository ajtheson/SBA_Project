package com.quiz.controller;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.ApiResponse;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.service.SubjectCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SubjectCategoryController {
    
    private final SubjectCategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettingItemResponse>>> getAllCategories(
            @RequestParam(required = false) Boolean activeOnly) {
        List<SettingItemResponse> categories = activeOnly != null && activeOnly
                ? categoryService.getActiveCategories()
                : categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> getCategoryById(@PathVariable Integer id) {
        SettingItemResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SettingItemResponse>> createCategory(
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<SettingItemResponse>> toggleCategoryStatus(@PathVariable Integer id) {
        SettingItemResponse category = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Category status updated successfully", category));
    }
}
