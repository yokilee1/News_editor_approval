package com.example.approval.dto;

import java.util.List;
import lombok.Data;

@Data
public class AIRequestDto {
    // 用户输入的关键词或主题，用于 AI 辅助写作
    private String prompt;
    private String systemPrompt;  // 新增系统提示词
    private String model = "doubao-1-5-pro-32k-250115";  // 更新默认模型
    private double temperature = 0.7;
    private int maxTokens = 2000;
    private String category;
    private List<String> tags;

    // 默认构造函数
    public AIRequestDto() {
    }

    // 带提示词的构造函数
    public AIRequestDto(String prompt) {
        this.prompt = prompt;
    }

    // 完整参数的构造函数
    public AIRequestDto(String prompt, String model, double temperature, 
                       int maxTokens, String category, List<String> tags) {
        this.prompt = prompt;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.category = category;
        this.tags = tags;
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
