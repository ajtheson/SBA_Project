package com.quizonline.dto;

import lombok.Data;

@Data
public class ChoiceDTO {
    private Integer choiceId;
    private String choiceContent;
    private Boolean isCorrectChoice;
}
