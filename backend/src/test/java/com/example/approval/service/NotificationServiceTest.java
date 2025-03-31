package com.example.approval.service;

import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private ApprovalRecord testRecord;
    private Content testContent;
    private User testUser;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));

        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // 创建测试内容
        testContent = new Content();
        testContent.setId(1L);
        testContent.setTitle("测试内容");
        testContent.setCreatedBy(1L);

        // 创建测试审批记录
        testRecord = new ApprovalRecord();
        testRecord.setId(1L);
        testRecord.setApproverId(1L);
    }

    @Test
    void sendApprovalTaskNotification_shouldSendNotification() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        notificationService.sendApprovalTaskNotification(testRecord, testContent);

        String output = outContent.toString();
        assertTrue(output.contains("向 testuser 发送审批任务通知"));
        assertTrue(output.contains("测试内容"));
    }

    @Test
    void sendApprovalResultNotification_shouldSendNotification() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        notificationService.sendApprovalResultNotification(testContent, true);

        String output = outContent.toString();
        assertTrue(output.contains("向 testuser 发送审批结果通知"));
        assertTrue(output.contains("测试内容"));
        assertTrue(output.contains("通过"));
    }

    @Test
    void sendApprovalResultNotification_shouldSendRejectionNotification() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        notificationService.sendApprovalResultNotification(testContent, false);

        String output = outContent.toString();
        assertTrue(output.contains("向 testuser 发送审批结果通知"));
        assertTrue(output.contains("测试内容"));
        assertTrue(output.contains("拒绝"));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
} 