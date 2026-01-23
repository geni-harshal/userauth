package com.example.userauth.service;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final MemcachedClient memcachedClient;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private final int otpTtlSeconds;
    private final int verifiedTtlSeconds;

    public OtpService(MemcachedClient memcachedClient,
                      @Value("${otp.ttl.seconds:120}") int otpTtlSeconds,
                      @Value("${otp.verified.ttl.seconds:600}") int verifiedTtlSeconds) {
        this.memcachedClient = memcachedClient;
        this.otpTtlSeconds = otpTtlSeconds;
        this.verifiedTtlSeconds = verifiedTtlSeconds;
    }

    private String otpKey(String email) {
        return "otp:" + email.toLowerCase();
    }

    private String otpTsKey(String email) {
        return "otp_ts:" + email.toLowerCase();
    }

    private String verifiedTempKey(String email) {
        return "otp_verified_temp:" + email.toLowerCase();
    }

    /**
     * Generate OTP, store hashed value and timestamp in memcached with TTL.
     * Returns plain OTP (so caller can send email) but we never persist plain OTP anywhere else.
     */
    public String generateOtp(String email) {
        // generate 6-digit numeric OTP
        int raw = 100000 + (int)(Math.random() * 900000);
        String otp = String.valueOf(raw);

        String hashed = bcrypt.encode(otp);

        String key = otpKey(email);
        String tsKey = otpTsKey(email);
        long now = Instant.now().getEpochSecond();

        // store hashed OTP with TTL
        memcachedClient.set(key, otpTtlSeconds, hashed);
        // store timestamp to compute remaining TTL
        memcachedClient.set(tsKey, otpTtlSeconds, String.valueOf(now));

        return otp;
    }

    public boolean hasActiveOtp(String email) {
        Object val = memcachedClient.get(otpKey(email));
        return val != null;
    }

    /**
     * returns seconds remaining until OTP expiry, or 0 if none.
     */
    public long getRemainingSeconds(String email) {
        Object ts = memcachedClient.get(otpTsKey(email));
        if (ts == null) return 0;
        try {
            long created = Long.parseLong(ts.toString());
            long expiresAt = created + otpTtlSeconds;
            long now = Instant.now().getEpochSecond();
            long rem = expiresAt - now;
            return Math.max(0, rem);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verify OTP: compare provided plain OTP with hashed OTP stored. If valid, remove OTP key and set a temp verified flag.
     */
    public boolean verifyOtp(String email, String otp) {
        Object stored = memcachedClient.get(otpKey(email));
        if (stored == null) return false;

        String hashed = stored.toString();
        boolean ok = bcrypt.matches(otp, hashed);
        if (!ok) return false;

        // verification success -> remove otp keys and create a temp verified key for registration
        memcachedClient.delete(otpKey(email));
        memcachedClient.delete(otpTsKey(email));
        memcachedClient.set(verifiedTempKey(email), verifiedTtlSeconds, "true");
        return true;
    }

    /**
     * Check if email has temporary verified flag
     */
    public boolean isTemporarilyVerified(String email) {
        Object v = memcachedClient.get(verifiedTempKey(email));
        return v != null;
    }

    /**
     * Remove temporary verified flag (after registration)
     */
    public void clearTemporaryVerified(String email) {
        memcachedClient.delete(verifiedTempKey(email));
    }
}
