package com.collegepidia.collegepidia.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for Course data in API responses.
 */
@Setter
@Getter
public class CourseResponse {
    // Getters and Setters
    private String id;
    private String name;
    private String code;
    private String degreeId;
    private List<String> departmentIds;

}
