package com.quiz.repository;

import com.quiz.entity.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
    
    Optional<TestType> findByName(String name);
    
    List<TestType> findByStatus(Boolean status);
    
    List<TestType> findAllByOrderByNameAsc();
}
