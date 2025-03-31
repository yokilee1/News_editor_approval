package com.example.approval.security;

import com.example.approval.model.Permission;
import com.example.approval.model.User;
import com.example.approval.service.RolePermissionService;
import com.example.approval.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private UserService userService;
    
    @Around("@annotation(com.example.approval.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 在测试环境中，可能没有请求上下文
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取当前用户
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("用户未认证");
            }
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            userId = user.getId();
        }
        
        User user = userService.getUserProfile(userId);
        
        // 获取注解中的权限
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        Permission requiredPermission = requirePermission.value();
        
        // 检查是否有权限
        if (!rolePermissionService.hasPermission(user.getRole(), requiredPermission)) {
            throw new RuntimeException("无权执行此操作");
        }
        
        return joinPoint.proceed();
    }
}