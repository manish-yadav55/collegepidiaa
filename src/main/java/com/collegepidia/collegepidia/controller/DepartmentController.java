package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.request.DepartmentRequest;
import com.collegepidia.collegepidia.dto.response.DepartmentResponse;
import com.collegepidia.collegepidia.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Department administration endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments/{deptId}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable String deptId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(deptId));
    }



    /** Add a new department to a course. */
    @PostMapping("/courses/{courseId}/departments")
    public ResponseEntity<DepartmentResponse> addDepartmentToCourse(@PathVariable String courseId,
                                                                    @RequestBody DepartmentRequest request) {
        var dept = departmentService.createDepartment(courseId, request);
        DepartmentResponse resp = new DepartmentResponse();
        resp.setId(dept.getId());
        resp.setName(dept.getName());
        resp.setCode(dept.getCode());
        resp.setCourseId(courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /** Get all active departments under a course. */
    @GetMapping("/courses/{courseId}/departments")
    public ResponseEntity<?> getDepartmentsByCourse(@PathVariable String courseId) {
        var departments = departmentService.listDepartmentsByCourse(courseId);
        var respList = departments.stream().map(d -> {
            DepartmentResponse dr = new DepartmentResponse();
            dr.setId(d.getId());
            dr.setName(d.getName());
            dr.setCode(d.getCode());
            return dr;
        }).toList();
        return ResponseEntity.ok(respList);
    }

    /** Update an existing department. */
    @PutMapping("/departments/{deptId}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable String deptId,
                                                               @RequestBody DepartmentRequest request) {
        var updated = departmentService.updateDepartment(deptId, request);
        DepartmentResponse resp = new DepartmentResponse();
        resp.setId(updated.getId());
        resp.setName(updated.getName());
        resp.setCode(updated.getCode());
        return ResponseEntity.ok(resp);
    }

    /** Delete (soft-delete) a department by ID. */
    @DeleteMapping("/departments/{deptId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String deptId) {
        departmentService.deleteDepartment(deptId);
        return ResponseEntity.noContent().build();
    }
}
