package com.collegepidia.collegepidia.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * Entity representing a Course under a Degree.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "courses")
public class Course {

    // Getters and Setters

    @Id
    private String id;
    @Setter
    @Getter
    private String degreeId;
    private String name;
    private String code;
    private boolean isActive;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
}
