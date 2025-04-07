package com.example.approval.service;

import com.example.approval.dto.AuthRequest;
import com.example.approval.dto.AuthResponse;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import com.example.approval.security.JwtTokenProvider;
import com.example.approval.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    /**
     * 通过用户名和密码验证用户，并生成 JWT Token
     */
    public String authenticate(String username, String password) {
        try {
            logger.debug("开始认证用户: {}", username);
            // 执行认证，但不存储结果
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // 获取用户详细信息
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 根据用户信息生成 JWT Token
            String token = jwtTokenProvider.createToken(username, user.getRole().name());
            logger.debug("用户 {} 认证成功，生成令牌", username);
            return token;
        } catch (Exception e) {
            logger.error("用户 {} 认证失败: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 用户认证（使用AuthRequest对象）
     */
    public AuthResponse authenticate(AuthRequest request) {
        try {
            logger.debug("开始认证用户: {}", request.getUsername());
            // 认证逻辑
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            // 获取用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 生成JWT令牌
            String token = jwtService.generateToken(userDetails);
            logger.debug("用户 {} 认证成功，生成令牌", request.getUsername());
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("name", user.getUsername());
            userInfo.put("role", user.getRole().name());
            userInfo.put("email", user.getEmail());
            
            // 构建响应
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setName(user.getUsername());
            response.setRole(user.getRole().name());
            response.setUser(userInfo);
            
            logger.debug("用户 {} 认证响应: {}", request.getUsername(), response);
            return response;
        } catch (Exception e) {
            logger.error("用户 {} 认证失败: {}", request.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("开始验证令牌");
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtService.validateToken(token, userDetails);
            logger.debug("令牌验证结果: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("令牌验证失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
