package com.example.userauth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // MUST be at least 256 bits for HS256
    private static final Key KEY =
            Keys.hmacShaKeyFor("THIS_IS_A_VERY_LONG_SECRET_KEY_FOR_JWT_256_BITS".getBytes());

    public String generateToken(String email, Long userId, long ttlSeconds) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlSeconds * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
