package com.example.approval.dto;

public class ContentDto {
    private String title;
    private String content;
    // 可扩展字段，例如附件路径、文档类型等

    // Getter 和 Setter
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
}
