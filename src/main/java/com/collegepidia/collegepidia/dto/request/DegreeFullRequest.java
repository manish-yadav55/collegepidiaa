package com.collegepidia.collegepidia.dto.request;

import java.util.List;

/**
 * DTO for creating a Degree with its Courses and Departments (nested creation).
 */
public class DegreeFullRequest {
    private String collegeId;
    private String name;
    private String code;
    private List<CourseNestedRequest> courses;  // Optional: can be empty or null if no courses to create

    // Getters and Setters
    public String getCollegeId() {
        return collegeId;
    }
    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }
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
    public List<CourseNestedRequest> getCourses() {
        return courses;
    }
    public void setCourses(List<CourseNestedRequest> courses) {
        this.courses = courses;
    }
}
