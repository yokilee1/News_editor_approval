package com.example.approval.repository;

import com.example.approval.model.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByTaskId(String taskId);
}
