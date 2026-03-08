package com.quizonline.dto;

public class StudentDashboardResponse {
    private int totalSubmissions;
    private int totalExams;
    private String fullname;
    private String email;

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public int getTotalExams() { return totalExams; }
    public void setTotalExams(int totalExams) { this.totalExams = totalExams; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
