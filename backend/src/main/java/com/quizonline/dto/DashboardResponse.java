package com.quizonline.dto;

public class DashboardResponse {
    private int totalQuizzes;
    private int validQuizzes;
    private int usedQuizzes;
    private int totalExams;
    private int onGoingExams;
    private int totalSubmissions;

    public int getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public int getValidQuizzes() { return validQuizzes; }
    public void setValidQuizzes(int validQuizzes) { this.validQuizzes = validQuizzes; }

    public int getUsedQuizzes() { return usedQuizzes; }
    public void setUsedQuizzes(int usedQuizzes) { this.usedQuizzes = usedQuizzes; }

    public int getTotalExams() { return totalExams; }
    public void setTotalExams(int totalExams) { this.totalExams = totalExams; }

    public int getOnGoingExams() { return onGoingExams; }
    public void setOnGoingExams(int onGoingExams) { this.onGoingExams = onGoingExams; }

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }
}
