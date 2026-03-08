package com.quizonline.repository;

import com.quizonline.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    // Get answers for a submission
    @Query("SELECT a FROM Answer a WHERE a.submission.submissionId = :submissionId")
    List<Answer> getAnswersOfSubmission(int submissionId);

    // Get answers for a question across an exam (for statistics)
    @Query("SELECT a FROM Answer a WHERE a.question.questionId = :questionId AND a.submission.exam.examId = :examId")
    List<Answer> getForStatistic(int questionId, int examId);

    // Get student ID from answer
    @Query("SELECT a.submission.student.studentId FROM Answer a WHERE a.answerId = :answerId")
    int getStudentId(int answerId);
}
