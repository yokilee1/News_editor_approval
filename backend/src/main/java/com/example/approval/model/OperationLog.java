package com.example.approval.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_logs")
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String operation;  // 操作类型
    
    @Column(name = "target_type")
    private String targetType;  // 操作对象类型
    
    @Column(name = "target_id")
    private String targetId;    // 操作对象ID
    
    @Column(nullable = false)
    private String description;  // 操作描述
    
    @Column(name = "user_id")
    private Long userId;         // 操作用户ID
    
    @Column(name = "user_name")
    private String userName;     // 操作用户名称
    
    @Column(name = "ip_address")
    private String ipAddress;    // 操作IP地址
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public OperationLog() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 构造器
    public OperationLog(String operation, String targetType, String targetId, 
                         String description, Long userId, String userName, String ipAddress) {
        this.operation = operation;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 