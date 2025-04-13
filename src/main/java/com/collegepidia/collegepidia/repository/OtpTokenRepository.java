// File: repository/OtpTokenRepository.java
package com.collegepidia.collegepidia.repository;

import com.collegepidia.collegepidia.model.OtpToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpTokenRepository extends MongoRepository<OtpToken, String> {
    Optional<OtpToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<OtpToken> findByEmailAndUsedTrue(String email);

    Optional<OtpToken> findByEmail(String email);
}
