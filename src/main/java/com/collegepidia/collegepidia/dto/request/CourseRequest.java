package com.collegepidia.collegepidia.dto.request;

/**
 * DTO for creating or updating a Course.
 */
public class CourseRequest {
    private String name;
    private String code;

    // Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
