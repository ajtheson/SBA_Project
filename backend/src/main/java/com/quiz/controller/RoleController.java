package com.quiz.controller;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.ApiResponse;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettingItemResponse>>> getAllRoles(
            @RequestParam(required = false) Boolean activeOnly) {
        List<SettingItemResponse> roles = activeOnly != null && activeOnly
                ? roleService.getActiveRoles()
                : roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> getRoleById(@PathVariable Integer id) {
        SettingItemResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SettingItemResponse>> createRole(
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", role));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettingItemResponse>> updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody SettingItemRequest request) {
        SettingItemResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", role));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<SettingItemResponse>> toggleRoleStatus(@PathVariable Integer id) {
        SettingItemResponse role = roleService.toggleRoleStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Role status updated successfully", role));
    }
}
