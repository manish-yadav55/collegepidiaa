package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.service.IndiaDegreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class IndiaDegreeController {

    private final IndiaDegreeService indiaDegreeService;

    public IndiaDegreeController(IndiaDegreeService indiaDegreeService) {
        this.indiaDegreeService = indiaDegreeService;
    }

    /**
     * GET /api/public/india/degrees-courses-departments
     * Returns the entire Degrees → Courses → Departments hierarchy from the JSON file.
     */
    @GetMapping("/india/degrees-courses-departments")
    public ResponseEntity<List<Map<String, Object>>> getAllDegreesCoursesDepartments() {
        try {
            List<Map<String, Object>> result = indiaDegreeService.getAllDegreesCoursesDepartments();
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
