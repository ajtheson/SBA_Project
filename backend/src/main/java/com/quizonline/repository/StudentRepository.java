package com.quizonline.repository;

import com.quizonline.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.password = :password WHERE s.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
