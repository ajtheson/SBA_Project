package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.*;
import com.quizonline.entity.Student;
import com.quizonline.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/settings")
public class StudentSettingsController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Get profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        int studentId = extractStudentId(request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ProfileResponse response = new ProfileResponse();
        response.setEmail(student.getEmail());
        response.setFullname(student.getFullname());
        response.setSchool(student.getSchool());
        response.setClassName(student.getClassName());
        return ResponseEntity.ok(response);
    }

    // Update profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileRequest profileRequest) {
        int studentId = extractStudentId(request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFullname(profileRequest.getFullname());
        student.setSchool(profileRequest.getSchool());
        student.setClassName(profileRequest.getClassName());
        studentRepository.save(student);

        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully."));
    }

    // Change password
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest passwordRequest) {
        int studentId = extractStudentId(request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), student.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Current password is incorrect."));
        }

        String regex = "^[a-zA-Z0-9]{8,}$";
        if (!passwordRequest.getNewPassword().matches(regex)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Password must contain at least 8 alphanumeric characters."));
        }

        student.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        studentRepository.save(student);

        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully."));
    }

    private int extractStudentId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
