package com.example.approval.service;

import com.example.approval.dto.AIRequestDto;
import com.example.approval.dto.AIResponseDto;
import com.example.approval.model.Permission;
import com.example.approval.model.User;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AIServiceTest {

    @Mock
    private RolePermissionService rolePermissionService;

    @Mock
    private LogService logService;

    @Mock
    private ArkService arkService;

    private AIService aiService;
    private Long userId;
    private User.Role adminRole;
    private AIRequestDto testRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aiService = new AIService();
        
        // 设置依赖
        ReflectionTestUtils.setField(aiService, "rolePermissionService", rolePermissionService);
        ReflectionTestUtils.setField(aiService, "logService", logService);
        ReflectionTestUtils.setField(aiService, "arkService", arkService);
        
        // 初始化测试数据
        userId = 1L;
        adminRole = User.Role.ADMIN;
        
        testRequest = new AIRequestDto();
        testRequest.setPrompt("测试提示词");
        testRequest.setModel("doubao-1-5-pro-32k-250115");
        testRequest.setTemperature(0.7);
        testRequest.setMaxTokens(2000);
        testRequest.setCategory("测试类别");
        testRequest.setSystemPrompt("你是一个专业的编辑助手");
    }

    @Test
    void generateContent_Success() {
        // 模拟权限检查
        when(rolePermissionService.hasPermission(adminRole, Permission.AI_GENERATE))
            .thenReturn(true);

        // 构造API响应
        ChatCompletionChoice mockChoice = mock(ChatCompletionChoice.class);
        ChatMessage mockMessage = mock(ChatMessage.class);
        when(mockMessage.getContent()).thenReturn("AI生成的测试内容");
        when(mockChoice.getMessage()).thenReturn(mockMessage);
        when(mockChoice.getFinishReason()).thenReturn("stop");

        List<ChatCompletionChoice> choices = Collections.singletonList(mockChoice);
        
        // 构造完整的响应对象
        ChatCompletionResult mockResult = mock(ChatCompletionResult.class);
        when(mockResult.getChoices()).thenReturn(choices);
        
        // 模拟API调用
        when(arkService.createChatCompletion(any(ChatCompletionRequest.class)))
            .thenReturn(mockResult);

        // 执行测试
        AIResponseDto response = aiService.generateContent(testRequest, userId, adminRole);

        // 验证结果
        assertNotNull(response, "响应对象不应为空");
        assertNotNull(response.getContent(), "响应内容不应为空");
        assertEquals("AI生成的测试内容", response.getContent());
        assertNull(response.getError());

        // 验证日志记录
        verify(logService).recordLog(
            eq("AI_GENERATE"),
            eq("AIRequest"),
            isNull(),
            contains("AI内容生成"),
            eq(userId),
            any(String.class),
            isNull()
        );
    }

    @Test
    void generateContent_APIError() {
        // 模拟权限检查
        when(rolePermissionService.hasPermission(adminRole, Permission.AI_GENERATE))
            .thenReturn(true);

        // 模拟API调用异常
        when(arkService.createChatCompletion(any(ChatCompletionRequest.class)))
            .thenThrow(new RuntimeException("API调用失败"));

        // 执行测试
        AIResponseDto response = aiService.generateContent(testRequest, userId, adminRole);

        // 验证结果
        assertNotNull(response, "响应不应为空");
        assertNotNull(response.getError(), "错误信息不应为空");
        assertEquals("AI请求失败：API调用失败", response.getError());

        // 验证错误日志记录
        verify(logService).recordLog(
            eq("ERROR"),
            eq("AIRequest"),
            isNull(),
            eq("AI内容生成失败: API调用失败"),
            eq(userId),
            eq("API调用失败"),
            isNull()
        );
    }

    @Test
    void generateTrendAnalysis_Success() {
        // 模拟权限检查
        when(rolePermissionService.hasPermission(adminRole, Permission.AI_TREND))
            .thenReturn(true);
        when(rolePermissionService.hasPermission(adminRole, Permission.AI_GENERATE))
            .thenReturn(true);

        // 构造API响应
        ChatCompletionChoice mockChoice = mock(ChatCompletionChoice.class);
        ChatMessage mockMessage = mock(ChatMessage.class);
        when(mockMessage.getContent()).thenReturn("趋势分析测试内容");
        when(mockChoice.getMessage()).thenReturn(mockMessage);
        when(mockChoice.getFinishReason()).thenReturn("stop");

        List<ChatCompletionChoice> choices = Collections.singletonList(mockChoice);
        
        // 构造完整的响应对象
        ChatCompletionResult mockResult = mock(ChatCompletionResult.class);
        when(mockResult.getChoices()).thenReturn(choices);
        
        // 模拟API调用
        when(arkService.createChatCompletion(any(ChatCompletionRequest.class)))
            .thenReturn(mockResult);

        // 执行测试
        AIResponseDto response = aiService.generateTrendAnalysis("测试话题", userId, adminRole);

        // 验证结果
        assertNotNull(response, "响应不应为空");
        assertEquals("趋势分析测试内容", response.getContent());
        assertNull(response.getError());

        // 验证API调用参数
        verify(arkService).createChatCompletion(argThat(request -> {
            List<ChatMessage> messages = request.getMessages();
            return messages.size() == 2 &&
                   messages.get(0).getRole().equals(ChatMessageRole.SYSTEM) &&
                   messages.get(1).getRole().equals(ChatMessageRole.USER) &&
                   request.getModel().equals("doubao-1-5-pro-32k-250115") &&
                   request.getTemperature() == 0.7 &&
                   request.getMaxTokens() == 2000;
        }));
    }
} 