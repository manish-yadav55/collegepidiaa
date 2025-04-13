// File: controller/DashboardController.java
package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getDashboardInfo(email));
    }
}
