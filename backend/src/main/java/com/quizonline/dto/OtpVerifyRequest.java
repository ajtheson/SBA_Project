package com.quizonline.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private String otp;
    private String mode; // "student_register", "teacher_register", "student_forgot", "teacher_forgot"
}
