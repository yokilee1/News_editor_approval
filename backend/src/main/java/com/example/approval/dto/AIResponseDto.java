package com.example.approval.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseDto {
    private String content;
    private String model;
    private Integer tokensUsed;
    private String error;

    public AIResponseDto(String error) {
        this.error = error;
    }

    public AIResponseDto(String content, String model, Integer tokensUsed) {
        this.content = content;
        this.model = model;
        this.tokensUsed = tokensUsed;
    }
} 