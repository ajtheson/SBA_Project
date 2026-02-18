package com.quiz.repository;

import com.quiz.entity.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionLevelRepository extends JpaRepository<QuestionLevel, Integer> {
    
    Optional<QuestionLevel> findByName(String name);
    
    List<QuestionLevel> findByStatus(Boolean status);
    
    List<QuestionLevel> findAllByOrderByNameAsc();
}
