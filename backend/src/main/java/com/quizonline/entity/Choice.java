package com.quizonline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Choice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choiceID")
    private Integer choiceId;

    @Column(name = "choiceContent")
    private String choiceContent;

    @Column(name = "isCorrectChoice")
    private Boolean isCorrectChoice;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "questionID")
    private Question question;
}
