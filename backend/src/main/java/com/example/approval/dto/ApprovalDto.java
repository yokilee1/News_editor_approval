package com.example.approval.dto;

public class ApprovalDto {
    private Long approverId;
    private String comment;
    // 审批结果：true 表示同意；false 表示驳回
    private Boolean approved;

    // Getter 和 Setter
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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
