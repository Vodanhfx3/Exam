package com.lms.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptResponseDto {
    private Long attemptId;
    private Integer score;
    private List<Integer> userAnswers;
    private List<Integer> correctAnswers;
    private Integer triesLeft;
    private List<QuestionFeedbackDto> questionFeedbacks;
}