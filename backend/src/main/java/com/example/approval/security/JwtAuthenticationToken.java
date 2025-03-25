package com.example.approval.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * 自定义 JWT 认证令牌，可扩展保存 token 信息
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String token;

    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
