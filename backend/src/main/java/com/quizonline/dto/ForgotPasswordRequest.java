package com.quizonline.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String role; // "teacher" or "student"
}
