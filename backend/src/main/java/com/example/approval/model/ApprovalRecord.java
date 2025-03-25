package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_record")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 对应审批节点ID，可与 ApprovalNode 关联
    @Column(name = "node_id")
    private Long nodeId;

    @Column(name = "task_type", length = 255)
    private String taskType;  // 任务类型，例如 Content

    @Column(name = "task_id", length = 50)
    private String taskId;    // 稿件ID或工作计划ID

    @Column(name = "approver_id")
    private Long approverId;  // 审批人ID

    @Lob
    private String comment;   // 审批意见

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ApprovalRecord() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getNodeId() {
        return nodeId;
    }
 
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }
 
    public String getTaskType() {
        return taskType;
    }
 
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
 
    public String getTaskId() {
        return taskId;
    }
 
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
 
    public Long getApproverId() {
        return approverId;
    }
 
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
 
    public String getComment() {
        return comment;
    }
 
    public void setComment(String comment) {
        this.comment = comment;
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
}
