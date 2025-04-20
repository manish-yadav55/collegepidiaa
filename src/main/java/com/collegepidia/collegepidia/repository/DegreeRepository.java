package com.collegepidia.collegepidia.repository;

import com.collegepidia.collegepidia.model.Degree;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Degree documents. Only returns active records (isActive = true).
 */
@Repository
public interface DegreeRepository extends MongoRepository<Degree, String> {

    Optional<Degree> findByIdAndIsActiveTrue(String id);
    List<Degree> findByCollegeIdAndIsActiveTrue(String collegeId);
    boolean existsByCodeAndCollegeIdAndIsActiveTrue(String code, String collegeId);
    boolean existsByCodeAndCollegeIdAndIdNot(String code, String collegeId, String id);
}
