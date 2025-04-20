package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.service.DegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for public (non-authenticated) endpoints.
 */
@RestController
@RequestMapping("/api/public/colleges")
public class PublicController {

    private final DegreeService degreeService;

    @Autowired
    public PublicController(DegreeService degreeService) {
        this.degreeService = degreeService;
    }

    /** Public API to get Degree → Course → Department tree for a given college (dropdown data). */
    @GetMapping("/{collegeId}/dropdown")
    public ResponseEntity<?> getCollegeDropdown(@PathVariable String collegeId) {
        // Fetch nested hierarchy of degrees, courses, departments
        var tree = degreeService.getFullHierarchyByCollege(collegeId);
        return ResponseEntity.ok(tree);
    }
}
