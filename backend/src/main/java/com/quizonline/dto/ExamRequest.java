package com.quizonline.dto;

import lombok.Data;

@Data
public class ExamRequest {
    private Integer quizId;
    private String examName;
    private Integer duration;
    private String startTime;  // "yyyy-MM-dd'T'HH:mm"
    private String endTime;
    private Integer attempts;
    private Boolean isReview;
}
