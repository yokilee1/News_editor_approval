package com.example.approval.repository;

import com.example.approval.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);
    
    Page<OperationLog> findByOperationAndCreatedAtBetween(
            String operation, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<OperationLog> findByTargetTypeAndTargetId(String targetType, String targetId);
} 