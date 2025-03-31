package com.example.approval.dto;

import java.util.Map;

public class AuthResponse {
    private String token;
    private Map<String, Object> user;
    private Long userId;
    private String username;
    private String name;
    private String role;

    public AuthResponse(String token, Map<String, Object> user) {
        this.token = token;
        this.user = user;
    }

    public AuthResponse() {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 