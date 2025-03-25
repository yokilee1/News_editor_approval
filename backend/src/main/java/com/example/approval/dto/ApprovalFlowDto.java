package com.example.approval.dto;

public class ApprovalFlowDto {
    private String title;  // 流程标题
    private String name;   // 流程名称或描述
    private Integer level; // 流程层级或审批顺序

    // Getter 和 Setter
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
}
