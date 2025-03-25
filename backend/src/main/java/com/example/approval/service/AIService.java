package com.example.approval.service;

import com.example.approval.dto.AIRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    // 示例 URL，请根据实际接口地址替换
    private static final String AI_MODEL_URL = "https://api.example.com/v3/chat";
    private static final String HOT_NEWS_URL = "https://newsapi.example.com/hot";

    /**
     * 调用外部 AI 模型生成文章内容，基于关键词
     */
    public String generateContent(String keywords) {
        RestTemplate restTemplate = new RestTemplate();
        // 构造请求对象
        AIRequestDto requestDto = new AIRequestDto();
        requestDto.setKeywords(keywords);
        // 发送 POST 请求，返回生成的内容
        String response = restTemplate.postForObject(AI_MODEL_URL, requestDto, String.class);
        return response;
    }

    /**
     * 调用外部新闻接口获取热点新闻
     */
    public String getHotNews() {
        RestTemplate restTemplate = new RestTemplate();
        String news = restTemplate.getForObject(HOT_NEWS_URL, String.class);
        return news;
    }
}
