package com.quizonline.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmissionDetailResponse {
    private Integer submissionId;
    private String examName;
    private Integer examId;
    private LocalDateTime submitTime;
    private Integer duration;
    private Integer selected;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double score;
    private Boolean isReview;
    private String studentName;
    private String studentEmail;
    private List<AnswerDetailDTO> answers;
}
