package com.example.userauth.dto;

public class LoginResponse {
    private String token;
    private long expiresInSeconds;

    public LoginResponse(String token, long expiresInSeconds) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getToken() { return token; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}
