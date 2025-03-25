package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_node")
public class ApprovalNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flow_id", nullable = false)
    private Long flowId;  // 所属审批流程ID

    @Column(name = "next_node")
    private Long nextNode;  // 下一审批节点ID（可为空）

    @Column(nullable = false)
    private Long approver;  // 当前节点审批人ID

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ApprovalNode() {
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
 
    public Long getFlowId() {
        return flowId;
    }
 
    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
 
    public Long getNextNode() {
        return nextNode;
    }
 
    public void setNextNode(Long nextNode) {
        this.nextNode = nextNode;
    }
 
    public Long getApprover() {
        return approver;
    }
 
    public void setApprover(Long approver) {
        this.approver = approver;
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
