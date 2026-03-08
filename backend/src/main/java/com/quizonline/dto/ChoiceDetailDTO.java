package com.quizonline.dto;

import lombok.Data;

@Data
public class ChoiceDetailDTO {
    private Integer choiceId;
    private String choiceContent;
    private Boolean isSelected;
    private Boolean isCorrectChoice; // only populated when review is allowed
}
