package com.quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    @Column(name = "status", nullable = false)
    private Boolean status = true;
}
