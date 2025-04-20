package com.collegepidia.collegepidia.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class FullDegreeRequest {
    private String collegeId;
    private String name;
    private String code;
    private List<FullCourse> courses;

    @Data
    public static class FullCourse {
        private String name;
        private String code;
        private List<FullDepartment> departments;
    }

    @Data
    public static class FullDepartment {
        private String name;
        private String code;
    }
}
