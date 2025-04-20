package com.collegepidia.collegepidia.repository;

import com.collegepidia.collegepidia.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

/**
 * Repository for Department documents. Only returns active records (isActive = true).
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    Optional<Department> findByIdAndIsActiveTrue(String id);
    List<Department> findByCourseIdAndIsActiveTrue(String courseId);
    List<Department> findByCourseIdInAndIsActiveTrue(Collection<String> courseIds);
    boolean existsByCodeAndCourseIdAndIsActiveTrue(String code, String courseId);
    boolean existsByCodeAndCourseIdAndIdNot(String code, String courseId, String id);
}
