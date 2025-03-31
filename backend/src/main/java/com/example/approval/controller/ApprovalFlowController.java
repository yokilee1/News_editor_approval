package com.example.approval.controller;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.dto.ApprovalNodeDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.model.ApprovalNode;
import com.example.approval.model.Permission;
import com.example.approval.security.RequirePermission;
import com.example.approval.service.ApprovalFlowService;
import com.example.approval.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批流程配置管理接口
 */
@RestController
@RequestMapping("/api/approval-flow")
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService approvalFlowService;
    
    @Autowired
    private LogService logService;
    
    /**
     * 创建审批流程
     */
    @PostMapping("/create")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> createApprovalFlow(
            @RequestBody ApprovalFlowDto flowDto,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalFlow flow = approvalFlowService.createApprovalFlow(flowDto, userId);
        
        // 记录操作日志
        logService.recordLog(
            "CREATE", 
            "ApprovalFlow", 
            flow.getId().toString(), 
            "创建审批流程：" + flow.getFlowName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        return ResponseEntity.ok(flow);
    }
    
    /**
     * 获取所有审批流程
     */
    @GetMapping("/list")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getAllApprovalFlows() {
        List<ApprovalFlow> flows = approvalFlowService.getAllApprovalFlows();
        return ResponseEntity.ok(flows);
    }
    
    /**
     * 获取活跃的审批流程
     */
    @GetMapping("/active")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getActiveApprovalFlows() {
        List<ApprovalFlow> flows = approvalFlowService.getActiveApprovalFlows();
        return ResponseEntity.ok(flows);
    }
    
    /**
     * 获取某个分类的审批流程
     */
    @GetMapping("/category/{category}")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getApprovalFlowsByCategory(@PathVariable String category) {
        List<ApprovalFlow> flows = approvalFlowService.getApprovalFlowsByCategory(category);
        return ResponseEntity.ok(flows);
    }
    
    /**
     * 获取审批流程详情
     */
    @GetMapping("/{id}")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getApprovalFlow(@PathVariable Long id) {
        ApprovalFlow flow = approvalFlowService.getApprovalFlowById(id);
        return ResponseEntity.ok(flow);
    }
    
    /**
     * 更新审批流程
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> updateApprovalFlow(
            @PathVariable Long id,
            @RequestBody ApprovalFlowDto flowDto,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalFlow flow = approvalFlowService.updateApprovalFlow(id, flowDto);
        
        // 记录操作日志
        logService.recordLog(
            "UPDATE", 
            "ApprovalFlow", 
            id.toString(), 
            "更新审批流程：" + flow.getFlowName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        return ResponseEntity.ok(flow);
    }
    
    /**
     * 设置默认审批流程
     */
    @PutMapping("/{id}/set-default")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> setDefaultApprovalFlow(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalFlow flow = approvalFlowService.setDefaultApprovalFlow(id);
        
        // 记录操作日志
        logService.recordLog(
            "UPDATE", 
            "ApprovalFlow", 
            id.toString(), 
            "设置默认审批流程：" + flow.getFlowName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("flow", flow);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 停用审批流程
     */
    @PutMapping("/{id}/deactivate")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> deactivateApprovalFlow(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalFlow flow = approvalFlowService.deactivateApprovalFlow(id);
        
        // 记录操作日志
        logService.recordLog(
            "DEACTIVATE", 
            "ApprovalFlow", 
            id.toString(), 
            "停用审批流程：" + flow.getFlowName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("flow", flow);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 启用审批流程
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> activateApprovalFlow(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalFlow flow = approvalFlowService.activateApprovalFlow(id);
        
        // 记录操作日志
        logService.recordLog(
            "ACTIVATE", 
            "ApprovalFlow", 
            id.toString(), 
            "启用审批流程：" + flow.getFlowName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("flow", flow);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除审批流程
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> deleteApprovalFlow(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        String flowName = approvalFlowService.getApprovalFlowById(id).getFlowName();
        approvalFlowService.deleteApprovalFlow(id);
        
        // 记录操作日志
        logService.recordLog(
            "DELETE", 
            "ApprovalFlow", 
            id.toString(), 
            "删除审批流程：" + flowName, 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 添加审批节点
     */
    @PostMapping("/{flowId}/nodes")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> addApprovalNode(
            @PathVariable Long flowId,
            @RequestBody ApprovalNodeDto nodeDto,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalNode node = approvalFlowService.addApprovalNode(flowId, nodeDto);
        
        // 记录操作日志
        logService.recordLog(
            "CREATE", 
            "ApprovalNode", 
            node.getId().toString(), 
            "添加审批节点：" + node.getNodeName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        return ResponseEntity.ok(node);
    }
    
    /**
     * 获取流程的所有节点
     */
    @GetMapping("/{flowId}/nodes")
    @RequirePermission(Permission.APPROVAL_VIEW)
    public ResponseEntity<?> getApprovalNodes(@PathVariable Long flowId) {
        List<ApprovalNode> nodes = approvalFlowService.getApprovalNodesByFlowId(flowId);
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 更新审批节点
     */
    @PutMapping("/nodes/{nodeId}")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> updateApprovalNode(
            @PathVariable Long nodeId,
            @RequestBody ApprovalNodeDto nodeDto,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        ApprovalNode node = approvalFlowService.updateApprovalNode(nodeId, nodeDto);
        
        // 记录操作日志
        logService.recordLog(
            "UPDATE", 
            "ApprovalNode", 
            nodeId.toString(), 
            "更新审批节点：" + node.getNodeName(), 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        return ResponseEntity.ok(node);
    }
    
    /**
     * 删除审批节点
     */
    @DeleteMapping("/nodes/{nodeId}")
    @RequirePermission(Permission.APPROVAL_MANAGE)
    public ResponseEntity<?> deleteApprovalNode(
            @PathVariable Long nodeId,
            @RequestAttribute("userId") Long userId,
            HttpServletRequest request) {
        
        String nodeName = approvalFlowService.getApprovalNodeById(nodeId).getNodeName();
        approvalFlowService.deleteApprovalNode(nodeId);
        
        // 记录操作日志
        logService.recordLog(
            "DELETE", 
            "ApprovalNode", 
            nodeId.toString(), 
            "删除审批节点：" + nodeName, 
            userId, 
            null,
            request.getRemoteAddr()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}
