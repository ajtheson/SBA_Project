package com.quizonline.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizResponse {
    private Integer quizId;
    private String quizName;
    private Integer quantity;
    private List<QuestionDTO> questions;
}
