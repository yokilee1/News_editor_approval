package com.example.approval.service;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.repository.ApprovalFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalFlowService {

    @Autowired
    private ApprovalFlowRepository flowRepository;

    /**
     * 创建审批流程配置
     */
    public ApprovalFlow createApprovalFlow(ApprovalFlowDto flowDto, Long creatorId) {
        ApprovalFlow flow = new ApprovalFlow();
        flow.setTitle(flowDto.getTitle());
        flow.setName(flowDto.getName());
        flow.setLevel(flowDto.getLevel());
        flow.setCreatedBy(creatorId);
        flow.setCreatedAt(LocalDateTime.now());
        flow.setUpdatedAt(LocalDateTime.now());
        return flowRepository.save(flow);
    }

    /**
     * 获取所有审批流程配置
     */
    public List<ApprovalFlow> getAllApprovalFlows() {
        return flowRepository.findAll();
    }

    /**
     * 更新审批流程配置
     */
    public ApprovalFlow updateApprovalFlow(Long flowId, ApprovalFlowDto flowDto) {
        ApprovalFlow flow = flowRepository.findById(flowId)
                .orElseThrow(() -> new RuntimeException("审批流程不存在"));
        flow.setTitle(flowDto.getTitle());
        flow.setName(flowDto.getName());
        flow.setLevel(flowDto.getLevel());
        flow.setUpdatedAt(LocalDateTime.now());
        return flowRepository.save(flow);
    }

    /**
     * 删除审批流程配置
     */
    public void deleteApprovalFlow(Long flowId) {
        flowRepository.deleteById(flowId);
    }
}
