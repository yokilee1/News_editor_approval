package com.example.approval.controller;

import com.example.approval.dto.ApprovalDto;
import com.example.approval.model.Content;
import com.example.approval.model.Permission;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.ApprovalService;
import com.example.approval.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批处理接口
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;
    
    @Autowired
    private LogService logService;
    
    /**
     * 提交内容审批
     */
    @PostMapping("/submit/{contentId}")
    @RequirePermission(Permission.APPROVAL_SUBMIT)
    public ResponseEntity<?> submitForApproval(
            @PathVariable Long contentId,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        Content content = approvalService.submitForApproval(contentId, userId);
        
        // 记录操作日志
        logService.recordLog(
            "SUBMIT", 
            "Content", 
            contentId.toString(), 
            "提交内容审批：" + content.getTitle(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("content", content);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 审批操作（通过或拒绝）
     */
    @PostMapping("/process")
    @RequirePermission(Permission.APPROVAL_REVIEW)
    public ResponseEntity<?> processApproval(
            @RequestBody ApprovalDto approvalDto,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        Content content = approvalService.processApproval(approvalDto, userId);
        
        // 记录操作日志
        String action = approvalDto.isApproved() ? "APPROVE" : "REJECT";
        logService.recordLog(
            action, 
            "Content", 
            approvalDto.getContentId().toString(), 
            (approvalDto.isApproved() ? "审批通过：" : "审批拒绝：") + content.getTitle(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("content", content);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取审批状态
     */
    @GetMapping("/status/{contentId}")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getApprovalStatus(@PathVariable Long contentId) {
        return ResponseEntity.ok(approvalService.getApprovalStatus(contentId));
    }
    
    /**
     * 获取审批进度
     */
    @GetMapping("/progress/{contentId}")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getApprovalProgress(@PathVariable Long contentId) {
        return ResponseEntity.ok(approvalService.getApprovalProgress(contentId));
    }
    
    /**
     * 撤回审批
     */
    @PostMapping("/withdraw/{contentId}")
    @RequirePermission(Permission.APPROVAL_SUBMIT)
    public ResponseEntity<?> withdrawApproval(
            @PathVariable Long contentId,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        Content content = approvalService.withdrawApproval(contentId, userId);
        
        // 记录操作日志
        logService.recordLog(
            "WITHDRAW", 
            "Content", 
            contentId.toString(), 
            "撤回审批：" + content.getTitle(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("content", content);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户待审批任务
     */
    @GetMapping("/pending-tasks")
    @RequirePermission(Permission.APPROVAL_REVIEW)
    public ResponseEntity<?> getPendingTasks(@RequestAttribute("userId") Long userId) {
        List<Map<String, Object>> tasks = approvalService.getUserPendingTasks(userId);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * 获取用户历史审批任务
     */
    @GetMapping("/history-tasks")
    @RequirePermission(Permission.APPROVAL_REVIEW)
    public ResponseEntity<?> getHistoryTasks(@RequestAttribute("userId") Long userId) {
        List<Map<String, Object>> tasks = approvalService.getUserHistoryTasks(userId);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * 获取待审批任务数量
     */
    @GetMapping("/count-pending")
    @RequirePermission(Permission.APPROVAL_REVIEW)
    public ResponseEntity<?> countPendingApprovals(@RequestAttribute("userId") Long userId) {
        Long count = approvalService.countPendingApprovals(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
