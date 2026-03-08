package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.*;
import com.quizonline.service.ExamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- List ongoing exams ---
    @GetMapping
    public ResponseEntity<?> getOnGoingExams(HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<ExamResponse> exams = examService.getOnGoingExams(teacherId);
            return ResponseEntity.ok(exams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Search ongoing exams ---
    @GetMapping("/search")
    public ResponseEntity<?> searchOnGoingExams(@RequestParam String name, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<ExamResponse> exams = examService.searchOnGoingExams(teacherId, name);
            return ResponseEntity.ok(exams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- List completed exams ---
    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedExams(HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<ExamResponse> exams = examService.getCompletedExams(teacherId);
            return ResponseEntity.ok(exams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Search completed exams ---
    @GetMapping("/completed/search")
    public ResponseEntity<?> searchCompletedExams(@RequestParam String name, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<ExamResponse> exams = examService.searchCompletedExams(teacherId, name);
            return ResponseEntity.ok(exams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Create exam ---
    @PostMapping
    public ResponseEntity<?> createExam(@RequestBody ExamRequest examRequest, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            ExamResponse exam = examService.createExam(examRequest, teacherId);
            return ResponseEntity.ok(exam);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- End exam ---
    @PutMapping("/{id}/end")
    public ResponseEntity<?> endExam(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            examService.endExam(id, teacherId);
            return ResponseEntity.ok(new ApiResponse(true, "Exam ended successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Toggle review ---
    @PutMapping("/{id}/toggle-review")
    public ResponseEntity<?> toggleReview(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            examService.toggleReview(id, teacherId);
            return ResponseEntity.ok(new ApiResponse(true, "Review toggled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Get exam results (completed) ---
    @GetMapping("/{id}/results")
    public ResponseEntity<?> getExamResults(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            ExamResultResponse results = examService.getExamResults(id, teacherId);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Get ongoing exam detail (submissions + unsubmitted) ---
    @GetMapping("/ongoing/{id}/detail")
    public ResponseEntity<?> getOnGoingDetail(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<SubmissionResponse> subs = examService.getOnGoingDetail(id, teacherId);
            return ResponseEntity.ok(subs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- View submission detail (teacher always sees full review) ---
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<?> getSubmissionDetail(@PathVariable int submissionId, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            SubmissionDetailResponse detail = examService.getSubmissionDetail(submissionId, teacherId);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Force submit a submission ---
    @PutMapping("/{examId}/force-submit/{submissionId}")
    public ResponseEntity<?> forceSubmit(@PathVariable int examId, @PathVariable int submissionId, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            examService.forceSubmit(submissionId, teacherId);
            return ResponseEntity.ok(new ApiResponse(true, "Submission force submitted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    private int extractTeacherId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
