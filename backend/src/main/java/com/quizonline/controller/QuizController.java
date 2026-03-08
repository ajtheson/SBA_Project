package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.ApiResponse;
import com.quizonline.dto.QuizRequest;
import com.quizonline.dto.QuizResponse;
import com.quizonline.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- List all quizzes for current teacher ---
    @GetMapping
    public ResponseEntity<?> getQuizzes(HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<QuizResponse> quizzes = quizService.getTeacherQuizzes(teacherId);
            return ResponseEntity.ok(quizzes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Search quizzes by name ---
    @GetMapping("/search")
    public ResponseEntity<?> searchQuizzes(@RequestParam String name, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            List<QuizResponse> quizzes = quizService.searchQuizzes(teacherId, name);
            return ResponseEntity.ok(quizzes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Get quiz detail ---
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            QuizResponse quiz = quizService.getQuizDetail(id, teacherId);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Create quiz ---
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizRequest quizRequest, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            QuizResponse quiz = quizService.createQuiz(quizRequest, teacherId);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Update quiz ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable int id, @RequestBody QuizRequest quizRequest, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            QuizResponse quiz = quizService.updateQuiz(id, quizRequest, teacherId);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // --- Delete quiz (soft) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable int id, HttpServletRequest request) {
        try {
            int teacherId = extractTeacherId(request);
            quizService.deleteQuiz(id, teacherId);
            return ResponseEntity.ok(new ApiResponse(true, "Quiz deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Extract teacher ID from JWT token
    private int extractTeacherId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
