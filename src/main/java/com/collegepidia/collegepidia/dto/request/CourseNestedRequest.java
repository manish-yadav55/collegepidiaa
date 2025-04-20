package com.collegepidia.collegepidia.dto.request;

import java.util.List;

/**
 * DTO for nested Course creation (with Departments).
 */
public class CourseNestedRequest extends CourseRequest {
    private List<DepartmentRequest> departments;  // Optional list of departments for this course

    // Getters and Setters
    public List<DepartmentRequest> getDepartments() {
        return departments;
    }
    public void setDepartments(List<DepartmentRequest> departments) {
        this.departments = departments;
    }
}
