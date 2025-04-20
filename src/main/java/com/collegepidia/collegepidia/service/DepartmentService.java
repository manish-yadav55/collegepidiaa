package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.request.DepartmentRequest;
import com.collegepidia.collegepidia.dto.response.CourseResponse;
import com.collegepidia.collegepidia.dto.response.DegreeResponse;
import com.collegepidia.collegepidia.dto.response.DepartmentResponse;
import com.collegepidia.collegepidia.exception.DuplicateCodeException;
import com.collegepidia.collegepidia.exception.ResourceNotFoundException;
import com.collegepidia.collegepidia.model.Course;
import com.collegepidia.collegepidia.model.Degree;
import com.collegepidia.collegepidia.model.Department;
import com.collegepidia.collegepidia.repository.CourseRepository;
import com.collegepidia.collegepidia.repository.DegreeRepository;
import com.collegepidia.collegepidia.repository.DepartmentRepository;
import com.collegepidia.collegepidia.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * Service for managing Department entities.
 */
@Service
public class DepartmentService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserUtil currentUserUtil;
    private final DegreeRepository degreeRepository;

    @Autowired
    public DepartmentService(CourseRepository courseRepository,
                             DepartmentRepository departmentRepository,
                             CurrentUserUtil currentUserUtil, DegreeRepository degreeRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.currentUserUtil = currentUserUtil;
        this.degreeRepository = degreeRepository;
    }

    public DepartmentResponse getDepartmentById(String deptId) {
        Department dept = departmentRepository.findByIdAndIsActiveTrue(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        DepartmentResponse resp = new DepartmentResponse();
        resp.setId(dept.getId());
        resp.setName(dept.getName());
        resp.setCode(dept.getCode());
        resp.setCourseId(dept.getCourseId());
        return resp;
    }

    /**
     * Creates a new Department under the given Course.
     */
    public Department createDepartment(String courseId, DepartmentRequest request) {
        // Ensure parent Course exists and is active
        Course course = courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found (id=" + courseId + ")"));
        // Check duplicate department code under the course
        if (departmentRepository.existsByCodeAndCourseIdAndIsActiveTrue(request.getCode(), courseId)) {
            throw new DuplicateCodeException("Department code '" + request.getCode() + "' already exists under this course.");
        }
        Department department = new Department();
        department.setCourseId(courseId);
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setActive(true);
        Date now = new Date();
        department.setCreatedAt(now);
        department.setUpdatedAt(now);
        String userEmail = currentUserUtil.getCurrentUserEmail();
        department.setCreatedBy(userEmail);
        department.setUpdatedBy(userEmail);
        return departmentRepository.save(department);
    }

    /**
     * Lists all active Departments under a given Course.
     */
    public List<Department> listDepartmentsByCourse(String courseId) {
        // Ensure parent Course exists
        courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found (id=" + courseId + ")"));
        return departmentRepository.findByCourseIdAndIsActiveTrue(courseId);
    }

    /**
     * Updates an existing Department's details.
     */
    public Department updateDepartment(String deptId, DepartmentRequest request) {
        Department department = departmentRepository.findByIdAndIsActiveTrue(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found (id=" + deptId + ")"));
        if (request.getCode() != null && !request.getCode().equals(department.getCode())) {
            if (departmentRepository.existsByCodeAndCourseIdAndIdNot(request.getCode(), department.getCourseId(), deptId)) {
                throw new DuplicateCodeException("Department code '" + request.getCode() + "' already exists under this course.");
            }
            department.setCode(request.getCode());
        }
        if (request.getName() != null) {
            department.setName(request.getName());
        }
        department.setUpdatedAt(new Date());
        department.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        return departmentRepository.save(department);
    }

    /**
     * Soft-deletes a Department.
     */
    public void deleteDepartment(String deptId) {
        Department department = departmentRepository.findByIdAndIsActiveTrue(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found (id=" + deptId + ")"));
        department.setActive(false);
        department.setUpdatedAt(new Date());
        department.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        departmentRepository.save(department);
    }
}
