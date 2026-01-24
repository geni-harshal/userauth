package com.example.userauth.controller;

import com.example.userauth.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    // GET example (already done)
    @GetMapping
    public ApiResponse<String> dashboard() {
        return new ApiResponse<>(
                true,
                "Dashboard data fetched successfully",
                "Welcome to your dashboard!"
        );
    }

    // ✅ POST example (PROTECTED)
    @PostMapping("/update-profile")
    public ApiResponse<String> updateProfile(@RequestBody UpdateProfileRequest request) {

        // At this point:
        // - JWT is already validated by AuthFilter
        // - Token exists in Memcached
        // - User is authenticated

        return new ApiResponse<>(
                true,
                "Profile updated successfully",
                "Updated username to: " + request.getUsername()
        );
    }

    // simple inner DTO for demo
    static class UpdateProfileRequest {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
