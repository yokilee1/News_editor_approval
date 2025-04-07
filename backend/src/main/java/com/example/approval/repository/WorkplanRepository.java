package com.example.approval.repository;

import com.example.approval.model.Workplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkplanRepository extends JpaRepository<Workplan, Long> {

    Workplan findByTitle(String title);

    List<Workplan> findByCreatedBy(Long userId);

    List<Workplan> findByDocumentId(Long documentId);
}
