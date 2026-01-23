package com.example.userauth.service;

import com.example.userauth.entity.User;
import com.example.userauth.exception.ApiException;
import com.example.userauth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       OtpService otpService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public long sendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        if (otpService.hasActiveOtp(email)) {
            long rem = otpService.getRemainingSeconds(email);
            throw new ApiException("OTP already sent. Please wait " + rem + " seconds before requesting another.");
        }
        String otp = otpService.generateOtp(email);
        // send by email - in prod we don't return it
        emailService.sendOtpEmail(email, otp);
        return otpService.getRemainingSeconds(email);
    }

    public long resendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        // Only allow resend if no active otp
        if (otpService.hasActiveOtp(email)) {
            long rem = otpService.getRemainingSeconds(email);
            throw new ApiException("OTP already active. Wait " + rem + " seconds.");
        }
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return otpService.getRemainingSeconds(email);
    }

    public void verifyOtp(String email, String otp) {
        boolean ok = otpService.verifyOtp(email, otp);
        if (!ok) {
            throw new ApiException("Invalid or expired OTP");
        }
    }

    public void register(String username, String email, String password, String confirmPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        if (!otpService.isTemporarilyVerified(email)) {
            throw new ApiException("Email not verified via OTP. Please verify email first.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ApiException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setEmailVerified(true);

        userRepository.save(user);
        // remove temporary verification flag
        otpService.clearTemporaryVerified(email);
    }
}
