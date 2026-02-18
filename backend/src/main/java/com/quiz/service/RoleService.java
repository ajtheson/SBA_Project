package com.quiz.service;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.entity.Role;
import com.quiz.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    public List<SettingItemResponse> getAllRoles() {
        return roleRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<SettingItemResponse> getActiveRoles() {
        return roleRepository.findByStatus(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SettingItemResponse getRoleById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return toResponse(role);
    }
    
    @Transactional
    public SettingItemResponse createRole(SettingItemRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role already exists with name: " + request.getName());
        }
        
        Role role = new Role();
        role.setName(request.getName());
        role.setStatus(request.getStatus());
        
        Role savedRole = roleRepository.save(role);
        return toResponse(savedRole);
    }
    
    @Transactional
    public SettingItemResponse updateRole(Integer id, SettingItemRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        // Check if name already exists (excluding current role)
        roleRepository.findByName(request.getName()).ifPresent(existingRole -> {
            if (!existingRole.getId().equals(id)) {
                throw new RuntimeException("Role already exists with name: " + request.getName());
            }
        });
        
        role.setName(request.getName());
        role.setStatus(request.getStatus());
        
        Role updatedRole = roleRepository.save(role);
        return toResponse(updatedRole);
    }
    
    @Transactional
    public void deleteRole(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
    
    @Transactional
    public SettingItemResponse toggleRoleStatus(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        role.setStatus(!role.getStatus());
        Role updatedRole = roleRepository.save(role);
        return toResponse(updatedRole);
    }
    
    private SettingItemResponse toResponse(Role role) {
        return new SettingItemResponse(
                role.getId(),
                role.getName(),
                role.getStatus()
        );
    }
}
