package com.example.userauth.dto;

public class OtpResponse {

    private long expiresInSeconds;

    public OtpResponse(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
