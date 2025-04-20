package com.collegepidia.collegepidia.repository;

import com.collegepidia.collegepidia.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

/**
 * Repository for Course documents. Only returns active records (isActive = true).
 */
@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findByIdAndIsActiveTrue(String id);
    List<Course> findByDegreeIdAndIsActiveTrue(String degreeId);
    List<Course> findByDegreeIdInAndIsActiveTrue(Collection<String> degreeIds);
    boolean existsByCodeAndDegreeIdAndIsActiveTrue(String code, String degreeId);
    boolean existsByCodeAndDegreeIdAndIdNot(String code, String degreeId, String id);
}
