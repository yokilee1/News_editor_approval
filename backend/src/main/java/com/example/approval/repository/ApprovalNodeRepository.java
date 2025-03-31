package com.example.approval.repository;

import com.example.approval.model.ApprovalNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalNodeRepository extends JpaRepository<ApprovalNode, Long> {
    List<ApprovalNode> findByFlowId(Long flowId);
    List<ApprovalNode> findByApprover(Long approverId);
} 