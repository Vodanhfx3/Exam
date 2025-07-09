package com.lms.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptHistoryDto {
    private List<ExamAttemptResponseDto> attempts;
}