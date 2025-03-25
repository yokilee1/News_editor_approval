package com.example.approval.service;

import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import com.example.approval.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    /**
     * 通过用户名和密码验证用户，并生成 JWT Token
     */
    public String authenticate(String username, String password) {
        // 执行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        // 获取用户详细信息（若需要从中提取其他信息）
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 根据用户信息生成 JWT Token
        return jwtTokenProvider.createToken(username, user.getRole().name());
    }
}
