package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.StudentDashboardResponse;
import com.quizonline.entity.Student;
import com.quizonline.repository.StudentRepository;
import com.quizonline.repository.SubmissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/dashboard")
public class StudentDashboardController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        int studentId = extractStudentId(request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentDashboardResponse response = new StudentDashboardResponse();
        response.setFullname(student.getFullname());
        response.setEmail(student.getEmail());
        response.setTotalSubmissions(submissionRepository.countStudentSubmissions(studentId));
        response.setTotalExams(submissionRepository.countStudentExams(studentId));
        return ResponseEntity.ok(response);
    }

    private int extractStudentId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
