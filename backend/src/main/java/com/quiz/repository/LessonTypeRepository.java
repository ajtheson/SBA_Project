package com.quiz.repository;

import com.quiz.entity.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonTypeRepository extends JpaRepository<LessonType, Integer> {
    
    Optional<LessonType> findByName(String name);
    
    List<LessonType> findByStatus(Boolean status);
    
    List<LessonType> findAllByOrderByNameAsc();
}
