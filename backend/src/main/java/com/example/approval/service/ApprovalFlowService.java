package com.example.approval.service;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.dto.ApprovalNodeDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.model.ApprovalNode;
import com.example.approval.repository.ApprovalFlowRepository;
import com.example.approval.repository.ApprovalNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalFlowService {

    @Autowired
    private ApprovalFlowRepository flowRepository;

    @Autowired
    private ApprovalNodeRepository nodeRepository;

    /**
     * 创建审批流程配置
     */
    public ApprovalFlow createApprovalFlow(ApprovalFlowDto flowDto, Long userId) {
        ApprovalFlow flow = new ApprovalFlow();
        flow.setTitle(flowDto.getTitle());
        flow.setName(flowDto.getName());
        flow.setFlowName(flowDto.getName());
        flow.setLevel(flowDto.getLevel());
        flow.setCreatedBy(userId);
        flow.setCreatedAt(LocalDateTime.now());
        flow.setUpdatedAt(LocalDateTime.now());
        flow.setIsActive(true);
        flow.setIsDefault(false);

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
    public ApprovalFlow updateApprovalFlow(Long id, ApprovalFlowDto flowDto) {
        ApprovalFlow flow = flowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("审批流程不存在"));

        flow.setTitle(flowDto.getTitle());
        flow.setName(flowDto.getName());
        flow.setFlowName(flowDto.getName());
        flow.setLevel(flowDto.getLevel());
        flow.setUpdatedAt(LocalDateTime.now());

        return flowRepository.save(flow);
    }

    /**
     * 删除审批流程配置
     */
    public void deleteApprovalFlow(Long id) {
        ApprovalFlow flow = flowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("审批流程不存在"));

        // 如果是默认流程，不允许删除
        if (flow.getIsDefault()) {
            throw new RuntimeException("默认审批流程不允许删除");
        }

        flowRepository.delete(flow);
    }

    /**
     * 获取活跃的审批流程
     */
    public List<ApprovalFlow> getActiveApprovalFlows() {
        return flowRepository.findByIsActiveTrue();
    }
    
    /**
     * 获取某个分类的审批流程
     */
    public List<ApprovalFlow> getApprovalFlowsByCategory(String category) {
        return flowRepository.findByCategory(category);
    }
    
    /**
     * 通过ID获取审批流程
     */
    public ApprovalFlow getApprovalFlowById(Long id) {
        return flowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("审批流程不存在"));
    }
    
    /**
     * 设置默认审批流程
     */
    public ApprovalFlow setDefaultApprovalFlow(Long id) {
        // 先将所有流程设置为非默认
        List<ApprovalFlow> flows = flowRepository.findAll();
        for (ApprovalFlow flow : flows) {
            flow.setIsDefault(false);
        }
        flowRepository.saveAll(flows);
        
        // 将指定流程设置为默认
        ApprovalFlow flow = getApprovalFlowById(id);
        flow.setIsDefault(true);
        return flowRepository.save(flow);
    }
    
    /**
     * 停用审批流程
     */
    public ApprovalFlow deactivateApprovalFlow(Long id) {
        ApprovalFlow flow = getApprovalFlowById(id);
        if (flow.getIsDefault()) {
            throw new RuntimeException("默认审批流程不能停用");
        }
        flow.setIsActive(false);
        return flowRepository.save(flow);
    }
    
    /**
     * 启用审批流程
     */
    public ApprovalFlow activateApprovalFlow(Long id) {
        ApprovalFlow flow = getApprovalFlowById(id);
        flow.setIsActive(true);
        return flowRepository.save(flow);
    }
    
    /**
     * 添加审批节点
     */
    public ApprovalNode addApprovalNode(Long flowId, ApprovalNodeDto nodeDto) {
        // 验证流程是否存在，不需要使用返回值
        getApprovalFlowById(flowId);
        
        ApprovalNode node = new ApprovalNode();
        node.setNodeName(nodeDto.getNodeName());
        node.setApprover(nodeDto.getApprover());
        node.setApproverRole(nodeDto.getApproverRole());
        node.setOrderNum(nodeDto.getOrderNum());
        node.setCountersign(nodeDto.isCountersign());
        node.setDescription(nodeDto.getDescription());
        node.setFlowId(flowId);
        node.setAllowReject(nodeDto.isAllowReject());
        node.setRejectStrategy(nodeDto.getRejectStrategy());
        
        return nodeRepository.save(node);
    }
    
    /**
     * 获取流程的所有节点
     */
    public List<ApprovalNode> getApprovalNodesByFlowId(Long flowId) {
        return nodeRepository.findByFlowId(flowId);
    }
    
    /**
     * 通过ID获取审批节点
     */
    public ApprovalNode getApprovalNodeById(Long nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("审批节点不存在"));
    }
    
    /**
     * 更新审批节点
     */
    public ApprovalNode updateApprovalNode(Long nodeId, ApprovalNodeDto nodeDto) {
        ApprovalNode node = getApprovalNodeById(nodeId);
        
        node.setNodeName(nodeDto.getNodeName());
        node.setApprover(nodeDto.getApprover());
        node.setApproverRole(nodeDto.getApproverRole());
        node.setOrderNum(nodeDto.getOrderNum());
        node.setCountersign(nodeDto.isCountersign());
        node.setDescription(nodeDto.getDescription());
        node.setAllowReject(nodeDto.isAllowReject());
        node.setRejectStrategy(nodeDto.getRejectStrategy());
        
        return nodeRepository.save(node);
    }
    
    /**
     * 删除审批节点
     */
    public void deleteApprovalNode(Long nodeId) {
        ApprovalNode node = getApprovalNodeById(nodeId);
        nodeRepository.delete(node);
    }
}
