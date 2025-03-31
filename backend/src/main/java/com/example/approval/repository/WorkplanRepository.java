package com.example.approval.repository;

import com.example.approval.model.WorkPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkplanRepository extends JpaRepository<WorkPlan, Long> {

    WorkPlan findByTitle(String title);

    List<WorkPlan> findByCreatedBy(Long userId);

    List<WorkPlan> findByDocumentId(Long documentId);
}
