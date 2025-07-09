package com.xdever.demo.utils;

import io.micrometer.common.util.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class BasedSecureCommunication {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private static final String key = "";

    // 生成密钥
    protected static SecretKey generateKey() throws NoSuchAlgorithmException {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
//        keyGenerator.init(256);
//        return keyGenerator.generateKey();
        byte[] keyBytes = "12345678901234567890123456789012".getBytes();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    // 加密方法（GCM模式）
    protected static byte[] encrypt(SecretKey key, byte[] plaintext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // 生成随机IV
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        // 初始化GCM参数
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        // 加密并附加IV
        byte[] ciphertext = cipher.doFinal(plaintext);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);
        outputStream.write(ciphertext);
        return outputStream.toByteArray();
    }

    // 解密方法（GCM模式）
    protected static String decrypt(SecretKey key, byte[] encryptedData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // 分离IV和密文
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);

        // 初始化GCM参数
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 重载解密方法（用于接收分离的IV和密文）
    protected static String decrypt(SecretKey key, byte[] iv, byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


}
