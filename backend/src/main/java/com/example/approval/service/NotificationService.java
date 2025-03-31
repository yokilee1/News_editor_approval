package com.example.approval.service;

import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 发送审批任务通知
     */
    public void sendApprovalTaskNotification(ApprovalRecord record, Content content) {
        User approver = userRepository.findById(record.getApproverId())
                .orElse(null);
        
        if (approver != null && approver.getEmail() != null) {
            // 这里可以实现发送邮件通知
            String subject = "新的审批任务: " + content.getTitle();
            String message = "您有一个新的审批任务需要处理。\n" +
                    "内容标题: " + content.getTitle() + "\n" +
                    "请登录系统进行审批。";
            
            // 调用邮件发送服务
            // emailService.sendEmail(approver.getEmail(), subject, message);
            
            // 打印日志替代实际发送
            System.out.println("向 " + approver.getUsername() + " 发送审批任务通知: " + subject + "\n内容: " + message);
        }
    }
    
    /**
     * 发送审批结果通知
     */
    public void sendApprovalResultNotification(Content content, boolean approved) {
        User creator = userRepository.findById(content.getCreatedBy())
                .orElse(null);
        
        if (creator != null && creator.getEmail() != null) {
            String status = approved ? "通过" : "拒绝";
            String subject = "审批结果通知: " + content.getTitle();
            String message = "您提交的内容 \"" + content.getTitle() + "\" 已审批" + status + "。\n" +
                    "请登录系统查看详情。";
            
            // 调用邮件发送服务
            // emailService.sendEmail(creator.getEmail(), subject, message);
            
            // 打印日志替代实际发送
            System.out.println("向 " + creator.getUsername() + " 发送审批结果通知: " + subject + "\n内容: " + message);
        }
    }
} 