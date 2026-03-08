package com.quizonline.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SubmitExamRequest {
    private Integer duration; // seconds spent
    private Map<String, String> answers; // questionId -> choiceId(s) space-separated
}
