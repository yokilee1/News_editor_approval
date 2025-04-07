package com.example.approval.dto;

import java.util.List;

public class AIRequestDto {
    // 用户输入的关键词或主题，用于 AI 辅助写作
    private String prompt;
    private String model = "deepseek-coder";
    private double temperature = 0.7;
    private int maxTokens = 2000;
    private List<String> tags;
    private String category;

    // 构造函数
    public AIRequestDto() {}
    
    public AIRequestDto(String prompt) {
        this.prompt = prompt;
    }

    // Getters and Setters
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}
