package com.example.approval.controller;

import com.example.approval.dto.ApprovalFlowDto;
import com.example.approval.model.ApprovalFlow;
import com.example.approval.service.ApprovalFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 审批流程配置接口
 */
@RestController
@RequestMapping("/api/approval-flow")
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService flowService;

    /**
     * 创建审批流程
     */
    @PostMapping("/create")
    public ResponseEntity<?> createFlow(@RequestBody ApprovalFlowDto flowDto,
                                        @RequestAttribute("userId") Long userId) {
        ApprovalFlow flow = flowService.createApprovalFlow(flowDto, userId);
        return ResponseEntity.ok(flow);
    }

    /**
     * 查询所有审批流程配置
     */
    @GetMapping("/list")
    public ResponseEntity<?> listFlows() {
        List<ApprovalFlow> flows = flowService.getAllApprovalFlows();
        return ResponseEntity.ok(flows);
    }

    /**
     * 更新审批流程配置
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFlow(@PathVariable Long id,
                                        @RequestBody ApprovalFlowDto flowDto) {
        ApprovalFlow flow = flowService.updateApprovalFlow(id, flowDto);
        return ResponseEntity.ok(flow);
    }

    /**
     * 删除审批流程配置
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFlow(@PathVariable Long id) {
        flowService.deleteApprovalFlow(id);
        return ResponseEntity.ok("删除成功");
    }
}
