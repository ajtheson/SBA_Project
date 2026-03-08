package com.quizonline.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnswerDetailDTO {
    private Integer questionId;
    private String questionContent;
    private Boolean isMultipleChoice;
    private Boolean isCorrect;
    private List<ChoiceDetailDTO> choices;
}
