package com.example.userauth.service;

import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpStore otpStore;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       OtpStore otpStore,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.otpStore = otpStore;
        this.emailService = emailService;
    }

    public void sendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        String otp = otpStore.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        return otpStore.verifyOtp(email, otp);
    }

    public void register(String username, String email,
                         String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setEmailVerified(true);

        userRepository.save(user);
    }
}
