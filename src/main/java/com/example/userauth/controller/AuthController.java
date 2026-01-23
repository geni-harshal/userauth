package com.example.userauth.controller;

import com.example.userauth.dto.ApiResponse;
import com.example.userauth.dto.OtpRequest;
import com.example.userauth.dto.RegisterRequest;
import com.example.userauth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1) send otp
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Object>> sendOtp(@RequestParam String email) {
        long seconds = authService.sendOtp(email); // if fails throws ApiException
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent to email (check inbox).", 
                new Object() { public final long expiresInSeconds = seconds; }));
    }

    // 2) resend otp
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Object>> resendOtp(@RequestParam String email) {
        long seconds = authService.resendOtp(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP resent to email.", 
                new Object() { public final long expiresInSeconds = seconds; }));
    }

    // 3) verify otp
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@RequestBody OtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp()); // throws if invalid
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP verified. Proceed to register within the allowed time.",
                null));
    }

    // 4) register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody RegisterRequest request) {
        authService.register(request.getUsername(), request.getEmail(), request.getPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", null));
    }
}
