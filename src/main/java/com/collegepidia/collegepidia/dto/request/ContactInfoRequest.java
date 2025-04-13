// File: dto/request/ContactInfoRequest.java
package com.collegepidia.collegepidia.dto.request;

import lombok.Data;

@Data
public class ContactInfoRequest {
    private String primaryEmail;
    private String secondaryEmail;
    private String phone;
    private String alternatePhone;
    private String officialDomain;
}
