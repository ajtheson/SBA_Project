package com.quizonline.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String password;
    private String repassword;
    private String role; // "teacher" or "student"
}
