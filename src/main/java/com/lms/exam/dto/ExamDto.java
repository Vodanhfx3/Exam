package com.lms.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDto {
    private Long id;
    private String title;
    private Integer durationMinutes;
    private String location;
    private String timeslot;
    private List<QuestionDto> questions;
}