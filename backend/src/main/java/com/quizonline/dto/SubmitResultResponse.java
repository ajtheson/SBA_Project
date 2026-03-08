package com.quizonline.dto;

import lombok.Data;

@Data
public class SubmitResultResponse {
    private Integer submissionId;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double score;
}
