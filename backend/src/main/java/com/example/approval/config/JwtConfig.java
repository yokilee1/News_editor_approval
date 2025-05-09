package com.example.approval.config;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${jwt.expiration}")
    private long expiration;
    
    // 使用固定的密钥字符串
    private static final String SECRET_KEY_STRING = "this_is_a_very_secure_and_long_secret_key_for_jwt_signing_in_our_approval_system_2024";

    @Bean
    public SecretKey jwtSecretKey() {
        // 从固定字符串创建密钥，确保每次启动使用相同的密钥
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        // 确保密钥长度符合 HS256 算法的要求（至少 32 字节）
        if (keyBytes.length < 32) {
            byte[] paddedKeyBytes = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKeyBytes, 0, keyBytes.length);
            keyBytes = paddedKeyBytes;
        }
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
} 