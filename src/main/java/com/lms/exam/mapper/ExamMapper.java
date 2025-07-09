package com.lms.exam.mapper;

import com.lms.exam.dto.ExamDto;
import com.lms.exam.model.Exam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExamMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Exam toEntity(ExamDto dto) {
        Exam exam = new Exam();
        exam.setId(dto.getId());
        exam.setTitle(dto.getTitle());
        exam.setDurationMinutes(dto.getDurationMinutes());
        exam.setLocation(dto.getLocation());
        exam.setTimeslot(LocalDateTime.parse(dto.getTimeslot(), FORMATTER));
        return exam;
    }

    public static ExamDto toDto(Exam entity) {
        ExamDto dto = new ExamDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setLocation(entity.getLocation());
        dto.setTimeslot(entity.getTimeslot().format(FORMATTER));
        return dto;
    }
}
