package com.example.approval.controller;

import com.example.approval.dto.UserDto;
import com.example.approval.model.User;
import com.example.approval.service.LogService;
import com.example.approval.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LogService logService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @PostMapping
    public Map<String, Object> createUser(@RequestBody UserDto userDto, @RequestHeader("X-User-ID") Long operatorId) {
        User user = userService.createUser(userDto);
        logService.recordLog("用户管理", "创建用户", "创建了用户: " + user.getUsername(), "USER", operatorId, null, "");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        return response;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody UserDto userDto, @RequestHeader("X-User-ID") Long operatorId) {
        User user = userService.updateUserInfo(id, userDto);
        logService.recordLog("用户管理", "更新用户", "更新了用户: " + user.getUsername(), "USER", operatorId, null, "");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        return response;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id, @RequestHeader("X-User-ID") Long operatorId) {
        userService.deleteUser(id);
        logService.recordLog("用户管理", "删除用户", "删除了用户ID: " + id, "USER", operatorId, null, "");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }
    
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return userService.getUsersByRole(role);
    }

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
    /*
    * 展示所有用户列表
     */
    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.getAllUsers();
    }
}
