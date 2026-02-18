package com.quiz.service;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.entity.TestType;
import com.quiz.repository.TestTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestTypeService {
    
    private final TestTypeRepository testTypeRepository;
    
    public List<SettingItemResponse> getAllTestTypes() {
        return testTypeRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<SettingItemResponse> getActiveTestTypes() {
        return testTypeRepository.findByStatus(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SettingItemResponse getTestTypeById(Integer id) {
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test type not found with id: " + id));
        return toResponse(testType);
    }
    
    @Transactional
    public SettingItemResponse createTestType(SettingItemRequest request) {
        if (testTypeRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Test type already exists with name: " + request.getName());
        }
        
        TestType testType = new TestType();
        testType.setName(request.getName());
        testType.setStatus(request.getStatus());
        
        TestType saved = testTypeRepository.save(testType);
        return toResponse(saved);
    }
    
    @Transactional
    public SettingItemResponse updateTestType(Integer id, SettingItemRequest request) {
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test type not found with id: " + id));
        
        testTypeRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Test type already exists with name: " + request.getName());
            }
        });
        
        testType.setName(request.getName());
        testType.setStatus(request.getStatus());
        
        TestType updated = testTypeRepository.save(testType);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteTestType(Integer id) {
        if (!testTypeRepository.existsById(id)) {
            throw new RuntimeException("Test type not found with id: " + id);
        }
        testTypeRepository.deleteById(id);
    }
    
    @Transactional
    public SettingItemResponse toggleTestTypeStatus(Integer id) {
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test type not found with id: " + id));
        
        testType.setStatus(!testType.getStatus());
        TestType updated = testTypeRepository.save(testType);
        return toResponse(updated);
    }
    
    private SettingItemResponse toResponse(TestType testType) {
        return new SettingItemResponse(
                testType.getId(),
                testType.getName(),
                testType.getStatus()
        );
    }
}
