package com.quizonline.service;

import com.quizonline.dto.*;
import com.quizonline.entity.*;
import com.quizonline.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // === EXAM CRUD (Teacher) ===

    @Transactional
    public ExamResponse createExam(ExamRequest request, int teacherId) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (!quiz.getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }

        // Generate unique 5-digit exam code (same as old project)
        Random random = new Random();
        int examCode;
        do {
            examCode = 10000 + random.nextInt(90000);
        } while (examRepository.countByCode(examCode) > 0);

        Exam exam = new Exam();
        exam.setExamCode(examCode);
        exam.setExamName(request.getExamName());
        exam.setDuration(request.getDuration());
        exam.setStartTime(LocalDateTime.parse(request.getStartTime(), FORMATTER));
        exam.setEndTime(LocalDateTime.parse(request.getEndTime(), FORMATTER));
        exam.setAttempts(request.getAttempts());
        exam.setIsReview(Boolean.TRUE.equals(request.getIsReview()));
        exam.setQuiz(quiz);

        exam = examRepository.save(exam);
        return toExamResponse(exam);
    }

    public List<ExamResponse> getOnGoingExams(int teacherId) {
        return examRepository.getOnGoingExams(teacherId).stream()
                .map(this::toExamResponse).collect(Collectors.toList());
    }

    public List<ExamResponse> searchOnGoingExams(int teacherId, String name) {
        return examRepository.searchOnGoingExams(teacherId, name).stream()
                .map(this::toExamResponse).collect(Collectors.toList());
    }

    public List<ExamResponse> getCompletedExams(int teacherId) {
        return examRepository.getCompletedExams(teacherId).stream()
                .map(this::toExamResponse).collect(Collectors.toList());
    }

    public List<ExamResponse> searchCompletedExams(int teacherId, String name) {
        return examRepository.searchCompletedExams(teacherId, name).stream()
                .map(this::toExamResponse).collect(Collectors.toList());
    }

    @Transactional
    public void endExam(int examId, int teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        if (!exam.getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }
        examRepository.endExam(examId);
    }

    @Transactional
    public void toggleReview(int examId, int teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        if (!exam.getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }
        examRepository.toggleReview(examId);
    }

    // === EXAM RESULTS (Teacher) ===

    public ExamResultResponse getExamResults(int examId, int teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        if (!exam.getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }

        List<Submission> submissions = submissionRepository.getSubmissionOfExam(examId);
        int total = submissions.size();
        double sum = 0, highest = 0;
        for (Submission s : submissions) {
            double sc = s.getScore() != null ? s.getScore().doubleValue() : 0;
            sum += sc;
            if (sc > highest) highest = sc;
        }

        ExamResultResponse result = new ExamResultResponse();
        result.setExamName(exam.getExamName());
        result.setTotalSubmission(total);
        result.setAverageScore(total == 0 ? 0.0 : Math.round(sum / total * 10.0) / 10.0);
        result.setHighestScore(Math.round(highest * 10.0) / 10.0);
        result.setSubmissions(submissions.stream().map(this::toSubmissionResponse).collect(Collectors.toList()));
        return result;
    }

    // === ONGOING DETAIL (Teacher) ===

    public List<SubmissionResponse> getOnGoingDetail(int examId, int teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        if (!exam.getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }
        List<Submission> submissions = submissionRepository.getOnGoing(examId);
        return submissions.stream().map(this::toSubmissionResponse).collect(Collectors.toList());
    }

    @Transactional
    public void forceSubmit(int submissionId, int teacherId) {
        Submission sub = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        if (!sub.getExam().getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }
        submissionRepository.forceSubmit(submissionId);
    }

    // === SUBMISSION DETAIL (Teacher - always full review) ===

    public SubmissionDetailResponse getSubmissionDetail(int submissionId, int teacherId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Exam exam = submission.getExam();
        if (!exam.getQuiz().getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized");
        }
        if (!Boolean.TRUE.equals(submission.getIsSubmit())) {
            throw new RuntimeException("Submission not yet submitted");
        }

        List<Answer> answers = answerRepository.getAnswersOfSubmission(submissionId);

        List<AnswerDetailDTO> answerDTOs = answers.stream().map(a -> {
            Question question = a.getQuestion();
            AnswerDetailDTO dto = new AnswerDetailDTO();
            dto.setQuestionId(question.getQuestionId());
            dto.setQuestionContent(question.getContent());
            dto.setIsMultipleChoice(question.getIsMultipleChoice());
            dto.setIsCorrect(a.getIsCorrect());

            Set<Integer> selectedIds = new HashSet<>();
            if (a.getStudentChoice() != null && !a.getStudentChoice().isEmpty()) {
                for (String s : a.getStudentChoice().trim().split("\\s+")) {
                    try { selectedIds.add(Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
                }
            }

            List<Choice> choices = choiceRepository.findByQuestionId(question.getQuestionId());
            List<ChoiceDetailDTO> choiceDTOs = choices.stream().map(c -> {
                ChoiceDetailDTO cdto = new ChoiceDetailDTO();
                cdto.setChoiceId(c.getChoiceId());
                cdto.setChoiceContent(c.getChoiceContent());
                cdto.setIsSelected(selectedIds.contains(c.getChoiceId()));
                cdto.setIsCorrectChoice(c.getIsCorrectChoice());
                return cdto;
            }).collect(Collectors.toList());

            dto.setChoices(choiceDTOs);
            return dto;
        }).collect(Collectors.toList());

        List<Question> allQuestions = questionRepository.findByQuizId(exam.getQuiz().getQuizId());

        Student student = studentRepository.findById(submission.getStudent().getStudentId()).orElse(null);

        SubmissionDetailResponse response = new SubmissionDetailResponse();
        response.setSubmissionId(submissionId);
        response.setExamName(exam.getExamName());
        response.setExamId(exam.getExamId());
        response.setSubmitTime(submission.getSubmitTime());
        response.setDuration(submission.getDuration());
        response.setSelected(submission.getSelected());
        response.setCorrectAnswers(submission.getCorrectAnswers());
        response.setTotalQuestions(allQuestions.size());
        response.setScore(submission.getScore() != null ? submission.getScore().doubleValue() : null);
        response.setIsReview(true);
        response.setStudentName(student != null ? student.getFullname() : null);
        response.setStudentEmail(student != null ? student.getEmail() : null);
        response.setAnswers(answerDTOs);
        return response;
    }

    // === Dashboard counts ===

    public int countTeacherExams(int teacherId) {
        return examRepository.countTeacherExams(teacherId);
    }

    public int countOnGoingExams(int teacherId) {
        return examRepository.countOnGoingExams(teacherId);
    }

    public int submissionCount(int teacherId) {
        return submissionRepository.submissionCount(teacherId);
    }

    // === Helper ===

    private ExamResponse toExamResponse(Exam exam) {
        ExamResponse res = new ExamResponse();
        res.setExamId(exam.getExamId());
        res.setExamCode(exam.getExamCode());
        res.setExamName(exam.getExamName());
        res.setDuration(exam.getDuration());
        res.setStartTime(exam.getStartTime());
        res.setEndTime(exam.getEndTime());
        res.setAttempts(exam.getAttempts());
        res.setIsReview(exam.getIsReview());
        if (exam.getQuiz() != null) {
            res.setQuizName(exam.getQuiz().getQuizName());
            res.setQuizId(exam.getQuiz().getQuizId());
        }
        return res;
    }

    private SubmissionResponse toSubmissionResponse(Submission s) {
        SubmissionResponse res = new SubmissionResponse();
        res.setSubmissionId(s.getSubmissionId());
        res.setSubmitTime(s.getSubmitTime());
        res.setDuration(s.getDuration());
        res.setSelected(s.getSelected());
        res.setCorrectAnswers(s.getCorrectAnswers());
        res.setScore(s.getScore() != null ? s.getScore().doubleValue() : null);
        res.setIsSubmit(s.getIsSubmit());
        if (s.getStudent() != null) {
            Student student = studentRepository.findById(s.getStudent().getStudentId()).orElse(null);
            if (student != null) {
                res.setStudentName(student.getFullname());
                res.setStudentEmail(student.getEmail());
                res.setStudentId(student.getStudentId());
            }
        }
        if (s.getExam() != null) {
            res.setExamId(s.getExam().getExamId());
            Exam exam = examRepository.findById(s.getExam().getExamId()).orElse(null);
            if (exam != null) {
                res.setExamName(exam.getExamName());
            }
        }
        return res;
    }
}
