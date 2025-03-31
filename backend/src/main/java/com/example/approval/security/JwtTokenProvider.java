package com.example.approval.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import java.util.Date;

@Component
public class JwtTokenProvider {

    // 从配置文件读取密钥和有效期
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    // 令牌有效期，单位为毫秒（例如：3600000毫秒=1小时）
    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    // 根据用户名生成 JWT Token
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // 从 Token 中解析用户名
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // 验证 Token 是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 记录错误日志：无效或过期的 Token
            return false;
        }
    }
}
