package com.example.approval.service;

import com.example.approval.model.OperationLog;
import com.example.approval.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@Service
public class LogService {
    
    @Autowired
    private OperationLogRepository logRepository;
    
    /**
     * 记录操作日志
     */
    public OperationLog recordLog(String operation, String targetType, String targetId,
                                  String description, Long userId, String userName, String ipAddress) {
        OperationLog log = new OperationLog(operation, targetType, targetId, 
                                           description, userId, userName, ipAddress);
        return logRepository.save(log);
    }
    
    /**
     * 查询用户操作日志
     */
    public Page<OperationLog> getUserLogs(Long userId, Pageable pageable) {
        return logRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 查询特定类型和时间范围的操作日志
     */
    public Page<OperationLog> getLogsByTypeAndTimeRange(
            String operation, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return logRepository.findByOperationAndCreatedAtBetween(operation, start, end, pageable);
    }

    // 添加重载方法，自动从request中提取IP地址
    public void recordLog(String action, String entityType, String entityId, 
                         String details, Long userId, HttpServletRequest request) {
        recordLog(action, entityType, entityId, details, userId, null, 
                  request != null ? request.getRemoteAddr() : "unknown");
    }
} 