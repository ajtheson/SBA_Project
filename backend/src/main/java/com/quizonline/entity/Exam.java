package com.quizonline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "examID")
    private Integer examId;

    @Column(name = "examCode")
    private Integer examCode;

    @Column(name = "examName")
    private String examName;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "isReview")
    private Boolean isReview;

    @ManyToOne
    @JoinColumn(name = "quizID")
    private Quiz quiz;

    @OneToMany(mappedBy = "exam")
    private List<Submission> submissions;
}
