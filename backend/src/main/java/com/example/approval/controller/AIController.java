package com.example.approval.controller;

import com.example.approval.dto.AIRequestDto;
import com.example.approval.dto.AIResponseDto;
import com.example.approval.model.Permission;
import com.example.approval.model.User;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.AIService;
import com.example.approval.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI功能接口
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 生成AI内容
     */
    @PostMapping("/generate")
    @RequirePermission(Permission.AI_GENERATE)
    public ResponseEntity<?> generateContent(
            @RequestBody AIRequestDto request,
            @RequestAttribute("userId") Long userId) {
        
        User user = userService.getUserProfile(userId);
        AIResponseDto response = aiService.generateContent(request, userId, user.getRole());
        
        if (response.getError() != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", response.getError());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("content", response.getContent());
        successResponse.put("model", response.getModel());
        successResponse.put("tokensUsed", response.getTokensUsed());
        
        return ResponseEntity.ok(successResponse);
    }
    
    /**
     * 获取热点趋势分析
     */
    @GetMapping("/trend/{topic}")
    @RequirePermission(Permission.AI_TREND)
    public ResponseEntity<?> getTrendAnalysis(
            @PathVariable String topic,
            @RequestAttribute("userId") Long userId) {
        
        User user = userService.getUserProfile(userId);
        AIResponseDto response = aiService.generateTrendAnalysis(topic, userId, user.getRole());
        
        if (response.getError() != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", response.getError());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("analysis", response.getContent());
        successResponse.put("topic", topic);
        successResponse.put("tokensUsed", response.getTokensUsed());
        
        return ResponseEntity.ok(successResponse);
    }
}
