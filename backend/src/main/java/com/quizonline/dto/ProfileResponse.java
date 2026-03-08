package com.quizonline.dto;

public class ProfileResponse {
    private String email;
    private String fullname;
    private String school;
    private String className;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
