package com.example.approval.repository;

import com.example.approval.model.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Long> {
    List<ApprovalFlow> findByIsActiveTrue();
    Optional<ApprovalFlow> findByIsDefaultTrue();
    List<ApprovalFlow> findByCategory(String category);
    List<ApprovalFlow> findByFlowNameContainingIgnoreCase(String keyword);
}