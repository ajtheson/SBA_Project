package com.quizonline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submissionID")
    private Integer submissionId;

    @Column(name = "submitTime")
    private LocalDateTime submitTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "selected")
    private Integer selected;

    @Column(name = "correctAnswers")
    private Integer correctAnswers;

    @Column(name = "score", precision = 3, scale = 1)
    private BigDecimal score;

    @Column(name = "isSubmit")
    private Boolean isSubmit;

    @ManyToOne
    @JoinColumn(name = "studentID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "examID")
    private Exam exam;

    @OneToMany(mappedBy = "submission")
    private List<Answer> answers;
}
