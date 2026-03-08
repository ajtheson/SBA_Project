package com.quizonline.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private Integer submissionId;
    private LocalDateTime submitTime;
    private Integer duration;
    private Integer selected;
    private Integer correctAnswers;
    private Double score;
    private Boolean isSubmit;
    private String studentName;
    private String studentEmail;
    private Integer studentId;
    private String examName;
    private Integer examId;
}
