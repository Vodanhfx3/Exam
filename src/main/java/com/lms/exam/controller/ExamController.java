package com.lms.exam.controller;

import com.lms.exam.dto.*;
import com.lms.exam.dto.*;
import com.lms.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
public class ExamController {
    private final ExamService service;

    public ExamController(ExamService service) { this.service = service; }

    @Operation(summary = "Create a new exam")
    @PostMapping
    public ExamDto createExam(@RequestBody ExamDto examDto) {
        return service.createExam(examDto);
    }

    @Operation(summary = "Get all exams")
    @GetMapping
    public List<ExamDto> getAllExams() {
        return service.getAllExams();
    }

    @Operation(summary = "Get a specific exam by id")
    @GetMapping("/{id}")
    public ExamDto getExam(@PathVariable Long id) {
        return service.getExam(id);
    }

    @Operation(summary = "Delete an exam")
    @DeleteMapping("/{id}")
    public void deleteExam(@PathVariable Long id) {
        service.deleteExam(id);
    }

    @Operation(summary = "Add a question to an exam")
    @PostMapping("/{examId}/questions")
    public QuestionDto addQuestion(@PathVariable Long examId, @RequestBody QuestionDto q) {
        return service.addQuestion(examId, q);
    }

    @Operation(summary = "Delete a question")
    @DeleteMapping("/questions/{questionId}")
    public void deleteQuestion(@PathVariable Long questionId) {
        service.deleteQuestion(questionId);
    }

    @Operation(summary = "Submit answers for an attempt")
    @PostMapping("/{examId}/attempt")
    public ExamAttemptResponseDto attemptExam(
            @PathVariable Long examId,
            @RequestBody ExamAttemptRequestDto dto
    ) {
        return service.attemptExam(examId, dto.userId, dto.answers);
    }

    @GetMapping("/{examId}/attempts/history")
    public ExamAttemptHistoryDto getExamAttemptHistory(
            @PathVariable Long examId,
            @RequestParam String userId
    ) {
        return service.getExamAttemptHistory(examId, userId);
    }
}