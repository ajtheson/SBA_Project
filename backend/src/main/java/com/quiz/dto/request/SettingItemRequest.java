package com.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingItemRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private Boolean status = true;
}
