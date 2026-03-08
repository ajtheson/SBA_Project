package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.*;
import com.quizonline.service.StudentExamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    @Autowired
    private StudentExamService studentExamService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- Join exam by code ---
    @GetMapping("/join")
    public ResponseEntity<?> joinExam(@RequestParam int code, HttpServletRequest request) {
        try {
            int studentId = extractStudentId(request);
            ExamResponse exam = studentExamService.joinExam(code, studentId);
            return ResponseEntity.ok(exam);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Start exam (create submission, get shuffled questions) ---
    @PostMapping("/{examId}/start")
    public ResponseEntity<?> startExam(@PathVariable int examId, HttpServletRequest request) {
        try {
            int studentId = extractStudentId(request);
            ExamTakingResponse exam = studentExamService.startExam(examId, studentId);
            return ResponseEntity.ok(exam);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Submit exam ---
    @PostMapping("/submit/{submissionId}")
    public ResponseEntity<?> submitExam(@PathVariable int submissionId,
                                         @RequestBody SubmitExamRequest submitRequest,
                                         HttpServletRequest request) {
        try {
            int studentId = extractStudentId(request);
            SubmitResultResponse result = studentExamService.submitExam(submissionId, submitRequest, studentId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Check submission status (for force-submit polling) ---
    @GetMapping("/check-submission/{submissionId}")
    public ResponseEntity<?> checkSubmission(@PathVariable int submissionId, HttpServletRequest request) {
        try {
            int studentId = extractStudentId(request);
            boolean isSubmitted = studentExamService.checkSubmissionStatus(submissionId, studentId);
            return ResponseEntity.ok(Map.of("isSubmit", isSubmitted));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    private int extractStudentId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
