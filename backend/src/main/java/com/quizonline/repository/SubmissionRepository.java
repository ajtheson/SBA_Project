package com.quizonline.repository;

import com.quizonline.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {

    // Get submitted submissions for an exam
    @Query("SELECT s FROM Submission s WHERE s.exam.examId = :examId AND s.isSubmit = true ORDER BY s.submitTime DESC")
    List<Submission> getSubmissionOfExam(int examId);

    // Get all submissions for an exam (ongoing detail - includes unsubmitted)
    @Query("SELECT s FROM Submission s WHERE s.exam.examId = :examId ORDER BY s.isSubmit ASC")
    List<Submission> getOnGoing(int examId);

    // Get submitted submissions for a student
    @Query("SELECT s FROM Submission s WHERE s.student.studentId = :studentId AND s.isSubmit = true ORDER BY s.submitTime DESC")
    List<Submission> getSubmissionOfStudent(int studentId);

    // Count submitted attempts for student+exam
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.studentId = :studentId AND s.exam.examId = :examId AND s.isSubmit = true")
    int countAttempts(int studentId, int examId);

    // Count submitted submissions for teacher
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.isSubmit = true AND s.exam.quiz.teacher.teacherId = :teacherId")
    int submissionCount(int teacherId);

    // Student dashboard counts
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.studentId = :studentId AND s.isSubmit = true")
    int countStudentSubmissions(int studentId);

    @Query("SELECT COUNT(DISTINCT s.exam.examId) FROM Submission s WHERE s.student.studentId = :studentId AND s.isSubmit = true")
    int countStudentExams(int studentId);

    // Force submit
    @Modifying
    @Transactional
    @Query("UPDATE Submission s SET s.isSubmit = true WHERE s.submissionId = :submissionId")
    int forceSubmit(int submissionId);
}
