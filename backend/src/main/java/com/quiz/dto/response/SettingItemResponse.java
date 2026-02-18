package com.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingItemResponse {
    private Integer id;
    private String name;
    private Boolean status;
}
