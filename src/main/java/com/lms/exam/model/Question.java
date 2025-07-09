package com.lms.exam.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    private String text;
    private Integer correctAnswerIndex;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "question_choices",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "choice")
    private List<String> choices;
}