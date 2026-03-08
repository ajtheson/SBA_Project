package com.quizonline.repository;

import com.quizonline.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    // Ongoing exams: endTime > now
    @Query("SELECT e FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId AND e.endTime > CURRENT_TIMESTAMP ORDER BY e.examId DESC")
    List<Exam> getOnGoingExams(int teacherId);

    // Search ongoing by name
    @Query("SELECT e FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId AND e.endTime > CURRENT_TIMESTAMP AND e.examName LIKE %:name% ORDER BY e.examId DESC")
    List<Exam> searchOnGoingExams(int teacherId, String name);

    // Completed exams: endTime <= now
    @Query("SELECT e FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId AND e.endTime <= CURRENT_TIMESTAMP ORDER BY e.examId DESC")
    List<Exam> getCompletedExams(int teacherId);

    // Search completed by name
    @Query("SELECT e FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId AND e.endTime <= CURRENT_TIMESTAMP AND e.examName LIKE %:name% ORDER BY e.examId DESC")
    List<Exam> searchCompletedExams(int teacherId, String name);

    // Count exams for teacher
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId")
    int countTeacherExams(int teacherId);

    // Count ongoing exams
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.quiz.teacher.teacherId = :teacherId AND e.endTime > CURRENT_TIMESTAMP")
    int countOnGoingExams(int teacherId);

    // Search by exam code (active exam)
    @Query("SELECT e FROM Exam e WHERE e.examCode = :examCode AND e.startTime <= CURRENT_TIMESTAMP AND e.endTime > CURRENT_TIMESTAMP")
    Optional<Exam> searchByCode(int examCode);

    // Check if code exists (for generating unique codes)
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.examCode = :examCode AND e.endTime > CURRENT_TIMESTAMP")
    int countByCode(int examCode);

    // End exam: set endTime to now
    @Modifying
    @Transactional
    @Query("UPDATE Exam e SET e.endTime = CURRENT_TIMESTAMP WHERE e.examId = :examId")
    int endExam(int examId);

    // Toggle review
    @Modifying
    @Transactional
    @Query("UPDATE Exam e SET e.isReview = CASE WHEN e.isReview = true THEN false ELSE true END WHERE e.examId = :examId")
    int toggleReview(int examId);
}
