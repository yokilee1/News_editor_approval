package com.example.approval.model;

public enum Permission {
    // 内容管理权限
    CONTENT_CREATE,      // 创建内容
    CONTENT_EDIT,        // 编辑内容
    CONTENT_VIEW,        // 查看内容
    CONTENT_DELETE,      // 删除内容
    
    // 审批流程权限
    APPROVAL_SUBMIT,     // 提交审批
    APPROVAL_REVIEW,     // 审批操作
    APPROVAL_VIEW,       // 查看审批
    APPROVAL_MANAGE,     // 管理审批流程
    
    // 用户管理权限
    USER_CREATE,         // 创建用户
    USER_EDIT,           // 编辑用户
    USER_VIEW,           // 查看用户
    USER_DELETE,         // 删除用户
    
    // 系统管理权限
    SYSTEM_CONFIG,       // 系统配置
    LOG_VIEW,            // 查看日志
    
    // AI工具权限
    AI_GENERATE,         // 使用AI生成内容
    AI_TREND             // 查看热点趋势
} 