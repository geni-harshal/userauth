package com.example.userauth.controller;

import com.example.userauth.dto.OtpRequest;
import com.example.userauth.dto.RegisterRequest;
import com.example.userauth.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        authService.sendOtp(email);
        return "OTP sent to email";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpRequest request) {
        return authService.verifyOtp(request.getEmail(), request.getOtp())
                ? "OTP verified"
                : "Invalid OTP";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword()
        );
        return "User registered successfully";
    }
}
