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

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    @Autowired
    private ContentRepository contentRepository;

    /**
     * 提交稿件至审批流程，更新稿件状态为 PENDING
     */
    public Content submitForApproval(Long contentId, Long userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));
        content.setStatus(Content.Status.PENDING);
        content.setUpdatedAt(LocalDateTime.now());
        // 设置提交时间
        content.setSubmittedAt(LocalDateTime.now());
        
        // 创建初始审批记录（记录提交信息，可根据实际流程调整）
        ApprovalRecord record = new ApprovalRecord();
        record.setTaskId(contentId.toString());
        record.setTaskType("Content");
        record.setApproverId(userId); // 此处作为初始记录，后续审批时由审批人更新记录
        record.setCreatedAt(LocalDateTime.now());
        approvalRecordRepository.save(record);
        
        return contentRepository.save(content);
    }

    /**
     * 审批操作：审批人执行审批，并记录审批意见，同时更新稿件状态
     */
    public Content reviewApproval(Long contentId, ApprovalDto approvalDto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("稿件不存在"));

        ApprovalRecord record = new ApprovalRecord();
        record.setTaskId(contentId.toString());
        record.setTaskType("Content");
        record.setApproverId(approvalDto.getApproverId());
        record.setComment(approvalDto.getComment());
        record.setCreatedAt(LocalDateTime.now());
        approvalRecordRepository.save(record);

        if (approvalDto.getApproved()) {
            content.setStatus(Content.Status.APPROVED);
            content.setApprovedAt(LocalDateTime.now());
        } else {
            content.setStatus(Content.Status.REJECTED);
            content.setRejectedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    /**
     * 获取稿件的审批记录
     */
    public List<ApprovalRecord> getApprovalStatus(Long contentId) {
        return approvalRecordRepository.findByTaskId(contentId.toString());
    }
}
