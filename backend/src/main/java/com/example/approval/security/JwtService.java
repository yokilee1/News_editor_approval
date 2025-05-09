package com.example.approval.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;
    
    @Value("${jwt.expiration}")
    private long expiration; // 从配置中读取过期时间

    @Autowired
    public JwtService(SecretKey jwtSecretKey) {
        // 使用 JwtConfig 提供的密钥
        this.key = jwtSecretKey;
        logger.debug("JwtService 初始化完成，使用注入的密钥");
    }

    public String generateToken(UserDetails userDetails) {
        try {
            logger.debug("开始为用户 {} 生成令牌", userDetails.getUsername());
            Map<String, Object> claims = new HashMap<>();
            String token = createToken(claims, userDetails.getUsername());
            logger.debug("用户 {} 的令牌生成成功", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            logger.error("为用户 {} 生成令牌失败: {}", userDetails.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    private String createToken(Map<String, Object> claims, String subject) {
        try {
            logger.debug("创建令牌，主题: {}", subject);
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            logger.error("创建令牌失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String extractUsername(String token) {
        try {
            logger.debug("从令牌中提取用户名");
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("从令牌中提取用户名失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        try {
            logger.debug("从令牌中提取过期时间");
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            logger.error("从令牌中提取过期时间失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            logger.debug("从令牌中提取声明");
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("从令牌中提取声明失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            logger.debug("解析令牌的所有声明");
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("解析令牌的所有声明失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            logger.debug("检查令牌是否过期");
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.error("检查令牌是否过期失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            logger.debug("验证令牌");
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.debug("令牌验证结果: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("验证令牌失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 