package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "content") // 确保表名与 schema.sql 一致
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;
    
    @Lob
    @Column(nullable = false)
    private String content;
    
    @Lob // 摘要也可能很长
    private String summary;
    
    @Column(length = 255) // 标签，存储逗号分隔的字符串
    private String tags;

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

    @Column(name = "published_at") // 添加发布时间字段
    private LocalDateTime publishedAt;

    @Column(length = 50) // 允许 category 为空，或设置默认值
    private String category; // 改为 String 类型，与前端传递的值对应

    @Column(length = 50) // 允许 type 为空，或设置默认值
    private String type;     // 改为 String 类型，暂时保留，可用于区分文章类型等

    public enum Status {
        DRAFT, PENDING, APPROVED, REJECTED, PUBLISHED, ARCHIVED
    }

    public Content() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.DRAFT;
    }

    // --- Getter 和 Setter ---

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    // 提供一个方便获取 List<String> 标签的方法
    @Transient // 不映射到数据库列
    public List<String> getTagList() {
        if (this.tags == null || this.tags.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(this.tags.split(","));
    }

    // 提供一个方便设置 List<String> 标签的方法
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = tagList.stream().map(String::trim).collect(Collectors.joining(","));
        }
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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
