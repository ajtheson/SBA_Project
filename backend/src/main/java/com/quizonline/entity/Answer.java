package com.quizonline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answerID")
    private Integer answerId;

    @Column(name = "studentChoice")
    private String studentChoice;

    @Column(name = "isCorrect")
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "questionID")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "submissionID")
    private Submission submission;
}
