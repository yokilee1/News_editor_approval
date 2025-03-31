package com.example.approval.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.aspectj.lang.ProceedingJoinPoint;

@TestConfiguration
public class PermissionAspectTestConfig {
    
    @Bean
    @Primary
    public PermissionAspect testPermissionAspect() {
        return new PermissionAspect() {
            @Override
            public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
                // 测试环境中直接放行所有权限检查
                return joinPoint.proceed();
            }
        };
    }
} 