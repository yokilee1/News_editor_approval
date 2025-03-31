package com.example.approval.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ApprovalNodeDto {
    
    private Long id;
    
    @NotNull(message = "节点名称不能为空")
    private String nodeName;
    
    @NotNull(message = "审批人不能为空")
    private Long approver;
    
    private String approverRole;
    
    @Min(value = 1, message = "节点序号最小为1")
    private Integer orderNum;
    
    private boolean isCountersign = false;
    
    private String description;
    
    private Long flowId;
    
    // 是否允许退回
    private boolean allowReject = true;
    
    // 退回策略：TO_PREVIOUS(退回上一节点), TO_START(退回发起人)
    private String rejectStrategy = "TO_PREVIOUS";

    // Getters and Setters
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
        return isCountersign;
    }

    public void setCountersign(boolean countersign) {
        isCountersign = countersign;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
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
} 