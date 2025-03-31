package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_node")
public class ApprovalNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "flow_id", nullable = false)
    private Long flowId;  // 所属审批流程ID

    @Column(name = "next_node")
    private Long nextNode;  // 下一审批节点ID（可为空）

    @Column(nullable = false)
    private Long approver;  // 当前节点审批人ID

    @Column(name = "approver_role")
    private String approverRole;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "is_countersign")
    private boolean countersign = false;

    @Column(name = "description")
    private String description;

    @Column(name = "allow_reject")
    private boolean allowReject = true;

    @Column(name = "reject_strategy")
    private String rejectStrategy = "TO_PREVIOUS";

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
 
    public String getNodeName() {
        return nodeName;
    }
 
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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
 
    public String getApproverRole() {
        return approverRole;
    }
 
    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }
 
    public Integer getOrderNum() {
        return orderNum;
    }
 
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
 
    public boolean isCountersign() {
        return countersign;
    }
 
    public void setCountersign(boolean countersign) {
        this.countersign = countersign;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public boolean isAllowReject() {
        return allowReject;
    }
 
    public void setAllowReject(boolean allowReject) {
        this.allowReject = allowReject;
    }
 
    public String getRejectStrategy() {
        return rejectStrategy;
    }
 
    public void setRejectStrategy(String rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
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
