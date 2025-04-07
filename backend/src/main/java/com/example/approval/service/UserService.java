package com.example.approval.service;

import com.example.approval.dto.UserDto;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 创建新用户（注册）
     */
    public User createUser(UserDto userDto) {
        logger.info("开始创建用户: {}", userDto.getUsername());
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(userDto.getUsername())) {
                logger.warn("用户名已存在: {}", userDto.getUsername());
                throw new RuntimeException("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(userDto.getEmail())) {
                logger.warn("邮箱已存在: {}", userDto.getEmail());
                throw new RuntimeException("邮箱已存在");
            }
            
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setEmail(userDto.getEmail());
            user.setName(userDto.getName());
            user.setRole(User.Role.valueOf(userDto.getRole().toUpperCase()));
            user.setCreatedAt(new Date());
            
            User savedUser = userRepository.save(user);
            logger.info("用户创建成功: {}", savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            logger.error("创建用户失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据用户ID获取用户信息
     */
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserInfo(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(User.Role.valueOf(userDto.getRole().toUpperCase()));
        return userRepository.save(user);
    }

    public boolean resetPassword(String email) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void deleteUser(long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));
    }

    /**
     * 根据角色获取用户列表
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
    }
}
