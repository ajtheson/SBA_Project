package com.quizonline.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    private Integer questionId;
    private String content;
    private Boolean isMultipleChoice;
    private List<ChoiceDTO> choices;
}
