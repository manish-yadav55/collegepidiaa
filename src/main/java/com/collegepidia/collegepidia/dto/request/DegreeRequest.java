package com.collegepidia.collegepidia.dto.request;

/**
 * DTO for basic Degree data (used for updating a Degree).
 */
public class DegreeRequest {
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
