package com.collegepidia.collegepidia.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class FullCourseResponse {
    private String id;
    private String name;
    private String code;
    private List<FullDepartmentResponse> departments;

    // Getters and Setters
}
