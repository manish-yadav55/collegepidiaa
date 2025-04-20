package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.request.DegreeFullRequest;
import com.collegepidia.collegepidia.dto.request.DegreeRequest;
import com.collegepidia.collegepidia.dto.response.DegreeResponse;
import com.collegepidia.collegepidia.service.DegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Degree administration endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class DegreeController {

    private final DegreeService degreeService;

    @Autowired
    public DegreeController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    /** Create a new degree with nested courses and departments (one-shot). */
    @PostMapping("/degrees/full")
    public ResponseEntity<DegreeResponse> createDegreeWithCourses(@RequestBody DegreeFullRequest request) {
        DegreeResponse created = degreeService.createFullDegree(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Update an existing degree's details. */
    @PutMapping("/degrees/{degreeId}")
    public ResponseEntity<DegreeResponse> updateDegree(@PathVariable String degreeId,
                                                       @RequestBody DegreeRequest request) {
        var updated = degreeService.updateDegree(degreeId, request);
        DegreeResponse resp = new DegreeResponse();
        resp.setId(updated.getId());
        resp.setName(updated.getName());
        resp.setCode(updated.getCode());
        return ResponseEntity.ok(resp);
    }

    /** Delete (soft-delete) a degree by ID. */
    @DeleteMapping("/degrees/{degreeId}")
    public ResponseEntity<Void> deleteDegree(@PathVariable String degreeId) {
        degreeService.deleteDegree(degreeId);
        return ResponseEntity.noContent().build();
    }

    /** Get all active degrees for a given college (admin use). */
    @GetMapping("/colleges/{collegeId}/degrees")
    public ResponseEntity<List<Map<String,Object>>> getDegreesByCollege(
            @PathVariable String collegeId) {

        List<Map<String,Object>> data =
                degreeService.listDegreesWithCourseDeptIds(collegeId);

        return ResponseEntity.ok(data);
    }

}
