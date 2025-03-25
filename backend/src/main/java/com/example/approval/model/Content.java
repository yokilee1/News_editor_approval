package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;
    
    @Lob
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status; // 草稿、待审批、已发布、已存档、审批拒绝

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    public enum Status {
        DRAFT, PENDING, APPROVED, REJECTED, PUBLISHED, ARCHIVED
    }

    public Content() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.DRAFT;
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content;
    }
 
    public Status getStatus() {
        return status;
    }
 
    public void setStatus(Status status) {
        this.status = status;
    }
 
    public Long getCreatedBy() {
        return createdBy;
    }
 
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
 
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
 
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
 
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
 
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
 
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
 
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
 
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
 
    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }
 
    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }
}
