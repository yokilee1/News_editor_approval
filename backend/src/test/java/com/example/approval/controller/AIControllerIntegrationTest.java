package com.example.approval.controller;

import com.example.approval.config.TestConfig;
import com.example.approval.dto.AIRequestDto;
import com.example.approval.dto.AIResponseDto;
import com.example.approval.model.User;
import com.example.approval.security.PermissionAspectTestConfig;
import com.example.approval.service.AIService;
import com.example.approval.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestConfig.class, PermissionAspectTestConfig.class})
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class AIControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIService aiService;
    
    @MockBean
    private UserService userService;

    private AIRequestDto testRequest;
    private AIResponseDto testResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 设置测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setRole(User.Role.ADMIN);
        
        // 设置测试请求
        testRequest = new AIRequestDto();
        testRequest.setPrompt("测试提示词");
        testRequest.setModel("deepseek-coder");
        testRequest.setTemperature(0.7);
        testRequest.setMaxTokens(2000);
        testRequest.setTags(Arrays.asList("测试", "AI"));
        testRequest.setCategory("测试类别");
        
        // 设置测试响应
        testResponse = new AIResponseDto("这是AI生成的测试内容", "deepseek-coder", 150);
    }

    @Test
    void generateContent_shouldReturnGeneratedContent() throws Exception {
        when(userService.getUserProfile(anyLong())).thenReturn(testUser);
        when(aiService.generateContent(any(AIRequestDto.class), anyLong(), any(User.Role.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest))
                .requestAttr("userId", 1L)
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content").value("这是AI生成的测试内容"))
                .andExpect(jsonPath("$.model").value("deepseek-coder"))
                .andExpect(jsonPath("$.tokensUsed").value(150));
    }
    
    @Test
    void generateContent_whenError_shouldReturnErrorResponse() throws Exception {
        when(userService.getUserProfile(anyLong())).thenReturn(testUser);
        when(aiService.generateContent(any(AIRequestDto.class), anyLong(), any(User.Role.class)))
                .thenReturn(new AIResponseDto("用户无权使用AI生成功能"));

        mockMvc.perform(post("/api/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest))
                .requestAttr("userId", 1L)
                .header("X-User-ID", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户无权使用AI生成功能"));
    }

    @Test
    void getTrendAnalysis_shouldReturnTrendAnalysis() throws Exception {
        when(userService.getUserProfile(anyLong())).thenReturn(testUser);
        when(aiService.generateTrendAnalysis(anyString(), anyLong(), any(User.Role.class)))
                .thenReturn(testResponse);

        mockMvc.perform(get("/api/ai/trend/测试话题")
                .requestAttr("userId", 1L)
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.analysis").value("这是AI生成的测试内容"))
                .andExpect(jsonPath("$.topic").value("测试话题"))
                .andExpect(jsonPath("$.tokensUsed").value(150));
    }
    
    @Test
    void getTrendAnalysis_whenError_shouldReturnErrorResponse() throws Exception {
        when(userService.getUserProfile(anyLong())).thenReturn(testUser);
        when(aiService.generateTrendAnalysis(anyString(), anyLong(), any(User.Role.class)))
                .thenReturn(new AIResponseDto("用户无权使用趋势分析功能"));

        mockMvc.perform(get("/api/ai/trend/测试话题")
                .requestAttr("userId", 1L)
                .header("X-User-ID", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户无权使用趋势分析功能"));
    }
} 