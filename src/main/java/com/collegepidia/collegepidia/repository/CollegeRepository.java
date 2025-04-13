// File: repository/CollegeRepository.java
package com.collegepidia.collegepidia.repository;

import com.collegepidia.collegepidia.model.College;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CollegeRepository extends MongoRepository<College, String> {
    Optional<College> findByAdminAccountEmail(String email);
}
