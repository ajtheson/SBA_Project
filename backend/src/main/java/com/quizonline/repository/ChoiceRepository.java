package com.quizonline.repository;

import com.quizonline.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Integer> {

    @Query("SELECT c FROM Choice c WHERE c.question.questionId = :questionId AND c.isDeleted = false")
    List<Choice> findByQuestionId(int questionId);

    @Modifying
    @Transactional
    @Query("UPDATE Choice c SET c.isDeleted = true WHERE c.question.questionId = :questionId")
    int softDeleteByQuestionId(int questionId);

    @Modifying
    @Transactional
    @Query("UPDATE Choice c SET c.isDeleted = true WHERE c.question.questionId IN (SELECT q.questionId FROM Question q WHERE q.quiz.quizId = :quizId)")
    int softDeleteByQuizId(int quizId);
}
