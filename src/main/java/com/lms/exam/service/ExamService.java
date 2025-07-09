package com.lms.exam.service;

import com.lms.exam.dto.*;
import com.lms.exam.model.*;
import com.lms.exam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter; // Import for formatting
import java.time.LocalDateTime;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository attemptRepository;

    public ExamService(ExamRepository examRepository, QuestionRepository questionRepository, ExamAttemptRepository attemptRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    // Map Exam to DTO
    private ExamDto toExamDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setLocation(exam.getLocation());
        // Convert LocalDateTime to String for timeslot
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeslot = exam.getTimeslot();
        dto.setTimeslot(timeslot != null ? timeslot.format(formatter) : null);
        dto.setQuestions(
                exam.getQuestions() != null
                        ? exam.getQuestions().stream().map(this::toQuestionDto).collect(Collectors.toList())
                        : List.of()
        );
        return dto;
    }

    private QuestionDto toQuestionDto(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getId());
        dto.setText(q.getText());
        dto.setChoices(q.getChoices());
        dto.setCorrectAnswerIndex(q.getCorrectAnswerIndex());
        return dto;
    }

    public ExamDto createExam(ExamDto dto) {
        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDurationMinutes(dto.getDurationMinutes());
        exam.setLocation(dto.getLocation());
        // Parse String to LocalDateTime for timeslot
        if (dto.getTimeslot() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime.parse(dto.getTimeslot(), formatter);
        } else {
            exam.setTimeslot(null);
        }
        if (dto.getQuestions() != null) {
            List<Question> questions = dto.getQuestions().stream().map(qdto -> {
                Question q = new Question();
                q.setText(qdto.getText());
                q.setChoices(qdto.getChoices());
                q.setCorrectAnswerIndex(qdto.getCorrectAnswerIndex());
                q.setExam(exam);
                return q;
            }).collect(Collectors.toList());
            exam.setQuestions(questions);
        }
        Exam saved = examRepository.save(exam);
        return toExamDto(saved);
    }

    public ExamDto getExam(Long id) {
        return examRepository.findById(id).map(this::toExamDto).orElseThrow();
    }

    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream().map(this::toExamDto).collect(Collectors.toList());
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    public QuestionDto addQuestion(Long examId, QuestionDto qdto) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        Question q = new Question();
        q.setText(qdto.getText());
        q.setChoices(qdto.getChoices());
        q.setCorrectAnswerIndex(qdto.getCorrectAnswerIndex());
        q.setExam(exam);
        Question saved = questionRepository.save(q);
        return toQuestionDto(saved);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    @Transactional
    public ExamAttemptResponseDto attemptExam(Long examId, String userId, List<Integer> userAnswers) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        List<ExamAttempt> attempts = attemptRepository.findByExamIdAndUserIdOrderByCreatedAtAsc(examId, userId);

        if (attempts.size() >= 3) {
            ExamAttempt last = attempts.get(attempts.size() - 1);
            return toAttemptResponseDto(last, exam.getQuestions());
        }

        List<Question> questions = exam.getQuestions();
        List<Integer> correctAnswers = questions.stream()
                .map(Question::getCorrectAnswerIndex)
                .collect(Collectors.toList());
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (i < userAnswers.size() && userAnswers.get(i).equals(correctAnswers.get(i))) score++;
        }

        boolean completed = (attempts.size() + 1) >= 3;
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setUserId(userId);
        attempt.setAnswers(userAnswers);
        attempt.setScore(score);
        attempt.setTries(attempts.size() + 1);

        ExamAttempt savedAttempt = attemptRepository.save(attempt);

        return toAttemptResponseDto(savedAttempt, questions);
    }

    private ExamAttemptResponseDto toAttemptResponseDto(ExamAttempt attempt, List<Question> questions) {
        ExamAttemptResponseDto resp = new ExamAttemptResponseDto();
        resp.setAttemptId(attempt.getId());
        resp.setScore(attempt.getScore());
        resp.setUserAnswers(attempt.getAnswers());
        resp.setCorrectAnswers(questions.stream().map(Question::getCorrectAnswerIndex).collect(Collectors.toList()));
        resp.setTriesLeft(Math.max(0, 3 - attempt.getTries()));

        // Populate per-question feedbacks
        List<QuestionFeedbackDto> feedbacks = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            QuestionFeedbackDto fb = new QuestionFeedbackDto();
            fb.setQuestionId(q.getId());
            fb.setQuestionText(q.getText());
            fb.setCorrectAnswerIndex(q.getCorrectAnswerIndex());
            fb.setCorrectAnswerText(q.getChoices().get(q.getCorrectAnswerIndex()));
            if (i < attempt.getAnswers().size()) {
                fb.setChosenAnswerIndex(attempt.getAnswers().get(i));
                fb.setChosenAnswerText(q.getChoices().get(fb.getChosenAnswerIndex()));
                fb.setCorrect(fb.getChosenAnswerIndex().equals(fb.getCorrectAnswerIndex()));
            } else {
                fb.setChosenAnswerIndex(null);
                fb.setChosenAnswerText(null);
                fb.setCorrect(false);
            }
            feedbacks.add(fb);
        }
        resp.setQuestionFeedbacks(feedbacks);
        return resp;
    }

    public ExamAttemptHistoryDto getExamAttemptHistory(Long examId, String userId) {
        List<ExamAttempt> attempts = attemptRepository.findByExamIdAndUserIdOrderByCreatedAtAsc(examId, userId);
        Exam exam = examRepository.findById(examId).orElseThrow();
        List<Question> questions = exam.getQuestions();

        List<ExamAttemptResponseDto> history = attempts.stream()
                .map(attempt -> toAttemptResponseDto(attempt, questions))
                .collect(Collectors.toList());

        ExamAttemptHistoryDto dto = new ExamAttemptHistoryDto();
        dto.setAttempts(history);
        return dto;
    }
}