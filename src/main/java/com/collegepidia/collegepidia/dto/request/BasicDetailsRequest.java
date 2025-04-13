// File: dto/request/BasicDetailsRequest.java
package com.collegepidia.collegepidia.dto.request;

import com.collegepidia.collegepidia.model.College.Address;
import lombok.Data;

@Data
public class BasicDetailsRequest {
    private String name;
    private String shortName;
    private Integer establishmentYear;
    private String institutionId;
    private String type; // e.g., government/private
    private Address address; // re-use the same Address model or create a separate DTO if required
    private String website;
}
