package com.collegepidia.collegepidia.controller;


import com.collegepidia.collegepidia.dto.request.CourseRequest;
import com.collegepidia.collegepidia.dto.response.CourseResponse;
import com.collegepidia.collegepidia.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Course administration endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable String courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }



    /** Add a new course to a degree. */
    @PostMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<CourseResponse> addCourseToDegree(@PathVariable String degreeId,
                                                            @RequestBody CourseRequest request) {
        var course = courseService.createCourse(degreeId, request);
        CourseResponse resp = new CourseResponse();
        resp.setId(course.getId());
        resp.setName(course.getName());
        resp.setCode(course.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /** Get all active courses under a degree. */
    @GetMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<?> getCoursesByDegree(@PathVariable String degreeId) {
        var courses = courseService.listCoursesByDegree(degreeId);
        var respList = courses.stream().map(c -> {
            CourseResponse cr = new CourseResponse();
            cr.setId(c.getId());
            cr.setName(c.getName());
            cr.setCode(c.getCode());
            return cr;
        }).toList();
        return ResponseEntity.ok(respList);
    }

    /** Update an existing course. */
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable String courseId,
                                                       @RequestBody CourseRequest request) {
        var updated = courseService.updateCourse(courseId, request);
        CourseResponse resp = new CourseResponse();
        resp.setId(updated.getId());
        resp.setName(updated.getName());
        resp.setCode(updated.getCode());
        return ResponseEntity.ok(resp);
    }

    /** Delete (soft-delete) a course by ID. */
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
