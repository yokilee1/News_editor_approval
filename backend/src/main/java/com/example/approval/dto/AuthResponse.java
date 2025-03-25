package com.example.approval.dto;

import java.util.Map;

public class AuthResponse {
    private String token;
    private Map<String, Object> user;

    public AuthResponse(String token, Map<String, Object> user) {
        this.token = token;
        this.user = user;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
} 