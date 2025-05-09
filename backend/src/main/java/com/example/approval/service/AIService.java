package com.example.approval.service;

import com.example.approval.dto.AIRequestDto;
import com.example.approval.dto.AIResponseDto;
import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.model.Permission;
import com.example.approval.model.User;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    @Value("${volc.api.key:8bd176a4-fc4c-4ec1-bde4-62a79566bf2c}")
    private String apiKey;

    @Value("${volc.api.url:https://ark.cn-beijing.volces.com/api/v3}")
    private String apiUrl;

    private ArkService arkService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private LogService logService;

    @PostConstruct
    public void init() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        arkService = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(apiUrl)
                .apiKey(apiKey)
                .build();
    }

    @Autowired
    private ContentService contentService;

    /**
     * 调用火山引擎 API 生成内容
     */

    public AIResponseDto generateContent(AIRequestDto request, Long userId, User.Role userRole) {
        try {
            // 检查用户权限
            if (!rolePermissionService.hasPermission(userRole, Permission.AI_GENERATE)) {
                return new AIResponseDto("用户无权使用AI生成功能");
            }

            // 准备消息列表
            List<ChatMessage> messages = new ArrayList<>();

            // 添加系统提示词
            if (request.getSystemPrompt() != null) {
                messages.add(ChatMessage.builder()
                        .role(ChatMessageRole.SYSTEM)
                        .content(request.getSystemPrompt())
                        .build());
            }

            // 添加用户提示词
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(request.getPrompt())
                    .build());

            // 构建请求
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model(request.getModel())
                    .messages(messages)
                    .temperature(request.getTemperature())
                    .maxTokens(request.getMaxTokens())
                    .topP(0.7)
                    .stream(false)
                    .build();

            // 打印请求信息以便调试
            System.out.println("Sending request to API: " + chatRequest);

            // 发送请求并获取响应
            ChatCompletionResult response = arkService.createChatCompletion(chatRequest);
            System.out.println("Raw API Response: " + response);

            if (response != null && !response.getChoices().isEmpty()) {
                ChatCompletionChoice choice = response.getChoices().get(0);
                String generatedText = String.valueOf(choice.getMessage().getContent());

                // 记录日志
                Map<String, Object> extraData = new HashMap<>();
                extraData.put("category", request.getCategory());
                extraData.put("tags", request.getTags());
                extraData.put("model", request.getModel());
                extraData.put("finish_reason", choice.getFinishReason());

                logService.recordLog(
                        "AI_GENERATE",
                        "AIRequest",
                        null,
                        "AI内容生成：" + request.getCategory(),
                        userId,
                        String.valueOf(extraData),
                        null
                );

                // 将AI生成的内容保存到数据库
                ContentDto contentDto = new ContentDto();
                contentDto.setTitle(request.getPrompt());
                contentDto.setContent(generatedText);
                contentDto.setCategory(request.getCategory());
                contentDto.setTags(request.getTags());
                contentDto.setIsDraft(true); // 设置为草稿状态

                // 保存内容
                Content savedContent = contentService.createContent(contentDto, userId);

                return new AIResponseDto(generatedText, request.getModel(), Math.toIntExact(savedContent.getId()));
            }

            return new AIResponseDto("AI响应解析失败");

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            System.out.println("Exception in generateContent: " + errorMessage);
            e.printStackTrace();

            logService.recordLog(
                    "ERROR",
                    "AIRequest",
                    null,
                    "AI内容生成失败: " + errorMessage,
                    userId,
                    errorMessage,
                    null
            );

            return new AIResponseDto("AI请求失败：" + errorMessage);
        }
    }

    /**
     * 生成热点趋势分析
     */
    public AIResponseDto generateTrendAnalysis(String topic, Long userId, User.Role userRole) {
        if (!rolePermissionService.hasPermission(userRole, Permission.AI_TREND)) {
            return new AIResponseDto("用户无权使用趋势分析功能");
        }

        AIRequestDto request = new AIRequestDto();
        // 设置提示词
        request.setPrompt(String.format(
                "请对热门话题 '%s' 进行深度分析，包括：\n" +
                        "1. 话题背景和当前发展状况\n" +
                        "2. 网络热度趋势分析\n" +
                        "3. 主要讨论观点汇总\n" +
                        "4. 未来发展预测\n\n" +
                        "要求：\n" +
                        "- 分析要客观专业\n" +
                        "- 数据支持论点\n" +
                        "- 分点清晰展示\n" +
                        "- 结构完整规范",
                topic
        ));

        // 确保设置所有必需的字段
        request.setModel("doubao-1-5-pro-32k-250115");
        request.setTemperature(0.7);
        request.setMaxTokens(2000);
        request.setCategory("趋势分析");
        request.setSystemPrompt("你是一个专业的趋势分析师，请用客观的数据和专业的视角进行分析。");

        // 打印请求信息以便调试
        System.out.println("Trend analysis request: " + request);

        return generateContent(request, userId, userRole);
    }
}
