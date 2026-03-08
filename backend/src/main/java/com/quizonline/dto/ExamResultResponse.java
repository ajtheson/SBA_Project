package com.quizonline.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExamResultResponse {
    private String examName;
    private Integer totalSubmission;
    private Double averageScore;
    private Double highestScore;
    private List<SubmissionResponse> submissions;
}
