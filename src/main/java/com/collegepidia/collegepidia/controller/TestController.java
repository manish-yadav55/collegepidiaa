// Update: File: controller/TestController.java
package com.collegepidia.collegepidia.controller;

import com.collegepidia.collegepidia.dto.request.*;
import com.collegepidia.collegepidia.dto.response.ApiResponse;
import com.collegepidia.collegepidia.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    private final AuthService authService;
    @GetMapping
    public String welcome() {
        return "Welcome to CollegePidia API!";
    }


}
