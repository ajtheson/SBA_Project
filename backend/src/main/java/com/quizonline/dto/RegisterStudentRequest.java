package com.quizonline.dto;

import lombok.Data;

@Data
public class RegisterStudentRequest {
    private String email;
    private String password;
    private String repassword;
    private String fullname;
    private String className;
    private String school;
}
