package com.quiz.service;

import com.quiz.dto.request.SettingItemRequest;
import com.quiz.dto.response.SettingItemResponse;
import com.quiz.entity.QuestionLevel;
import com.quiz.repository.QuestionLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionLevelService {
    
    private final QuestionLevelRepository questionLevelRepository;
    
    public List<SettingItemResponse> getAllQuestionLevels() {
        return questionLevelRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<SettingItemResponse> getActiveQuestionLevels() {
        return questionLevelRepository.findByStatus(true).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SettingItemResponse getQuestionLevelById(Integer id) {
        QuestionLevel questionLevel = questionLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question level not found with id: " + id));
        return toResponse(questionLevel);
    }
    
    @Transactional
    public SettingItemResponse createQuestionLevel(SettingItemRequest request) {
        if (questionLevelRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Question level already exists with name: " + request.getName());
        }
        
        QuestionLevel questionLevel = new QuestionLevel();
        questionLevel.setName(request.getName());
        questionLevel.setStatus(request.getStatus());
        
        QuestionLevel saved = questionLevelRepository.save(questionLevel);
        return toResponse(saved);
    }
    
    @Transactional
    public SettingItemResponse updateQuestionLevel(Integer id, SettingItemRequest request) {
        QuestionLevel questionLevel = questionLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question level not found with id: " + id));
        
        questionLevelRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Question level already exists with name: " + request.getName());
            }
        });
        
        questionLevel.setName(request.getName());
        questionLevel.setStatus(request.getStatus());
        
        QuestionLevel updated = questionLevelRepository.save(questionLevel);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteQuestionLevel(Integer id) {
        if (!questionLevelRepository.existsById(id)) {
            throw new RuntimeException("Question level not found with id: " + id);
        }
        questionLevelRepository.deleteById(id);
    }
    
    @Transactional
    public SettingItemResponse toggleQuestionLevelStatus(Integer id) {
        QuestionLevel questionLevel = questionLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question level not found with id: " + id));
        
        questionLevel.setStatus(!questionLevel.getStatus());
        QuestionLevel updated = questionLevelRepository.save(questionLevel);
        return toResponse(updated);
    }
    
    private SettingItemResponse toResponse(QuestionLevel questionLevel) {
        return new SettingItemResponse(
                questionLevel.getId(),
                questionLevel.getName(),
                questionLevel.getStatus()
        );
    }
}
