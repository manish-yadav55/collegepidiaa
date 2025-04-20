package com.collegepidia.collegepidia.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for Degree data in API responses.
 */
@Setter
@Getter
public class DegreeResponse {
    // Getters and Setters
    private String id;
    private String name;
    private String code;
    private List<CourseResponse> courses;  // Nested courses (optional, used in hierarchical responses)

}
