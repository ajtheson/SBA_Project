package com.quizonline.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private String quizName;
    private List<QuestionDTO> questions;
}
