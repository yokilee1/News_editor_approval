package com.example.approval.controller;

import com.example.approval.dto.ApprovalDto;
import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 审批管理接口
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    /**
     * 提交稿件至审批流程
     */
    @PostMapping("/submit/{contentId}")
    public ResponseEntity<?> submitForApproval(@PathVariable Long contentId,
                                               @RequestAttribute("userId") Long userId) {
        Content content = approvalService.submitForApproval(contentId, userId);
        return ResponseEntity.ok(content);
    }

    /**
     * 审批人执行审批操作，传入审批意见及结果
     */
    @PostMapping("/review/{contentId}")
    public ResponseEntity<?> reviewApproval(@PathVariable Long contentId,
                                              @RequestBody ApprovalDto approvalDto) {
        Content content = approvalService.reviewApproval(contentId, approvalDto);
        return ResponseEntity.ok(content);
    }

    /**
     * 获取稿件审批状态与审批历史记录
     */
    @GetMapping("/status/{contentId}")
    public ResponseEntity<?> getApprovalStatus(@PathVariable Long contentId) {
        List<ApprovalRecord> records = approvalService.getApprovalStatus(contentId);
        return ResponseEntity.ok(records);
    }
}
