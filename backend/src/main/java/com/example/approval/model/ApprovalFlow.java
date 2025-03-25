package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_flows")
public class ApprovalFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;   // 流程标题

    @Column(name = "flow_name", length = 100)
    private String flowName;    // 流程名称

    @Column(length = 100)
    private String name;    // 流程描述

    private Integer level;  // 流程层级或审批顺序

    @Column(name = "is_active")
    private Boolean isActive = true;  // 流程是否激活

    @Column(name = "is_default")
    private Boolean isDefault = false;  // 是否为默认流程

    @Column(length = 50)
    private String category;  // 流程分类

    @Column(name = "created_by")
    private Long createdBy; // 流程创建者

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ApprovalFlow() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public Integer getLevel() {
        return level;
    }
 
    public void setLevel(Integer level) {
        this.level = level;
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

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
