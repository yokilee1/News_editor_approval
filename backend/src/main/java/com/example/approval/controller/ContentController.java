package com.example.approval.controller;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 稿件管理接口
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 创建稿件（默认保存为草稿）
     * 假设通过请求属性 "userId" 获取当前登录用户ID
     */
    @PostMapping("/create")
    public ResponseEntity<?> createContent(@RequestBody ContentDto contentDto,
                                           @RequestAttribute("userId") Long userId) {
        Content content = contentService.createContent(contentDto, userId);
        return ResponseEntity.ok(content);
    }

    /**
     * 更新稿件（仅允许草稿状态稿件修改）
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateContent(@PathVariable Long id,
                                           @RequestBody ContentDto contentDto) {
        Content content = contentService.updateContent(id, contentDto);
        return ResponseEntity.ok(content);
    }

    // 可以增加 GET 接口用于获取稿件详情、列表等
}
