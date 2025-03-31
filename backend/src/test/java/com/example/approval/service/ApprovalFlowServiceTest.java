package com.example.approval.service;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.dto.ApprovalNodeDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.model.ApprovalNode;
import com.example.approval.repository.ApprovalFlowRepository;
import com.example.approval.repository.ApprovalNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApprovalFlowServiceTest {

    @Mock
    private ApprovalFlowRepository flowRepository;

    @Mock
    private ApprovalNodeRepository nodeRepository;

    @InjectMocks
    private ApprovalFlowService approvalFlowService;

    private ApprovalFlow testFlow;
    private ApprovalNode testNode;
    private ApprovalFlowDto testFlowDto;
    private ApprovalNodeDto testNodeDto;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试流程
        testFlow = new ApprovalFlow();
        testFlow.setId(1L);
        testFlow.setTitle("测试流程");
        testFlow.setName("测试流程");
        testFlow.setFlowName("测试流程");
        testFlow.setLevel(2);
        testFlow.setIsActive(true);
        testFlow.setIsDefault(false);

        // 创建测试节点
        testNode = new ApprovalNode();
        testNode.setId(1L);
        testNode.setNodeName("测试节点");
        testNode.setFlowId(1L);
        testNode.setApprover(2L);
        testNode.setOrderNum(1);

        // 创建测试DTO
        testFlowDto = new ApprovalFlowDto();
        testFlowDto.setTitle("测试流程");
        testFlowDto.setName("测试流程");
        testFlowDto.setLevel(2);

        testNodeDto = new ApprovalNodeDto();
        testNodeDto.setNodeName("测试节点");
        testNodeDto.setApprover(2L);
        testNodeDto.setOrderNum(1);
    }

    @Test
    void createApprovalFlow_shouldReturnCreatedFlow() {
        when(flowRepository.save(any(ApprovalFlow.class))).thenReturn(testFlow);

        ApprovalFlow createdFlow = approvalFlowService.createApprovalFlow(testFlowDto, userId);

        assertNotNull(createdFlow);
        assertEquals("测试流程", createdFlow.getTitle());
        assertEquals(2, createdFlow.getLevel());
        assertTrue(createdFlow.getIsActive());
        verify(flowRepository, times(1)).save(any(ApprovalFlow.class));
    }

    @Test
    void getAllApprovalFlows_shouldReturnAllFlows() {
        when(flowRepository.findAll()).thenReturn(Arrays.asList(testFlow));

        List<ApprovalFlow> flows = approvalFlowService.getAllApprovalFlows();

        assertNotNull(flows);
        assertEquals(1, flows.size());
        assertEquals("测试流程", flows.get(0).getTitle());
    }

    @Test
    void updateApprovalFlow_shouldUpdateAndReturnFlow() {
        when(flowRepository.findById(1L)).thenReturn(Optional.of(testFlow));
        when(flowRepository.save(any(ApprovalFlow.class))).thenReturn(testFlow);

        testFlowDto.setTitle("更新的流程");
        ApprovalFlow updatedFlow = approvalFlowService.updateApprovalFlow(1L, testFlowDto);

        assertNotNull(updatedFlow);
        assertEquals("更新的流程", updatedFlow.getTitle());
        verify(flowRepository, times(1)).save(any(ApprovalFlow.class));
    }

    @Test
    void deleteApprovalFlow_shouldDeleteFlow() {
        when(flowRepository.findById(1L)).thenReturn(Optional.of(testFlow));
        doNothing().when(flowRepository).delete(any(ApprovalFlow.class));

        approvalFlowService.deleteApprovalFlow(1L);

        verify(flowRepository, times(1)).delete(any(ApprovalFlow.class));
    }

    @Test
    void addApprovalNode_shouldAddAndReturnNode() {
        when(flowRepository.findById(1L)).thenReturn(Optional.of(testFlow));
        when(nodeRepository.save(any(ApprovalNode.class))).thenReturn(testNode);

        ApprovalNode addedNode = approvalFlowService.addApprovalNode(1L, testNodeDto);

        assertNotNull(addedNode);
        assertEquals("测试节点", addedNode.getNodeName());
        assertEquals(1L, addedNode.getFlowId());
        verify(nodeRepository, times(1)).save(any(ApprovalNode.class));
    }

    @Test
    void getApprovalNodesByFlowId_shouldReturnNodes() {
        when(nodeRepository.findByFlowId(1L)).thenReturn(Arrays.asList(testNode));

        List<ApprovalNode> nodes = approvalFlowService.getApprovalNodesByFlowId(1L);

        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        assertEquals("测试节点", nodes.get(0).getNodeName());
    }
} 