package com.quiz.repository;

import com.quiz.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory, Integer> {
    
    Optional<SubjectCategory> findByName(String name);
    
    List<SubjectCategory> findByStatus(Boolean status);
    
    List<SubjectCategory> findAllByOrderByNameAsc();
}
