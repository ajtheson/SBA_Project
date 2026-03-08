package com.quizonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuizOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizOnlineApplication.class, args);
    }
}
