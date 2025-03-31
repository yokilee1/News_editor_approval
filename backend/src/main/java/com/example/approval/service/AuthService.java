package com.example.approval.service;

import com.example.approval.dto.AuthRequest;
import com.example.approval.dto.AuthResponse;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import com.example.approval.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

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
        // 执行认证，但不存储结果
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        
        // 获取用户详细信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 根据用户信息生成 JWT Token
        return jwtTokenProvider.createToken(username, user.getRole().name());
    }

    /**
     * 用户认证（使用AuthRequest对象）
     */
    public AuthResponse authenticate(AuthRequest request) {
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
        
        // 构建响应
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getUsername());
        response.setRole(user.getRole().name());
        
        return response;
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtService.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
