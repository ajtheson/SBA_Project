package com.quiz.controller;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.ApiResponse;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.service.LessonTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/lesson-types")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class LessonTypeController {
    
    private final LessonTypeService lessonTypeService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettingItemResponse>>> getAllLessonTypes(
            @RequestParam(required = false) Boolean activeOnly) {
        List<SettingItemResponse> types = activeOnly != null && activeOnly
                ? lessonTypeService.getActiveLessonTypes()
                : lessonTypeService.getAllLessonTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> getLessonTypeById(@PathVariable Integer id) {
        SettingItemResponse type = lessonTypeService.getLessonTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(type));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SettingItemResponse>> createLessonType(
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse type = lessonTypeService.createLessonType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson type created successfully", type));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> updateLessonType(
            @PathVariable Integer id,
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse type = lessonTypeService.updateLessonType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Lesson type updated successfully", type));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLessonType(@PathVariable Integer id) {
        lessonTypeService.deleteLessonType(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson type deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<SettingItemResponse>> toggleLessonTypeStatus(@PathVariable Integer id) {
        SettingItemResponse type = lessonTypeService.toggleLessonTypeStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson type status updated successfully", type));
    }
}
