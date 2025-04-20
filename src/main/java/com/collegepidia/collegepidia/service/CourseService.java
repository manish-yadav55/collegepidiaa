package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.request.CourseRequest;
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
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Service for managing Course entities.
 */
@Service
public class CourseService {

    private final DegreeRepository degreeRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserUtil currentUserUtil;

    @Autowired
    public CourseService(DegreeRepository degreeRepository,
                         CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         CurrentUserUtil currentUserUtil) {
        this.degreeRepository = degreeRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.currentUserUtil = currentUserUtil;
    }

    public CourseResponse getCourseById(String courseId) {
        Course course = courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Fetch department IDs
        List<Department> depts = departmentRepository.findByCourseIdAndIsActiveTrue(courseId);
        List<String> deptIds = depts.stream()
                .map(Department::getId)
                .toList();

        CourseResponse resp = new CourseResponse();
        resp.setId(course.getId());
        resp.setName(course.getName());
        resp.setCode(course.getCode());
        resp.setDegreeId(course.getDegreeId());
        resp.setDepartmentIds(deptIds);
        return resp;
    }

    /**
     * Creates a new Course under the given Degree.
     */

    public Course createCourse(String degreeId, CourseRequest request) {
        // Ensure parent Degree exists and is active
        Degree degree = degreeRepository.findByIdAndIsActiveTrue(degreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found (id=" + degreeId + ")"));
        // Check duplicate course code under the degree
        if (courseRepository.existsByCodeAndDegreeIdAndIsActiveTrue(request.getCode(), degreeId)) {
            throw new DuplicateCodeException("Course code '" + request.getCode() + "' already exists under this degree.");
        }
        Course course = new Course();
        course.setDegreeId(degreeId);
        course.setName(request.getName());
        course.setCode(request.getCode());
        course.setActive(true);
        Date now = new Date();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        String userEmail = currentUserUtil.getCurrentUserEmail();
        course.setCreatedBy(userEmail);
        course.setUpdatedBy(userEmail);
        return courseRepository.save(course);
    }

    /**
     * Lists all active Courses under a given Degree.
     */
    public List<Course> listCoursesByDegree(String degreeId) {
        // Ensure parent Degree exists
        degreeRepository.findByIdAndIsActiveTrue(degreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found (id=" + degreeId + ")"));
        return courseRepository.findByDegreeIdAndIsActiveTrue(degreeId);
    }

    /**
     * Updates an existing Course's details.
     */
    public Course updateCourse(String courseId, CourseRequest request) {
        Course course = courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found (id=" + courseId + ")"));
        if (request.getCode() != null && !request.getCode().equals(course.getCode())) {
            if (courseRepository.existsByCodeAndDegreeIdAndIdNot(request.getCode(), course.getDegreeId(), courseId)) {
                throw new DuplicateCodeException("Course code '" + request.getCode() + "' already exists under this degree.");
            }
            course.setCode(request.getCode());
        }
        if (request.getName() != null) {
            course.setName(request.getName());
        }
        course.setUpdatedAt(new Date());
        course.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        return courseRepository.save(course);
    }

    /**
     * Soft-deletes a Course and all its Departments.
     */
    @Transactional
    public void deleteCourse(String courseId) {
        Course course = courseRepository.findByIdAndIsActiveTrue(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found (id=" + courseId + ")"));
        course.setActive(false);
        course.setUpdatedAt(new Date());
        course.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        courseRepository.save(course);
        // Cascade: mark all departments under this course as inactive
        List<Department> departments = departmentRepository.findByCourseIdAndIsActiveTrue(courseId);
        for (Department dept : departments) {
            dept.setActive(false);
            dept.setUpdatedAt(new Date());
            dept.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
            departmentRepository.save(dept);
        }
    }
}
