package com.quizonline.service;

import com.quizonline.dto.*;
import com.quizonline.entity.Choice;
import com.quizonline.entity.Question;
import com.quizonline.entity.Quiz;
import com.quizonline.entity.Teacher;
import com.quizonline.repository.ChoiceRepository;
import com.quizonline.repository.QuestionRepository;
import com.quizonline.repository.QuizRepository;
import com.quizonline.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // --- Get teacher's quizzes (reuse old getTeacherQuiz) ---
    public List<QuizResponse> getTeacherQuizzes(int teacherId) {
        List<Quiz> quizzes = quizRepository.getTeacherQuiz(teacherId);
        return quizzes.stream().map(this::toQuizResponse).collect(Collectors.toList());
    }

    // --- Search quizzes by name (reuse old searchByName) ---
    public List<QuizResponse> searchQuizzes(int teacherId, String name) {
        List<Quiz> quizzes = quizRepository.searchByName(teacherId, name);
        return quizzes.stream().map(this::toQuizResponse).collect(Collectors.toList());
    }

    // --- Get quiz detail ---
    public QuizResponse getQuizDetail(int quizId, int teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (!quiz.getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized to view this quiz");
        }
        if (Boolean.TRUE.equals(quiz.getIsDeleted())) {
            throw new RuntimeException("Quiz has been deleted");
        }
        return toQuizResponseWithQuestions(quiz);
    }

    // --- Create quiz (reuse old createQuiz logic) ---
    @Transactional
    public QuizResponse createQuiz(QuizRequest request, int teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Quiz quiz = new Quiz();
        quiz.setQuizName(request.getQuizName());
        quiz.setIsDeleted(false);
        quiz.setTeacher(teacher);
        quiz.setQuantity(request.getQuestions().size());
        quiz = quizRepository.save(quiz);

        saveQuestionsAndChoices(quiz, request.getQuestions());

        return getQuizDetail(quiz.getQuizId(), teacherId);
    }

    // --- Update quiz (reuse old updateQuiz: soft-delete old + create new) ---
    @Transactional
    public QuizResponse updateQuiz(int quizId, QuizRequest request, int teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (!quiz.getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized to edit this quiz");
        }

        // Soft-delete old choices and questions (same pattern as old project)
        choiceRepository.softDeleteByQuizId(quizId);
        questionRepository.softDeleteByQuizId(quizId);

        // Update quiz info
        quiz.setQuizName(request.getQuizName());
        quiz.setQuantity(request.getQuestions().size());
        quizRepository.save(quiz);

        // Save new questions and choices
        saveQuestionsAndChoices(quiz, request.getQuestions());

        return getQuizDetail(quiz.getQuizId(), teacherId);
    }

    // --- Delete quiz (soft delete, reuse old deleteQuiz) ---
    @Transactional
    public void deleteQuiz(int quizId, int teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (!quiz.getTeacher().getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Not authorized to delete this quiz");
        }
        choiceRepository.softDeleteByQuizId(quizId);
        questionRepository.softDeleteByQuizId(quizId);
        quizRepository.softDeleteQuiz(quizId);
    }

    // --- Quiz counts for dashboard ---
    public int countTotalQuiz(int teacherId) {
        return quizRepository.countTotalQuiz(teacherId);
    }

    public int countValidQuiz(int teacherId) {
        return quizRepository.countValidQuiz(teacherId);
    }

    public int countUsedQuiz(int teacherId) {
        return quizRepository.countUsedQuiz(teacherId);
    }

    // === Helper methods ===

    private void saveQuestionsAndChoices(Quiz quiz, List<QuestionDTO> questionDTOs) {
        for (QuestionDTO qDTO : questionDTOs) {
            Question question = new Question();
            question.setContent(qDTO.getContent());
            question.setIsDeleted(false);
            question.setQuiz(quiz);

            // Auto-detect multiple choice (>1 correct answer)
            long correctCount = qDTO.getChoices().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrectChoice()))
                    .count();
            question.setIsMultipleChoice(correctCount > 1);

            question = questionRepository.save(question);

            for (ChoiceDTO cDTO : qDTO.getChoices()) {
                Choice choice = new Choice();
                choice.setChoiceContent(cDTO.getChoiceContent());
                choice.setIsCorrectChoice(Boolean.TRUE.equals(cDTO.getIsCorrectChoice()));
                choice.setIsDeleted(false);
                choice.setQuestion(question);
                choiceRepository.save(choice);
            }
        }
    }

    private QuizResponse toQuizResponse(Quiz quiz) {
        QuizResponse res = new QuizResponse();
        res.setQuizId(quiz.getQuizId());
        res.setQuizName(quiz.getQuizName());
        res.setQuantity(quiz.getQuantity());
        return res;
    }

    private QuizResponse toQuizResponseWithQuestions(Quiz quiz) {
        QuizResponse res = toQuizResponse(quiz);

        List<Question> questions = questionRepository.findByQuizId(quiz.getQuizId());
        List<QuestionDTO> questionDTOs = new ArrayList<>();

        for (Question q : questions) {
            QuestionDTO qDTO = new QuestionDTO();
            qDTO.setQuestionId(q.getQuestionId());
            qDTO.setContent(q.getContent());
            qDTO.setIsMultipleChoice(q.getIsMultipleChoice());

            List<Choice> choices = choiceRepository.findByQuestionId(q.getQuestionId());
            List<ChoiceDTO> choiceDTOs = choices.stream().map(c -> {
                ChoiceDTO cDTO = new ChoiceDTO();
                cDTO.setChoiceId(c.getChoiceId());
                cDTO.setChoiceContent(c.getChoiceContent());
                cDTO.setIsCorrectChoice(c.getIsCorrectChoice());
                return cDTO;
            }).collect(Collectors.toList());

            qDTO.setChoices(choiceDTOs);
            questionDTOs.add(qDTO);
        }

        res.setQuestions(questionDTOs);
        return res;
    }
}
