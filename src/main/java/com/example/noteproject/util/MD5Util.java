package com.example.noteproject.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类，使用 JDK 自带的 MD5 算法。
 */
public class MD5Util {

    private MD5Util() {
    }

    /**
     * 对字符串进行 MD5 加密并返回 32 位十六进制字符串。
     *
     * @param str 原始字符串
     * @return MD5 加密后的字符串
     */
    public static String encrypt(String str) {
        if (str == null) {
            throw new IllegalArgumentException("待加密字符串不能为空");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                String hex = Integer.toHexString((b & 0xFF));
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 在标准 JDK 中不会发生，仅作兜底。
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
}

