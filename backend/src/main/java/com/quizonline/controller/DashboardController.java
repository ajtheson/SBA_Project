package com.quizonline.controller;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.DashboardResponse;
import com.quizonline.service.ExamService;
import com.quizonline.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/dashboard")
public class DashboardController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private ExamService examService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        int teacherId = extractTeacherId(request);
        DashboardResponse response = new DashboardResponse();
        response.setTotalQuizzes(quizService.countTotalQuiz(teacherId));
        response.setValidQuizzes(quizService.countValidQuiz(teacherId));
        response.setUsedQuizzes(quizService.countUsedQuiz(teacherId));
        response.setTotalExams(examService.countTeacherExams(teacherId));
        response.setOnGoingExams(examService.countOnGoingExams(teacherId));
        response.setTotalSubmissions(examService.submissionCount(teacherId));
        return ResponseEntity.ok(response);
    }

    private int extractTeacherId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
