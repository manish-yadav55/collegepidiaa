// File: model/OtpToken.java
package com.collegepidia.collegepidia.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "otp_tokens")
public class OtpToken {

    @Id
    private String id;

    private String email;
    private String otp;
    private Date createdAt;
    private Date expiresAt;
    private boolean used;
    private int attemptCount;
    private Date resentAt;
}
