package com.lms.exam.dto;

import java.util.List;

public class ExamAttemptRequestDto {
    public String userId;
    public List<Integer> answers; // index per question
}