package com.example.approval.service;

import com.example.approval.dto.ApprovalDto;
import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.repository.ApprovalRecordRepository;
import com.example.approval.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ApprovalService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * 提交稿件至审批流程，更新稿件状态为 PENDING
     */
    public Content submitForApproval(Long contentId, Long userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));

        // 验证提交者是否为稿件创建者
        if (!content.getCreatedBy().equals(userId)) {
            throw new RuntimeException("只有稿件创建者才能提交审批");
        }

        // 只有草稿状态的稿件才能提交审批
        if (content.getStatus() != Content.Status.DRAFT) {
            throw new RuntimeException("只有草稿状态的稿件才能提交审批");
        }

        content.setStatus(Content.Status.PENDING);
        content.setSubmittedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());

        // 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setTaskType("Content");
        record.setTaskId(contentId.toString());
        record.setApproverId(3L);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        approvalRecordRepository.save(record);

        // 添加通知
        notificationService.sendApprovalTaskNotification(record, content);

        return contentRepository.save(content);
    }

    /**
     * 审批操作：审批人执行审批，并记录审批意见，同时更新稿件状态
     */
    public Content reviewApproval(Long contentId, ApprovalDto approvalDto, Long userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));

        // 只有待审批状态的稿件才能进行审批操作
        if (content.getStatus() != Content.Status.PENDING) {
            throw new RuntimeException("只有待审批状态的稿件才能进行审批操作");
        }

        // 记录审批意见
        ApprovalRecord record = new ApprovalRecord();
        record.setTaskType("Content");
        record.setTaskId(contentId.toString());
        record.setApproverId(userId);
        record.setComment(approvalDto.getComment());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        approvalRecordRepository.save(record);

        // 更新稿件状态
        if (approvalDto.isApproved()) {
            content.setStatus(Content.Status.APPROVED);
            content.setApprovedAt(LocalDateTime.now());
        } else {
            content.setStatus(Content.Status.REJECTED);
            content.setRejectedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());

        // 添加通知
        notificationService.sendApprovalTaskNotification(record, content);

        return contentRepository.save(content);
    }

    /**
     * 获取审批统计数据
     */
    public Map<String, Object> getApprovalStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计待审批稿件数量
        long pendingCount = contentRepository.countByStatus(Content.Status.PENDING);
        stats.put("pending", pendingCount);
        
        // 统计已通过稿件数量
        long approvedCount = contentRepository.countByStatus(Content.Status.APPROVED);
        stats.put("approved", approvedCount);
        
        // 统计已驳回稿件数量
        long rejectedCount = contentRepository.countByStatus(Content.Status.REJECTED);
        stats.put("rejected", rejectedCount);
        
        // 统计草稿箱数量
        long draftCount = contentRepository.countByStatusAndCreatedBy(Content.Status.DRAFT, userId);
        stats.put("draft", draftCount);
        
        return stats;
    }

    /**
     * 获取稿件的审批记录
     */
    public List<ApprovalRecord> getApprovalStatus(Long contentId) {
        // 校验稿件是否存在
        contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));

        // 获取稿件的审批记录
        return approvalRecordRepository.findByTaskTypeAndTaskId("Content", contentId.toString());
    }

    /**
     * 根据内容ID获取当前审批人
     */
    public Long getCurrentApprover(Long contentId) {
        List<ApprovalRecord> records = approvalRecordRepository.findByTaskTypeAndTaskId("Content", contentId.toString());
        
        if (records.isEmpty()) {
            return null;  // 未开始审批
        }
        
        // 查找状态为PENDING的记录
        for (ApprovalRecord record : records) {
            if (record.getStatus() == ApprovalRecord.Status.PENDING) {
                return record.getApproverId();
            }
        }
        
        return null;  // 没有待审批的记录，可能已完成或已拒绝
    }
    
    /**
     * 获取内容的审批流程进度
     */
    public Map<String, Object> getApprovalProgress(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在"));
        
        List<ApprovalRecord> records = approvalRecordRepository.findByTaskTypeAndTaskId("Content", contentId.toString());
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("contentId", contentId);
        progress.put("status", content.getStatus());
        progress.put("records", records);
        
        // 计算总节点数和已完成节点数
        long totalNodes = records.size();
        long completedNodes = records.stream()
                .filter(r -> r.getStatus() != ApprovalRecord.Status.PENDING)
                .count();
        
        progress.put("totalNodes", totalNodes);
        progress.put("completedNodes", completedNodes);
        progress.put("progress", totalNodes > 0 ? (completedNodes * 100 / totalNodes) : 0);
        
        return progress;
    }
    
    /**
     * 获取用户待审批的任务
     */
    public List<Map<String, Object>> getUserPendingTasks(Long userId) {
        List<ApprovalRecord> pendingRecords = approvalRecordRepository.findByApproverIdAndStatus(
                userId, ApprovalRecord.Status.PENDING);
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        for (ApprovalRecord record : pendingRecords) {
            if ("Content".equals(record.getTaskType())) {
                Long contentId = Long.parseLong(record.getTaskId());
                Content content = contentRepository.findById(contentId).orElse(null);
                
                if (content != null) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("recordId", record.getId());
                    task.put("contentId", contentId);
                    task.put("title", content.getTitle());
                    task.put("submitTime", record.getCreatedAt());
                    tasks.add(task);
                }
            }
        }
        
        return tasks;
    }
    
    /**
     * 获取用户已审批的历史任务
     */
    public List<Map<String, Object>> getUserHistoryTasks(Long userId) {
        List<ApprovalRecord> historyRecords = approvalRecordRepository.findByApproverIdAndStatusNot(
                userId, ApprovalRecord.Status.PENDING);
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        for (ApprovalRecord record : historyRecords) {
            if ("Content".equals(record.getTaskType())) {
                Long contentId = Long.parseLong(record.getTaskId());
                Content content = contentRepository.findById(contentId).orElse(null);
                
                if (content != null) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("recordId", record.getId());
                    task.put("contentId", contentId);
                    task.put("title", content.getTitle());
                    task.put("approvalTime", record.getUpdatedAt());
                    task.put("status", record.getStatus());
                    task.put("comment", record.getComment());
                    tasks.add(task);
                }
            }
        }
        
        return tasks;
    }
    
    /**
     * 撤回提交的审批
     */
    public Content withdrawApproval(Long contentId, Long userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在"));
        
        // 验证是否是内容创建者
        if (!content.getCreatedBy().equals(userId)) {
            throw new RuntimeException("只有内容创建者才能撤回审批");
        }
        
        // 验证内容状态是否为待审批
        if (content.getStatus() != Content.Status.PENDING) {
            throw new RuntimeException("只有待审批状态的内容才能撤回");
        }
        
        // 删除所有审批记录
        List<ApprovalRecord> records = approvalRecordRepository.findByTaskTypeAndTaskId("Content", contentId.toString());
        approvalRecordRepository.deleteAll(records);
        
        // 将内容状态改为草稿
        content.setStatus(Content.Status.DRAFT);
        return contentRepository.save(content);
    }
    
    /**
     * 查询待我审批的内容数量
     */
    public Long countPendingApprovals(Long userId) {
        return approvalRecordRepository.countByApproverIdAndStatus(userId, ApprovalRecord.Status.PENDING);
    }

    /**
     * 处理审批请求
     */
    public Content processApproval(ApprovalDto approvalDto, Long userId) {
        Content content = contentRepository.findById(approvalDto.getContentId())
                .orElseThrow(() -> new RuntimeException("内容不存在"));
        
        // 验证审批人权限
        ApprovalRecord record = approvalRecordRepository.findByTaskTypeAndTaskIdAndApproverId(
                "Content", approvalDto.getContentId().toString(), userId)
                .orElseThrow(() -> new RuntimeException("没有审批权限"));
        
        // 更新审批记录
        record.setStatus(approvalDto.isApproved() ? ApprovalRecord.Status.APPROVED : ApprovalRecord.Status.REJECTED);
        record.setComment(approvalDto.getComment());
        record.setUpdatedAt(LocalDateTime.now());
        approvalRecordRepository.save(record);
        
        // 更新内容状态
        if (approvalDto.isApproved()) {
            content.setStatus(Content.Status.APPROVED);
        } else {
            content.setStatus(Content.Status.REJECTED);
        }
        content.setUpdatedAt(LocalDateTime.now());
        
        // 发送通知
        notificationService.sendApprovalResultNotification(content, approvalDto.isApproved());
        
        return contentRepository.save(content);
    }
}
