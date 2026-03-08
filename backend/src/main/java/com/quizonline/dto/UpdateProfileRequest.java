package com.quizonline.dto;

public class UpdateProfileRequest {
    private String fullname;
    private String school;
    private String className;

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
