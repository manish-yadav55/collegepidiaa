// File: security/CustomUserDetailsService.java
package com.collegepidia.collegepidia.security;

import com.collegepidia.collegepidia.model.College;
import com.collegepidia.collegepidia.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CollegeRepository collegeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        College college = collegeRepository.findByAdminAccountEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("College not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(college.getAdminAccount().getEmail())
                .password(college.getAdminAccount().getPassword())
                .roles("COLLEGE") // Simple role assignment; customize as needed
                .build();
    }
}
