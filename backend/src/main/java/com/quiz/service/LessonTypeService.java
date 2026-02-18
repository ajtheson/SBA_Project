package com.quiz.service;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.entity.LessonType;
import com.quiz.repository.LessonTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonTypeService {
    
    private final LessonTypeRepository lessonTypeRepository;
    
    public List<SettingItemResponse> getAllLessonTypes() {
        return lessonTypeRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<SettingItemResponse> getActiveLessonTypes() {
        return lessonTypeRepository.findByStatus(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SettingItemResponse getLessonTypeById(Integer id) {
        LessonType lessonType = lessonTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson type not found with id: " + id));
        return toResponse(lessonType);
    }
    
    @Transactional
    public SettingItemResponse createLessonType(SettingItemRequest request) {
        if (lessonTypeRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Lesson type already exists with name: " + request.getName());
        }
        
        LessonType lessonType = new LessonType();
        lessonType.setName(request.getName());
        lessonType.setStatus(request.getStatus());
        
        LessonType saved = lessonTypeRepository.save(lessonType);
        return toResponse(saved);
    }
    
    @Transactional
    public SettingItemResponse updateLessonType(Integer id, SettingItemRequest request) {
        LessonType lessonType = lessonTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson type not found with id: " + id));
        
        lessonTypeRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Lesson type already exists with name: " + request.getName());
            }
        });
        
        lessonType.setName(request.getName());
        lessonType.setStatus(request.getStatus());
        
        LessonType updated = lessonTypeRepository.save(lessonType);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteLessonType(Integer id) {
        if (!lessonTypeRepository.existsById(id)) {
            throw new RuntimeException("Lesson type not found with id: " + id);
        }
        lessonTypeRepository.deleteById(id);
    }
    
    @Transactional
    public SettingItemResponse toggleLessonTypeStatus(Integer id) {
        LessonType lessonType = lessonTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson type not found with id: " + id));
        
        lessonType.setStatus(!lessonType.getStatus());
        LessonType updated = lessonTypeRepository.save(lessonType);
        return toResponse(updated);
    }
    
    private SettingItemResponse toResponse(LessonType lessonType) {
        return new SettingItemResponse(
                lessonType.getId(),
                lessonType.getName(),
                lessonType.getStatus()
        );
    }
}
