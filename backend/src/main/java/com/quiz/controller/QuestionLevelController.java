package com.quiz.controller;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.ApiResponse;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.service.QuestionLevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/question-levels")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class QuestionLevelController {
    
    private final QuestionLevelService questionLevelService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettingItemResponse>>> getAllQuestionLevels(
            @RequestParam(required = false) Boolean activeOnly) {
        List<SettingItemResponse> levels = activeOnly != null && activeOnly
                ? questionLevelService.getActiveQuestionLevels()
                : questionLevelService.getAllQuestionLevels();
        return ResponseEntity.ok(ApiResponse.success(levels));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> getQuestionLevelById(@PathVariable Integer id) {
        SettingItemResponse level = questionLevelService.getQuestionLevelById(id);
        return ResponseEntity.ok(ApiResponse.success(level));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SettingItemResponse>> createQuestionLevel(
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse level = questionLevelService.createQuestionLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question level created successfully", level));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> updateQuestionLevel(
            @PathVariable Integer id,
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse level = questionLevelService.updateQuestionLevel(id, request);
        return ResponseEntity.ok(ApiResponse.success("Question level updated successfully", level));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionLevel(@PathVariable Integer id) {
        questionLevelService.deleteQuestionLevel(id);
        return ResponseEntity.ok(ApiResponse.success("Question level deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<SettingItemResponse>> toggleQuestionLevelStatus(@PathVariable Integer id) {
        SettingItemResponse level = questionLevelService.toggleQuestionLevelStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Question level status updated successfully", level));
    }
}
