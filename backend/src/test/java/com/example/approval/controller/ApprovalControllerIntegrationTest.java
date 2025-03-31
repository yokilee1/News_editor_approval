package com.example.approval.controller;

import com.example.approval.config.TestConfig;
import com.example.approval.dto.ApprovalDto;
import com.example.approval.model.ApprovalRecord;
import com.example.approval.model.Content;
import com.example.approval.security.PermissionAspectTestConfig;
import com.example.approval.service.ApprovalService;
import com.example.approval.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestConfig.class, PermissionAspectTestConfig.class})
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class ApprovalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApprovalService approvalService;

    @MockBean
    private LogService logService;

    private Content testContent;
    private ApprovalDto testApprovalDto;
    private List<ApprovalRecord> testRecords;
    private Map<String, Object> testProgress;
    private List<Map<String, Object>> testTasks;

    @BeforeEach
    void setUp() {
        testContent = new Content();
        testContent.setId(1L);
        testContent.setTitle("测试内容");
        testContent.setStatus(Content.Status.PENDING);

        testApprovalDto = new ApprovalDto();
        testApprovalDto.setContentId(1L);
        testApprovalDto.setApproved(true);
        testApprovalDto.setComment("测试通过");

        ApprovalRecord record = new ApprovalRecord();
        record.setId(1L);
        record.setTaskType("Content");
        record.setTaskId("1");
        record.setStatus(ApprovalRecord.Status.PENDING);
        testRecords = Arrays.asList(record);

        testProgress = new HashMap<>();
        testProgress.put("contentId", 1L);
        testProgress.put("status", Content.Status.PENDING);
        testProgress.put("totalNodes", 2L);
        testProgress.put("completedNodes", 1L);
        testProgress.put("progress", 50L);

        Map<String, Object> task = new HashMap<>();
        task.put("contentId", 1L);
        task.put("title", "测试内容");
        testTasks = Arrays.asList(task);
    }

    @Test
    void submitForApproval_shouldReturnSubmittedContent() throws Exception {
        when(approvalService.submitForApproval(anyLong(), anyLong())).thenReturn(testContent);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/approval/submit/1")
                .requestAttr("userId", 1L)
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.title").value("测试内容"));
    }

    @Test
    void processApproval_shouldReturnProcessedContent() throws Exception {
        when(approvalService.processApproval(any(ApprovalDto.class), anyLong())).thenReturn(testContent);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/approval/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApprovalDto))
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.title").value("测试内容"));
    }

    @Test
    void getApprovalStatus_shouldReturnStatus() throws Exception {
        when(approvalService.getApprovalStatus(anyLong())).thenReturn(testRecords);

        mockMvc.perform(get("/api/approval/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskType").value("Content"));
    }

    @Test
    void getApprovalProgress_shouldReturnProgress() throws Exception {
        when(approvalService.getApprovalProgress(anyLong())).thenReturn(testProgress);

        mockMvc.perform(get("/api/approval/progress/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentId").value(1))
                .andExpect(jsonPath("$.progress").value(50));
    }

    @Test
    void withdrawApproval_shouldReturnWithdrawnContent() throws Exception {
        when(approvalService.withdrawApproval(anyLong(), anyLong())).thenReturn(testContent);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/approval/withdraw/1")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.content.title").value("测试内容"));
    }

    @Test
    void getPendingTasks_shouldReturnTasks() throws Exception {
        when(approvalService.getUserPendingTasks(anyLong())).thenReturn(testTasks);

        mockMvc.perform(get("/api/approval/pending-tasks")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("测试内容"));
    }
} 