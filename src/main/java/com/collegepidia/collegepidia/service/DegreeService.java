package com.collegepidia.collegepidia.service;

import com.collegepidia.collegepidia.dto.request.CourseNestedRequest;
import com.collegepidia.collegepidia.dto.request.DegreeFullRequest;
import com.collegepidia.collegepidia.dto.request.DegreeRequest;
import com.collegepidia.collegepidia.dto.request.DepartmentRequest;
import com.collegepidia.collegepidia.dto.response.*;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing Degree entities and related operations.
 */
@Service
public class DegreeService {

    private final DegreeRepository degreeRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserUtil currentUserUtil;
    @Autowired
    public DegreeService(DegreeRepository degreeRepository,
                         CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         CurrentUserUtil currentUserUtil) {
        this.degreeRepository = degreeRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.currentUserUtil = currentUserUtil;
    }

    /**
     * Creates a new Degree along with its Courses and Departments (nested creation).
     * Throws DuplicateCodeException if any code conflicts.
     */
    @Transactional
    public DegreeResponse createFullDegree(DegreeFullRequest request) {
        // 1️⃣ Duplicate‐degree check
        if (degreeRepository.existsByCodeAndCollegeIdAndIsActiveTrue(
                request.getCode(), request.getCollegeId())) {
            throw new DuplicateCodeException(
                    "Degree code '" + request.getCode() + "' already exists in this college."
            );
        }

        // 2️⃣ Duplicate checks in the payload
        List<CourseNestedRequest> courseReqs = request.getCourses();
        if (courseReqs != null) {
            for (CourseNestedRequest cReq : courseReqs) {
                long dupC = courseReqs.stream()
                        .filter(x -> x.getCode().equalsIgnoreCase(cReq.getCode()))
                        .count();
                if (dupC > 1) {
                    throw new DuplicateCodeException(
                            "Duplicate course code '" + cReq.getCode() + "' in request."
                    );
                }
                List<DepartmentRequest> dReqs = cReq.getDepartments();
                if (dReqs != null) {
                    for (DepartmentRequest dReq : dReqs) {
                        long dupD = dReqs.stream()
                                .filter(x -> x.getCode().equalsIgnoreCase(dReq.getCode()))
                                .count();
                        if (dupD > 1) {
                            throw new DuplicateCodeException(
                                    "Duplicate department code '" + dReq.getCode() +
                                            "' in course '" + cReq.getName() + "'."
                            );
                        }
                    }
                }
            }
        }

        // 3️⃣ Create Degree via setters
        Date now = new Date();
        String user = currentUserUtil.getCurrentUserEmail();

        Degree degree = new Degree();
        degree.setCollegeId(request.getCollegeId());
        degree.setName(request.getName());
        degree.setCode(request.getCode());
        degree.setActive(true);
        degree.setCreatedAt(now);
        degree.setUpdatedAt(now);
        degree.setCreatedBy(user);
        degree.setUpdatedBy(user);
        degree = degreeRepository.save(degree);

        // 4️⃣ Build DegreeResponse
        DegreeResponse degreeResp = new DegreeResponse();
        degreeResp.setId(degree.getId());
        degreeResp.setName(degree.getName());
        degreeResp.setCode(degree.getCode());

        // 5️⃣ Create Courses + Departments, collect IDs
        List<CourseResponse> courseRespList = new ArrayList<>();
        if (courseReqs != null) {
            for (CourseNestedRequest cReq : courseReqs) {
                // — Save Course
                Course course = new Course();
                course.setDegreeId(degree.getId());
                course.setName(cReq.getName());
                course.setCode(cReq.getCode());
                course.setActive(true);
                course.setCreatedAt(now);
                course.setUpdatedAt(now);
                course.setCreatedBy(user);
                course.setUpdatedBy(user);
                course = courseRepository.save(course);

                // — Save Departments (if any) and collect IDs
                List<String> deptIds = new ArrayList<>();
                List<DepartmentRequest> dReqs = cReq.getDepartments();
                if (dReqs != null) {
                    for (DepartmentRequest dReq : dReqs) {
                        Department dept = new Department();
                        dept.setCourseId(course.getId());
                        dept.setName(dReq.getName());
                        dept.setCode(dReq.getCode());
                        dept.setActive(true);
                        dept.setCreatedAt(now);
                        dept.setUpdatedAt(now);
                        dept.setCreatedBy(user);
                        dept.setUpdatedBy(user);
                        dept = departmentRepository.save(dept);
                        deptIds.add(dept.getId());
                    }
                }

                // — Build CourseResponse
                CourseResponse courseResp = new CourseResponse();
                courseResp.setId(course.getId());
                courseResp.setName(course.getName());
                courseResp.setCode(course.getCode());
                courseResp.setDegreeId(degree.getId());
                courseResp.setDepartmentIds(deptIds);

                courseRespList.add(courseResp);
            }
        }

        // 6️⃣ Attach courses to degree and return
        degreeResp.setCourses(courseRespList);
        return degreeResp;
    }

    /**
     * Returns all active Degrees for a given college.
     */
    public List<Map<String,Object>> listDegreesWithCourseDeptIds(String collegeId) {
        List<Degree> degrees = degreeRepository.findByCollegeIdAndIsActiveTrue(collegeId);
        List<Map<String,Object>> out = new ArrayList<>();

        for (Degree deg : degrees) {
            // Use LinkedHashMap to preserve ordering
            Map<String,Object> degreeMap = new LinkedHashMap<>();
            degreeMap.put("id", deg.getId());
            degreeMap.put("name", deg.getName());
            degreeMap.put("code", deg.getCode());

            List<Map<String,Object>> coursesList = new ArrayList<>();
            List<Course> courses = courseRepository.findByDegreeIdAndIsActiveTrue(deg.getId());
            for (Course c : courses) {
                Map<String,Object> courseMap = new LinkedHashMap<>();
                courseMap.put("courseId", c.getId());

                List<String> deptIds = departmentRepository.findByCourseIdAndIsActiveTrue(c.getId())
                        .stream()
                        .map(Department::getId)
                        .toList();
                courseMap.put("departmentIds", deptIds);
                coursesList.add(courseMap);
            }

            degreeMap.put("courses", coursesList);
            out.add(degreeMap);
        }

        return out;
    }

    /**
     * Updates an existing Degree's details. Throws DuplicateCodeException on code conflict.
     */
    public Degree updateDegree(String degreeId, DegreeRequest request) {
        Degree degree = degreeRepository.findByIdAndIsActiveTrue(degreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found (id=" + degreeId + ")"));
        // Update fields if provided and check for duplicates on code change
        if (request.getCode() != null && !request.getCode().equals(degree.getCode())) {
            if (degreeRepository.existsByCodeAndCollegeIdAndIdNot(request.getCode(), degree.getCollegeId(), degreeId)) {
                throw new DuplicateCodeException("Degree code '" + request.getCode() + "' already exists in this college.");
            }
            degree.setCode(request.getCode());
        }
        if (request.getName() != null) {
            degree.setName(request.getName());
        }
        // (We do not allow changing collegeId via update to maintain hierarchy integrity)
        degree.setUpdatedAt(new Date());
        degree.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        return degreeRepository.save(degree);
    }

    /**
     * Soft-deletes a Degree (marks it inactive) and cascades inactivation to its Courses and Departments.
     */
    @Transactional
    public void deleteDegree(String degreeId) {
        Degree degree = degreeRepository.findByIdAndIsActiveTrue(degreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found (id=" + degreeId + ")"));
        // Mark degree as inactive
        degree.setActive(false);
        degree.setUpdatedAt(new Date());
        degree.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
        degreeRepository.save(degree);
        // Cascade: mark all courses under this degree as inactive
        List<Course> courses = courseRepository.findByDegreeIdAndIsActiveTrue(degreeId);
        for (Course course : courses) {
            course.setActive(false);
            course.setUpdatedAt(new Date());
            course.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
            courseRepository.save(course);
            // Mark all departments under this course as inactive
            List<Department> departments = departmentRepository.findByCourseIdAndIsActiveTrue(course.getId());
            for (Department dept : departments) {
                dept.setActive(false);
                dept.setUpdatedAt(new Date());
                dept.setUpdatedBy(currentUserUtil.getCurrentUserEmail());
                departmentRepository.save(dept);
            }
        }
    }

    /**
     * Fetches the entire Degree → Course → Department hierarchy for a college (for public dropdown tree).
     */
    public List<FullDegreeResponse> getFullHierarchyByCollege(String collegeId) {
        List<Degree> degrees = degreeRepository.findByCollegeIdAndIsActiveTrue(collegeId);
        List<FullDegreeResponse> result = new ArrayList<>();

        for (Degree degree : degrees) {
            FullDegreeResponse degreeRes = new FullDegreeResponse();
            degreeRes.setId(degree.getId());
            degreeRes.setName(degree.getName());
            degreeRes.setCode(degree.getCode());

            List<Course> courses = courseRepository.findByDegreeIdAndIsActiveTrue(degree.getId());
            List<FullCourseResponse> courseResList = new ArrayList<>();

            for (Course course : courses) {
                FullCourseResponse courseRes = new FullCourseResponse();
                courseRes.setId(course.getId());
                courseRes.setName(course.getName());
                courseRes.setCode(course.getCode());

                List<Department> departments = departmentRepository.findByCourseIdAndIsActiveTrue(course.getId());
                List<FullDepartmentResponse> deptResList = new ArrayList<>();

                for (Department dept : departments) {
                    FullDepartmentResponse deptRes = new FullDepartmentResponse();
                    deptRes.setId(dept.getId());
                    deptRes.setName(dept.getName());
                    deptRes.setCode(dept.getCode());
                    deptResList.add(deptRes);
                }

                courseRes.setDepartments(deptResList);
                courseResList.add(courseRes);
            }

            degreeRes.setCourses(courseResList);
            result.add(degreeRes);
        }

        return result;
    }

}
