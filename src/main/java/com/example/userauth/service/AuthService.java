package com.example.userauth.service;

import com.example.userauth.dto.LoginRequest;
import com.example.userauth.dto.LoginResponse;
import com.example.userauth.entity.LoginActivity;
import com.example.userauth.entity.User;
import com.example.userauth.exception.ApiException;
import com.example.userauth.repository.LoginActivityRepository;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.util.JwtUtil;
import net.spy.memcached.MemcachedClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final MemcachedClient memcachedClient;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // token validity: 1 hour
    private static final int TOKEN_TTL_SECONDS = 3600;

    public AuthService(UserRepository userRepository,
                       LoginActivityRepository loginActivityRepository,
                       OtpService otpService,
                       EmailService emailService,
                       JwtUtil jwtUtil,
                       MemcachedClient memcachedClient) {
        this.userRepository = userRepository;
        this.loginActivityRepository = loginActivityRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.memcachedClient = memcachedClient;
    }

    /* =========================
       OTP + REGISTRATION FLOW
       ========================= */

    public long sendOtp(String email) {
        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        if (otpService.hasActiveOtp(email)) {
            long rem = otpService.getRemainingSeconds(email);
            throw new ApiException("OTP already sent. Please wait " + rem + " seconds.");
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        return otpService.getRemainingSeconds(email);
    }

    public long resendOtp(String email) {
        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        if (otpService.hasActiveOtp(email)) {
            long rem = otpService.getRemainingSeconds(email);
            throw new ApiException("OTP already active. Wait " + rem + " seconds.");
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return otpService.getRemainingSeconds(email);
    }

    public void verifyOtp(String email, String otp) {
        email = email.trim().toLowerCase();

        boolean ok = otpService.verifyOtp(email, otp);
        if (!ok) {
            throw new ApiException("Invalid or expired OTP");
        }
    }

    @Transactional
    public void register(String username, String email, String password, String confirmPassword) {
        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already registered");
        }
        if (!otpService.isTemporarilyVerified(email)) {
            throw new ApiException("Email not verified via OTP");
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
        otpService.clearTemporaryVerified(email);
    }

    /* =========================
       LOGIN / LOGOUT FLOW
       ========================= */

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Email not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid password");
        }

        // generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), TOKEN_TTL_SECONDS);

        // store token in memcached
        String cacheKey = "auth:token:" + token;
        String cacheValue = user.getId() + "|" + user.getEmail();
        memcachedClient.set(cacheKey, TOKEN_TTL_SECONDS, cacheValue);

        // save login activity
        LoginActivity activity = LoginActivity.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .loginTime(LocalDateTime.now())
                .build();

        loginActivityRepository.save(activity);

        return new LoginResponse(token, TOKEN_TTL_SECONDS);
    }

@Transactional
public void logout(String token) {

    String key = "auth:token:" + token;
    Object val = memcachedClient.get(key);

    // 🔴 IMPORTANT: token not found = expired or already logged out
    if (val == null) {
        throw new ApiException("Invalid or expired token");
    }

    String[] parts = val.toString().split("\\|");
    Long userId = Long.parseLong(parts[0]);

    // delete token
    memcachedClient.delete(key);

    // update logout time
    loginActivityRepository.findTopByUserIdOrderByLoginTimeDesc(userId)
            .ifPresent(activity -> {
                activity.setLogoutTime(LocalDateTime.now());
                loginActivityRepository.save(activity);
            });
}

}
