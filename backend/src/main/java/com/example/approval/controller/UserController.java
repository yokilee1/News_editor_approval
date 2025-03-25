package com.example.approval.controller;

import com.example.approval.model.User;
import com.example.approval.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     * 假设通过请求属性 "userId" 获取当前登录用户ID（需通过 JWT 过滤器注入）
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestAttribute("userId") Long userId) {
        User user = userService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }

    // 可继续添加更新用户信息等接口
}
