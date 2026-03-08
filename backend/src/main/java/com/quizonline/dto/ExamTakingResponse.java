package com.quizonline.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamTakingResponse {
    private Integer examId;
    private String examName;
    private Integer duration; // minutes
    private Integer submissionId;
    private List<QuestionTakingDTO> questions;
}
