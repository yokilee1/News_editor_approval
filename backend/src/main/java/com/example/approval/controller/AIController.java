package com.example.approval.controller;

import com.example.approval.dto.AIRequestDto;
import com.example.approval.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI 辅助写作与热点推荐接口
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 调用 AI 模型生成文章内容
     */
    @PostMapping("/generate-content")
    public ResponseEntity<?> generateContent(@RequestBody AIRequestDto aiRequestDto) {
        String content = aiService.generateContent(aiRequestDto.getKeywords());
        return ResponseEntity.ok(content);
    }

    /**
     * 获取热点新闻推荐
     */
    @GetMapping("/hot-news")
    public ResponseEntity<?> getHotNews() {
        String news = aiService.getHotNews();
        return ResponseEntity.ok(news);
    }
}
