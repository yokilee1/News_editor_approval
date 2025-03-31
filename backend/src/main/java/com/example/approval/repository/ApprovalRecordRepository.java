package com.example.approval.repository;

import com.example.approval.model.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {
    List<ApprovalRecord> findByTaskTypeAndTaskId(String taskType, String taskId);
    List<ApprovalRecord> findByApproverId(Long approverId);
    List<ApprovalRecord> findByApproverIdAndStatus(Long approverId, ApprovalRecord.Status status);
    List<ApprovalRecord> findByApproverIdAndStatusNot(Long approverId, ApprovalRecord.Status status);
    Long countByApproverIdAndStatus(Long approverId, ApprovalRecord.Status status);
    Optional<ApprovalRecord> findByTaskTypeAndTaskIdAndApproverId(String taskType, String taskId, Long approverId);
}
