package com.example.approval.repository;

import com.example.approval.model.Content;
import com.example.approval.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByStatus(Content.Status status);
    List<Content> findByCreatedByAndStatus(Long createdBy, Content.Status status);

    List<Content> findByTitleContainingIgnoreCase(String keyword);
}