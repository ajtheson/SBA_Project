package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.*;
import com.quizonline.entity.Teacher;
import com.quizonline.repository.TeacherRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/settings")
public class TeacherSettingsController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Get profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        int teacherId = extractTeacherId(request);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ProfileResponse response = new ProfileResponse();
        response.setEmail(teacher.getEmail());
        response.setFullname(teacher.getFullname());
        response.setSchool(teacher.getSchool());
        return ResponseEntity.ok(response);
    }

    // Update profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest request, @RequestBody UpdateProfileRequest profileRequest) {
        int teacherId = extractTeacherId(request);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacher.setFullname(profileRequest.getFullname());
        teacher.setSchool(profileRequest.getSchool());
        teacherRepository.save(teacher);

        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully."));
    }

    // Change password
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody ChangePasswordRequest passwordRequest) {
        int teacherId = extractTeacherId(request);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), teacher.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Current password is incorrect."));
        }

        String regex = "^[a-zA-Z0-9]{8,}$";
        if (!passwordRequest.getNewPassword().matches(regex)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Password must contain at least 8 alphanumeric characters."));
        }

        teacher.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        teacherRepository.save(teacher);

        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully."));
    }

    private int extractTeacherId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
