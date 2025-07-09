package com.lms.exam.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private String userId;

    @ElementCollection
    @CollectionTable(
            name = "attempt_answers",
            joinColumns = @JoinColumn(name = "attempt_id")
    )
    @Column(name = "answer")
    private List<Integer> answers;

    private int score;   // primitive int for NOT NULL
    private int tries;   // primitive int for NOT NULL

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}