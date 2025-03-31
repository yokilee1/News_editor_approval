package com.example.approval.dto;

import javax.validation.constraints.NotNull;

public class ApprovalDto {
    @NotNull
    private Long contentId;
    
    private boolean approved;
    
    private String comment;
    
    // Getters and Setters
    public Long getContentId() {
        return contentId;
    }
    
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}
