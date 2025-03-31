package com.example.approval.service;

import com.example.approval.model.Permission;
import com.example.approval.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolePermissionService {

    private final Map<User.Role, List<Permission>> rolePermissionMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 管理员拥有所有权限
        rolePermissionMap.put(User.Role.ADMIN, Arrays.asList(
                Permission.CONTENT_CREATE, Permission.CONTENT_EDIT, 
                Permission.CONTENT_VIEW, Permission.CONTENT_DELETE,
                Permission.APPROVAL_SUBMIT, Permission.APPROVAL_REVIEW, 
                Permission.APPROVAL_VIEW, Permission.APPROVAL_MANAGE,
                Permission.USER_CREATE, Permission.USER_EDIT, 
                Permission.USER_VIEW, Permission.USER_DELETE,
                Permission.SYSTEM_CONFIG, Permission.LOG_VIEW,
                Permission.AI_GENERATE, Permission.AI_TREND
        ));

        // 编辑人员权限
        rolePermissionMap.put(User.Role.EDITOR, Arrays.asList(
                Permission.CONTENT_CREATE, Permission.CONTENT_EDIT, 
                Permission.CONTENT_VIEW, Permission.APPROVAL_SUBMIT,
                Permission.APPROVAL_VIEW, Permission.AI_GENERATE, 
                Permission.AI_TREND
        ));

        // 审批人员权限
        rolePermissionMap.put(User.Role.APPROVER, Arrays.asList(
                Permission.CONTENT_VIEW, Permission.APPROVAL_REVIEW, 
                Permission.APPROVAL_VIEW, Permission.AI_TREND
        ));

        // 普通用户权限
        rolePermissionMap.put(User.Role.USER, Arrays.asList(
                Permission.CONTENT_VIEW, Permission.AI_TREND
        ));
    }

    public boolean hasPermission(User.Role role, Permission permission) {
        List<Permission> permissions = rolePermissionMap.get(role);
        return permissions != null && permissions.contains(permission);
    }

    public List<Permission> getPermissionsByRole(User.Role role) {
        return rolePermissionMap.get(role);
    }
} 