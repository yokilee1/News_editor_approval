package com.example.approval.service;

import com.example.approval.dto.ApprovalDto;
import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.repository.ApprovalRecordRepository;
import com.example.approval.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApprovalServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ApprovalRecordRepository approvalRecordRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApprovalService approvalService;

    private Content testContent;
    private ApprovalRecord testRecord;
    private ApprovalDto testApprovalDto;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试内容
        testContent = new Content();
        testContent.setId(1L);
        testContent.setTitle("测试内容");
        testContent.setCreatedBy(userId);
        testContent.setStatus(Content.Status.DRAFT);

        // 创建测试审批记录
        testRecord = new ApprovalRecord();
        testRecord.setId(1L);
        testRecord.setTaskType("Content");
        testRecord.setTaskId("1");
        testRecord.setApproverId(2L);
        testRecord.setStatus(ApprovalRecord.Status.PENDING);

        // 创建测试审批DTO
        testApprovalDto = new ApprovalDto();
        testApprovalDto.setContentId(1L);
        testApprovalDto.setApproved(true);
        testApprovalDto.setComment("测试通过");
    }

    @Test
    void submitForApproval_shouldChangeStatusAndCreateRecord() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);
        when(approvalRecordRepository.save(any(ApprovalRecord.class))).thenReturn(testRecord);
        doNothing().when(notificationService).sendApprovalTaskNotification(any(), any());

        Content submittedContent = approvalService.submitForApproval(1L, userId);

        assertNotNull(submittedContent);
        assertEquals(Content.Status.PENDING, submittedContent.getStatus());
        verify(approvalRecordRepository, times(1)).save(any(ApprovalRecord.class));
        verify(notificationService, times(1)).sendApprovalTaskNotification(any(), any());
    }

    @Test
    void processApproval_shouldUpdateRecordAndContent() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(approvalRecordRepository.findByTaskTypeAndTaskIdAndApproverId(anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(testRecord));
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);
        when(approvalRecordRepository.save(any(ApprovalRecord.class))).thenReturn(testRecord);
        doNothing().when(notificationService).sendApprovalResultNotification(any(), anyBoolean());

        Content processedContent = approvalService.processApproval(testApprovalDto, 2L);

        assertNotNull(processedContent);
        assertEquals(Content.Status.APPROVED, processedContent.getStatus());
        verify(approvalRecordRepository, times(1)).save(any(ApprovalRecord.class));
        verify(notificationService, times(1)).sendApprovalResultNotification(any(), anyBoolean());
    }

    @Test
    void getApprovalStatus_shouldReturnRecords() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(approvalRecordRepository.findByTaskTypeAndTaskId("Content", "1"))
                .thenReturn(Arrays.asList(testRecord));

        List<ApprovalRecord> records = approvalService.getApprovalStatus(1L);

        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(ApprovalRecord.Status.PENDING, records.get(0).getStatus());
    }

    @Test
    void getApprovalProgress_shouldReturnProgressInfo() {
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(approvalRecordRepository.findByTaskTypeAndTaskId("Content", "1"))
                .thenReturn(Arrays.asList(testRecord));

        Map<String, Object> progress = approvalService.getApprovalProgress(1L);

        assertNotNull(progress);
        assertEquals(1L, progress.get("contentId"));
        assertEquals(Content.Status.DRAFT, progress.get("status"));
        assertEquals(1L, progress.get("totalNodes"));
        assertEquals(0L, progress.get("completedNodes"));
        assertEquals(0L, progress.get("progress"));
    }

    @Test
    void withdrawApproval_shouldResetStatusAndDeleteRecords() {
        testContent.setStatus(Content.Status.PENDING);
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(approvalRecordRepository.findByTaskTypeAndTaskId("Content", "1"))
                .thenReturn(Arrays.asList(testRecord));
        doNothing().when(approvalRecordRepository).deleteAll(anyList());
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);

        Content withdrawnContent = approvalService.withdrawApproval(1L, userId);

        assertNotNull(withdrawnContent);
        assertEquals(Content.Status.DRAFT, withdrawnContent.getStatus());
        verify(approvalRecordRepository, times(1)).deleteAll(anyList());
    }
} 