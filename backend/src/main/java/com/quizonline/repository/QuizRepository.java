package com.quizonline.repository;

import com.quizonline.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @Query("SELECT q FROM Quiz q WHERE q.isDeleted = false AND q.teacher.teacherId = :teacherId ORDER BY q.quizId DESC")
    List<Quiz> getTeacherQuiz(int teacherId);

    @Query("SELECT q FROM Quiz q WHERE q.isDeleted = false AND q.teacher.teacherId = :teacherId AND q.quizName LIKE %:name% ORDER BY q.quizId DESC")
    List<Quiz> searchByName(int teacherId, String name);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.teacher.teacherId = :teacherId")
    int countTotalQuiz(int teacherId);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.isDeleted = false AND q.teacher.teacherId = :teacherId")
    int countValidQuiz(int teacherId);

    @Query("SELECT COUNT(DISTINCT e.quiz.quizId) FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId")
    int countUsedQuiz(int teacherId);

    @Modifying
    @Transactional
    @Query("UPDATE Quiz q SET q.isDeleted = true WHERE q.quizId = :quizId")
    int softDeleteQuiz(int quizId);
}
