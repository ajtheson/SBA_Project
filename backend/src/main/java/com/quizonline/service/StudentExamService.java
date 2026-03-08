package com.quizonline.service;

import com.quizonline.dto.*;
import com.quizonline.entity.*;
import com.quizonline.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private StudentRepository studentRepository;

    // === JOIN EXAM ===

    public ExamResponse joinExam(int examCode, int studentId) {
        Exam exam = examRepository.searchByCode(examCode)
                .orElseThrow(() -> new RuntimeException("Exam not found or not active"));

        // Check attempts
        int attempts = submissionRepository.countAttempts(studentId, exam.getExamId());
        if (attempts >= exam.getAttempts()) {
            throw new RuntimeException("No more attempts allowed. You have used all " + exam.getAttempts() + " attempts.");
        }

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

    // === START EXAM (create submission + return shuffled questions) ===

    @Transactional
    public ExamTakingResponse startExam(int examId, int studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Verify exam is still active
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Exam has ended");
        }
        if (exam.getStartTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Exam has not started yet");
        }

        // Check attempts
        int attempts = submissionRepository.countAttempts(studentId, examId);
        if (attempts >= exam.getAttempts()) {
            throw new RuntimeException("No more attempts allowed");
        }

        // Create submission record (not yet submitted)
        Submission submission = new Submission();
        submission.setIsSubmit(false);
        submission.setStudent(new Student());
        submission.getStudent().setStudentId(studentId);
        submission.setExam(exam);
        submission = submissionRepository.save(submission);

        // Load questions (not deleted) and shuffle
        List<Question> questions = questionRepository.findByQuizId(exam.getQuiz().getQuizId());
        Collections.shuffle(questions);

        List<QuestionTakingDTO> questionDTOs = new ArrayList<>();
        for (Question q : questions) {
            QuestionTakingDTO dto = new QuestionTakingDTO();
            dto.setQuestionId(q.getQuestionId());
            dto.setContent(q.getContent());
            dto.setIsMultipleChoice(q.getIsMultipleChoice());

            // Load choices (not deleted) and shuffle
            List<Choice> choices = choiceRepository.findByQuestionId(q.getQuestionId());
            Collections.shuffle(choices);

            List<ChoiceTakingDTO> choiceDTOs = choices.stream().map(c -> {
                ChoiceTakingDTO cdto = new ChoiceTakingDTO();
                cdto.setChoiceId(c.getChoiceId());
                cdto.setChoiceContent(c.getChoiceContent());
                return cdto;
            }).collect(Collectors.toList());

            dto.setChoices(choiceDTOs);
            questionDTOs.add(dto);
        }

        ExamTakingResponse response = new ExamTakingResponse();
        response.setExamId(exam.getExamId());
        response.setExamName(exam.getExamName());
        response.setDuration(exam.getDuration());
        response.setSubmissionId(submission.getSubmissionId());
        response.setQuestions(questionDTOs);
        return response;
    }

    // === SUBMIT EXAM ===

    @Transactional
    public SubmitResultResponse submitExam(int submissionId, SubmitExamRequest request, int studentId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // Verify ownership
        if (!submission.getStudent().getStudentId().equals(studentId)) {
            throw new RuntimeException("Not authorized");
        }

        // Already submitted
        if (Boolean.TRUE.equals(submission.getIsSubmit())) {
            throw new RuntimeException("Already submitted");
        }

        Exam exam = submission.getExam();
        Quiz quiz = exam.getQuiz();

        // Load all questions for this quiz (answer key)
        List<Question> questions = questionRepository.findByQuizId(quiz.getQuizId());

        // Build answer key: questionId -> set of correct choiceIds
        Map<Integer, Set<Integer>> answerKey = new HashMap<>();
        for (Question q : questions) {
            List<Choice> choices = choiceRepository.findByQuestionId(q.getQuestionId());
            Set<Integer> correctIds = choices.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrectChoice()))
                    .map(Choice::getChoiceId)
                    .collect(Collectors.toSet());
            answerKey.put(q.getQuestionId(), correctIds);
        }

        // Score calculation
        int correctCount = 0;
        int selectedCount = 0;
        Map<String, String> studentAnswers = request.getAnswers() != null ? request.getAnswers() : new HashMap<>();

        List<Answer> answerEntities = new ArrayList<>();

        for (Question q : questions) {
            String qIdStr = String.valueOf(q.getQuestionId());
            String studentChoice = studentAnswers.getOrDefault(qIdStr, "");

            if (!studentChoice.isEmpty()) {
                selectedCount++;
            }

            // Parse student choice IDs
            Set<Integer> studentChoiceIds = new HashSet<>();
            if (!studentChoice.isEmpty()) {
                for (String s : studentChoice.trim().split("\\s+")) {
                    try {
                        studentChoiceIds.add(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            boolean isCorrect = studentChoiceIds.equals(answerKey.get(q.getQuestionId()));
            if (isCorrect) correctCount++;

            Answer answer = new Answer();
            answer.setStudentChoice(studentChoice);
            answer.setIsCorrect(isCorrect);
            answer.setQuestion(q);
            answer.setSubmission(submission);
            answerEntities.add(answer);
        }

        // Save answers
        answerRepository.saveAll(answerEntities);

        // Calculate score: (correct / total) * 10
        int totalQuestions = questions.size();
        double score = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 10.0 : 0;
        BigDecimal scoreBD = BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP);

        // Update submission
        submission.setSubmitTime(LocalDateTime.now());
        submission.setDuration(request.getDuration());
        submission.setSelected(selectedCount);
        submission.setCorrectAnswers(correctCount);
        submission.setScore(scoreBD);
        submission.setIsSubmit(true);
        submissionRepository.save(submission);

        SubmitResultResponse result = new SubmitResultResponse();
        result.setSubmissionId(submissionId);
        result.setCorrectAnswers(correctCount);
        result.setTotalQuestions(totalQuestions);
        result.setScore(scoreBD.doubleValue());
        return result;
    }

    // === CHECK SUBMISSION STATUS (for force-submit polling) ===

    public boolean checkSubmissionStatus(int submissionId, int studentId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        if (!submission.getStudent().getStudentId().equals(studentId)) {
            throw new RuntimeException("Not authorized");
        }
        return Boolean.TRUE.equals(submission.getIsSubmit());
    }
}
