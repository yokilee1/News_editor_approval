package com.example.approval.service;

import com.example.approval.dto.AIRequestDto;
import com.example.approval.dto.AIResponseDto;
import com.example.approval.model.Permission;
import com.example.approval.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private LogService logService;

    /**
     * 调用DeepSeek API生成内容
     */
    public AIResponseDto generateContent(AIRequestDto request, Long userId, User.Role userRole) {
        try {
            // 检查用户是否有AI生成权限
            if (!rolePermissionService.hasPermission(userRole, Permission.AI_GENERATE)) {
                return new AIResponseDto("用户无权使用AI生成功能");
            }
            
            // 准备API请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            
            // 构建消息数组
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getPrompt());
            
            requestBody.put("model", request.getModel());
            requestBody.put("messages", new Object[]{userMessage});
            requestBody.put("temperature", request.getTemperature());
            requestBody.put("max_tokens", request.getMaxTokens());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送API请求
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            
            // 处理响应
            if (response != null && response.containsKey("choices")) {
                Object[] choices = (Object[]) response.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String generatedText = (String) message.get("content");
                    
                    // 提取使用的tokens
                    Map<String, Object> usage = (Map<String, Object>) response.get("usage");
                    int tokensUsed = ((Number) usage.get("total_tokens")).intValue();
                    
                    // 记录API使用日志
                    logService.recordLog(
                        "AI_GENERATE",
                        "AIRequest",
                        null,
                        "AI内容生成：" + (request.getCategory() != null ? request.getCategory() : "无分类"),
                        userId,
                        null,
                        null
                    );
                    
                    return new AIResponseDto(generatedText, request.getModel(), tokensUsed);
                }
            }
            
            return new AIResponseDto("AI响应解析失败");
        } catch (Exception e) {
            // 记录错误日志
            logService.recordLog(
                "ERROR",
                "AIRequest",
                null,
                "AI内容生成失败: " + e.getMessage(),
                userId,
                null,
                null
            );
            return new AIResponseDto("AI请求失败：" + e.getMessage());
        }
    }

    /**
     * 生成热点趋势分析
     */
    public AIResponseDto generateTrendAnalysis(String topic, Long userId, User.Role userRole) {
        if (!rolePermissionService.hasPermission(userRole, Permission.AI_TREND)) {
            return new AIResponseDto("用户无权使用趋势分析功能");
        }
        
        AIRequestDto request = new AIRequestDto(
            "请对当前热门话题 '" + topic + "' 进行分析，包括：\n" +
            "1. 该话题的背景和现状\n" +
            "2. 该话题在网络上的热度趋势\n" +
            "3. 围绕该话题的主要讨论点\n" +
            "4. 该话题未来可能的发展方向\n" +
            "以清晰的标题和段落进行分析，使用专业客观的语言。"
        );
        request.setCategory("趋势分析");
        
        return generateContent(request, userId, userRole);
    }
}
