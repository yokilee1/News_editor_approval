package com.example.approval.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    // AES-GCM 模式参数：128位认证标签长度（单位：bit）
    private static final int TAG_LENGTH_BIT = 128;
    // IV（初始化向量）长度：12 字节推荐用于 GCM
    private static final int IV_LENGTH_BYTE = 12;
    // AES 256 位密钥要求 32 字节密钥
    private static final int AES_KEY_SIZE = 256;

    /**
     * 加密明文，返回 Base64 编码后的字符串，包含 IV 和密文
     *
     * @param plainText 明文
     * @param keyStr    密钥字符串（32 字节 Base64 编码的密钥）
     * @return 加密后的 Base64 字符串
     */
    public static String encrypt(String plainText, String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 生成随机 IV
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 将 IV 和密文拼接在一起
        byte[] encrypted = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

        // 返回 Base64 编码结果
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密 Base64 编码后的密文
     *
     * @param encryptedText Base64 编码后的字符串（IV + 密文）
     * @param keyStr        密钥字符串（32 字节 Base64 编码的密钥）
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText, String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        // 提取 IV 和密文
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byte[] cipherText = new byte[encryptedBytes.length - IV_LENGTH_BYTE];
        System.arraycopy(encryptedBytes, 0, iv, 0, IV_LENGTH_BYTE);
        System.arraycopy(encryptedBytes, IV_LENGTH_BYTE, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] plainBytes = cipher.doFinal(cipherText);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 生成随机 AES 256 密钥，并返回 Base64 编码的字符串
     */
    public static String generateAES256Key() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // 测试方法（可选）
    public static void main(String[] args) throws Exception {
        // 生成 AES 256 密钥
        String key = generateAES256Key();
        System.out.println("生成的密钥：" + key);

        String originalText = "这是一段待加密的敏感信息。";
        String encrypted = encrypt(originalText, key);
        System.out.println("加密后：" + encrypted);

        String decrypted = decrypt(encrypted, key);
        System.out.println("解密后：" + decrypted);
    }
}
