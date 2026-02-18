package com.quiz.controller;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.ApiResponse;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.service.TestTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/test-types")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class TestTypeController {
    
    private final TestTypeService testTypeService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettingItemResponse>>> getAllTestTypes(
            @RequestParam(required = false) Boolean activeOnly) {
        List<SettingItemResponse> testTypes = activeOnly != null && activeOnly
                ? testTypeService.getActiveTestTypes()
                : testTypeService.getAllTestTypes();
        return ResponseEntity.ok(ApiResponse.success(testTypes));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> getTestTypeById(@PathVariable Integer id) {
        SettingItemResponse testType = testTypeService.getTestTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(testType));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SettingItemResponse>> createTestType(
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse testType = testTypeService.createTestType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test type created successfully", testType));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> updateTestType(
            @PathVariable Integer id,
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse testType = testTypeService.updateTestType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test type updated successfully", testType));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestType(@PathVariable Integer id) {
        testTypeService.deleteTestType(id);
        return ResponseEntity.ok(ApiResponse.success("Test type deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<SettingItemResponse>> toggleTestTypeStatus(@PathVariable Integer id) {
        SettingItemResponse testType = testTypeService.toggleTestTypeStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Test type status updated successfully", testType));
    }
}
