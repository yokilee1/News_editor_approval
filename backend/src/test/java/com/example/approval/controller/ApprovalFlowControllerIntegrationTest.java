package com.example.approval.controller;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.service.ApprovalFlowService;
import com.example.approval.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApprovalFlowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApprovalFlowService approvalFlowService;

    @MockBean
    private LogService logService;

    private ApprovalFlow testFlow;
    private ApprovalFlowDto testFlowDto;

    @BeforeEach
    void setUp() {
        testFlow = new ApprovalFlow();
        testFlow.setId(1L);
        testFlow.setTitle("测试流程");
        testFlow.setName("测试流程");
        testFlow.setLevel(2);

        testFlowDto = new ApprovalFlowDto();
        testFlowDto.setTitle("测试流程");
        testFlowDto.setName("测试流程");
        testFlowDto.setLevel(2);
    }

    @Test
    void createApprovalFlow_shouldReturnCreatedFlow() throws Exception {
        when(approvalFlowService.createApprovalFlow(any(ApprovalFlowDto.class), anyLong())).thenReturn(testFlow);
        doNothing().when(logService).recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString());

        mockMvc.perform(post("/api/approval-flow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlowDto))
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.flow.title").value("测试流程"));
    }

    @Test
    void getAllApprovalFlows_shouldReturnFlows() throws Exception {
        when(approvalFlowService.getAllApprovalFlows()).thenReturn(Arrays.asList(testFlow));

        mockMvc.perform(get("/api/approval-flow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("测试流程"));
    }

    @Test
    void updateApprovalFlow_shouldReturnUpdatedFlow() throws Exception {
        when(approvalFlowService.updateApprovalFlow(anyLong(), any(ApprovalFlowDto.class))).thenReturn(testFlow);
        doNothing().when(logService).recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString());

        mockMvc.perform(put("/api/approval-flow/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlowDto))
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.flow.title").value("测试流程"));
    }

    @Test
    void deleteApprovalFlow_shouldReturnSuccess() throws Exception {
        doNothing().when(approvalFlowService).deleteApprovalFlow(anyLong());
        doNothing().when(logService).recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString());

        mockMvc.perform(delete("/api/approval-flow/1")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
} 