package com.example.userauth.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    private record OtpData(String otp, Instant expiry) {}

    private final Map<String, OtpData> store = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + (int)(Math.random() * 900000));
        store.put(email, new OtpData(otp, Instant.now().plusSeconds(300)));
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData data = store.get(email);
        if (data == null || Instant.now().isAfter(data.expiry())) {
            return false;
        }
        boolean valid = data.otp().equals(otp);
        if (valid) {
            store.remove(email);
        }
        return valid;
    }
}
