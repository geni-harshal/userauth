package com.example.userauth.controller;

import com.example.userauth.dto.*;
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

    // 1) Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@RequestParam String email) {
        long seconds = authService.sendOtp(email);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OTP sent to email", new OtpResponse(seconds))
        );
    }

    // 2) Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> resendOtp(@RequestParam String email) {
        long seconds = authService.resendOtp(email);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OTP resent", new OtpResponse(seconds))
        );
    }

    // 3) Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody OtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OTP verified", null)
        );
    }

    // 4) Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest request) {
        authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User registered successfully", null)
        );
    }

    // 5) Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", response)
        );
    }

    // 6) Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorization) {

        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Invalid Authorization header", null));
        }

        String token = authorization.substring(7);
        authService.logout(token);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Logged out successfully", null)
        );
    }
}
