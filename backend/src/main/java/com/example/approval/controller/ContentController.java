package com.example.approval.controller;

import com.example.approval.dto.ContentDto;
import com.example.approval.model.Content;
import com.example.approval.model.Permission;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.ContentService;
import com.example.approval.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 稿件管理接口
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;
    
    @Autowired
    private LogService logService;
    /**
     * 创建稿件（默认保存为草稿）
     * 需要CONTENT_CREATE权限
     */
    @PostMapping("/create")
    @RequirePermission(Permission.CONTENT_CREATE)
    public ResponseEntity<?> createContent(@RequestBody ContentDto contentDto,
                                           @RequestAttribute("userId") Long userId,
                                           HttpServletRequest request) {
        Content content = contentService.createContent(contentDto, userId);
        
        // 记录操作日志
        logService.recordLog(
            "CREATE", 
            "Content", 
            content.getId().toString(), 
            "创建稿件：" + content.getTitle(), 
            userId, 
            null, // 此处可以通过用户服务获取用户名
            request.getRemoteAddr()
        );
        
        return ResponseEntity.ok(content);
    }

    /**
     * 更新稿件（仅允许草稿状态稿件修改）
     * 需要CONTENT_EDIT权限
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.CONTENT_EDIT)
    public Map<String, Object> updateContent(@PathVariable Long id,
                                         @RequestBody ContentDto contentDto,
                                         @RequestAttribute("userId") Long userId,
                                         HttpServletRequest request) {
        Content content = contentService.updateContent(id, contentDto, userId);
        
        // 记录操作日志
        logService.recordLog(
            "UPDATE", 
            "Content", 
            id.toString(),
            "内容更新",
            userId,
            null, // 此处可以通过用户服务获取用户名
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "内容已更新");
        response.put("content", content);
        return response;
    }
    
    /**
     * 获取用户的稿件列表
     * 需要CONTENT_VIEW权限
     */
    @GetMapping("/list")
    @RequirePermission(Permission.CONTENT_VIEW)
    public ResponseEntity<?> getContentList(@RequestAttribute("userId") Long userId) {
        List<Content> contents = contentService.getContentsByUser(userId);
        return ResponseEntity.ok(contents);
    }
    
    /**
     * 获取稿件详情
     * 需要CONTENT_VIEW权限
     */
    @GetMapping("/{id}")
    @RequirePermission(Permission.CONTENT_VIEW)
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        Content content = contentService.getContentById(id);
        return ResponseEntity.ok(content);
    }
}
