package com.collegepidia.collegepidia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * Entity representing a Degree in a college.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "degrees")
public class Degree {

    @Id
    private String id;
    private String collegeId;
    private String name;
    private String code;
    private boolean isActive;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

}
