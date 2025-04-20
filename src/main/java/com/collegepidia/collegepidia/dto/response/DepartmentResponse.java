package com.collegepidia.collegepidia.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for Department data in API responses.
 */
@Setter
@Getter
public class DepartmentResponse {
    private String id;
    private String name;
    private String code;
    private String courseId;

}
