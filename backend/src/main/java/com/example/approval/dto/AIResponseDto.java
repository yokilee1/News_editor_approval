package com.example.approval.dto;

public class AIResponseDto {
    private String text;
    private String model;
    private int tokensUsed;
    private boolean success;
    private String error;

    public AIResponseDto() {
        this.success = true;
    }

    public AIResponseDto(String text, String model, int tokensUsed) {
        this.text = text;
        this.model = model;
        this.tokensUsed = tokensUsed;
        this.success = true;
    }

    public AIResponseDto(String error) {
        this.success = false;
        this.error = error;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
} 