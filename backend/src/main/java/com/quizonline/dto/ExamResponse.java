package com.quizonline.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private Integer examId;
    private Integer examCode;
    private String examName;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer attempts;
    private Boolean isReview;
    private String quizName;
    private Integer quizId;
}
