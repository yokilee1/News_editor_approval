package com.example.approval.repository;

import com.example.approval.model.Workplan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkplanRepository extends JpaRepository<Workplan, Long> {

    List<Workplan> findByCreatedBy(Long userId);

    List<Workplan> findByDocumentId(Long documentId);
}
