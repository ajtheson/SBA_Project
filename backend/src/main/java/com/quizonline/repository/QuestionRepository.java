package com.quizonline.repository;

import com.quizonline.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.quiz.quizId = :quizId AND q.isDeleted = false")
    List<Question> findByQuizId(int quizId);

    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.isDeleted = true WHERE q.quiz.quizId = :quizId")
    int softDeleteByQuizId(int quizId);
}
