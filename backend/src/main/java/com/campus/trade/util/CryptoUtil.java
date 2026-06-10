package com.campus.trade.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** 手机号等敏感字段 AES 加密存储 */
public final class CryptoUtil {
    private static final String ALGO = "AES";
    private static final byte[] KEY = "DB2026CampusKey!".getBytes(StandardCharsets.UTF_8);

    private CryptoUtil() {}

    public static String encrypt(String plain) {
        if (plain == null || plain.isBlank()) return plain;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, ALGO));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("加密失败", e);
        }
    }

    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isBlank()) return cipherText;
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, ALGO));
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return cipherText;
        }
    }
}
